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
package org.thoriumlang.antlr.parser;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.thoriumlang.antlr.ThoriumLexer;

import static org.thoriumlang.antlr.parser.TokenStub.token;

@SuppressWarnings("squid:S1192")
@Tag("parser")
class UseTest {
    @Test
    void simple() {
        Assertions.assertThat(
                new Tree(
                        token("use", ThoriumLexer.USE),
                        token("a", ThoriumLexer.IDENTIFIER),
                        token("."),
                        token("b", ThoriumLexer.IDENTIFIER),
                        token("."),
                        token("c", ThoriumLexer.IDENTIFIER),
                        token(";")
                ).serialize("use")
        ).isEqualTo("(use use (fqIdentifier a . b . c) ;)");
    }

    @Test
    void wildcard() {
        Assertions.assertThat(
                new Tree(
                        token("use", ThoriumLexer.USE),
                        token("a", ThoriumLexer.IDENTIFIER),
                        token("."),
                        token("b", ThoriumLexer.IDENTIFIER),
                        token("."),
                        token("*"),
                        token(";")
                ).serialize("use")
        ).isEqualTo("(use use (fqIdentifier a . b) . * ;)");
    }

    @Test
    void simpleAs() {
        Assertions.assertThat(
                new Tree(
                        token("use", ThoriumLexer.USE),
                        token("a", ThoriumLexer.IDENTIFIER),
                        token("."),
                        token("b", ThoriumLexer.IDENTIFIER),
                        token("."),
                        token("c", ThoriumLexer.IDENTIFIER),
                        token(":"),
                        token("d", ThoriumLexer.IDENTIFIER),
                        token(";")
                ).serialize("use")
        ).isEqualTo("(use use (fqIdentifier a . b . c) : d ;)");
    }

    @Test
    void complex() {
        Assertions.assertThat(
                new Tree(
                        token("use", ThoriumLexer.USE),
                        token("a", ThoriumLexer.IDENTIFIER),
                        token("."),
                        token("b", ThoriumLexer.IDENTIFIER),
                        token("."),
                        token("{"),
                        token("c", ThoriumLexer.IDENTIFIER),
                        token("."),
                        token("f", ThoriumLexer.IDENTIFIER),
                        token(":"),
                        token("d", ThoriumLexer.IDENTIFIER),
                        token(","),
                        token("e", ThoriumLexer.IDENTIFIER),
                        token("}"),
                        token(";")
                ).serialize("use")
        ).isEqualTo("(use use (fqIdentifier a . b) . { (fqIdentifier c . f) : d , (fqIdentifier e) } ;)");
    }
}
