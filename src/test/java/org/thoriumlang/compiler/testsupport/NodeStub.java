package org.thoriumlang.compiler.testsupport;

import org.thoriumlang.compiler.ast.nodes.Node;
import org.thoriumlang.compiler.ast.nodes.NodeId;
import org.thoriumlang.compiler.ast.visitor.Visitor;

public class NodeStub extends Node {
    public NodeStub() {
        super(new NodeId(1L));
    }

    @Override
    public <T> T accept(Visitor<? extends T> visitor) {
        return null;
    }
}
