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

import java.util.Objects;

public class Parameter implements Node {
    private final NodeId nodeId;
    private final String name;
    private final TypeSpec type;

    public Parameter(NodeId nodeId, String name, TypeSpec type) {
        if (nodeId == null) {
            throw new NullPointerException("nodeId cannot be null");
        }
        if (name == null) {
            throw new NullPointerException("name cannot be null");
        }
        if (type == null) {
            throw new NullPointerException("type cannot be null");
        }
        this.nodeId = nodeId;
        this.name = name;
        this.type = type;
    }

    @Override
    public <T> T accept(Visitor<? extends T> visitor) {
        return visitor.visitParameter(this);
    }

    @Override
    public String toString() {
        return name + ": " + type;
    }

    public NodeId getNodeId() {
        return nodeId;
    }

    public String getName() {
        return name;
    }

    public TypeSpec getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Parameter parameter = (Parameter) o;
        return nodeId.equals(parameter.nodeId) &&
                name.equals(parameter.name) &&
                type.equals(parameter.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeId, name, type);
    }
}
