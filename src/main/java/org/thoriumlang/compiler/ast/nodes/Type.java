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

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Type extends TopLevelNode {
    private final Visibility visibility;
    private final String name;
    private final List<TypeParameter> typeParameters;
    private final TypeSpec superType;
    private final List<MethodSignature> methods;

    public Type(NodeId nodeId, Visibility visibility, String name, List<TypeParameter> typeParameters,
            TypeSpec superType, List<MethodSignature> methods) {
        super(nodeId);
        this.visibility = Objects.requireNonNull(visibility, "visibility cannot be null");
        this.name = Objects.requireNonNull(name, "name cannot be null");
        this.typeParameters = Objects.requireNonNull(typeParameters, "typeParameters cannot be null");
        this.superType = Objects.requireNonNull(superType, "superType cannot be null");
        this.methods = Objects.requireNonNull(methods, "methods cannot be null");
    }

    @Override
    public <T> T accept(Visitor<? extends T> visitor) {
        return visitor.visit(this);
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

    public Visibility getVisibility() {
        return visibility;
    }

    @Override
    public String getName() {
        return name;
    }

    public List<TypeParameter> getTypeParameters() {
        return typeParameters;
    }

    public TypeSpec getSuperType() {
        return superType;
    }

    public List<MethodSignature> getMethods() {
        return methods;
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
        return getNodeId().equals(type.getNodeId()) &&
                visibility == type.visibility &&
                name.equals(type.name) &&
                typeParameters.equals(type.typeParameters) &&
                superType.equals(type.superType) &&
                methods.equals(type.methods);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNodeId(), visibility, name, typeParameters, superType, methods);
    }
}
