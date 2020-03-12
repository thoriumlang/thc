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

import java.util.Objects;

public class Parameter {
    private final org.thoriumlang.compiler.ast.nodes.Parameter parameter;

    Parameter(org.thoriumlang.compiler.ast.nodes.Parameter node) {
        this.parameter = Objects.requireNonNull(node, "node cannot be null");
    }

    public String getName() {
        return parameter.getName();
    }

    public Type getType() {
        return new TypeSpecToTypeConverter().apply(parameter.getType());
    }

    @Override
    public String toString() {
        return String.format("%s: %s", getName(), getType());
    }
}
