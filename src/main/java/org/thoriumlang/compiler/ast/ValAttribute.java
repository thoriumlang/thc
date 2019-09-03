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

public class ValAttribute implements Visitable{
    private final String identifier;
    private final TypeSpec type;
    private final Value value;

    public ValAttribute(String identifier, TypeSpec type, Value value) {
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


    @Override
    public <T> T accept(Visitor<? extends T> visitor) {
        return visitor.visitValAttribute(identifier, type, value);
    }

    @Override
    public String toString() {
        return String.format("VAL %s: %s = %s", identifier, type, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ValAttribute that = (ValAttribute) o;
        return identifier.equals(that.identifier) &&
                type.equals(that.type) &&
                value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, type, value);
    }
}
