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
package org.thoriumlang.antlr.lexer;

import org.antlr.v4.runtime.Token;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.thoriumlang.antlr.ThoriumLexer;

class NumberTest {
    @ParameterizedTest
    @ValueSource(strings = {
            "0", "+0", "-0",
            "00.00", "0.00", "00.0", ".00", "00.",
            ".0", "0.", "0.0",
            "+.0", "+0.", "+0.0",
            "-.0", "-0.", "-0.0",
            "0e1", "0e+0", "0e-0",
            "0E0", "0E+0", "0E-0",
            ".0e0", "0.e0", "0.0e0",
            ".0e+0", "0.e+0", "0.0e+0",
            ".0e-0", "0.e-0", "0.0e-0",
            ".0E0", "0.E0", "0.0E0",
            ".0E+0", "0.E+0", "0.0E+0",
            ".0E-0", "0.E-0", "0.0E-0",
            "+.0e0", "+0.e0", "+0.0e0",
            "+.0e+0", "+0.e+0", "+0.0e+0",
            "+.0e-0", "+0.e-0", "+0.0e-0",
            "+.0E0", "+0.E0", "+0.0E0",
            "+.0E+0", "+0.E+0", "+0.0E+0",
            "+.0E-0", "+0.E-0", "+0.0E-0",
            "-.0e0", "-0.e0", "-0.0e0",
            "-.0e+0", "-0.e+0", "-0.0e+0",
            "-.0e-0", "-0.e-0", "-0.0e-0",
            "-.0E0", "-0.E0", "-0.0E0",
            "-.0E+0", "-0.E+0", "-0.0E+0",
            "-.0E-0", "-0.E-0", "-0.0E-0"
    })
    void validNumbers(String string) {
        Assertions.assertThat(new Tokens(string).parse())
                .hasSize(1)
                .element(0)
                .extracting(Token::getType, Token::getText)
                .containsExactly(ThoriumLexer.NUMBER, string);
    }
}
