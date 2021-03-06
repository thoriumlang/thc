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

public class TypeSpecFunction extends TypeSpec {
    private final List<TypeSpec> arguments;
    private final TypeSpec returnType;

    public TypeSpecFunction(NodeId nodeId, List<TypeSpec> arguments, TypeSpec returnType) {
        super(nodeId);
        this.arguments = Objects.requireNonNull(arguments, "arguments cannot be null");
        this.returnType = Objects.requireNonNull(returnType, "returnType cannot be null");
    }

    @Override
    public <T> T accept(Visitor<? extends T> visitor) {
        return visitor.visit(this);
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
        return getNodeId().equals(that.getNodeId()) &&
                arguments.equals(that.arguments) &&
                returnType.equals(that.returnType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNodeId(), arguments, returnType);
    }
}
