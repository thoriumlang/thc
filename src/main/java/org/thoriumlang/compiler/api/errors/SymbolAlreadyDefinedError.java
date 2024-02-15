package org.thoriumlang.compiler.api.errors;

import org.thoriumlang.compiler.ast.context.SourcePosition;
import org.thoriumlang.compiler.ast.symbols.NodeRef;

public class SymbolAlreadyDefinedError extends SemanticError {
    public SymbolAlreadyDefinedError(String name, NodeRef sourceNode, NodeRef initialDefiningNode) {
        super(
                String.format("Symbol %s is already defined at %s:%s",
                        name,
                        // TODO source position should be part of the node
                        initialDefiningNode.node().getContext().get(SourcePosition.class).get().getStartLine(),
                        initialDefiningNode.node().getContext().get(SourcePosition.class).get().getStartColumn()
                ),
                sourceNode.node()
        );
    }
}
