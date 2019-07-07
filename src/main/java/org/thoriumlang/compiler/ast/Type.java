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
import java.util.stream.Collectors;

public class Type implements Visitable {
    private final String name;
    private final List<MethodSignature> methods;

    public Type(String name, List<MethodSignature> methods) {
        if (name == null) {
            throw new NullPointerException("name cannot be null");
        }
        if (methods == null) {
            throw new NullPointerException("methods cannot be null");
        }
        this.name = name;
        this.methods = methods;
    }

    @Override
    public <T> T accept(Visitor<? extends T> visitor) {
        return visitor.visitType(name, methods);
    }

    @Override
    public String toString() {
        return String.format("type %s:%n%s",
                name,
                methods.stream()
                        .map(MethodSignature::toString)
                        .collect(Collectors.joining(String.format("%n")))
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
        Type type = (Type) o;
        return name.equals(type.name) &&
                methods.equals(type.methods);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, methods);
    }
}
