package org.thoriumlang.compiler.ast.algorithms.typeinference;

import org.thoriumlang.compiler.api.errors.SemanticError;
import org.thoriumlang.compiler.ast.context.ReferencedNode;
import org.thoriumlang.compiler.ast.context.Relatives;
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
import org.thoriumlang.compiler.ast.nodes.Type;
import org.thoriumlang.compiler.ast.nodes.TypeParameter;
import org.thoriumlang.compiler.ast.nodes.TypeSpec;
import org.thoriumlang.compiler.ast.nodes.TypeSpecFunction;
import org.thoriumlang.compiler.ast.nodes.TypeSpecInferred;
import org.thoriumlang.compiler.ast.nodes.TypeSpecIntersection;
import org.thoriumlang.compiler.ast.nodes.TypeSpecSimple;
import org.thoriumlang.compiler.ast.nodes.TypeSpecUnion;
import org.thoriumlang.compiler.ast.nodes.Use;
import org.thoriumlang.compiler.ast.visitor.BaseVisitor;
import org.thoriumlang.compiler.ast.visitor.PredicateVisitor;
import org.thoriumlang.compiler.ast.visitor.Visitor;
import org.thoriumlang.compiler.collections.Lists;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class TypeResolvingVisitor implements Visitor<List<SemanticError>> {
    private final NodeIdGenerator nodeIdGenerator;

    public TypeResolvingVisitor(NodeIdGenerator nodeIdGenerator) {
        this.nodeIdGenerator = nodeIdGenerator;
    }

    @Override
    public List<SemanticError> visit(Root node) {
        return node.getTopLevelNode().accept(this);
    }

    @Override
    public List<SemanticError> visit(Use node) {
        return Collections.emptyList();
    }

    @Override
    public List<SemanticError> visit(Type node) {
        return Collections.emptyList();
    }

    private List<SemanticError> visitRecursive(List<? extends Node> nodes) {
        return nodes.stream()
                .map(n -> n.accept(this))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @Override
    public List<SemanticError> visit(Class node) {
        List<SemanticError> superTypeErrors = node.getSuperType().accept(this);

        String namespace = node
                .getContext()
                .require(Relatives.class)
                .parent()
                .map(n -> (Root) n.node())
                .map(Root::getNamespace)
                .orElseThrow(() -> new IllegalStateException("no parent found"));

        node.getContext().put(
                TypeSpec.class,
                new TypeSpecUnion(
                        nodeIdGenerator.next(),
                        Arrays.asList(
                                node.getSuperType(),
                                new TypeSpecSimple(
                                        nodeIdGenerator.next(),
                                        String.format("%s.%s", namespace, node.getName()),
                                        Collections.emptyList()
                                )
                        )
                )
        );

        return Lists.merge(
                superTypeErrors,
                visitRecursive(node.getAttributes()),
                visitRecursive(node.getMethods())
        );
    }

    @Override
    public List<SemanticError> visit(TypeSpecIntersection node) {
        node.getContext().put(TypeSpec.class, node);
        return visitRecursive(node.getTypes());
    }

    @Override
    public List<SemanticError> visit(TypeSpecUnion node) {
        node.getContext().put(TypeSpec.class, node);
        return visitRecursive(node.getTypes());
    }

    @Override
    public List<SemanticError> visit(TypeSpecSimple node) {
        node.getContext().put(TypeSpec.class, node);
        return Collections.emptyList();
    }

    @Override // TODO implement
    public List<SemanticError> visit(TypeSpecFunction node) {
        throw new IllegalStateException("TypeSpecFunction not implemented");
    }

    @Override
    public List<SemanticError> visit(TypeSpecInferred node) {
        node.getContext().put(TypeSpec.class, node);
        return Collections.emptyList();
    }

    @Override
    public List<SemanticError> visit(MethodSignature node) {
        List<SemanticError> errors = node.getReturnType().accept(this);

        node.getContext()
                .require(Relatives.class)
                .parent()
                .orElseThrow(() -> new IllegalStateException("no parent found"))
                .node()
                .accept(new BaseVisitor<Void>() {
                    @Override
                    public Void visit(Type parentNode) {
                        // this is already done in the visit(Method) to infer statements types (and thus return type)
                        visitRecursive(node.getTypeParameters());
                        copyTypeSpec(node, node.getReturnType());
                        return null;
                    }

                    @Override
                    public Void visit(Method parentNode) {
                        copyTypeSpec(node, parentNode);
                        return null;
                    }
                });

        return errors;
    }

    @Override
    public List<SemanticError> visit(Parameter node) {
        List<SemanticError> errors = node.getType().accept(this);

        copyTypeSpec(node, node.getType());

        return errors;
    }

    private void copyTypeSpec(Node to, Node from) {
        if (needsTypeInference(from)) {
            throw new IllegalStateException(String.format(
                    "no inferred type found for [ %s ] on [ %s ]", to, from
            ));
        }
        to.getContext().put(
                TypeSpec.class,
                from.getContext().require(TypeSpec.class)
        );
    }

    private boolean needsTypeInference(Node node) {
        return node.getContext()
                .get(TypeSpec.class)
                .map(t -> t.accept(new PredicateVisitor() {
                    @Override
                    public Boolean visit(TypeSpecInferred node) {
                        return true;
                    }
                }))
                .orElse(true);
    }

    @Override // TODO implement
    public List<SemanticError> visit(TypeParameter node) {
        throw new IllegalStateException("TypeParameter not implemented");
    }

    @Override
    public List<SemanticError> visit(StringValue node) {
        node.getContext().put(
                TypeSpec.class,
                new TypeSpecSimple(nodeIdGenerator.next(), "org.thoriumlang.String", Collections.emptyList())
        );
        return Collections.emptyList();
    }

    @Override
    public List<SemanticError> visit(NumberValue node) {
        node.getContext().put(
                TypeSpec.class,
                new TypeSpecSimple(nodeIdGenerator.next(), "org.thoriumlang.Number", Collections.emptyList())
        );
        return Collections.emptyList();
    }

    @Override
    public List<SemanticError> visit(BooleanValue node) {
        node.getContext().put(
                TypeSpec.class,
                new TypeSpecSimple(nodeIdGenerator.next(), "org.thoriumlang.Boolean", Collections.emptyList())
        );
        return Collections.emptyList();
    }

    @Override
    public List<SemanticError> visit(NoneValue node) {
        node.getContext().put(
                TypeSpec.class,
                new TypeSpecSimple(nodeIdGenerator.next(), "org.thoriumlang.None", Collections.emptyList())
        );
        return Collections.emptyList();
    }

    @Override
    public List<SemanticError> visit(IdentifierValue node) {
        node.getReference().accept(this);

        copyTypeSpec(node, node.getReference());

        return Collections.emptyList();
    }

    @Override
    public List<SemanticError> visit(NewAssignmentValue node) {
        List<SemanticError> errors = Lists.merge(
                node.getValue().accept(this),
                node.getType().accept(this)
        );

        copyTypeSpec(node, node.getValue());

        return errors;
    }

    @Override
    public List<SemanticError> visit(DirectAssignmentValue node) {
        List<SemanticError> errors = Lists.merge(
                node.getValue().accept(this),
                node.getReference().accept(this)
        );

        Optional<Node> target = getTargetNode(node.getReference());

        if (!target.isPresent()) {
            // TODO error handling: shouldn't we put some dummy match-all type as the inferred type here?
            //  see https://github.com/thoriumlang/thc/issues/72
            return Collections.singletonList(new SemanticError("No matching target found", node));
        }

        TypeSpec inferredType = target.get().getContext()
                .get(TypeSpec.class)
                .orElseThrow(() -> new IllegalStateException("no inferred type found"));

        target.get().getContext().put(
                TypeSpec.class,
                new TypeSpecIntersection(
                        nodeIdGenerator.next(),
                        Arrays.asList(inferredType, node.getValue().getContext().require(TypeSpec.class))
                )
        );

        copyTypeSpec(node, target.get());
        return errors;
    }

    @Override // TODO implement
    public List<SemanticError> visit(IndirectAssignmentValue node) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public List<SemanticError> visit(MethodCallValue node) {
        List<SemanticError> errors = Lists.merge(
                visitRecursive(node.getMethodArguments()),
                node.getMethodReference().accept(this)
        );
        copyTypeSpec(node, node.getMethodReference());
        return errors;
    }

    @Override // TODO implement
    public List<SemanticError> visit(NestedValue node) {
        throw new IllegalStateException("NestedValue not implemented");
    }

    @Override
    public List<SemanticError> visit(FunctionValue node) {
        List<SemanticError> errors = Lists.merge(
                visitRecursive(node.getParameters()),
                visitRecursive(node.getStatements()),
                node.getReturnType().accept(this)
        );

        node.getContext().put(
                TypeSpec.class,
                new TypeSpecSimple(nodeIdGenerator.next(), "org.thoriumlang.Function", Collections.emptyList())
        );

        return errors;
    }

    @Override
    public List<SemanticError> visit(Statement node) {
        List<SemanticError> errors = node.getValue().accept(this);

        copyTypeSpec(node, node.getValue());

        return errors;
    }

    @Override
    public List<SemanticError> visit(Method node) {
        List<SemanticError> errors = Lists.merge(
                // We need the inferred statement types to infer the method return type (here, Method type)
                // We need the inferred parameters types to infer the statements types
                // So, we cannot infer the signature at once (return type AND parameters are in the signature)...
                visitRecursive(node.getSignature().getTypeParameters()),
                visitRecursive(node.getStatements())
        );

        node.getContext().put(
                TypeSpec.class,
                new TypeSpecIntersection(
                        nodeIdGenerator.next(),
                        node.getStatements().stream()
                                .filter(Statement::isLast)
                                .map(s -> s.getContext().require(TypeSpec.class))
                                .collect(Collectors.toList())
                )
        );

        node.getSignature().accept(this);

        return errors;
    }

    @Override
    public List<SemanticError> visit(Attribute node) {
        List<SemanticError> errors = Lists.merge(
                node.getValue().accept(this),
                node.getType().accept(this)
        );

        copyTypeSpec(node, node.getValue());

        return errors;
    }

    @Override
    public List<SemanticError> visit(Reference node) {
        List<SemanticError> errors = Collections.emptyList();

        Optional<Node> target = getTargetNode(node);

        if (!target.isPresent()) {
            // TODO error handling: shouldn't we put some dummy match-all type as the inferred type here?
            //  see https://github.com/thoriumlang/thc/issues/72
            return Collections.singletonList(new SemanticError("No matching target found", node));
        }

        if (needsTypeInference(target.get())) {
            errors = target.get().accept(this);
        }

        copyTypeSpec(node, target.get());

        return errors;
    }

    private Optional<Node> getTargetNode(Reference node) {
        List<Node> targetNodes = getTargetNodes(node);
        return Optional.ofNullable(node
                .getContext()
                .require(Relatives.class)
                .parent()
                .orElseThrow(() -> new IllegalStateException("no parent found"))
                .node()
                .accept(new BaseVisitor<Optional<Node>>() {
                    @Override
                    public Optional<Node> visit(MethodCallValue node) {
                        return new MethodParameterTypes(
                                node.getMethodArguments().stream()
                                        .map(p -> p.getContext().require(TypeSpec.class))
                                        .collect(Collectors.toList())
                        ).findBestMatch(toTypeMatcherMap());
                    }

                    private Map<Node, List<TypeSpec>> toTypeMatcherMap() {
                        return targetNodes.stream()
                                .collect(Collectors.toMap(
                                        n -> n,
                                        n -> n.accept(new BaseVisitor<List<TypeSpec>>() {
                                            @Override
                                            public List<TypeSpec> visit(MethodSignature node) {
                                                return node.getParameters().stream()
                                                        .map(Parameter::getType)
                                                        .collect(Collectors.toList());
                                            }

                                            @Override
                                            public List<TypeSpec> visit(Method node) {
                                                return node.getSignature().accept(this);
                                            }
                                        })
                                ));
                    }
                })
        ).orElseGet(() -> {
            if (targetNodes.size() != 1) {
                throw new IllegalStateException(String.format("found %d targetNodes, expected 1", targetNodes.size()));
            }
            return Optional.of(targetNodes.get(0));
        });
    }

    private List<Node> getTargetNodes(Reference node) {
        return node.getContext().require(ReferencedNode.class).nodes();
    }
}
