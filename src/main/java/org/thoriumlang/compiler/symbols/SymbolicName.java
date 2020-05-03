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

/**
 * Represents a variable, a constant, a parameter, a method or a class attribute.
 */
// TODO one ctor for each accepted defining node
// TODO rename to BoundName?
public class SymbolicName implements Symbol {
    private final Node definingNode;

    public SymbolicName(Node definingNode) {
        this.definingNode = Objects.requireNonNull(definingNode, "definingNode cannot be null");
    }

    @Override
    public Node getDefiningNode() {
        return definingNode;
    }

    @Override
    public String toString() {
        return definingNode.getNodeId().toString();
    }
}
