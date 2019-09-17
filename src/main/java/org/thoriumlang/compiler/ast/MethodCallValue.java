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

public class MethodCallValue implements Value {
    private final NodeId nodeId;
    private final String methodName;
    private final List<TypeSpec> typeArguments;
    private final List<Value> methodArguments;

    public MethodCallValue(NodeId nodeId, String methodName, List<TypeSpec> typeArguments,
            List<Value> methodArguments) {
        if (nodeId == null) {
            throw new NullPointerException("nodeId cannot be null");
        }
        if (methodName == null) {
            throw new NullPointerException("methodName cannot be null");
        }
        if (typeArguments == null) {
            throw new NullPointerException("typeArguments cannot be null");
        }
        if (methodArguments == null) {
            throw new NullPointerException("methodArguments cannot be null");
        }
        this.nodeId = nodeId;
        this.methodName = methodName;
        this.typeArguments = typeArguments;
        this.methodArguments = methodArguments;
    }

    @Override
    public <T> T accept(Visitor<? extends T> visitor) {
        return visitor.visitMethodCallValue(this);
    }

    @Override
    public String toString() {
        return String.format(
                "%s[%s](%s)",
                methodName,
                typeArguments.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(",")),
                methodArguments.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(","))
        );
    }

    public NodeId getNodeId() {
        return nodeId;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<TypeSpec> getTypeArguments() {
        return typeArguments;
    }

    public List<Value> getMethodArguments() {
        return methodArguments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MethodCallValue that = (MethodCallValue) o;
        return nodeId.equals(that.nodeId) &&
                methodName.equals(that.methodName) &&
                typeArguments.equals(that.typeArguments) &&
                methodArguments.equals(that.methodArguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeId, methodName, typeArguments, methodArguments);
    }
}
