package org.thoriumlang.compiler.api.errors;

import org.thoriumlang.compiler.ast.symbols.NodeRef;

public class SymbolNotDefinedError extends SemanticError {
    public SymbolNotDefinedError(String name, NodeRef sourceNode) {
        super(
                String.format("Symbol %s is not defined", name),
                sourceNode.node()
        );
    }
}
