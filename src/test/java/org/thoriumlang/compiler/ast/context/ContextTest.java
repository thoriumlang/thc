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
package org.thoriumlang.compiler.ast.context;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.testsupport.NodeStub;

class ContextTest {
    @Test
    void put_keyType_nullKey() {
        Context context = new NodeStub().getContext();
        try {
            context.put(null, String.class, "Hello");
        } catch (IllegalArgumentException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("key cannot be null");
            return;
        }
        Assertions.fail("Exception not thrown");
    }

    @Test
    void put_keyType_nullType() {
        Context context = new NodeStub().getContext();
        try {
            context.put("key", null, "Hello");
        } catch (IllegalArgumentException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("type cannot be null");
            return;
        }
        Assertions.fail("Exception not thrown");
    }

    @Test
    void put_keyType_nullValue() {
        Context context = new NodeStub().getContext();
        try {
            context.put("key", String.class, null);
        } catch (IllegalArgumentException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("value cannot be null");
            return;
        }
        Assertions.fail("Exception not thrown");
    }

    @Test
    void putAll() {
        Context sourceContext = new NodeStub().getContext();
        sourceContext.put(Object.class, new Object());
        sourceContext.put("someStrKey", String.class, "someStrVal");

        Context destinationContext = new NodeStub().getContext();
        destinationContext.put(Object.class, new Object());
        destinationContext.putAll(sourceContext);

        Assertions.assertThat(destinationContext.get(Object.class))
                .get()
                .isSameAs(sourceContext.get(Object.class).orElse(null));
    }

    @Test
    void copyFrom() {
        Context sourceContext = new NodeStub().getContext();
        sourceContext.put(Object.class, new Object());

        Context destinationContext = new NodeStub().getContext();
        destinationContext.copyFrom(Object.class, sourceContext.getNode());

        Assertions.assertThat(destinationContext.get(Object.class))
                .get()
                .isSameAs(sourceContext.get(Object.class).orElse(new Object()));
    }

    @Test
    void get_keyType_nullKey() {
        Context context = new NodeStub().getContext();
        try {
            context.get(null, String.class);
        } catch (IllegalArgumentException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("key cannot be null");
            return;
        }
        Assertions.fail("Exception not thrown");
    }

    @Test
    void get_keyType_nullType() {
        Context context = new NodeStub().getContext();
        try {
            context.get("key", null);
        } catch (IllegalArgumentException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("type cannot be null");
            return;
        }
        Assertions.fail("Exception not thrown");
    }

    @Test
    void get_keyType_present() {
        Context context = new NodeStub().getContext();

        context.put("key", String.class, "Hello");

        Assertions.assertThat(context.get("key", String.class))
                .isNotEmpty()
                .get()
                .isEqualTo("Hello");
    }

    @Test
    void get_keyType_keyAbsent() {
        Context context = new NodeStub().getContext();

        context.put("key", String.class, "Hello");

        Assertions.assertThat(context.get("absent", String.class))
                .isEmpty();
    }

    @Test
    void get_keyType_typeAbsent() {
        Context context = new NodeStub().getContext();

        context.put("key", String.class, "Hello");

        Assertions.assertThat(context.get("key", Integer.class))
                .isEmpty();
    }

    @Test
    void put_keyType_returnsContext() {
        Context context = new NodeStub().getContext();

        Assertions.assertThat(context.put("key", String.class, ""))
                .isSameAs(context);
    }

    @Test
    void putIfAbsentAndGet_keyType_nullKey() {
        Context context = new NodeStub().getContext();
        try {
            context.putIfAbsentAndGet(null, String.class, "Hello");
        } catch (IllegalArgumentException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("key cannot be null");
            return;
        }
        Assertions.fail("Exception not thrown");
    }

    @Test
    void putIfAbsentAndGet_keyType_nullType() {
        Context context = new NodeStub().getContext();
        try {
            context.putIfAbsentAndGet("key", null, "Hello");
        } catch (IllegalArgumentException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("type cannot be null");
            return;
        }
        Assertions.fail("Exception not thrown");
    }

    @Test
    void putIfAbsentAndGet_keyType_nullValue() {
        Context context = new NodeStub().getContext();
        try {
            context.putIfAbsentAndGet("key", String.class, null);
        } catch (IllegalArgumentException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("value cannot be null");
            return;
        }
        Assertions.fail("Exception not thrown");
    }

    @Test
    void putIfAbsentAndGet_keyType_present() {
        Context context = new NodeStub().getContext();
        context.put("key", String.class, "World");
        Assertions.assertThat(context.putIfAbsentAndGet("key", String.class, "Hello"))
                .isEqualTo("World");
    }

    @Test
    void putIfAbsentAndGet_keyType_absent() {
        Context context = new NodeStub().getContext();
        Assertions.assertThat(context.putIfAbsentAndGet("key", String.class, "Hello"))
                .isEqualTo("Hello");
    }

    @Test
    void contains_keyType_nullKey() {
        Context context = new NodeStub().getContext();
        try {
            context.contains(null, String.class);
        } catch (IllegalArgumentException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("key cannot be null");
            return;
        }
        Assertions.fail("Exception not thrown");
    }

    @Test
    void contains_keyType_nullType() {
        Context context = new NodeStub().getContext();
        try {
            context.contains("key", null);
        } catch (IllegalArgumentException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("type cannot be null");
            return;
        }
        Assertions.fail("Exception not thrown");
    }

    @Test
    void contains_keyType_present() {
        Context context = new NodeStub().getContext();
        context.put("key", String.class, "");
        Assertions.assertThat(context.contains("key", String.class))
                .isTrue();
    }

