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

public class MethodSignature implements Node {
    private final NodeId nodeId;
    private final Visibility visibility;
    private final String name;
    private final List<TypeParameter> typeParameters;
    private final List<Parameter> parameters;
    private final TypeSpec returnType;
    private final Context context;

    public MethodSignature(NodeId nodeId, Visibility visibility, String name, List<TypeParameter> typeParameters,
            List<Parameter> parameters, TypeSpec returnType) {
        if (nodeId == null) {
            throw new NullPointerException("nodeId cannot be null");
        }
        if (visibility == null) {
            throw new NullPointerException("visibility cannot be null");
        }
        if (name == null) {
            throw new NullPointerException("name cannot be null");
        }
        if (typeParameters == null) {
            throw new NullPointerException("typeParameters cannot be null");
        }
        if (parameters == null) {
            throw new NullPointerException("parameters cannot be null");
        }
        if (returnType == null) {
            throw new NullPointerException("returnType cannot be null");
        }
        this.nodeId = nodeId;
        this.visibility = visibility;
        this.name = name;
        this.typeParameters = typeParameters;
        this.parameters = parameters;
        this.returnType = returnType;
        this.context = new Context(this);
    }

    @Override
    public <T> T accept(Visitor<? extends T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Context getContext() {
        return context;
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

    public NodeId getNodeId() {
        return nodeId;
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
        return nodeId.equals(that.nodeId) &&
                visibility == that.visibility &&
                name.equals(that.name) &&
                typeParameters.equals(that.typeParameters) &&
                parameters.equals(that.parameters) &&
                returnType.equals(that.returnType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeId, visibility, name, typeParameters, parameters, returnType);
    }
}