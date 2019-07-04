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
package org.thoriumlang.compiler.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Node<T extends PrintableWrapper> {
    private static final String INDENT = "  ";
    private final T object;
    private final List<Node<T>> children;

    public Node(Node<T> parent, T object) {
        this.object = object;
        this.children = new ArrayList<>();
        if (parent != null) {
            parent.addChild(this);
        }
    }

    public Node(T object) {
        this(null, object);
    }

    @Override
    public String toString() {
        if (children.isEmpty()) {
            return object.toString();
        }
        return String.join("\n",
                object.startString(),
                children.stream()
                        .map(c -> Arrays.stream(c.toString().split("\n"))
                                .map(l -> INDENT + l)
                                .collect(Collectors.joining("\n"))
                        )
                        .collect(Collectors.joining("\n")),
                object.endString()
        );
    }

    private void addChild(Node<T> node) {
        children.add(node);
    }
}
