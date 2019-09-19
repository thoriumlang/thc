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
    void put_nullKey() {
        Context context = new NodeStub().getContext();
        try {
            context.put(null, String.class, "Hello");
        }
        catch (IllegalArgumentException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("key cannot be null");
            return;
        }
        Assertions.fail("Exception not thrown");
    }

    @Test
    void put_nullType() {
        Context context = new NodeStub().getContext();
        try {
            context.put("key", null, "Hello");
        }
        catch (IllegalArgumentException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("type cannot be null");
            return;
        }
        Assertions.fail("Exception not thrown");
    }

    @Test
    void put_nullValue() {
        Context context = new NodeStub().getContext();
        try {
            context.put("key", String.class, null);
        }
        catch (IllegalArgumentException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("value cannot be null");
            return;
        }
        Assertions.fail("Exception not thrown");
    }

    @Test
    void get_nullKey() {
        Context context = new NodeStub().getContext();
        try {
            context.get(null, String.class);
        }
        catch (IllegalArgumentException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("key cannot be null");
            return;
        }
        Assertions.fail("Exception not thrown");
    }

    @Test
    void get_nullType() {
        Context context = new NodeStub().getContext();
        try {
            context.get("key", null);
        }
        catch (IllegalArgumentException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("type cannot be null");
            return;
        }
        Assertions.fail("Exception not thrown");
    }

    @Test
    void get_present() {
        Context context = new NodeStub().getContext();

        context.put("key", String.class, "Hello");

        Assertions.assertThat(context.get("key", String.class))
                .isNotEmpty()
                .get()
                .isEqualTo("Hello");
    }

    @Test
    void get_keyAbsent() {
        Context context = new NodeStub().getContext();

        context.put("key", String.class, "Hello");

        Assertions.assertThat(context.get("absent", String.class))
                .isEmpty();
    }

    @Test
    void get_typeAbsent() {
        Context context = new NodeStub().getContext();

        context.put("key", String.class, "Hello");

        Assertions.assertThat(context.get("key", Integer.class))
                .isEmpty();
    }

    @Test
    void put_returnsContext() {
        Context context = new NodeStub().getContext();

        Assertions.assertThat(context.put("key", String.class, ""))
                .isSameAs(context);
    }

    @Test
    void getNode() {
        NodeStub node = new NodeStub();
        Assertions.assertThat(node.getContext().getNode())
                .isSameAs(node);
    }

    @Test
    void putIfAbsentAndGet_nullKey() {
        Context context = new NodeStub().getContext();
        try {
            context.putIfAbsentAndGet(null, String.class, "Hello");
        }
        catch (IllegalArgumentException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("key cannot be null");
            return;
        }
        Assertions.fail("Exception not thrown");
    }

    @Test
    void putIfAbsentAndGet_nullType() {
        Context context = new NodeStub().getContext();
        try {
            context.putIfAbsentAndGet("key", null, "Hello");
        }
        catch (IllegalArgumentException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("type cannot be null");
            return;
        }
        Assertions.fail("Exception not thrown");
    }

    @Test
    void putIfAbsentAndGet_nullValue() {
        Context context = new NodeStub().getContext();
        try {
            context.putIfAbsentAndGet("key", String.class, null);
        }
        catch (IllegalArgumentException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("value cannot be null");
            return;
        }
        Assertions.fail("Exception not thrown");
    }

    @Test
    void putIfAbsentAndGet_present() {
        Context context = new NodeStub().getContext();
        context.put("key", String.class, "World");
        Assertions.assertThat(context.putIfAbsentAndGet("key", String.class, "Hello"))
                .isEqualTo("World");
    }

    @Test
    void putIfAbsentAndGet_absent() {
        Context context = new NodeStub().getContext();
        Assertions.assertThat(context.putIfAbsentAndGet("key", String.class, "Hello"))
                .isEqualTo("Hello");
    }

    @Test
    void contains_nullKey() {
        Context context = new NodeStub().getContext();
        try {
            context.contains(null, String.class);
        }
        catch (IllegalArgumentException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("key cannot be null");
            return;
        }
        Assertions.fail("Exception not thrown");
    }

    @Test
    void contains_nullType() {
        Context context = new NodeStub().getContext();
        try {
            context.contains("key", null);
        }
        catch (IllegalArgumentException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("type cannot be null");
            return;
        }
        Assertions.fail("Exception not thrown");
    }

    @Test
    void contains_present() {
        Context context = new NodeStub().getContext();
        context.put("key", String.class, "");
        Assertions.assertThat(context.contains("key", String.class))
                .isTrue();
    }

    @Test
    void contains_absent() {
        Context context = new NodeStub().getContext();
        Assertions.assertThat(context.contains("key", String.class))
                .isFalse();
    }

    @Test
    void contains_keyAbsent() {
        Context context = new NodeStub().getContext();
        context.put("key", String.class, "Hello");
        Assertions.assertThat(context.contains("absent", String.class))
                .isFalse();
    }

    @Test
    void contains_typeAbsent() {
        Context context = new NodeStub().getContext();
        context.put("key", String.class, "Hello");
        Assertions.assertThat(context.contains("key", Integer.class))
                .isFalse();
    }

    private static class NodeStub implements Node {
        @Override
        public <T> T accept(Visitor<? extends T> visitor) {
            return null;
        }

        @Override
        public Context getContext() {
            return new Context(this);
        }
    }
}
