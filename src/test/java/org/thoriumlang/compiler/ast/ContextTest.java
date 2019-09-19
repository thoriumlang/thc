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

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.ast.visitor.Visitor;

class ContextTest {
    @Test
    void get_present() {
        Context context = new NodeStub().getContext();

        context.put(String.class, "Hello");

        Assertions.assertThat(context.get(String.class))
                .isNotEmpty()
                .get()
                .isEqualTo("Hello");
    }

    @Test
    void get_absent() {
        Context context = new NodeStub().getContext();

        context.put(String.class, "Hello");

        Assertions.assertThat(context.get(Integer.class))
                .isEmpty();
    }

    @Test
    void put_returnsContext() {
        Context context = new NodeStub().getContext();

        Assertions.assertThat(context.put(String.class, ""))
                .isSameAs(context);
    }

    @Test
    void getNode() {
        NodeStub node = new NodeStub();
        Assertions.assertThat(node.getContext().getNode())
                .isSameAs(node);
    }

    @Test
    void ensureAndGet_present() {
        Context context = new NodeStub().getContext();
        context.put(String.class, "World");
        Assertions.assertThat(context.putIfAbsentAndGet(String.class, "Hello"))
                .isEqualTo("World");
    }

    @Test
    void putIfAbsentAndGet_absent() {
        Context context = new NodeStub().getContext();
        Assertions.assertThat(context.putIfAbsentAndGet(String.class, "Hello"))
                .isEqualTo("Hello");
    }

    @Test
    void putIfAbsentAndGet_present() {
        Context context = new NodeStub().getContext();
        context.put(String.class, "");
        Assertions.assertThat(context.contains(String.class))
                .isTrue();
    }

    @Test
    void contains_absent() {
        Context context = new NodeStub().getContext();
        Assertions.assertThat(context.contains(String.class))
                .isFalse();
    }

    private static class NodeStub implements Node {
        @Override
        public <T> T accept(Visitor<? extends T> visitor) {
            return null;
        }

        Context getContext() {
            return new Context(this);
        }
    }
}
