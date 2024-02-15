package org.thoriumlang.compiler.api.errors;

import org.thoriumlang.compiler.ast.symbols.NodeRef;

public class TypeNotDefinedError extends SemanticError {
    public TypeNotDefinedError(String name, NodeRef sourceNode) {
        super(
                String.format("Type %s is not defined", name),
                sourceNode.node()
        );
    }
}
