/*
 * Copyright 2020 Christophe Pollet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thoriumlang.compiler.ast.context;

import org.thoriumlang.compiler.ast.nodes.Node;
import org.thoriumlang.compiler.ast.nodes.NodeId;
import org.thoriumlang.compiler.collections.Lists;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ReferencedNode {
    private final List<Node> nodes;

    public ReferencedNode(List<Node> referencedNodes) {
        this.nodes = Lists.requireNonEmpty(
                Objects.requireNonNull(referencedNodes, "nodes cannot be null"),
                "nodes cannot be empty"
        );
    }

    /**
     * The list of target node is never null nor empty.
     * @return the list of potential target nodes.
     */
    public List<Node> nodes() {
        return nodes;
    }

    @Override
    public String toString() {
        return String.format("nodeRef %s",
                nodes.stream()
                        .map(Node::getNodeId)
                        .map(NodeId::toString)
                        .collect(Collectors.joining(", "))
        );
    }
}