    @Test
    void contains_keyType_absent() {
        Context context = new NodeStub().getContext();
        Assertions.assertThat(context.contains("key", String.class))
                .isFalse();
    }

    @Test
    void contains_keyType_keyAbsent() {
        Context context = new NodeStub().getContext();
        context.put("key", String.class, "Hello");
        Assertions.assertThat(context.contains("absent", String.class))
                .isFalse();
    }

    @Test
    void contains_keyType_typeAbsent() {
        Context context = new NodeStub().getContext();
        context.put(String.class, "Hello");
        Assertions.assertThat(context.contains(Integer.class))
                .isFalse();
    }

    @Test
    void put_type_nullType() {
        Context context = new NodeStub().getContext();
        try {
            context.put(null, "Hello");
        } catch (IllegalArgumentException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("type cannot be null");
            return;
        }
        Assertions.fail("Exception not thrown");
    }

    @Test
    void put_type_nullValue() {
        Context context = new NodeStub().getContext();
        try {
            context.put(String.class, null);
        } catch (IllegalArgumentException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("value cannot be null");
            return;
        }
        Assertions.fail("Exception not thrown");
    }

    @Test
    void get_type_nullType() {
        Context context = new NodeStub().getContext();
        try {
            context.get((Class<Object>) null);
        } catch (IllegalArgumentException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("type cannot be null");
            return;
        }
        Assertions.fail("Exception not thrown");
    }

    @Test
    void get_type_present() {
        Context context = new NodeStub().getContext();

        context.put(String.class, "Hello");

        Assertions.assertThat(context.get(String.class))
                .isNotEmpty()
                .get()
                .isEqualTo("Hello");
    }

    @Test
    void get_type_typeAbsent() {
        Context context = new NodeStub().getContext();

        context.put(String.class, "Hello");

        Assertions.assertThat(context.get(Integer.class))
                .isEmpty();
    }

    @Test
    void get_key_null() {
        Context context = new NodeStub().getContext();
        Assertions.assertThatIllegalArgumentException()
                .isThrownBy(() -> context.get((Context.Key) null))
                .withMessage("key cannot be null");
    }

    @Test
    void put_type_returnsContext() {
        Context context = new NodeStub().getContext();

        Assertions.assertThat(context.put(String.class, ""))
                .isSameAs(context);
    }

    @Test
    void putIfAbsentAndGet_type_nullType() {
        Context context = new NodeStub().getContext();
        try {
            context.putIfAbsentAndGet(null, "Hello");
        } catch (IllegalArgumentException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("type cannot be null");
            return;
        }
        Assertions.fail("Exception not thrown");
    }

    @Test
    void putIfAbsentAndGet_type_nullValue() {
        Context context = new NodeStub().getContext();
        try {
            context.putIfAbsentAndGet(String.class, null);
        } catch (IllegalArgumentException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("value cannot be null");
            return;
        }
        Assertions.fail("Exception not thrown");
    }

    @Test
    void putIfAbsentAndGet_type_present() {
        Context context = new NodeStub().getContext();
        context.put(String.class, "World");
        Assertions.assertThat(context.putIfAbsentAndGet(String.class, "Hello"))
                .isEqualTo("World");
    }

    @Test
    void putIfAbsentAndGet_type_absent() {
        Context context = new NodeStub().getContext();
        Assertions.assertThat(context.putIfAbsentAndGet(String.class, "Hello"))
                .isEqualTo("Hello");
    }

    @Test
    void contains_type_nullType() {
        Context context = new NodeStub().getContext();
        try {
            context.contains(null);
        } catch (IllegalArgumentException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("type cannot be null");
            return;
        }
        Assertions.fail("Exception not thrown");
    }

    @Test
    void contains_type_present() {
        Context context = new NodeStub().getContext();
        context.put(String.class, "");
        Assertions.assertThat(context.contains(String.class))
                .isTrue();
    }

    @Test
    void contains_type_absent() {
        Context context = new NodeStub().getContext();
        Assertions.assertThat(context.contains(String.class))
                .isFalse();
    }

    @Test
    void contains_type_typeAbsent() {
        Context context = new NodeStub().getContext();
        context.put(String.class, "Hello");
        Assertions.assertThat(context.contains(Integer.class))
                .isFalse();
    }

    @Test
    void getNode() {
        NodeStub node = new NodeStub();
        Assertions.assertThat(node.getContext().getNode())
                .isSameAs(node);
    }

    @Test
    void require_type_present() {
        NodeStub node = new NodeStub();
        node.getContext().put(String.class, "someString");
        Assertions.assertThat(node.getContext().require(String.class))
                .isEqualTo("someString");
    }

    @Test
    void require_type_absent() {
        NodeStub node = new NodeStub();
        Assertions.assertThatThrownBy(() -> node.getContext().require(String.class))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("no java.lang.String found");
    }

    @Test
    void require_key_type_present() {
        NodeStub node = new NodeStub();
        node.getContext().put("key", String.class, "someString");
        Assertions.assertThat(node.getContext().require("key", String.class))
                .isEqualTo("someString");
    }

    @Test
    void require_key_type_absent() {
        NodeStub node = new NodeStub();
        Assertions.assertThatThrownBy(() -> node.getContext().require("key", String.class))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("no key(java.lang.String) found");
    }

    @Test
    void keys_empty() {
        NodeStub node = new NodeStub();
        Assertions.assertThat(node.getContext().keys())
                .isEmpty();
    }

    @Test
    void keys_nonEmpty() {
        NodeStub node = new NodeStub();
        node.getContext().put(String.class, "someStuff");
        Assertions.assertThat(node.getContext().keys())
                .hasSize(1);
    }

}
