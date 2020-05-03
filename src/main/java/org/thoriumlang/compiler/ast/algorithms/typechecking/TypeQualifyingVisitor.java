package org.thoriumlang.compiler.ast.algorithms.typechecking;

import org.thoriumlang.compiler.api.errors.SemanticError;
import org.thoriumlang.compiler.ast.nodes.Attribute;
import org.thoriumlang.compiler.ast.nodes.BooleanValue;
import org.thoriumlang.compiler.ast.nodes.Class;
import org.thoriumlang.compiler.ast.nodes.DirectAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.FunctionValue;
import org.thoriumlang.compiler.ast.nodes.IdentifierValue;
import org.thoriumlang.compiler.ast.nodes.IndirectAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.Method;
import org.thoriumlang.compiler.ast.nodes.MethodCallValue;
import org.thoriumlang.compiler.ast.nodes.MethodSignature;
import org.thoriumlang.compiler.ast.nodes.NestedValue;
import org.thoriumlang.compiler.ast.nodes.NewAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.Node;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.nodes.NoneValue;
import org.thoriumlang.compiler.ast.nodes.NumberValue;
import org.thoriumlang.compiler.ast.nodes.Parameter;
import org.thoriumlang.compiler.ast.nodes.Reference;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.ast.nodes.Statement;
import org.thoriumlang.compiler.ast.nodes.StringValue;
import org.thoriumlang.compiler.ast.nodes.TopLevelNode;
import org.thoriumlang.compiler.ast.nodes.Type;
import org.thoriumlang.compiler.ast.nodes.TypeParameter;
import org.thoriumlang.compiler.ast.nodes.TypeSpec;
import org.thoriumlang.compiler.ast.nodes.TypeSpecFunction;
import org.thoriumlang.compiler.ast.nodes.TypeSpecInferred;
import org.thoriumlang.compiler.ast.nodes.TypeSpecIntersection;
import org.thoriumlang.compiler.ast.nodes.TypeSpecSimple;
import org.thoriumlang.compiler.ast.nodes.TypeSpecUnion;
import org.thoriumlang.compiler.ast.nodes.Value;
import org.thoriumlang.compiler.ast.visitor.BaseVisitor;
import org.thoriumlang.compiler.collections.Lists;
import org.thoriumlang.compiler.data.Maybe;
import org.thoriumlang.compiler.symbols.AliasSymbol;
import org.thoriumlang.compiler.symbols.Name;
import org.thoriumlang.compiler.symbols.Symbol;
import org.thoriumlang.compiler.symbols.SymbolTable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This visitor is in charge of replacing all instances of {@link TypeSpecSimple} to canonical types.
 */
public class TypeQualifyingVisitor extends BaseVisitor<Maybe<? extends Node, List<SemanticError>>> {
    private final NodeIdGenerator nodeIdGenerator;

    public TypeQualifyingVisitor(NodeIdGenerator nodeIdGenerator) {
        this.nodeIdGenerator = nodeIdGenerator;
    }

    private static List<SemanticError> collectErrors(Maybe<? extends Node, List<SemanticError>> superType) {
        return superType.isFailure() ? superType.error() : Collections.emptyList();
    }

