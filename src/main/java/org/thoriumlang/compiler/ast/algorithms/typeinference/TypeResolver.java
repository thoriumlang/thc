package org.thoriumlang.compiler.ast.algorithms.typeinference;

import org.thoriumlang.compiler.api.errors.SemanticError;
import org.thoriumlang.compiler.ast.algorithms.Algorithm;
import org.thoriumlang.compiler.ast.context.ReferencedNode;
import org.thoriumlang.compiler.ast.nodes.Attribute;
import org.thoriumlang.compiler.ast.nodes.DirectAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.Method;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.ast.nodes.Statement;
import org.thoriumlang.compiler.ast.nodes.TypeSpec;
import org.thoriumlang.compiler.ast.nodes.TypeSpecIntersection;
import org.thoriumlang.compiler.ast.predicates.NodePredicates;
import org.thoriumlang.compiler.ast.visitor.BaseVisitor;
import org.thoriumlang.compiler.ast.visitor.NodesMatchingVisitor;
import org.thoriumlang.compiler.ast.visitor.PredicateVisitor;
import org.thoriumlang.compiler.ast.visitor.TypeFlatteningVisitor;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TypeResolver implements Algorithm {
    private final NodeIdGenerator nodeIdGenerator;
    private final TypeFlatteningVisitor typeFlatteningVisitor;

    public TypeResolver(NodeIdGenerator nodeIdGenerator) {
        this.nodeIdGenerator = nodeIdGenerator;
        this.typeFlatteningVisitor = new TypeFlatteningVisitor(nodeIdGenerator);
    }

    public static Predicate<SemanticError> distinctByKey(Function<? super SemanticError, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    @Override
    public List<SemanticError> walk(Root root) {
        List<SemanticError> errors = new TypeResolvingVisitor(nodeIdGenerator).visit(root);

        if (!errors.isEmpty()) {
            // TODO better error handing, should we try to flatten before?
            //  see https://github.com/thoriumlang/thc/issues/72
            return removeDuplicateErrors(errors);
        }

        flattenInferredTypes(root);
        removeNoneFomInferredTypeOfAttributesAssignedInAllConstructors(root);

        return Collections.emptyList();
    }

    private List<SemanticError> removeDuplicateErrors(List<SemanticError> errors) {
        return errors.stream()
                .filter(distinctByKey(SemanticError::getNode))
                .collect(Collectors.toList());
    }

    private void flattenInferredTypes(Root root) {
        new NodesMatchingVisitor(n -> true).visit(root)
                .forEach(n -> n.getContext()
                        .get(TypeSpec.class)
                        .map(t -> (TypeSpec) t.accept(typeFlatteningVisitor))
                        .ifPresent(t -> n.getContext().put(TypeSpec.class, t))
                );
    }

    private void removeNoneFomInferredTypeOfAttributesAssignedInAllConstructors(Root root) {
        if (NodePredicates.isClass(root.getTopLevelNode())) {
            List<List<Attribute>> attributesSetInConstructors = constructors(root).stream()
                    .map(this::attributesAssignedInMethod)
                    .collect(Collectors.toList());

            attributes(root).stream()
                    .filter(a -> isInAllLists(a, attributesSetInConstructors))
                    .forEach(a ->
                            removeNone(a.getContext().require(TypeSpec.class))
                                    .ifPresent(t -> a.getContext().put(TypeSpec.class, t))
                    );
        }
    }


    @SuppressWarnings("unchecked") // the nodes that are returned by the NodeMatchingVisitor are all instances of Method
    private List<Method> constructors(Root root) {
        String toplevelName = root.getTopLevelNode().getName();

        return (List<Method>) (List<?>) new NodesMatchingVisitor(n -> n.accept(new PredicateVisitor() {
            @Override
            public Boolean visit(Method node) {
                return node.getSignature().getName().equals(toplevelName);
            }
        })).visit(root);
    }

    /**
     * Return the list of {@link Attribute}s nodes that are assigned in the {@link Method}.
     *
     * @param method The {@link Method} to inspect.
     * @return the list of {@link Attribute}s.
     */
    private List<Attribute> attributesAssignedInMethod(Method method) {
        return method.getStatements().stream()
                .map(Statement::getValue)
                .filter(NodePredicates::isDirectAssignmentValue)
                .map(n -> (DirectAssignmentValue) n)
                .map(a -> a.getReference().getContext().require(ReferencedNode.class).nodes().get(0))
                .filter(NodePredicates::isAttribute)
                .map(n -> (Attribute) n)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked") // the nodes that are returned by the NodeMatchingVisitor are all instances of Method
    private List<Attribute> attributes(Root root) {
        return (List<Attribute>) (List<?>) new NodesMatchingVisitor(NodePredicates::isAttribute).visit(root);
    }

    /**
     * Tells whether an {@link Attribute} is present in all {@link Attribute}s list.
     *
     * @param attribute      the {@link Attribute} instance to check.
     * @param attributesList the list of list of {@link Attribute}s to perform the check on.
     * @return <code>true</code> if <code>attribute</code> is present in all lists of <code>attributesList</code>;
     * <code>false</code> otherwise.
     */
    private boolean isInAllLists(Attribute attribute, List<List<Attribute>> attributesList) {
        return !attributesList.isEmpty() && attributesList.stream()
                .map(l -> l.contains(attribute))
                .reduce(true, (a, b) -> a && b);
    }

    /**
     * Returns a new instance of {@link TypeSpecIntersection} based on <code>typeSpec</code>, but without any none type.
     * If <code>typeSpec</code> it not of type {@link TypeSpecIntersection}, returns an empty {@link Optional}.
     *
     * @param typeSpec the {@link TypeSpec} from which to remove the none type.
     * @return An {@link Optional} containing a {@link TypeSpecIntersection} will all types from the original
     * <code>typeSpec</code>, but without none; or an ampty {@link Optional} if <code>typeSpec</code> was
     * not of type {@link TypeSpecIntersection}.
     */
    private Optional<TypeSpecIntersection> removeNone(TypeSpec typeSpec) {
        return Optional.ofNullable(
                typeSpec.accept(new BaseVisitor<TypeSpecIntersection>() {
                    @Override
                    public TypeSpecIntersection visit(TypeSpecIntersection node) {
                        return new TypeSpecIntersection(
                                nodeIdGenerator.next(),
                                node.getTypes().stream()
                                        .filter(t -> !t.toString().equals("org.thoriumlang.None[]"))
                                        .collect(Collectors.toList())
                        );
                    }
                })
        );
    }
}
