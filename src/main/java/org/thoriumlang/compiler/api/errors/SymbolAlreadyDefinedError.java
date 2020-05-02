package org.thoriumlang.compiler.api.errors;

import org.thoriumlang.compiler.ast.nodes.Node;

public class SymbolAlreadyDefinedError extends SemanticError {
    public SymbolAlreadyDefinedError(Node errorSource, String symbol) {
        super(String.format("symbol already defined: %s", symbol), errorSource);
    }
}
