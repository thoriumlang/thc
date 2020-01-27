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
package org.thoriumlang.compiler.ast.nodes;

import org.thoriumlang.compiler.ast.visitor.Visitor;
import org.thoriumlang.compiler.collections.Lists;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Family {
    private final Node node;
    private final Family parent;

    public Family(Node node, Family parent) {
        this.node = node;
        this.parent = parent;
    }

    public Family(Node node) {
        this(node, null);
    }

    public Node node() {
        return node;
    }

    public Optional<Family> parent() {
        return Optional.ofNullable(parent);
    }

    public List<Family> children(Visitor<List<Node>> visitor) {
        return node.accept(visitor).stream()
                .map(n -> n.getContext()
                        .get(Family.class)
                        .orElseThrow(() -> new IllegalStateException("No family found in node's context")))
                .collect(Collectors.toList());
    }

    public List<Family> siblings(Visitor<List<Node>> visitor) {
        return parent()
                .map(p -> p.children(visitor))
                .orElse(Collections.emptyList());
    }

    public Optional<Family> nextSibling(Visitor<List<Node>> visitor) {
        return sibling(1, visitor);
    }

    public Optional<Family> sibling(int index, Visitor<List<Node>> visitor) {
        List<Family> siblings = siblings(visitor);
        return Lists.indexOf(siblings, n -> n.node().getNodeId().equals(node.getNodeId()))
                .flatMap(i -> Lists.get(siblings, i + index));
    }

    public Optional<Family> previousSibling(Visitor<List<Node>> visitor) {
        return sibling(-1, visitor);
    }
}
