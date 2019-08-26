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

public class IndirectAssignmentValue implements Value {
    private final Value indirectValue;
    private final String identifier;
    private final Value value;

    public IndirectAssignmentValue(Value indirectValue, String identifier, Value value) {
        if (indirectValue == null) {
            throw new NullPointerException("indirectValue cannot be null");
        }
        if (identifier == null) {
            throw new NullPointerException("identifier cannot be null");
        }
        if (value == null) {
            throw new NullPointerException("value cannot be null");
        }
        this.indirectValue = indirectValue;
        this.identifier = identifier;
        this.value = value;
    }

    @Override
    public <T> T accept(Visitor<? extends T> visitor) {
        return visitor.visitIndirectAssignmentValue(indirectValue, identifier, value);
    }

    @Override
    public String toString() {
        return String.format(
                "INDIRECT %s.%s = %s",
                indirectValue.toString(),
                identifier,
                value.toString()
        );
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
        return indirectValue.equals(that.indirectValue) &&
                identifier.equals(that.identifier) &&
                value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(indirectValue, identifier, value);
    }
}