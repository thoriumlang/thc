package org.thoriumlang.compiler.api.errors;

import org.thoriumlang.compiler.ast.nodes.Node;

public class TypeNotInferableError extends SemanticError {
    public TypeNotInferableError(Node errorSource) {
        super("Cannot infer type", errorSource);
    }
}
