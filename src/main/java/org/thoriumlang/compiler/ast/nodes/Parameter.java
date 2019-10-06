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

public class Parameter extends Node {
    private final String name;
    private final TypeSpec type;

    public Parameter(NodeId nodeId, String name, TypeSpec type) {
        super(nodeId);
        if (name == null) {
            throw new NullPointerException("name cannot be null");
        }
        if (type == null) {
            throw new NullPointerException("type cannot be null");
        }
        this.name = name;
        this.type = type;
    }

    @Override
    public <T> T accept(Visitor<? extends T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return name + ": " + type;
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
        return getNodeId().equals(parameter.getNodeId()) &&
                name.equals(parameter.name) &&
                type.equals(parameter.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNodeId(), name, type);
    }
}
