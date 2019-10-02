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

public class TypeSpecFunction implements TypeSpec {
    private final NodeId nodeId;
    private final List<TypeSpec> arguments;
    private final TypeSpec returnType;
    private final Context context;

    public TypeSpecFunction(NodeId nodeId, List<TypeSpec> arguments, TypeSpec returnType) {
        if (nodeId == null) {
            throw new NullPointerException("nodeId cannot be null");
        }
        if (arguments == null) {
            throw new NullPointerException("arguments cannot be null");
        }
        if (returnType == null) {
            throw new NullPointerException("returnType cannot be null");
        }
        this.nodeId = nodeId;
        this.arguments = arguments;
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
    public NodeId getNodeId() {
        return nodeId;
    }

    @Override
    public String toString() {
        return String.format("(%s):%s",
                arguments.stream().
                        map(TypeSpec::toString)
                        .collect(Collectors.joining(";")),
                returnType.toString()
        );
    }

    public List<TypeSpec> getArguments() {
        return arguments;
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
        TypeSpecFunction that = (TypeSpecFunction) o;
        return nodeId.equals(that.nodeId) &&
                arguments.equals(that.arguments) &&
                returnType.equals(that.returnType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeId, arguments, returnType);
    }
}
