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

class TypeSpecTest {
    @Test
    void single() {
        Assertions.assertThat(
                new Tree(
                        token("TA", ThoriumLexer.IDENTIFIER)
                ).serialize("typeSpec")
        )
                .contains("TA")
                .isEqualTo("(typeSpec TA)");
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
                .isEqualTo("(typeSpec (typeSpecOptional TA ?))");
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
                .isEqualTo("(typeSpec ( TA ))");
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
                .isEqualTo("(typeSpec ( (typeSpec ( (typeSpec ( TA )) )) ))");
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
                .isEqualTo("(typeSpec (typeSpecOptional ( TA ) ?))");
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
                .isEqualTo("(typeSpec (typeSpecOptional ( (typeSpec ( (typeSpec ( TA )) )) ) ?))");
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
                .isEqualTo("(typeSpec ( (typeSpecOptional ( TA ) ?) ))");
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
                .isEqualTo("(typeSpec ( (typeSpec ( (typeSpecOptional TA ?) )) ))");
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
                .isEqualTo("(typeSpec (typeSpecUnion TA & TB & TC))");
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
                .isEqualTo("(typeSpec (typeSpecOptional ( (typeSpecUnion TA & TB) ) ?))");
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
                .isEqualTo("(typeSpec (typeSpecUnion (typeSpecOptional TA ?) & TB))");
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
                .isEqualTo("(typeSpec (typeSpecUnion TA & (typeSpecOptional TB ?)))");
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
                .isEqualTo("(typeSpec (typeSpecUnion ( (typeSpecUnion TA & TB) ) & TC & TD))");
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
                .isEqualTo("(typeSpec (typeSpecUnion TA & ( (typeSpecUnion TB & TC) ) & TD))");
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
                .isEqualTo("(typeSpec (typeSpecUnion TA & TB & ( (typeSpecUnion TC & TD) )))");
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
                .isEqualTo("(typeSpec (typeSpecUnion ( TA ) & TB & TC))");
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
                .isEqualTo("(typeSpec (typeSpecUnion TA & ( TB ) & TC))");
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
                .isEqualTo("(typeSpec (typeSpecUnion TA & TB & ( TC )))");
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
                .isEqualTo("(typeSpec (typeSpecUnion ( (typeSpec ( TA )) ) & TB & TC))");
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
                .isEqualTo("(typeSpec (typeSpecUnion TA & ( (typeSpec ( TB )) ) & TC))");
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
                .isEqualTo("(typeSpec (typeSpecUnion TA & TB & ( (typeSpec ( TC )) )))");
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
                .isEqualTo("(typeSpec (typeSpecUnion ( (typeSpec ( (typeSpecUnion TA & TB) )) ) & TC & TD))");
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
                .isEqualTo("(typeSpec (typeSpecUnion TA & ( (typeSpec ( (typeSpecUnion TB & TC) )) ) & TD))");
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
                .isEqualTo("(typeSpec (typeSpecUnion TA & TB & ( (typeSpec ( (typeSpecUnion TC & TD) )) )))");
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
                .isEqualTo("(typeSpec (typeSpecUnion ( TA ) & " +
                        "( (typeSpec ( (typeSpecUnion TB & TC) )) ) & " +
                        "( (typeSpec ( (typeSpec ( (typeSpecUnion TD & TE) )) )) )))");
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
                .isEqualTo("(typeSpec ( (typeSpecUnion TA & TB) ))");
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
                .isEqualTo("(typeSpec (typeSpecUnion ( (typeSpecUnion TA & TB) ) & ( (typeSpecUnion TC & TD) )))");
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
                .isEqualTo("(typeSpec (typeSpecIntersection TA | TB | TC | TD))");
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
                .isEqualTo("(typeSpec (typeSpecOptional ( (typeSpecIntersection TA | TB) ) ?))");
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
                .isEqualTo("(typeSpec (typeSpecIntersection (typeSpecOptional TA ?) | TB))");
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
                .isEqualTo("(typeSpec (typeSpecIntersection TA | (typeSpecOptional TB ?)))");
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
                .isEqualTo("(typeSpec (typeSpecIntersection ( (typeSpecIntersection TA | TB) ) | TC | TD))");
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
                .isEqualTo("(typeSpec (typeSpecIntersection TA | ( (typeSpecIntersection TB | TC) ) | TD))");
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
                .isEqualTo("(typeSpec (typeSpecIntersection TA | TB | ( (typeSpecIntersection TC | TD) )))");
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
                .isEqualTo("(typeSpec (typeSpecIntersection ( TA ) | TB | TC))");
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
                .isEqualTo("(typeSpec (typeSpecIntersection TA | ( TB ) | TC))");
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
                .isEqualTo("(typeSpec (typeSpecIntersection TA | TB | ( TC )))");
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
                .isEqualTo("(typeSpec (typeSpecIntersection ( (typeSpec ( TA )) ) | TB | TC))");
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
                .isEqualTo("(typeSpec (typeSpecIntersection TA | ( (typeSpec ( TB )) ) | TC))");
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
                .isEqualTo("(typeSpec (typeSpecIntersection TA | TB | ( (typeSpec ( TC )) )))");
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
                .isEqualTo("(typeSpec (typeSpecIntersection ( (typeSpec ( (typeSpecIntersection TA | TB) )) ) | TC | TD))");
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
                .isEqualTo("(typeSpec (typeSpecIntersection TA | ( (typeSpec ( (typeSpecIntersection TB | TC) )) ) | TD))");
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
                .isEqualTo("(typeSpec (typeSpecIntersection TA | TB | ( (typeSpec ( (typeSpecIntersection TC | TD) )) )))");
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
                .isEqualTo("(typeSpec (typeSpecIntersection ( TA ) | " +
                        "( (typeSpec ( (typeSpecIntersection TB | TC) )) ) | " +
                        "( (typeSpec ( (typeSpec ( (typeSpecIntersection TD | TE) )) )) )))");
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
                .isEqualTo("(typeSpec ( (typeSpecIntersection TA | TB) ))");
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
                .isEqualTo("(typeSpec (typeSpecIntersection ( (typeSpecIntersection TA | TB) ) | ( (typeSpecIntersection TC | TD) )))");
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
                .isEqualTo("(typeSpec (typeSpecUnion ( (typeSpecIntersection TA | TB | TC) ) & ( (typeSpecIntersection TD | TE) )))");
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
                .isEqualTo("(typeSpec (typeSpecUnion TA & ( (typeSpecIntersection TB | TC) ) & ( (typeSpecIntersection TD | TE) )))");
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
                .isEqualTo("(typeSpec (typeSpecUnion ( (typeSpecIntersection TA | TB) ) & TC & ( (typeSpecIntersection TD | TE) )))");
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
                .isEqualTo("(typeSpec (typeSpecUnion ( (typeSpecIntersection TA | TB) ) & ( (typeSpecIntersection TC | TD) ) & TE))");
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
                .isEqualTo("(typeSpec (typeSpecIntersection ( (typeSpecUnion TA & TB & TC) ) | ( (typeSpecUnion TD & TE) )))");
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
                .isEqualTo("(typeSpec (typeSpecIntersection TA | ( (typeSpecUnion TB & TC) ) | ( (typeSpecUnion TD & TE) )))");
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
                .isEqualTo("(typeSpec (typeSpecIntersection ( (typeSpecUnion TA & TB) ) | TC | ( (typeSpecUnion TD & TE) )))");
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
                .isEqualTo("(typeSpec (typeSpecIntersection ( (typeSpecUnion TA & TB) ) | ( (typeSpecUnion TC & TD) ) | TE))");
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
                .isEqualTo("(typeSpec (typeSpecIntersection (typeSpecOptional ( (typeSpecIntersection ( (typeSpecUnion (typeSpecOptional TA ?) & TB) ) | TC) ) ?) | TD))");
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
                .isEqualTo("(typeSpec (typeSpecUnion (typeSpecOptional ( (typeSpecUnion ( (typeSpecIntersection (typeSpecOptional TA ?) | TB) ) & TC) ) ?) & TD))");
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
                .isEqualTo("(typeSpec (typeSpecUnion TA & TB & (typeSpecOptional ( (typeSpecOptional TC ?) ) ?) & ( (typeSpecUnion TD & TE) )))");
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
                .isEqualTo("(typeSpec (typeSpecIntersection TA | TB | (typeSpecOptional ( (typeSpecOptional TC ?) ) ?) | ( (typeSpecIntersection TD | TE) )))");
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
                .isEqualTo("(typeSpec (typeSpecUnion (typeSpecOptional ( (typeSpecOptional TA ?) ) ?) & TB & TC & ( (typeSpecUnion TD & TE) )))");
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
                .isEqualTo("(typeSpec (typeSpecIntersection (typeSpecOptional ( (typeSpecOptional TA ?) ) ?) | TB | TC | ( (typeSpecIntersection TD | TE) )))");
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
                .isEqualTo("(typeSpec (typeSpecIntersection (typeSpecOptional ( (typeSpecIntersection ( (typeSpecUnion (typeSpecOptional TA ?) & TB) ) | TC) ) ?) | ( TD )))");
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
                .isEqualTo("(typeSpec (typeSpecUnion (typeSpecOptional ( (typeSpecUnion ( (typeSpecIntersection (typeSpecOptional TA ?) | TB) ) & TC) ) ?) & ( TD )))");
    }
}
