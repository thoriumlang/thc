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
package org.thoriumlang.compiler.ast.nodes;

import org.thoriumlang.compiler.ast.visitor.Visitor;

import java.util.Objects;

public class Statement implements Node {
    private final NodeId nodeId;
    private final Value value;
    private final boolean last;
    private final Context context;

    public Statement(NodeId nodeId, Value value, boolean last) {
        if (nodeId == null) {
            throw new NullPointerException("nodeId cannot be null");
        }
        if (value == null) {
            throw new NullPointerException("value cannot be null");
        }
        this.nodeId = nodeId;
        this.value = value;
        this.last = last;
        this.context = new Context(this);
    }

    @Override
    public <T> T accept(Visitor<? extends T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public NodeId getNodeId() {
        return nodeId;
    }

    @Override
    public String toString() {
        return String.format("%s:%s", value, last);
    }

    public Value getValue() {
        return value;
    }

    public boolean isLast() {
        return last;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Statement statement = (Statement) o;
        return last == statement.last &&
                nodeId.equals(statement.nodeId) &&
                value.equals(statement.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeId, value, last);
    }
}
