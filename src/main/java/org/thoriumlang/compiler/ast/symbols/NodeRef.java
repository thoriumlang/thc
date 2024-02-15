package org.thoriumlang.compiler.ast.symbols;

import org.thoriumlang.compiler.ast.nodes.Node;

public class NodeRef {
    private final Node node;

    public NodeRef(Node node) {
        this.node = node;
    }

    public Node node() {
        return node;
    }
}
