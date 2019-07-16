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
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.antlr.ThoriumLexer;

import static org.thoriumlang.compiler.antlr.parser.TokenStub.token;

@SuppressWarnings("squid:S1192")
class TypeSpecTest {
    @Test
    void single() {
        Assertions.assertThat(
                new Tree(
                        token("TA", ThoriumLexer.IDENTIFIER)
                ).serialize("typeSpec")
        )
                .contains("TA")
                .isEqualTo("(typeSpec (fqIdentifier TA))");
    }

    @Test
    void singleOptional() {
        Assertions.assertThat(
                new Tree(
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("?")
                ).serialize("typeSpec")
        )
                .contains("TA")
                .isEqualTo("(typeSpec (typeSpecOptional (fqIdentifier TA) ?))");
    }

    @Test
    void group() {
        Assertions.assertThat(
                new Tree(
                        token("("),
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token(")")
                ).serialize("typeSpec")
        )
                .contains("TA")
                .isEqualTo("(typeSpec ( (fqIdentifier TA) ))");
    }

    @Test
    void nestedGroup() {
        Assertions.assertThat(
                new Tree(
                        token("("),
                        token("("),
                        token("("),
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token(")"),
                        token(")")
                ).serialize("typeSpec")
        )
                .contains("TA")
                .isEqualTo("(typeSpec ( (typeSpec ( (typeSpec ( (fqIdentifier TA) )) )) ))");
    }

    @Test
    void groupOptional() {
        Assertions.assertThat(
                new Tree(
                        token("("),
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token("?")
                ).serialize("typeSpec")
        )
                .contains("TA")
                .isEqualTo("(typeSpec (typeSpecOptional ( (fqIdentifier TA) ) ?))");
    }

    @Test
    void nestedGroupOptional() {
        Assertions.assertThat(
                new Tree(
                        token("("),
                        token("("),
                        token("("),
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token(")"),
                        token(")"),
                        token("?")
                ).serialize("typeSpec")
        )
                .contains("TA")
                .isEqualTo("(typeSpec (typeSpecOptional ( (typeSpec ( (typeSpec ( (fqIdentifier TA) )) )) ) ?))");
    }

    @Test
    void nestedOptional() {
        Assertions.assertThat(
                new Tree(
                        token("("),
                        token("("),
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token("?"),
                        token(")")
                ).serialize("typeSpec")
        )
                .contains("TA")
                .isEqualTo("(typeSpec ( (typeSpecOptional ( (fqIdentifier TA) ) ?) ))");
    }

    @Test
    void nestedNestedOptional() {
        Assertions.assertThat(
                new Tree(
                        token("("),
                        token("("),
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("?"),
                        token(")"),
                        token(")")
                ).serialize("typeSpec")
        )
                .contains("TA")
                .isEqualTo("(typeSpec ( (typeSpec ( (typeSpecOptional (fqIdentifier TA) ?) )) ))");
    }

    @Test
    void union() {
        Assertions.assertThat(
                new Tree(
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("TC", ThoriumLexer.IDENTIFIER)
                ).serialize("typeSpec")
        )
                .contains("TA", "TB", "TC")
                .isEqualTo("(typeSpec (typeSpecUnion (fqIdentifier TA) & (fqIdentifier TB) & (fqIdentifier TC)))");
    }

    @Test
    void unionOptional1() {
        Assertions.assertThat(
                new Tree(
                        token("("),
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token("?")
                ).serialize("typeSpec")
        )
                .contains("TA", "TB")
                .isEqualTo("(typeSpec (typeSpecOptional ( (typeSpecUnion (fqIdentifier TA) & (fqIdentifier TB)) ) ?))");
    }

    @Test
    void unionOptional2() {
        Assertions.assertThat(
                new Tree(
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("?"),
                        token("&"),
                        token("TB", ThoriumLexer.IDENTIFIER)
                ).serialize("typeSpec")
        )
                .contains("TA", "TB")
                .isEqualTo("(typeSpec (typeSpecUnion (typeSpecOptional (fqIdentifier TA) ?) & (fqIdentifier TB)))");
    }

