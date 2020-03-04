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

public class IndirectAssignmentValue extends AssignmentValue {
    private final Value indirectValue;

    public IndirectAssignmentValue(NodeId nodeId, Value indirectValue, Reference reference, Value value) {
        super(nodeId, reference, value);
        if (indirectValue == null) {
            throw new NullPointerException("indirectValue cannot be null");
        }
        this.indirectValue = indirectValue;
    }

    @Override
    public <T> T accept(Visitor<? extends T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format(
                "INDIRECT %s.%s = %s",
                indirectValue.toString(),
                getReference().toString(),
                getValue().toString()
        );
    }

    public Value getIndirectValue() {
        return indirectValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IndirectAssignmentValue that = (IndirectAssignmentValue) o;
        return getNodeId().equals(that.getNodeId()) &&
                indirectValue.equals(that.indirectValue) &&
                getReference().equals(that.getReference()) &&
                getValue().equals(that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNodeId(), getIndirectValue(), getValue(), indirectValue);
    }
}
