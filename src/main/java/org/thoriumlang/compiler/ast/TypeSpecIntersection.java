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

import java.util.List;
import java.util.Objects;

public class TypeSpecIntersection implements TypeSpec {
    private final List<TypeSpec> types;

    public TypeSpecIntersection(List<TypeSpec> types) {
        if (types==null){throw new NullPointerException("types cannot be null");}
        this.types = types;
    }

    public List<TypeSpec> types() {
        return types;
    }

    @Override
    public <T> T accept(Visitor<? extends T> visitor) {
        return visitor.visitTypeIntersection(types);
    }

    @Override
    public String toString() {
        return "i:" + types.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TypeSpecIntersection that = (TypeSpecIntersection) o;
        return types.equals(that.types);
    }

    @Override
    public int hashCode() {
        return Objects.hash(types);
    }
}