    @Test
    void unionOptional3() {
        Assertions.assertThat(
                new Tree(
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token("?")
                ).serialize("typeSpec")
        )
                .contains("TA", "TB")
                .isEqualTo("(typeSpec (typeSpecUnion (fqIdentifier TA) & (typeSpecOptional (fqIdentifier TB) ?)))");
    }

    @Test
    void unionGroup1() {
        Assertions.assertThat(
                new Tree(
                        token("("),
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token("&"),
                        token("TC", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("TD", ThoriumLexer.IDENTIFIER)
                ).serialize("typeSpec")
        )
                .contains("TA", "TB", "TC", "TD")
                .isEqualTo("(typeSpec (typeSpecUnion ( (typeSpecUnion (fqIdentifier TA) & (fqIdentifier TB)) ) & (fqIdentifier TC) & (fqIdentifier TD)))");
    }

    @Test
    void unionGroup2() {
        Assertions.assertThat(
                new Tree(
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("("),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("TC", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token("&"),
                        token("TD", ThoriumLexer.IDENTIFIER)
                ).serialize("typeSpec")
        )
                .contains("TA", "TB", "TC", "TD")
                .isEqualTo("(typeSpec (typeSpecUnion (fqIdentifier TA) & ( (typeSpecUnion (fqIdentifier TB) & (fqIdentifier TC)) ) & (fqIdentifier TD)))");
    }

    @Test
    void unionGroup3() {
        Assertions.assertThat(
                new Tree(
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("("),
                        token("TC", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("TD", ThoriumLexer.IDENTIFIER),
                        token(")")
                ).serialize("typeSpec")
        )
                .contains("TA", "TB", "TC", "TD")
                .isEqualTo("(typeSpec (typeSpecUnion (fqIdentifier TA) & (fqIdentifier TB) & ( (typeSpecUnion (fqIdentifier TC) & (fqIdentifier TD)) )))");
    }

    @Test
    void unionGroup4() {
        Assertions.assertThat(
                new Tree(
                        token("("),
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token("&"),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("TC", ThoriumLexer.IDENTIFIER)
                ).serialize("typeSpec")
        )
                .contains("TA", "TB", "TC")
                .isEqualTo("(typeSpec (typeSpecUnion ( (fqIdentifier TA) ) & (fqIdentifier TB) & (fqIdentifier TC)))");
    }

    @Test
    void unionGroup5() {
        Assertions.assertThat(
                new Tree(
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("("),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token("&"),
                        token("TC", ThoriumLexer.IDENTIFIER)
                ).serialize("typeSpec")
        )
                .contains("TA", "TB", "TC")
                .isEqualTo("(typeSpec (typeSpecUnion (fqIdentifier TA) & ( (fqIdentifier TB) ) & (fqIdentifier TC)))");
    }

    @Test
    void unionGroup6() {
        Assertions.assertThat(
                new Tree(
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("("),
                        token("TC", ThoriumLexer.IDENTIFIER),
                        token(")")
                ).serialize("typeSpec")
        )
                .contains("TA", "TB", "TC")
                .isEqualTo("(typeSpec (typeSpecUnion (fqIdentifier TA) & (fqIdentifier TB) & ( (fqIdentifier TC) )))");
    }

    @Test
    void unionGroup7() {
        Assertions.assertThat(
                new Tree(
                        token("("),
                        token("("),
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token(")"),
                        token("&"),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("TC", ThoriumLexer.IDENTIFIER)
                ).serialize("typeSpec")
        )
                .contains("TA", "TB", "TC")
                .isEqualTo("(typeSpec (typeSpecUnion ( (typeSpec ( (fqIdentifier TA) )) ) & (fqIdentifier TB) & (fqIdentifier TC)))");
    }

    @Test
    void unionGroup8() {
        Assertions.assertThat(
                new Tree(
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("("),
                        token("("),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token(")"),
                        token("&"),
                        token("TC", ThoriumLexer.IDENTIFIER)
                ).serialize("typeSpec")
        )
                .contains("TA", "TB", "TC")
                .isEqualTo("(typeSpec (typeSpecUnion (fqIdentifier TA) & ( (typeSpec ( (fqIdentifier TB) )) ) & (fqIdentifier TC)))");
    }

    @Test
    void unionGroup9() {
        Assertions.assertThat(
                new Tree(
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("("),
                        token("("),
                        token("TC", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token(")")
                ).serialize("typeSpec")
        )
                .contains("TA", "TB", "TC")
                .isEqualTo("(typeSpec (typeSpecUnion (fqIdentifier TA) & (fqIdentifier TB) & ( (typeSpec ( (fqIdentifier TC) )) )))");
    }

    @Test
    void unionGroup10() {
        Assertions.assertThat(
                new Tree(
                        token("("),
                        token("("),
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token(")"),
                        token("&"),
                        token("TC", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("TD", ThoriumLexer.IDENTIFIER)
                ).serialize("typeSpec")
        )
                .contains("TA", "TB", "TC", "TD")
                .isEqualTo("(typeSpec (typeSpecUnion ( (typeSpec ( (typeSpecUnion (fqIdentifier TA) & (fqIdentifier TB)) )) ) & (fqIdentifier TC) & (fqIdentifier TD)))");
    }

    @Test
    void unionGroup11() {
        Assertions.assertThat(
                new Tree(
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("("),
                        token("("),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("TC", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token(")"),
                        token("&"),
                        token("TD", ThoriumLexer.IDENTIFIER)
                ).serialize("typeSpec")
        )
                .contains("TA", "TB", "TC", "TD")
                .isEqualTo("(typeSpec (typeSpecUnion (fqIdentifier TA) & ( (typeSpec ( (typeSpecUnion (fqIdentifier TB) & (fqIdentifier TC)) )) ) & (fqIdentifier TD)))");
    }

    @Test
    void unionGroup12() {
        Assertions.assertThat(
                new Tree(
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("("),
                        token("("),
                        token("TC", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("TD", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token(")")
                ).serialize("typeSpec")
        )
                .contains("TA", "TB", "TC", "TD")
                .isEqualTo("(typeSpec (typeSpecUnion (fqIdentifier TA) & (fqIdentifier TB) & ( (typeSpec ( (typeSpecUnion (fqIdentifier TC) & (fqIdentifier TD)) )) )))");
    }

    @Test
    void unionGroup13() {
        Assertions.assertThat(
                new Tree(
                        token("("),
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token("&"),
                        token("("),
                        token("("),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("TC", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token(")"),
                        token("&"),
                        token("("),
                        token("("),
                        token("("),
                        token("TD", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("TE", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token(")"),
                        token(")")
                ).serialize("typeSpec")
        )
                .contains("TA", "TB", "TC", "TD", "TE")
                .isEqualTo("(typeSpec (typeSpecUnion ( (fqIdentifier TA) ) & " +
                        "( (typeSpec ( (typeSpecUnion (fqIdentifier TB) & (fqIdentifier TC)) )) ) & " +
                        "( (typeSpec ( (typeSpec ( (typeSpecUnion (fqIdentifier TD) & (fqIdentifier TE)) )) )) )))");
    }

    @Test
    void unionGroup14() {
        Assertions.assertThat(
                new Tree(
                        token("("),
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token(")")
                ).serialize("typeSpec")
        )
                .contains("TA", "TB")
                .isEqualTo("(typeSpec ( (typeSpecUnion (fqIdentifier TA) & (fqIdentifier TB)) ))");
    }

    @Test
    void unionGroup15() {
        Assertions.assertThat(
                new Tree(
                        token("("),
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token("&"),
                        token("("),
                        token("TC", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("TD", ThoriumLexer.IDENTIFIER),
                        token(")")
                ).serialize("typeSpec")
        )
                .contains("TA", "TB", "TC", "TD")
                .isEqualTo("(typeSpec (typeSpecUnion ( (typeSpecUnion (fqIdentifier TA) & (fqIdentifier TB)) ) & ( (typeSpecUnion (fqIdentifier TC) & (fqIdentifier TD)) )))");
    }

    @Test
    void intersection() {
        Assertions.assertThat(
                new Tree(
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("TC", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("TD", ThoriumLexer.IDENTIFIER)
                ).serialize("typeSpec")
        )
                .contains("TA", "TB", "TC", "TD")
                .isEqualTo("(typeSpec (typeSpecIntersection (fqIdentifier TA) | (fqIdentifier TB) | (fqIdentifier TC) | (fqIdentifier TD)))");
    }

    @Test
    void intersectionOptional1() {
        Assertions.assertThat(
                new Tree(
                        token("("),
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token("?")
                ).serialize("typeSpec")
        )
                .contains("TA", "TB")
                .isEqualTo("(typeSpec (typeSpecOptional ( (typeSpecIntersection (fqIdentifier TA) | (fqIdentifier TB)) ) ?))");
    }

    @Test
    void intersectionOptional2() {
        Assertions.assertThat(
                new Tree(
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("?"),
                        token("|"),
                        token("TB", ThoriumLexer.IDENTIFIER)
                ).serialize("typeSpec")
        )
                .contains("TA", "TB")
                .isEqualTo("(typeSpec (typeSpecIntersection (typeSpecOptional (fqIdentifier TA) ?) | (fqIdentifier TB)))");
    }

    @Test
    void intersectionOptional3() {
        Assertions.assertThat(
                new Tree(
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token("?")
                ).serialize("typeSpec")
        )
                .contains("TA", "TB")
                .isEqualTo("(typeSpec (typeSpecIntersection (fqIdentifier TA) | (typeSpecOptional (fqIdentifier TB) ?)))");
    }

    @Test
    void intersectionGroup1() {
        Assertions.assertThat(
                new Tree(
                        token("("),
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token("|"),
                        token("TC", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("TD", ThoriumLexer.IDENTIFIER)
                ).serialize("typeSpec")
        )
                .contains("TA", "TB", "TC", "TD")
                .isEqualTo("(typeSpec (typeSpecIntersection ( (typeSpecIntersection (fqIdentifier TA) | (fqIdentifier TB)) ) | (fqIdentifier TC) | (fqIdentifier TD)))");
    }

    @Test
    void intersectionGroup2() {
        Assertions.assertThat(
                new Tree(
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("("),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("TC", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token("|"),
                        token("TD", ThoriumLexer.IDENTIFIER)
                ).serialize("typeSpec")
        )
                .contains("TA", "TB", "TC", "TD")
                .isEqualTo("(typeSpec (typeSpecIntersection (fqIdentifier TA) | ( (typeSpecIntersection (fqIdentifier TB) | (fqIdentifier TC)) ) | (fqIdentifier TD)))");
    }

    @Test
    void intersectionGroup3() {
        Assertions.assertThat(
                new Tree(
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("("),
                        token("TC", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("TD", ThoriumLexer.IDENTIFIER),
                        token(")")
                ).serialize("typeSpec")
        )
                .contains("TA", "TB", "TC", "TD")
                .isEqualTo("(typeSpec (typeSpecIntersection (fqIdentifier TA) | (fqIdentifier TB) | ( (typeSpecIntersection (fqIdentifier TC) | (fqIdentifier TD)) )))");
    }

    @Test
    void intersectionGroup4() {
        Assertions.assertThat(
                new Tree(
                        token("("),
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token("|"),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("TC", ThoriumLexer.IDENTIFIER)
                ).serialize("typeSpec")
        )
                .contains("TA", "TB", "TC")
                .isEqualTo("(typeSpec (typeSpecIntersection ( (fqIdentifier TA) ) | (fqIdentifier TB) | (fqIdentifier TC)))");
    }

    @Test
    void intersectionGroup5() {
        Assertions.assertThat(
                new Tree(
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("("),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token("|"),
                        token("TC", ThoriumLexer.IDENTIFIER)
                ).serialize("typeSpec")
        )
                .contains("TA", "TB", "TC")
                .isEqualTo("(typeSpec (typeSpecIntersection (fqIdentifier TA) | ( (fqIdentifier TB) ) | (fqIdentifier TC)))");
    }

    @Test
    void intersectionGroup6() {
        Assertions.assertThat(
                new Tree(
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("("),
                        token("TC", ThoriumLexer.IDENTIFIER),
                        token(")")
                ).serialize("typeSpec")
        )
                .contains("TA", "TB", "TC")
                .isEqualTo("(typeSpec (typeSpecIntersection (fqIdentifier TA) | (fqIdentifier TB) | ( (fqIdentifier TC) )))");
    }

    @Test
    void intersectionGroup7() {
        Assertions.assertThat(
                new Tree(
                        token("("),
                        token("("),
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token(")"),
                        token("|"),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("TC", ThoriumLexer.IDENTIFIER)
                ).serialize("typeSpec")
        )
                .contains("TA", "TB", "TC")
                .isEqualTo("(typeSpec (typeSpecIntersection ( (typeSpec ( (fqIdentifier TA) )) ) | (fqIdentifier TB) | (fqIdentifier TC)))");
    }

    @Test
    void intersectionGroup8() {
        Assertions.assertThat(
                new Tree(
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("("),
                        token("("),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token(")"),
                        token("|"),
                        token("TC", ThoriumLexer.IDENTIFIER)
                ).serialize("typeSpec")
        )
                .contains("TA", "TB", "TC")
                .isEqualTo("(typeSpec (typeSpecIntersection (fqIdentifier TA) | ( (typeSpec ( (fqIdentifier TB) )) ) | (fqIdentifier TC)))");
    }

    @Test
    void intersectionGroup9() {
        Assertions.assertThat(
                new Tree(
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("("),
                        token("("),
                        token("TC", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token(")")
                ).serialize("typeSpec")
        )
                .contains("TA", "TB", "TC")
                .isEqualTo("(typeSpec (typeSpecIntersection (fqIdentifier TA) | (fqIdentifier TB) | ( (typeSpec ( (fqIdentifier TC) )) )))");
    }

    @Test
    void intersectionGroup10() {
        Assertions.assertThat(
                new Tree(
                        token("("),
                        token("("),
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token(")"),
                        token("|"),
                        token("TC", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("TD", ThoriumLexer.IDENTIFIER)
                ).serialize("typeSpec")
        )
                .contains("TA", "TB", "TC", "TD")
                .isEqualTo("(typeSpec (typeSpecIntersection ( (typeSpec ( (typeSpecIntersection (fqIdentifier TA) | (fqIdentifier TB)) )) ) | (fqIdentifier TC) | (fqIdentifier TD)))");
    }

    @Test
    void intersectionGroup11() {
        Assertions.assertThat(
                new Tree(
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("("),
                        token("("),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("TC", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token(")"),
                        token("|"),
                        token("TD", ThoriumLexer.IDENTIFIER)
                ).serialize("typeSpec")
        )
                .contains("TA", "TB", "TC", "TD")
                .isEqualTo("(typeSpec (typeSpecIntersection (fqIdentifier TA) | ( (typeSpec ( (typeSpecIntersection (fqIdentifier TB) | (fqIdentifier TC)) )) ) | (fqIdentifier TD)))");
    }

    @Test
    void intersectionGroup12() {
        Assertions.assertThat(
                new Tree(
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("("),
                        token("("),
                        token("TC", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("TD", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token(")")
                ).serialize("typeSpec")
        )
                .contains("TA", "TB", "TC", "TD")
                .isEqualTo("(typeSpec (typeSpecIntersection (fqIdentifier TA) | (fqIdentifier TB) | ( (typeSpec ( (typeSpecIntersection (fqIdentifier TC) | (fqIdentifier TD)) )) )))");
    }

    @Test
    void intersectionGroup13() {
        Assertions.assertThat(
                new Tree(
                        token("("),
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token("|"),
                        token("("),
                        token("("),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("TC", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token(")"),
                        token("|"),
                        token("("),
                        token("("),
                        token("("),
                        token("TD", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("TE", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token(")"),
                        token(")")
                ).serialize("typeSpec")
        )
                .contains("TA", "TB", "TC", "TD", "TE")
                .isEqualTo("(typeSpec (typeSpecIntersection ( (fqIdentifier TA) ) | " +
                        "( (typeSpec ( (typeSpecIntersection (fqIdentifier TB) | (fqIdentifier TC)) )) ) | " +
                        "( (typeSpec ( (typeSpec ( (typeSpecIntersection (fqIdentifier TD) | (fqIdentifier TE)) )) )) )))");
    }

    @Test
    void intersectionGroup14() {
        Assertions.assertThat(
                new Tree(
                        token("("),
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token(")")
                ).serialize("typeSpec")
        )
                .contains("TA", "TB")
                .isEqualTo("(typeSpec ( (typeSpecIntersection (fqIdentifier TA) | (fqIdentifier TB)) ))");
    }

    @Test
    void intersectionGroup15() {
        Assertions.assertThat(
                new Tree(
                        token("("),
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token("|"),
                        token("("),
                        token("TC", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("TD", ThoriumLexer.IDENTIFIER),
                        token(")")
                ).serialize("typeSpec")
        )
                .contains("TA", "TB", "TC", "TD")
                .isEqualTo("(typeSpec (typeSpecIntersection ( (typeSpecIntersection (fqIdentifier TA) | (fqIdentifier TB)) ) | ( (typeSpecIntersection (fqIdentifier TC) | (fqIdentifier TD)) )))");
    }

    @Test
    void unionOfIntersections1() {
        Assertions.assertThat(
                new Tree(
                        token("("),
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("TC", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token("&"),
                        token("("),
                        token("TD", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("TE", ThoriumLexer.IDENTIFIER),
                        token(")")
                ).serialize("typeSpec")
        )
                .contains("TA", "TB", "TC", "TD", "TE")
                .isEqualTo("(typeSpec (typeSpecUnion ( (typeSpecIntersection (fqIdentifier TA) | (fqIdentifier TB) | (fqIdentifier TC)) ) & ( (typeSpecIntersection (fqIdentifier TD) | (fqIdentifier TE)) )))");
    }

    @Test
    void unionOfIntersections2() {
        Assertions.assertThat(
                new Tree(
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("("),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("TC", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token("&"),
                        token("("),
                        token("TD", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("TE", ThoriumLexer.IDENTIFIER),
                        token(")")
                ).serialize("typeSpec")
        )
                .contains("TA", "TB", "TC", "TD", "TE")
                .isEqualTo("(typeSpec (typeSpecUnion (fqIdentifier TA) & ( (typeSpecIntersection (fqIdentifier TB) | (fqIdentifier TC)) ) & ( (typeSpecIntersection (fqIdentifier TD) | (fqIdentifier TE)) )))");
    }

    @Test
    void unionOfIntersections3() {
        Assertions.assertThat(
                new Tree(
                        token("("),
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token("&"),
                        token("TC", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("("),
                        token("TD", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("TE", ThoriumLexer.IDENTIFIER),
                        token(")")
                ).serialize("typeSpec")
        )
                .contains("TA", "TB", "TC", "TD", "TE")
                .isEqualTo("(typeSpec (typeSpecUnion ( (typeSpecIntersection (fqIdentifier TA) | (fqIdentifier TB)) ) & (fqIdentifier TC) & ( (typeSpecIntersection (fqIdentifier TD) | (fqIdentifier TE)) )))");
    }

    @Test
    void unionOfIntersections4() {
        Assertions.assertThat(
                new Tree(
                        token("("),
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token("&"),
                        token("("),
                        token("TC", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("TD", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token("&"),
                        token("TE", ThoriumLexer.IDENTIFIER)
                ).serialize("typeSpec")
        )
                .contains("TA", "TB", "TC", "TD", "TE")
                .isEqualTo("(typeSpec (typeSpecUnion ( (typeSpecIntersection (fqIdentifier TA) | (fqIdentifier TB)) ) & ( (typeSpecIntersection (fqIdentifier TC) | (fqIdentifier TD)) ) & (fqIdentifier TE)))");
    }

    @Test
    void intersectionOfUnions1() {
        Assertions.assertThat(
                new Tree(
                        token("("),
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("TC", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token("|"),
                        token("("),
                        token("TD", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("TE", ThoriumLexer.IDENTIFIER),
                        token(")")
                ).serialize("typeSpec")
        )
                .contains("TA", "TB", "TC", "TD", "TE")
                .isEqualTo("(typeSpec (typeSpecIntersection ( (typeSpecUnion (fqIdentifier TA) & (fqIdentifier TB) & (fqIdentifier TC)) ) | ( (typeSpecUnion (fqIdentifier TD) & (fqIdentifier TE)) )))");
    }

    @Test
    void intersectionOfUnions2() {
        Assertions.assertThat(
                new Tree(
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("("),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("TC", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token("|"),
                        token("("),
                        token("TD", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("TE", ThoriumLexer.IDENTIFIER),
                        token(")")
                ).serialize("typeSpec")
        )
                .contains("TA", "TB", "TC", "TD", "TE")
                .isEqualTo("(typeSpec (typeSpecIntersection (fqIdentifier TA) | ( (typeSpecUnion (fqIdentifier TB) & (fqIdentifier TC)) ) | ( (typeSpecUnion (fqIdentifier TD) & (fqIdentifier TE)) )))");
    }

    @Test
    void intersectionOfUnions3() {
        Assertions.assertThat(
                new Tree(
                        token("("),
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token("|"),
                        token("TC", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("("),
                        token("TD", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("TE", ThoriumLexer.IDENTIFIER),
                        token(")")
                ).serialize("typeSpec")
        )
                .contains("TA", "TB", "TC", "TD", "TE")
                .isEqualTo("(typeSpec (typeSpecIntersection ( (typeSpecUnion (fqIdentifier TA) & (fqIdentifier TB)) ) | (fqIdentifier TC) | ( (typeSpecUnion (fqIdentifier TD) & (fqIdentifier TE)) )))");
    }

    @Test
    void intersectionOfUnions4() {
        Assertions.assertThat(
                new Tree(
                        token("("),
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token("|"),
                        token("("),
                        token("TC", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("TD", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token("|"),
                        token("TE", ThoriumLexer.IDENTIFIER)
                ).serialize("typeSpec")
        )
                .contains("TA", "TB", "TC", "TD", "TE")
                .isEqualTo("(typeSpec (typeSpecIntersection ( (typeSpecUnion (fqIdentifier TA) & (fqIdentifier TB)) ) | ( (typeSpecUnion (fqIdentifier TC) & (fqIdentifier TD)) ) | (fqIdentifier TE)))");
    }

    @Test
    void complex1a() {
        Assertions.assertThat(
                new Tree(
                        token("("),
                        token("("),
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("?"),
                        token("&"),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token("|"),
                        token("TC", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token("?"),
                        token("|"),
                        token("TD", ThoriumLexer.IDENTIFIER)
                ).serialize("typeSpec")
        )
                .contains("TA", "TB", "TC", "TD")
                .isEqualTo("(typeSpec (typeSpecIntersection (typeSpecOptional ( (typeSpecIntersection ( (typeSpecUnion (typeSpecOptional (fqIdentifier TA) ?) & (fqIdentifier TB)) ) | (fqIdentifier TC)) ) ?) | (fqIdentifier TD)))");
    }

    @Test
    void complex1b() {
        Assertions.assertThat(
                new Tree(
                        token("("),
                        token("("),
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("?"),
                        token("|"),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token("&"),
                        token("TC", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token("?"),
                        token("&"),
                        token("TD", ThoriumLexer.IDENTIFIER)
                ).serialize("typeSpec")
        )
                .contains("TA", "TB", "TC", "TD")
                .isEqualTo("(typeSpec (typeSpecUnion (typeSpecOptional ( (typeSpecUnion ( (typeSpecIntersection (typeSpecOptional (fqIdentifier TA) ?) | (fqIdentifier TB)) ) & (fqIdentifier TC)) ) ?) & (fqIdentifier TD)))");
    }

    @Test
    void complex2a() {
        Assertions.assertThat(
                new Tree(
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("("),
                        token("TC", ThoriumLexer.IDENTIFIER),
                        token("?"),
                        token(")"),
                        token("?"),
                        token("&"),
                        token("("),
                        token("TD", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("TE", ThoriumLexer.IDENTIFIER),
                        token(")")
                ).serialize("typeSpec")
        )
                .contains("TA", "TB", "TC", "TD", "TE")
                .isEqualTo("(typeSpec (typeSpecUnion (fqIdentifier TA) & (fqIdentifier TB) & (typeSpecOptional ( (typeSpecOptional (fqIdentifier TC) ?) ) ?) & ( (typeSpecUnion (fqIdentifier TD) & (fqIdentifier TE)) )))");
    }

    @Test
    void complex2b() {
        Assertions.assertThat(
                new Tree(
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("("),
                        token("TC", ThoriumLexer.IDENTIFIER),
                        token("?"),
                        token(")"),
                        token("?"),
                        token("|"),
                        token("("),
                        token("TD", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("TE", ThoriumLexer.IDENTIFIER),
                        token(")")
                ).serialize("typeSpec")
        )
                .contains("TA", "TB", "TC", "TD", "TE")
                .isEqualTo("(typeSpec (typeSpecIntersection (fqIdentifier TA) | (fqIdentifier TB) | (typeSpecOptional ( (typeSpecOptional (fqIdentifier TC) ?) ) ?) | ( (typeSpecIntersection (fqIdentifier TD) | (fqIdentifier TE)) )))");
    }

    @Test
    void complex3a() {
        Assertions.assertThat(
                new Tree(
                        token("("),
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("?"),
                        token(")"),
                        token("?"),
                        token("&"),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("TC", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("("),
                        token("TD", ThoriumLexer.IDENTIFIER),
                        token("&"),
                        token("TE", ThoriumLexer.IDENTIFIER),
                        token(")")
                ).serialize("typeSpec")
        )
                .contains("TA", "TB", "TC", "TD", "TE")
                .isEqualTo("(typeSpec (typeSpecUnion (typeSpecOptional ( (typeSpecOptional (fqIdentifier TA) ?) ) ?) & (fqIdentifier TB) & (fqIdentifier TC) & ( (typeSpecUnion (fqIdentifier TD) & (fqIdentifier TE)) )))");
    }

    @Test
    void complex3b() {
        Assertions.assertThat(
                new Tree(
                        token("("),
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("?"),
                        token(")"),
                        token("?"),
                        token("|"),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("TC", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("("),
                        token("TD", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("TE", ThoriumLexer.IDENTIFIER),
                        token(")")
                ).serialize("typeSpec")
        )
                .contains("TA", "TB", "TC", "TD", "TE")
                .isEqualTo("(typeSpec (typeSpecIntersection (typeSpecOptional ( (typeSpecOptional (fqIdentifier TA) ?) ) ?) | (fqIdentifier TB) | (fqIdentifier TC) | ( (typeSpecIntersection (fqIdentifier TD) | (fqIdentifier TE)) )))");
    }

    @Test
    void complex4a() {
        Assertions.assertThat(
                new Tree(
                        token("("),
                        token("("),
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("?"),
                        token("&"),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token("|"),
                        token("TC", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token("?"),
                        token("|"),
                        token("("),
                        token("TD", ThoriumLexer.IDENTIFIER),
                        token(")")
                ).serialize("typeSpec")
        )
                .contains("TA", "TB", "TC", "TD")
                .isEqualTo("(typeSpec (typeSpecIntersection (typeSpecOptional ( (typeSpecIntersection ( (typeSpecUnion (typeSpecOptional (fqIdentifier TA) ?) & (fqIdentifier TB)) ) | (fqIdentifier TC)) ) ?) | ( (fqIdentifier TD) )))");
    }

    @Test
    void complex4b() {
        Assertions.assertThat(
                new Tree(
                        token("("),
                        token("("),
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("?"),
                        token("|"),
                        token("TB", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token("&"),
                        token("TC", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token("?"),
                        token("&"),
                        token("("),
                        token("TD", ThoriumLexer.IDENTIFIER),
                        token(")")
                ).serialize("typeSpec")
        )
                .contains("TA", "TB", "TC", "TD")
                .isEqualTo("(typeSpec (typeSpecUnion (typeSpecOptional ( (typeSpecUnion ( (typeSpecIntersection (typeSpecOptional (fqIdentifier TA) ?) | (fqIdentifier TB)) ) & (fqIdentifier TC)) ) ?) & ( (fqIdentifier TD) )))");
    }
}