    private static <T> List<SemanticError> collectErrors(List<Maybe<T, List<SemanticError>>> methods) {
        return methods.stream()
                .filter(Maybe::isFailure)
                .map(Maybe::error)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private static <T> boolean anyFailure(List<Maybe<T, List<SemanticError>>> attributes) {
        return attributes.stream().anyMatch(Maybe::isFailure);
    }

    private static <T> List<T> extractValue(List<Maybe<T, List<SemanticError>>> methods) {
        return methods.stream()
                .map(Maybe::value)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private static <T extends Node> Maybe<T, List<SemanticError>> visit(T node, TypeQualifyingVisitor visitor) {
        return (Maybe<T, List<SemanticError>>) node.accept(visitor);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Node> List<Maybe<T, List<SemanticError>>> visit(List<T> nodes, TypeQualifyingVisitor visitor) {
        return nodes.stream()
                .map(n -> (Maybe<T, List<SemanticError>>) n.accept(visitor))
                .collect(Collectors.toList());
    }

    /**
     * Returns the canonical name of a given type, i.e. the name one all aliases have been resolved.
     *
     * @param type        the type to get the canonical name for
     * @param symbolTable the symbol table in which to look for
     * @return the canonical name
     */
    private static String getCanonicalName(String type, SymbolTable symbolTable) {
        List<Symbol> symbols = symbolTable.find(new Name(type));

        if (symbols.size() != 1) {
            // type is not found, but this visitor's task is not to check the presence of types.
            // we consider what we received as the canonical name for the missing type and return it.
            return type;
        }

        if (symbols.get(0) instanceof AliasSymbol) {
            return getCanonicalName(((AliasSymbol) symbols.get(0)).getTarget(), symbolTable.root());
        }

        return type;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Node> T copyContext(T src, T dst) {
        return (T) dst.getContext().putAll(src.getContext()).getNode();
    }

    @Override
    public Maybe<Root, List<SemanticError>> visit(Root node) {
        // we don't visit uses on purpose, this is useless.
        Maybe<TopLevelNode, List<SemanticError>> topLevel = visit(node.getTopLevelNode(), this);

        if (topLevel.isFailure()) {
            return Maybe.failure(collectErrors(topLevel));
        }

        return Maybe.success(copyContext(node, new Root(
                nodeIdGenerator.next(),
                node.getNamespace(),
                node.getUses(),
                topLevel.value()
        )));
    }

    @Override
    public Maybe<Type, List<SemanticError>> visit(Type node) {
        Maybe<TypeSpec, List<SemanticError>> superType = visit(node.getSuperType(), this);
        List<Maybe<MethodSignature, List<SemanticError>>> methods = visit(node.getMethods(), this);

        if (superType.isFailure() || anyFailure(methods)) {
            return Maybe.failure(Lists.merge(
                    collectErrors(superType),
                    collectErrors(methods)
            ));
        }

        return Maybe.success(copyContext(node, new Type(
                nodeIdGenerator.next(),
                node.getVisibility(),
                node.getName(),
                node.getTypeParameters(),
                superType.value(),
                extractValue(methods)
        )));
    }

    @Override
    public Maybe<Class, List<SemanticError>> visit(Class node) {
        Maybe<TypeSpec, List<SemanticError>> superType = visit(node.getSuperType(), this);
        List<Maybe<Method, List<SemanticError>>> methods = visit(node.getMethods(), this);
        List<Maybe<Attribute, List<SemanticError>>> attributes = visit(node.getAttributes(), this);

        if (superType.isFailure() || anyFailure(attributes) || anyFailure(methods)) {
            return Maybe.failure(Lists.merge(
                    collectErrors(superType),
                    collectErrors(methods),
                    collectErrors(attributes)
            ));
        }

        return Maybe.success(copyContext(node, new Class(
                nodeIdGenerator.next(),
                node.getVisibility(),
                node.getName(),
                node.getTypeParameters(),
                superType.value(),
                extractValue(methods),
                extractValue(attributes)
        )));
    }

    @Override
    public Maybe<TypeSpecIntersection, List<SemanticError>> visit(TypeSpecIntersection node) {
        List<Maybe<TypeSpec, List<SemanticError>>> typeSpec = visit(node.getTypes(), this);

        if (anyFailure(typeSpec)) {
            return Maybe.failure(collectErrors(typeSpec));
        }

        return Maybe.success(copyContext(node, new TypeSpecIntersection(
                nodeIdGenerator.next(),
                extractValue(typeSpec)
        )));
    }

    @Override
    public Maybe<TypeSpecUnion, List<SemanticError>> visit(TypeSpecUnion node) {
        List<Maybe<TypeSpec, List<SemanticError>>> typeSpec = visit(node.getTypes(), this);

        if (anyFailure(typeSpec)) {
            return Maybe.failure(collectErrors(typeSpec));
        }

        return Maybe.success(copyContext(node, new TypeSpecUnion(
                nodeIdGenerator.next(),
                extractValue(typeSpec)
        )));
    }

    @Override
    public Maybe<TypeSpecSimple, List<SemanticError>> visit(TypeSpecSimple node) {
        List<Maybe<TypeSpec, List<SemanticError>>> arguments = visit(node.getArguments(), this);

        if (anyFailure(arguments)) {
            return Maybe.failure(collectErrors(arguments));
        }

        return Maybe.success(copyContext(node, new TypeSpecSimple(
                nodeIdGenerator.next(),
                getCanonicalName(node.getType(), node.getContext().require(SymbolTable.class)),
                extractValue(arguments)
        )));
    }

    @Override
    public Maybe<TypeSpecFunction, List<SemanticError>> visit(TypeSpecFunction node) {
        Maybe<TypeSpec, List<SemanticError>> returnType = visit(node.getReturnType(), this);
        List<Maybe<TypeSpec, List<SemanticError>>> arguments = visit(node.getArguments(), this);

        if (returnType.isFailure() || anyFailure(arguments)) {
            return Maybe.failure(Lists.merge(
                    collectErrors(returnType),
                    collectErrors(arguments)
            ));
        }

        return Maybe.success(copyContext(node, new TypeSpecFunction(
                nodeIdGenerator.next(),
                extractValue(arguments),
                returnType.value()
        )));
    }

    @Override
    public Maybe<TypeSpecInferred, List<SemanticError>> visit(TypeSpecInferred node) {
        return Maybe.success(node);
    }

    @Override
    public Maybe<MethodSignature, List<SemanticError>> visit(MethodSignature node) {
        List<Maybe<TypeParameter, List<SemanticError>>> typeParameters = visit(node.getTypeParameters(), this);
        List<Maybe<Parameter, List<SemanticError>>> parameters = visit(node.getParameters(), this);
        Maybe<TypeSpec, List<SemanticError>> returnType = visit(node.getReturnType(), this);

        if (anyFailure(parameters) || anyFailure(typeParameters) || returnType.isFailure()) {
            return Maybe.failure(Lists.merge(
                    collectErrors(parameters),
                    collectErrors(typeParameters),
                    collectErrors(returnType)
            ));
        }

        return Maybe.success(copyContext(node, new MethodSignature(
                nodeIdGenerator.next(),
                node.getVisibility(),
                node.getName(),
                extractValue(typeParameters),
                extractValue(parameters),
                returnType.value()
        )));
    }

    @Override
    public Maybe<Parameter, List<SemanticError>> visit(Parameter node) {
        Maybe<TypeSpec, List<SemanticError>> type = visit(node.getType(), this);

        if (type.isFailure()) {
            return Maybe.failure(collectErrors(type));
        }

        return Maybe.success(copyContext(node, new Parameter(
                nodeIdGenerator.next(),
                node.getName(),
                type.value()
        )));
    }

    @Override
    public Maybe<TypeParameter, List<SemanticError>> visit(TypeParameter node) {
        return Maybe.success(node);
    }

    @Override
    public Maybe<StringValue, List<SemanticError>> visit(StringValue node) {
        return Maybe.success(node);
    }

    @Override
    public Maybe<NumberValue, List<SemanticError>> visit(NumberValue node) {
        return Maybe.success(node);
    }

    @Override
    public Maybe<BooleanValue, List<SemanticError>> visit(BooleanValue node) {
        return Maybe.success(node);
    }

    @Override
    public Maybe<NoneValue, List<SemanticError>> visit(NoneValue node) {
        return Maybe.success(node);
    }

    @Override
    public Maybe<IdentifierValue, List<SemanticError>> visit(IdentifierValue node) {
        Maybe<Reference, List<SemanticError>> reference = visit(node.getReference(), this);

        if (reference.isFailure()) {
            return Maybe.failure(collectErrors(reference));
        }

        return Maybe.success(copyContext(node, new IdentifierValue(
                nodeIdGenerator.next(),
                reference.value()
        )));
    }

    @Override
    public Maybe<NewAssignmentValue, List<SemanticError>> visit(NewAssignmentValue node) {
        Maybe<TypeSpec, List<SemanticError>> type = visit(node.getType(), this);
        Maybe<Value, List<SemanticError>> value = visit(node.getValue(), this);

        if (type.isFailure() || value.isFailure()) {
            return Maybe.failure(Lists.merge(
                    collectErrors(type),
                    collectErrors(value)
            ));
        }

        return Maybe.success(copyContext(node, new NewAssignmentValue(
                nodeIdGenerator.next(),
                node.getName(),
                type.value(),
                value.value(),
                node.getMode()
        )));
    }

    @Override
    public Maybe<DirectAssignmentValue, List<SemanticError>> visit(DirectAssignmentValue node) {
        Maybe<Reference, List<SemanticError>> reference = visit(node.getReference(), this);
        Maybe<Value, List<SemanticError>> value = visit(node.getValue(), this);

        if (reference.isFailure() || value.isFailure()) {
            return Maybe.failure(Lists.merge(
                    collectErrors(reference),
                    collectErrors(value)
            ));
        }

        return Maybe.success(copyContext(node, new DirectAssignmentValue(
                nodeIdGenerator.next(),
                reference.value(),
                value.value()
        )));
    }

    @Override
    public Maybe<IndirectAssignmentValue, List<SemanticError>> visit(IndirectAssignmentValue node) {
        Maybe<Reference, List<SemanticError>> reference = visit(node.getReference(), this);
        Maybe<Value, List<SemanticError>> indirectValue = visit(node.getIndirectValue(), this);
        Maybe<Value, List<SemanticError>> value = visit(node.getValue(), this);

        if (reference.isFailure() || indirectValue.isFailure() || value.isFailure()) {
            return Maybe.failure(Lists.merge(
                    collectErrors(reference),
                    collectErrors(indirectValue),
                    collectErrors(value)
            ));
        }

        return Maybe.success(copyContext(node, new IndirectAssignmentValue(
                nodeIdGenerator.next(),
                indirectValue.value(),
                reference.value(),
                value.value()
        )));
    }

    @Override
    public Maybe<MethodCallValue, List<SemanticError>> visit(MethodCallValue node) {
        Maybe<Reference, List<SemanticError>> reference = visit(node.getMethodReference(), this);
        List<Maybe<Value, List<SemanticError>>> methodArguments = visit(node.getMethodArguments(), this);
        List<Maybe<TypeSpec, List<SemanticError>>> typeArguments = visit(node.getTypeArguments(), this);

        if (reference.isFailure() || anyFailure(methodArguments) || anyFailure(typeArguments)) {
            return Maybe.failure(Lists.merge(
                    collectErrors(reference),
                    collectErrors(methodArguments),
                    collectErrors(typeArguments)
            ));
        }

        return Maybe.success(copyContext(node, new MethodCallValue(
                nodeIdGenerator.next(),
                reference.value(),
                extractValue(typeArguments),
                extractValue(methodArguments)
        )));
    }

    @Override
    public Maybe<NestedValue, List<SemanticError>> visit(NestedValue node) {
        Maybe<Value, List<SemanticError>> inner = visit(node.getInner(), this);
        Maybe<Value, List<SemanticError>> outer = visit(node.getOuter(), this);

        if (outer.isFailure() || inner.isFailure()) {
            return Maybe.failure(Lists.merge(
                    collectErrors(inner),
                    collectErrors(outer)
            ));
        }

        return Maybe.success(copyContext(node, new NestedValue(
                nodeIdGenerator.next(),
                outer.value(),
                inner.value()
        )));
    }

    @Override
    public Maybe<FunctionValue, List<SemanticError>> visit(FunctionValue node) {
        List<Maybe<Parameter, List<SemanticError>>> parameters = visit(node.getParameters(), this);
        Maybe<TypeSpec, List<SemanticError>> returnType = visit(node.getReturnType(), this);
        List<Maybe<Statement, List<SemanticError>>> statements = visit(node.getStatements(), this);
        List<Maybe<TypeParameter, List<SemanticError>>> typeParameters = visit(node.getTypeParameters(), this);

        if (returnType.isFailure() || anyFailure(parameters) || anyFailure(statements) || anyFailure(typeParameters)) {
            return Maybe.failure(Lists.merge(
                    collectErrors(parameters),
                    collectErrors(returnType),
                    collectErrors(statements),
                    collectErrors(typeParameters)
            ));
        }

        return Maybe.success(copyContext(node, new FunctionValue(
                nodeIdGenerator.next(),
                extractValue(typeParameters),
                extractValue(parameters),
                returnType.value(),
                extractValue(statements)
        )));
    }

    @Override
    public Maybe<Statement, List<SemanticError>> visit(Statement node) {
        Maybe<Value, List<SemanticError>> value = visit(node.getValue(), this);

        if (value.isFailure()) {
            return Maybe.failure(collectErrors(value));
        }

        return Maybe.success(copyContext(node, new Statement(
                nodeIdGenerator.next(),
                value.value(),
                node.isLast()
        )));
    }

    @Override
    public Maybe<Method, List<SemanticError>> visit(Method node) {
        List<Maybe<Statement, List<SemanticError>>> statements = visit(node.getStatements(), this);
        Maybe<MethodSignature, List<SemanticError>> signature = visit(node.getSignature(), this);

        if (anyFailure(statements) || signature.isFailure()) {
            return Maybe.failure(Lists.merge(
                    collectErrors(signature),
                    collectErrors(statements)
            ));
        }

        return Maybe.success(copyContext(node, new Method(
                nodeIdGenerator.next(),
                signature.value(),
                extractValue(statements)
        )));
    }

    @Override
    public Maybe<Attribute, List<SemanticError>> visit(Attribute node) {
        Maybe<TypeSpec, List<SemanticError>> type = visit(node.getType(), this);
        Maybe<Value, List<SemanticError>> value = visit(node.getValue(), this);

        if (type.isFailure() || value.isFailure()) {
            return Maybe.failure(Lists.merge(
                    collectErrors(type),
                    collectErrors(value)
            ));
        }

        return Maybe.success(copyContext(node, new Attribute(
                nodeIdGenerator.next(),
                node.getName(),
                type.value(),
                value.value(),
                node.getMode()
        )));
    }

    @Override
    public Maybe<Reference, List<SemanticError>> visit(Reference node) {
        return Maybe.success(node);
    }
}
