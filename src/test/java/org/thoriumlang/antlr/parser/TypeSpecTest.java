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
class TypeSpecTest {
    @Test
    void simple() {
        Assertions.assertThat(
                new Tree(
                        token("typeName", ThoriumLexer.IDENTIFIER)
                ).serialize("typeSpec")
        ).isEqualTo("(typeSpec typeName)");
    }

    @Test
    void union() {
        Assertions.assertThat(
                new Tree(
                        token("typeA", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("typeB", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("typeC", ThoriumLexer.IDENTIFIER)
                ).serialize("typeSpec")
        ).isEqualTo("(typeSpec (typeSpecUnion typeA & typeB & typeC))");
    }

    @Test
    void intersection() {
        Assertions.assertThat(
                new Tree(
                        token("typeA", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("typeB", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("typeC", ThoriumLexer.IDENTIFIER)
                ).serialize("typeSpec")
        ).isEqualTo("(typeSpec (typeSpecIntersection typeA | typeB | typeC))");
    }

    @Test
    void innerUnion() {
        Assertions.assertThat(
                new Tree(
                        token("typeA", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("("),
                        token("typeB", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("typeC", ThoriumLexer.IDENTIFIER),
                        token(")")
                ).serialize("typeSpec")
        ).isEqualTo("(typeSpec (typeSpecIntersection typeA | ( (typeSpec (typeSpecUnion typeB & typeC)) )))");
    }

    @Test
    void innerIntersection() {
        Assertions.assertThat(
                new Tree(
                        token("typeA", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("("),
                        token("typeB", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("typeC", ThoriumLexer.IDENTIFIER),
                        token(")")
                ).serialize("typeSpec")
        ).isEqualTo("(typeSpec (typeSpecUnion typeA & ( (typeSpec (typeSpecIntersection typeB | typeC)) )))");
    }

    @Test
    void intersectionOfUnions() {
        Assertions.assertThat(
                new Tree(
                        token("("),
                        token("typeA", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("typeB", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token("|"),
                        token("("),
                        token("typeC", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("typeD", ThoriumLexer.IDENTIFIER),
                        token(")")
                ).serialize("typeSpec")
        ).isEqualTo("(typeSpec (typeSpecIntersection ( (typeSpec (typeSpecUnion typeA & typeB)) ) | ( (typeSpec (typeSpecUnion typeC & typeD)) )))");
    }

    @Test
    void unionOfIntersections() {
        Assertions.assertThat(
                new Tree(
                        token("("),
                        token("typeA", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("typeB", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token("&"),
                        new TokenStub("("),
                        new TokenStub("typeC", ThoriumLexer.IDENTIFIER),
                        new TokenStub("|"),
                        new TokenStub("typeD", ThoriumLexer.IDENTIFIER),
                        new TokenStub(")")
                ).serialize("typeSpec")
        ).isEqualTo("(typeSpec (typeSpecUnion ( (typeSpec (typeSpecIntersection typeA | typeB)) ) & ( (typeSpec (typeSpecIntersection typeC | typeD)) )))");
    }
}
