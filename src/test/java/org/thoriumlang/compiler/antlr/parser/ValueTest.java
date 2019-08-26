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
package org.thoriumlang.compiler.antlr.parser;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.thoriumlang.compiler.antlr.lexer.Tokens;

@Tag("parser")
class ValueTest {
    @ParameterizedTest()
    @CsvFileSource(resources = "/org/thoriumlang/compiler/antlr/parser/value.csv")
    void expressions(String input, String expected) {
        Assertions.assertThat(
                new Tree(new Tokens(input).parse()).serialize("value")
        ).isEqualTo(expected);
    }
}
