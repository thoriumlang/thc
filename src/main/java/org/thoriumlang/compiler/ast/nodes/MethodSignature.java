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

public class MethodSignature extends Node {
    private final Visibility visibility;
    private final String name;
    private final List<TypeParameter> typeParameters;
    private final List<Parameter> parameters;
    private final TypeSpec returnType;

    public MethodSignature(NodeId nodeId, Visibility visibility, String name, List<TypeParameter> typeParameters,
            List<Parameter> parameters, TypeSpec returnType) {
        super(nodeId);
        this.visibility = Objects.requireNonNull(visibility, "visibility cannot be null");
        this.name = Objects.requireNonNull(name, "name cannot be null");
        this.typeParameters = Objects.requireNonNull(typeParameters, "typeParameters cannot be null");
        this.parameters = Objects.requireNonNull(parameters, "parameters cannot be null");
        this.returnType = Objects.requireNonNull(returnType, "returnType cannot be null");
    }

    @Override
    public <T> T accept(Visitor<? extends T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("%s %s [%s] (%s) : %s",
                visibility,
                name,
                typeParameters.stream()
                        .map(TypeParameter::toString)
                        .collect(Collectors.joining(", ")),
                parameters.stream()
                        .map(Parameter::toString)
                        .collect(Collectors.joining(", ")),
                returnType
        );
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public String getName() {
        return name;
    }

    public List<TypeParameter> getTypeParameters() {
        return typeParameters;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public TypeSpec getReturnType() {
        return returnType;
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
        return getNodeId().equals(that.getNodeId()) &&
                visibility == that.visibility &&
                name.equals(that.name) &&
                typeParameters.equals(that.typeParameters) &&
                parameters.equals(that.parameters) &&
                returnType.equals(that.returnType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNodeId(), visibility, name, typeParameters, parameters, returnType);
    }
}
