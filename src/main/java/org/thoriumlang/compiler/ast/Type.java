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
    private final Visibility visibility;
    private final String name;
    private final List<TypeParameter> typeParameters;
    private final TypeSpec superType;
    private final List<MethodSignature> methods;

    public Type(Visibility visibility, String name, List<TypeParameter> typeParameters, TypeSpec superType,
            List<MethodSignature> methods) {
        if (visibility == null) {
            throw new NullPointerException("visibility cannot be null");
        }
        if (name == null) {
            throw new NullPointerException("name cannot be null");
        }
        if (typeParameters == null) {
            throw new NullPointerException("typeParameters cannot be null");
        }
        if (superType == null) {
            throw new NullPointerException("superType cannot be null");
        }
        if (methods == null) {
            throw new NullPointerException("methods cannot be null");
        }
        this.visibility = visibility;
        this.name = name;
        this.typeParameters = typeParameters;
        this.superType = superType;
        this.methods = methods;
    }

    @Override
    public <T> T accept(Visitor<? extends T> visitor) {
        return visitor.visitType(visibility, name, typeParameters, superType, methods);
    }

    @Override
    public String toString() {
        String method = methods.stream()
                .map(MethodSignature::toString)
                .collect(Collectors.joining(String.format("%n")));

        return String.format("%s TYPE %s[%s] : %s:%s",
                visibility,
                name,
                typeParameters.stream()
                        .map(TypeParameter::toString)
                        .collect(Collectors.joining(", ")),
                superType.toString(),
                method.isEmpty() ? "" : String.format("%n%s", method)
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
        return visibility == type.visibility &&
                name.equals(type.name) &&
                typeParameters.equals(type.typeParameters) &&
                superType.equals(type.superType) &&
                methods.equals(type.methods);
    }

    @Override
    public int hashCode() {
        return Objects.hash(visibility, name, typeParameters, superType, methods);
    }
}
