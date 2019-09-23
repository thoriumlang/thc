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

public class TypeSpecSimple implements TypeSpec {
    private final NodeId nodeId;
    private final String type;
    private final List<TypeSpec> arguments;
    private final Context context;

    public TypeSpecSimple(NodeId nodeId, String type, List<TypeSpec> arguments) {
        if (nodeId == null) {
            throw new NullPointerException("nodeId cannot be null");
        }
        if (type == null) {
            throw new NullPointerException("type cannot be null");
        }
        if (arguments == null) {
            throw new NullPointerException("arguments cannot be null");
        }
        this.nodeId = nodeId;
        this.type = type;
        this.arguments = arguments;
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
        return String.format(
                "%s[%s]",
                type,
                arguments.stream()
                        .map(TypeSpec::toString)
                        .collect(Collectors.joining(", "))
        );
    }

    public NodeId getNodeId() {
        return nodeId;
    }

    public String getType() {
        return type;
    }

    public List<TypeSpec> getArguments() {
        return arguments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TypeSpecSimple that = (TypeSpecSimple) o;
        return nodeId.equals(that.nodeId) &&
                type.equals(that.type) &&
                arguments.equals(that.arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeId, type, arguments);
    }
}
