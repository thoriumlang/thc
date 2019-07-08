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
package org.thoriumlang.compiler.it;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.thoriumlang.compiler.ast.AST;
import org.thoriumlang.compiler.output.th.Walker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Tag("integration")
class IntegrationTest {
    @ParameterizedTest
    @ValueSource(strings = {
            "/org/thoriumlang/compiler/tests/type",
            "/org/thoriumlang/compiler/tests/use"
    })
    void ast(String path) throws IOException {
        Assertions
                .assertThat(
                        new AST(IntegrationTest.class.getResourceAsStream(path + ".th")).root()
                                .toString())
                .isEqualTo(
                        new BufferedReader(
                                new InputStreamReader(
                                        IntegrationTest.class.getResourceAsStream(path + ".out.ast")
                                )
                        )
                                .lines()
                                .collect(Collectors.joining("\n"))
                );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/org/thoriumlang/compiler/tests/type",
            "/org/thoriumlang/compiler/tests/use"
    })
    void thorium(String path) throws IOException {
        Assertions
                .assertThat(
                        new Walker(
                                new AST(IntegrationTest.class.getResourceAsStream(path + ".th")).root()
                        ).walk()
                )
                .isEqualTo(
                        new BufferedReader(
                                new InputStreamReader(
                                        IntegrationTest.class.getResourceAsStream(path + ".out.th")
                                )
                        )
                                .lines()
                                .collect(Collectors.joining("\n"))
                );
    }
}