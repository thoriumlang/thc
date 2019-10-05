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

import java.util.Objects;

public abstract class Attribute extends Node {
    private final String identifier;
    private final TypeSpec type;
    private final Value value;

    Attribute(NodeId nodeId, String identifier, TypeSpec type, Value value) {
        super(nodeId);
        if (identifier == null) {
            throw new NullPointerException("identifier cannot be null");
        }
        if (type == null) {
            throw new NullPointerException("type cannot be null");
        }
        if (value == null) {
            throw new NullPointerException("value cannot be null");
        }
        this.identifier = identifier;
        this.type = type;
        this.value = value;
    }

    public String getIdentifier() {
        return identifier;
    }

    public TypeSpec getType() {
        return type;
    }

    public Value getValue() {
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
        Attribute attribute = (Attribute) o;
        return getNodeId().equals(attribute.getNodeId()) &&
                identifier.equals(attribute.identifier) &&
                type.equals(attribute.type) &&
                value.equals(attribute.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNodeId(), identifier, type, value);
    }
}
