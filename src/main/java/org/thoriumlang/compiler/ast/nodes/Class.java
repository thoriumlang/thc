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

public class Class extends TopLevelNode {
    private final Visibility visibility;
    private final String name;
    private final List<TypeParameter> typeParameters;
    private final TypeSpec superType;
    private final List<Method> methods;
    private final List<Attribute> attributes;

    public Class(NodeId nodeId, Visibility visibility, String name, List<TypeParameter> typeParameters,
            TypeSpec superType, List<Method> methods, List<Attribute> attributes) {
        super(nodeId);
        this.visibility = Objects.requireNonNull(visibility, "visibility cannot be null");
        this.name = Objects.requireNonNull(name, "name cannot be null");
        this.typeParameters = Objects.requireNonNull(typeParameters, "typeParameters cannot be null");
        this.superType = Objects.requireNonNull(superType, "superType cannot be null");
        this.methods = Objects.requireNonNull(methods, "methods cannot be null");
        this.attributes = Objects.requireNonNull(attributes, "attributes cannot be null");
    }

    @Override
    public <T> T accept(Visitor<? extends T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        String serializedMethods = methods.stream()
                .map(Method::toString)
                .collect(Collectors.joining(String.format("%n")));
        String serializedAttributes = attributes.stream()
                .map(Attribute::toString)
                .collect(Collectors.joining(String.format("%n")));

        return String.format("%s CLASS %s[%s] : %s:%s%s",
                visibility,
                name,
                typeParameters.stream()
                        .map(TypeParameter::toString)
                        .collect(Collectors.joining(", ")),
                superType.toString(),
                serializedAttributes.isEmpty() ? "" : String.format("%n%s", serializedAttributes),
                serializedMethods.isEmpty() ? "" : String.format("%n%s", serializedMethods)
        );
    }

    @Override
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

    public List<Method> getMethods() {
        return methods;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Class aClass = (Class) o;
        return getNodeId().equals(aClass.getNodeId()) &&
                visibility == aClass.visibility &&
                name.equals(aClass.name) &&
                typeParameters.equals(aClass.typeParameters) &&
                superType.equals(aClass.superType) &&
                methods.equals(aClass.methods) &&
                attributes.equals(aClass.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNodeId(), visibility, name, typeParameters, superType, methods, attributes);
    }
}
