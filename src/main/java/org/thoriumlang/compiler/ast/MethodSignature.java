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

public class MethodSignature implements Visitable {
    private final Visibility visibility;
    private final String name;
    private final List<Parameter> parameters;
    private final TypeSpec returnType;

    public MethodSignature(Visibility visibility, String name, List<Parameter> parameters, TypeSpec returnType) {
        if (visibility == null) {
            throw new NullPointerException("visibility cannot be null");
        }
        if (name == null) {
            throw new NullPointerException("name cannot be null");
        }
        if (parameters == null) {
            throw new NullPointerException("parameters cannot be null");
        }
        if (returnType == null) {
            throw new NullPointerException("returnType cannot be null");
        }
        this.visibility = visibility;
        this.name = name;
        this.parameters = parameters;
        this.returnType = returnType;
    }

    @Override
    public <T> T accept(Visitor<? extends T> visitor) {
        return visitor.visitMethodSignature(
                visibility,
                name,
                parameters,
                returnType
        );
    }

    @Override
    public String toString() {
        return String.format("%s %s ( %s ) : %s",
                visibility,
                name,
                parameters.stream()
                        .map(Parameter::toString)
                        .collect(Collectors.joining(", ")),
                returnType
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
        MethodSignature that = (MethodSignature) o;
        return visibility == that.visibility &&
                name.equals(that.name) &&
                parameters.equals(that.parameters) &&
                returnType.equals(that.returnType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(visibility, name, parameters, returnType);
    }
}
