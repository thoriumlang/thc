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
package org.thoriumlang.compiler.symbols;

import org.thoriumlang.compiler.ast.nodes.Node;

import java.util.Objects;

public class JavaClass implements Symbol {
    private final String name;
    private final Node node;
    private final Class<?> clazz;

    public JavaClass(String name, Node node, Class<?> clazz) {
        this.name = Objects.requireNonNull(name, "name cannot be null");
        this.node = Objects.requireNonNull(node, "node cannot be null");
        this.clazz = Objects.requireNonNull(clazz, "clazz cannot be null");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public String toString() {
        return String.format("%s -> (rt.jar: class %s)", name, clazz.getCanonicalName());
    }
}
