package org.thoriumlang.compiler.api.errors;

import org.thoriumlang.compiler.ast.nodes.Node;

public class TargetNotFoundError extends SemanticError {
    public TargetNotFoundError(Node errorSource) {
        super("No alternatives found", errorSource);
    }
}
