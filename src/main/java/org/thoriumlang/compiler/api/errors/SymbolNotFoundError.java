package org.thoriumlang.compiler.api.errors;

import org.thoriumlang.compiler.ast.nodes.Node;

public class SymbolNotFoundError extends SemanticError {
    public SymbolNotFoundError( Node errorSource, String symbol) {
        super(String.format("symbol not found: %s", symbol), errorSource);
    }
}
