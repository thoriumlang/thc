package org.thoriumlang.compiler.symbols;

import org.thoriumlang.compiler.ast.nodes.Node;

public class ThoriumLibType implements Symbol {
    private final Node node;
    private final String name;

    public ThoriumLibType(Node node, String name) {
        this.node = node;
        this.name = name;
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public String toString() {
        return String.format("(th-rt: %s)", name);
    }
}
