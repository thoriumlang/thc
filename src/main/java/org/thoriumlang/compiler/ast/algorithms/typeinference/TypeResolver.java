package org.thoriumlang.compiler.ast.algorithms.typeinference;

import org.thoriumlang.compiler.api.errors.SemanticError;
import org.thoriumlang.compiler.ast.algorithms.Algorithm;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.nodes.Root;

import java.util.List;

public class TypeResolver implements Algorithm {
    private final NodeIdGenerator nodeIdGenerator;

    public TypeResolver(NodeIdGenerator nodeIdGenerator) {
        this.nodeIdGenerator = nodeIdGenerator;
    }

    @Override
    public List<SemanticError> walk(Root root) {
        return new TypeResolvingVisitor(nodeIdGenerator).visit(root);
    }
}
