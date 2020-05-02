package org.thoriumlang.compiler.api.errors;

import org.thoriumlang.compiler.ast.nodes.Node;

import java.util.List;

public class TooManyAlternativesError extends SemanticError {
    private final List<Node> targetNodes;

    public TooManyAlternativesError(Node errorSource, List<Node> targetNodes) {
        super("Too many alternatives found", errorSource);
        this.targetNodes = targetNodes;
    }

    @Override
    public String toString() {
        return String.format("%s%nFound %d alternatives", super.toString(), targetNodes.size());
    }
}
