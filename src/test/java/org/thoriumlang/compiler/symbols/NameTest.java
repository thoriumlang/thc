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
package org.thoriumlang.compiler.symbols;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class NameTest {
    @Test
    void constructor_name1() {
        Assertions.assertThatThrownBy(() -> new Name(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("fqName cannot be null");
    }

    @Test
    void constructor_name2() {
        Assertions.assertThatThrownBy(() -> new Name(null, "packageName"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("name cannot be null");
    }

    @Test
    void constructor_package() {
        Assertions.assertThatThrownBy(() -> new Name("name", null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("packageName cannot be null");
    }

    @Test
    void getSimpleName_simple() {
        Assertions.assertThat(new Name("name").getSimpleName())
                .isEqualTo("name");
    }

    @Test
    void getSimpleName_qualified() {
        Assertions.assertThat(new Name("qualified.name").getSimpleName())
                .isEqualTo("name");
    }

    @Test
    void getFullName_simple() {
        Assertions.assertThat(new Name("name").getParts())
                .containsExactly("name");
    }

    @Test
    void getFullName_qualified() {
        Assertions.assertThat(new Name("qualified.name").getParts())
                .containsExactly("qualified", "name");
    }

    @Test
    void isQualified_simple() {
        Assertions.assertThat(new Name("name").isQualified())
                .isFalse();
    }

    @Test
    void isQualified_qualified() {
        Assertions.assertThat(new Name("qualified.name").isQualified())
                .isTrue();
    }

    @Test
    void equals_() {
        Assertions.assertThat(new Name("qualified.name").equals(new Name("qualified.name")))
                .isTrue();
    }
}
