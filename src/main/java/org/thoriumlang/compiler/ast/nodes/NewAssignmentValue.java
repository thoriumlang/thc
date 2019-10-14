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

public class NewAssignmentValue extends AssignmentValue {
    private final TypeSpec type;
    private final Mode mode;

    public NewAssignmentValue(NodeId nodeId, String identifier, TypeSpec type, Value value, Mode mode) {
        super(nodeId, identifier, value);
        if (type == null) {
            throw new NullPointerException("type cannot be null");
        }
        if (mode == null) {
            throw new NullPointerException("mode cannot be null");
        }
        this.type = type;
        this.mode = mode;
    }

    @Override
    public <T> T accept(Visitor<? extends T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format(
                "%s %s:%s = %s",
                mode.toString(),
                type.toString(),
                getIdentifier(),
                getValue().toString()
        );
    }

    public TypeSpec getType() {
        return type;
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
        NewAssignmentValue that = (NewAssignmentValue) o;
        return getNodeId().equals(that.getNodeId()) &&
                getIdentifier().equals(that.getIdentifier()) &&
                type.equals(that.type) &&
                getValue().equals(that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNodeId(), getIdentifier(), getValue(), type, mode);
    }
}