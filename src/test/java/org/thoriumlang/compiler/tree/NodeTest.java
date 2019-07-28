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

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("squid:S00100")
class NodeTest {
    @Test
    void toString_root() {
        Assertions.assertThat(new Node<>(new Block("Hello, world")).toString())
                .isEqualTo("Hello, world");
    }

    @Test
    void toString_child() {
        Node<Block> root = new Node<>(new Block("root"));
        new Node<>(root, new Block("child"));
        Assertions.assertThat(root.toString())
                .isEqualTo("root{\n  child\n}");
    }

    @Test
    void toString_multilineChild() {
        Node<Block> root = new Node<>(new Block("root"));
        new Node<>(root, new Block("line1\nline2"));
        Assertions.assertThat(root.toString())
                .isEqualTo("root{\n  line1\n  line2\n}");
    }

    @Test
    void toString_children() {
        Node<Block> root = new Node<>(new Block("root"));
        Node<Block> child = new Node<>(root, new Block("child1"));
        new Node<>(root, new Block("child2"));
        new Node<>(child, new Block("child1.1"));
        Assertions.assertThat(root.toString())
                .isEqualTo("root{\n  child1{\n    child1.1\n  }\n  child2\n}");
    }

    static class Block implements PrintableWrapper {
        private final String string;

        Block(String string) {
            this.string = string;
        }

        @Override
        public String toString() {
            return string;
        }

        @Override
        public String startString() {
            return string + "{";
        }

        @Override
        public String endString() {
            return "}";
        }
    }
}
