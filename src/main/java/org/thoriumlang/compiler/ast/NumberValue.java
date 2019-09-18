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

import java.util.Objects;

public class NumberValue implements Value {
    private final NodeId nodeId;
    private final String value;

    public NumberValue(NodeId nodeId, String value) {
        if (nodeId == null) {
            throw new NullPointerException("nodeId cannot be null");
        }
        if (value == null) {
            throw new NullPointerException("value cannot be null");
        }
        this.nodeId = nodeId;
        this.value = value;
    }

    @Override
    public <T> T accept(Visitor<? extends T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return value;
    }

    public NodeId getNodeId() {
        return nodeId;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NumberValue that = (NumberValue) o;
        return nodeId.equals(that.nodeId) &&
                value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeId, value);
    }
}
