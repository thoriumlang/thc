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
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.thoriumlang.antlr.ThoriumLexer;

@Tag("lexer")
class IdentifierTest {
    @ParameterizedTest
    @ValueSource(strings = {
            "_", "_a", "a_",
            "ab", "_ab", "a_b", "ab_", "_ab_", "a_b_",
            "aB", "Ba", "BA",
            "a_B", "B_a", "B_A",
            "_0", "_a0", "a_0",
            "ab0", "_ab0", "a_b0", "ab_0", "_ab_0", "a_b_0",
            "aB0", "Ba0", "BA0",
            "a_B0", "B_a0", "B_A0",
            "_0c", "_a0c", "a_0c",
            "ab0c", "_ab0c", "a_b0c", "ab_0c", "_ab_0c", "a_b_0c",
            "aB0c", "Ba0c", "BA0c",
            "a_B0c", "B_a0c", "B_A0c"
    })
    void validIdentifiers(String string) {
        Assertions.assertThat(new Tokens(string).parse())
                .hasSize(1)
                .element(0)
                .extracting(Token::getType, Token::getText)
                .containsExactly(ThoriumLexer.IDENTIFIER, string);
    }
}
