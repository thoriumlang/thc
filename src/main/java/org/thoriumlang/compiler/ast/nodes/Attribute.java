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

public class Attribute extends Node {
    private final String name;
    private final TypeSpec type;
    private final Value value;
    private final Mode mode;

    public Attribute(NodeId nodeId, String name, TypeSpec type, Value value, Mode mode) {
        super(nodeId);
        this.name = Objects.requireNonNull(name, "name cannot be null");
        this.type = Objects.requireNonNull(type, "type cannot be null");
        this.value = Objects.requireNonNull(value, "value cannot be null");
        this.mode = Objects.requireNonNull(mode, "mode cannot be null");
    }

    @Override
    public <T> T accept(Visitor<? extends T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("%s %s: %s = %s",
                mode.toString(),
                name,
                type,
                value
        );
    }

    public String getName() {
        return name;
    }

    public TypeSpec getType() {
        return type;
    }

    public Value getValue() {
        return value;
    }

    public Mode getMode() {
        return mode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Attribute attribute = (Attribute) o;
        return getNodeId().equals(attribute.getNodeId()) &&
                name.equals(attribute.name) &&
                type.equals(attribute.type) &&
                value.equals(attribute.value) &&
                mode == attribute.mode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNodeId(), name, type, value, mode);
    }
}
