/*
 * Copyright 2019 Christophe Pollet
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
package org.thoriumlang.compiler.ast;

import org.thoriumlang.compiler.ast.visitor.Visitor;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Root implements Node {
    private final NodeId nodeId;
    private final String namespace;
    private final List<Use> uses;
    private final TopLevelNode topLevelNode;

    public Root(NodeId nodeId, String namespace, List<Use> uses, TopLevelNode topLevel) {
        if (nodeId == null) {
            throw new NullPointerException("nodeId cannot be null");
        }
        if (namespace == null) {
            throw new NullPointerException("namespace cannot be null");
        }
        if (uses == null) {
            throw new NullPointerException("uses cannot be null");
        }
        if (topLevel == null) {
            throw new NullPointerException("topLevel cannot be null");
        }
        this.nodeId = nodeId;
        this.namespace = namespace;
        this.uses = uses;
        this.topLevelNode = topLevel;
    }

    @Override
    public <T> T accept(Visitor<? extends T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        String use = uses.stream()
                .map(Use::toString)
                .collect(Collectors.joining("\n"));

        return String.format("NAMESPACE %s%n%s%s",
                namespace,
                use.isEmpty() ? "" : String.format("%s%n", use),
                topLevelNode.toString()
        );
    }

    public NodeId getNodeId() {
        return nodeId;
    }

    public String getNamespace() {
        return namespace;
    }

    public List<Use> getUses() {
        return uses;
    }

    public TopLevelNode getTopLevelNode() {
        return topLevelNode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Root root = (Root) o;
        return nodeId.equals(root.nodeId) &&
                namespace.equals(root.namespace) &&
                uses.equals(root.uses) &&
                topLevelNode.equals(root.topLevelNode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeId, namespace, uses, topLevelNode);
    }
}
