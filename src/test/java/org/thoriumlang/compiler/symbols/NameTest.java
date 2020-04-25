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
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

class NameTest {
    private static Stream<DynamicTest> buildDynamicTests(Example example) {
        Name name = new Name(example.fqName);
        return Stream.of(
                DynamicTest.dynamicTest(
                        example.fqName + " / full name",
                        () -> Assertions.assertThat(name.getFullName())
                                .describedAs(example.fqName)
                                .isEqualTo(example.fqName)
                ),
                DynamicTest.dynamicTest(
                        example.fqName + " / is method",
                        () -> Assertions.assertThat(name.isMethod())
                                .describedAs(example.fqName)
                                .isEqualTo(example.isMethod)
                ),

                DynamicTest.dynamicTest(
                        example.fqName + " / normalized simple name",
                        () -> Assertions.assertThat(name.getNormalizedSimpleName())
                                .describedAs(example.fqName)
                                .isEqualTo(example.normalizedSimpleName)
                ),

                DynamicTest.dynamicTest(
                        example.fqName + " / is qualified",
                        () -> Assertions.assertThat(name.isQualified())
                                .isEqualTo(example.isQualified)
                ),

                DynamicTest.dynamicTest(
                        example.fqName + " / simple name",
                        () -> Assertions.assertThat(name.getSimpleName())
                                .describedAs(example.fqName)
                                .isEqualTo(example.simpleName)
                ),

                DynamicTest.dynamicTest(
                        example.fqName + " / parts",
                        () -> Assertions.assertThat(name.getParts())
                                .describedAs(example.fqName)
                                .containsExactlyElementsOf(example.parts)
                )
        );
    }

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

    @TestFactory
    Stream<DynamicTest> examples() {
        return new BufferedReader(
                new InputStreamReader(
                        NameTest.class.getResourceAsStream("/org/thoriumlang/compiler/symbols/Name.tests")
                )
        )
                .lines()
                .filter(l -> !l.startsWith("#"))
                .map(l -> l.replaceAll("\\s+", " "))
                .map(l -> l.split(" "))
                .map(Example::new)
                .flatMap(NameTest::buildDynamicTests);
    }

    private static class Example {
        private final String fqName;
        private final Boolean isQualified;
        private final Boolean isMethod;
        private final String simpleName;
        private final String normalizedSimpleName;
        private final List<String> parts;

        private Example(String... fields) {
            int i = 0;
            this.fqName = fields[i++];
            this.isQualified = Boolean.valueOf(fields[i++]);
            this.isMethod = Boolean.valueOf(fields[i++]);
            this.simpleName = fields[i++];
            this.normalizedSimpleName = fields[i++];
            this.parts = Arrays.asList(fields[i].split(";"));
        }
    }
}
