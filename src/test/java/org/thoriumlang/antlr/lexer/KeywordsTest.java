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

import com.google.common.collect.ImmutableMap;
import org.antlr.v4.runtime.Token;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.thoriumlang.antlr.ThoriumLexer;

import java.util.Map;

@Tag("lexer")
class KeywordsTest {
    private static final Map<String, Integer> keywords = ImmutableMap.<String, Integer>builder()
            .put("class", ThoriumLexer.CLASS)
            .put("type", ThoriumLexer.TYPE)
            .put("private", ThoriumLexer.PRIVATE)
            .put("public", ThoriumLexer.PUBLIC)
            .put("namespace", ThoriumLexer.NAMESPACE)
            .build();

    @ParameterizedTest
    @ValueSource(strings = {
            "class", "type", "private", "public", "namespace"
    })
    void validKeywords(String text) {
        Assertions.assertThat(new Tokens(text).parse())
                .hasSize(1)
                .element(0)
                .extracting(Token::getType, Token::getText)
                .containsExactly(keywords.get(text), text);
    }
}
