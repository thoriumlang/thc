package org.thoriumlang.compiler.ast.algorithms.typeinference;

import org.thoriumlang.compiler.api.errors.SemanticError;
import org.thoriumlang.compiler.ast.algorithms.Algorithm;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.ast.nodes.TypeSpec;
import org.thoriumlang.compiler.ast.visitor.NodesMatchingVisitor;
import org.thoriumlang.compiler.ast.visitor.TypeFlatteningVisitor;

import java.util.Collections;
import java.util.List;

public class TypeResolver implements Algorithm {
    private final NodeIdGenerator nodeIdGenerator;
    private final TypeFlatteningVisitor typeFlatteningVisitor;

    public TypeResolver(NodeIdGenerator nodeIdGenerator) {
        this.nodeIdGenerator = nodeIdGenerator;
        typeFlatteningVisitor = new TypeFlatteningVisitor(nodeIdGenerator);
    }

    @Override
    public List<SemanticError> walk(Root root) {
        List<SemanticError> errors = new TypeResolvingVisitor(nodeIdGenerator).visit(root);

        if (!errors.isEmpty()) {
            return errors;
        }

        new NodesMatchingVisitor(n -> true).visit(root)
                .forEach(n -> n.getContext()
                        .get(TypeSpec.class)
                        .map(t -> (TypeSpec) t.accept(typeFlatteningVisitor))
                        .ifPresent(t -> n.getContext().put(TypeSpec.class, t))
                );

        return Collections.emptyList();
    }
}
