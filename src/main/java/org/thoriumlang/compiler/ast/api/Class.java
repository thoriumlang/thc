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

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class Class implements Type {
    private final org.thoriumlang.compiler.ast.nodes.Class node;

    Class(org.thoriumlang.compiler.ast.nodes.Class node) {
        this.node = Objects.requireNonNull(node, "node cannot be null");
    }

    public String getName() {
        return node.getName();
    }

    @Override
    public Collection<Method> getMethods() {
        return node.getMethods().stream()
                .map(Method::new)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Method> findMethod(String name) {
        return node.getMethods().stream()
                .filter(m -> m.getSignature().getName().equals(name))
                .findFirst()
                .map(Method::new);
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
        return node.equals(aClass.node);
    }

    @Override
    public int hashCode() {
        return Objects.hash(node);
    }
}
