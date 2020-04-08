package org.thoriumlang.compiler.ast.api;

import org.thoriumlang.compiler.ast.api.unsafe.TypeSpec;

import java.util.Objects;

public class Parameter {
    private final org.thoriumlang.compiler.ast.nodes.Parameter node;

    public Parameter(org.thoriumlang.compiler.ast.nodes.Parameter node) {
        this.node = Objects.requireNonNull(node, "node cannot be null");
    }

    public String getName() {
        return node.getName();
    }

    public Type getType() {
        return new TypeSpec(node.getType()).getType();
    }

    @Override
    public String toString() {
        return String.format("%s: %s", getName(), getType());
    }
}
