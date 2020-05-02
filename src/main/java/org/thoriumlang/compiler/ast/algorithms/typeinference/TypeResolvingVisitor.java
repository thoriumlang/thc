package org.thoriumlang.compiler.ast.algorithms.typeinference;

import org.thoriumlang.compiler.api.errors.SemanticError;
import org.thoriumlang.compiler.api.errors.TargetNotFoundError;
import org.thoriumlang.compiler.api.errors.TooManyAlternativesError;
import org.thoriumlang.compiler.api.errors.TypeNotInferableError;
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
import org.thoriumlang.compiler.data.Maybe;

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
        return Lists.merge(
                node.getReturnType().accept(this),
                getParentNode(node)
                        .accept(new BaseVisitor<List<SemanticError>>() {
                            @Override
                            public List<SemanticError> visit(Type parentNode) {
                                // this is already done in the visit(Method) to infer statements types (and thus return type)
                                return Lists.merge(
                                        visitRecursive(node.getTypeParameters()),
                                        copyTypeSpec(node, node.getReturnType())
                                );
                            }

                            @Override
                            public List<SemanticError> visit(Method parentNode) {
                                return copyTypeSpec(node, parentNode);
                            }
                        })
        );
    }

    private Node getParentNode(Node node) {
        return node
                .getContext()
                .require(Relatives.class)
                .parent()
                .orElseThrow(() -> new IllegalStateException("no parent found"))
                .node();
    }

    @Override
    public List<SemanticError> visit(Parameter node) {
        return Lists.merge(
                node.getType().accept(this),
                copyTypeSpec(node, node.getType())
        );
    }

    private List<SemanticError> copyTypeSpec(Node to, Node from) {
        if (needsTypeInference(from)) {
            // TODO error handling: shouldn't we put some dummy match-all type as the inferred type here?
            //  see https://github.com/thoriumlang/thc/issues/72
            return Collections.singletonList(new TypeNotInferableError(to));
        }
        to.getContext().put(
                TypeSpec.class,
                from.getContext().require(TypeSpec.class)
        );
        return Collections.emptyList();
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
        return Lists.merge(
                node.getReference().accept(this),
                copyTypeSpec(node, node.getReference())
        );
    }

    @Override
    public List<SemanticError> visit(NewAssignmentValue node) {
        return Lists.merge(
                node.getValue().accept(this),
                node.getType().accept(this),
                copyTypeSpec(node, node.getValue())
        );
    }

    @Override
    public List<SemanticError> visit(DirectAssignmentValue node) {
        List<SemanticError> errors = Lists.merge(
                node.getValue().accept(this),
                node.getReference().accept(this)
        );

        Maybe<Node, SemanticError> target = getTargetNode(node.getReference());

        if (!target.success()) {
            // TODO error handling: shouldn't we put some dummy match-all type as the inferred type here?
            //  see https://github.com/thoriumlang/thc/issues/72
            return Lists.merge(
                    errors,
                    Collections.singletonList(target.error())
            );
        }

        // TODO this is probably wrong...
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

        return Lists.merge(
                errors,
                copyTypeSpec(node, node.getValue())
        );
    }

    @Override // TODO implement
    public List<SemanticError> visit(IndirectAssignmentValue node) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public List<SemanticError> visit(MethodCallValue node) {
        return Lists.merge(
                visitRecursive(node.getMethodArguments()),
                node.getMethodReference().accept(this),
                copyTypeSpec(node, node.getMethodReference())
        );
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
        return Lists.merge(
                node.getValue().accept(this),
                copyTypeSpec(node, node.getValue())
        );
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

        if (errors.isEmpty()) {
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
        }

        return Lists.merge(
                errors,
                node.getSignature().accept(this)
        );
    }

    @Override
    public List<SemanticError> visit(Attribute node) {
        return Lists.merge(
                node.getValue().accept(this),
                node.getType().accept(this),
                copyTypeSpec(node, node.getValue())
        );
    }

    @Override
    public List<SemanticError> visit(Reference node) {
        Maybe<Node, SemanticError> target = getTargetNode(node);

        if (!target.success()) {
            // TODO error handling: shouldn't we put some dummy match-all type as the inferred type here?
            //  see https://github.com/thoriumlang/thc/issues/72
            return Collections.singletonList(target.error());
        }

        return Lists.merge(
                needsTypeInference(target.get()) ? target.get().accept(this) : Collections.emptyList(),
                copyTypeSpec(node, target.get())
        );
    }

    private Maybe<Node, SemanticError> getTargetNode(Reference node) {
        List<Node> targetNodes = getTargetNodes(node);

        Maybe<Node, SemanticError> targetNode = Optional.ofNullable(
                getParentNode(node)
                        .accept(new BaseVisitor<Maybe<Node, SemanticError>>() {
                            @Override
                            public Maybe<Node, SemanticError> visit(MethodCallValue node) {
                                return new MethodParameterTypes(
                                        node.getMethodArguments().stream()
                                                .map(p -> p.getContext().require(TypeSpec.class))
                                                .collect(Collectors.toList())
                                ).findBestMatch(node, toTypeMatcherMap());
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
            if (targetNodes.size() > 1) {
                return Maybe.failure(new TooManyAlternativesError(node, targetNodes));
            }

            if (targetNodes.isEmpty()) {
                return Maybe.failure(new TargetNotFoundError(node));
            }

            return Maybe.success(targetNodes.get(0));
        });

        if (targetNode.success() && targetNodes.size() > 1) {
            node.getContext().put(
                    ReferencedNode.class,
                    new ReferencedNode(Collections.singletonList(targetNode.get()))
            );
        }

        return targetNode;
    }

    private List<Node> getTargetNodes(Reference node) {
        return node.getContext().require(ReferencedNode.class).nodes();
    }
}
