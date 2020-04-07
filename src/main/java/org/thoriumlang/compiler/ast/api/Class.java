package org.thoriumlang.compiler.ast.api;

import java.util.Objects;

public class Class {
    private final org.thoriumlang.compiler.ast.nodes.Class node;

    public Class(org.thoriumlang.compiler.ast.nodes.Class node) {
        this.node = Objects.requireNonNull(node, "node cannot be null");
    }

    public Type getType() {
        return new ClassType(node);
    }
}
