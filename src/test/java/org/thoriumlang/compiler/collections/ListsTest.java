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
package org.thoriumlang.compiler.collections;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

class ListsTest {
    @Test
    void last_empty() {
        Assertions.assertThat(
                Lists.last(Collections.emptyList())
        )
                .isEmpty();
    }

    @Test
    void last_notEmpty() {
        Assertions.assertThat(
                Lists.last(Collections.singletonList("last"))
        )
                .isNotEmpty()
                .get()
                .isEqualTo("last");
    }

    @Test
    void withoutLast_empty() {
        Assertions.assertThat(
                Lists.withoutLast(Collections.emptyList())
        )
                .isEmpty();
    }

    @Test
    void withoutLast_oneElement() {
        Assertions.assertThat(
                Lists.withoutLast(Collections.singletonList("last"))
        )
                .isEmpty();
    }

    @Test
    void withoutLast_nElement() {
        Assertions.assertThat(
                Lists.withoutLast(Arrays.asList("first", "second"))
        )
                .containsExactly("first");
    }

    @Test
    void append_empty() {
        Assertions.assertThat(
                Lists.append(Collections.emptyList(), "element")
        ).containsExactly("element");
    }

    @Test
    void append_notEmpty() {
        Assertions.assertThat(
                Lists.append(Arrays.asList("first", "second"), "third")
        ).containsExactly("first", "second", "third");
    }


    @Test
    void append_null_empty() {
        Assertions.assertThat(
                Lists.append(Collections.emptyList(), null)
        ).isEmpty();
    }

    @Test
    void append_null_notEmpty() {
        Assertions.assertThat(
                Lists.append(Arrays.asList("first", "second"), null)
        ).containsExactly("first", "second");
    }

    @Test
    void merge() {
        Assertions.assertThat(
                Lists.merge(Collections.singletonList("A"), Collections.singletonList("B"))
        ).containsExactly("A", "B");
    }

    @Test
    void indexOf_nodeFound() {
        Assertions.assertThat(Lists.indexOf(Collections.singletonList("A"), s -> true))
                .get()
                .isEqualTo(0);
    }

    @Test
    void indexOf_firstNodeFound() {
        Assertions.assertThat(Lists.indexOf(Arrays.asList("A", "B"), s -> true))
                .get()
                .isEqualTo(0);
    }

    @Test
    void indexOf_nodeNotFound() {
        Assertions.assertThat(Lists.indexOf(Collections.singletonList("A"), s -> false))
                .isEmpty();
    }

    @Test
    void get_nodeFound() {
        Assertions.assertThat(Lists.get(Collections.singletonList("A"), 0))
                .get()
                .isEqualTo("A");
    }

    @Test
    void get_nodeNotFound() {
        Assertions.assertThat(Lists.get(Collections.singletonList("A"), 1))
                .isEmpty();
        Assertions.assertThat(Lists.get(Collections.singletonList("A"), -1))
                .isEmpty();
    }

    @Test
    void requiresNonEmpty_empty() {
        Assertions.assertThatThrownBy(() -> Lists.requireNonEmpty(Collections.emptyList(), "list cannot be empty"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("list cannot be empty");
    }

}
