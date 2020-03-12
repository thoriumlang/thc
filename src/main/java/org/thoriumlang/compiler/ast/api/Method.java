/*
 * Copyright 2020 Christophe Pollet
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
package org.thoriumlang.compiler.ast.api;

import org.thoriumlang.compiler.ast.context.Relatives;
import org.thoriumlang.compiler.ast.nodes.MethodSignature;
import org.thoriumlang.compiler.ast.visitor.BaseVisitor;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class Method {
    private final org.thoriumlang.compiler.ast.nodes.Node node;
    private final MethodSignature signature;

    Method(org.thoriumlang.compiler.ast.nodes.Method node) {
        this.node = Objects.requireNonNull(node, "node cannot be null");
        this.signature = node.getSignature();
    }

    Method(MethodSignature node) {
        this.node = Objects.requireNonNull(node, "node cannot be null");
        this.signature = node;
    }

    public String getName() {
        return signature.getName();
    }

    // TODO is it useful?
    public Optional<Class> getDeclaringClass() {
        return Optional.ofNullable(
                node.getContext()
                        .get(Relatives.class).orElseThrow(() -> new IllegalStateException("no relatives found"))
                        .parent().orElseThrow(() -> new IllegalStateException("no parent found"))
                        .node()
                        .accept(new BaseVisitor<org.thoriumlang.compiler.ast.nodes.Class>() {
                            @Override
                            public org.thoriumlang.compiler.ast.nodes.Class visit(
                                    org.thoriumlang.compiler.ast.nodes.Class node) {
                                return node;
                            }
                        })
        ).map(Class::new);
    }

    public List<Parameter> getParameters() {
        return signature.getParameters().stream()
                .map(Parameter::new)
                .collect(Collectors.toList());
    }

    public Optional<Parameter> findParameter(String name) {
        return signature.getParameters().stream()
                .filter(p -> p.getName().equals(name))
                .findFirst()
                .map(Parameter::new);
    }

    public Type getReturnType() {
        return new TypeSpecToTypeConverter().apply(signature.getReturnType());
    }

    public String getSignature() {
        return String.format("%s(%s)%s",
                getName(),
                getParameters().stream()
                        .map(Parameter::getType)
                        .map(Type::getName)
                        .collect(Collectors.joining(",")),
                getReturnType().getName());
    }

    @Override
    public String toString() {
        return getSignature();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return getSignature().equals(((Method) o).getSignature());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSignature());
    }
}
