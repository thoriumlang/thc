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
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.antlr.ThoriumLexer;

import static org.thoriumlang.compiler.antlr.parser.TokenStub.token;

@SuppressWarnings("squid:S1192")
@Tag("parser")
class ClassDefTest {
    @Test
    void emptyClass() {
        Assertions.assertThat(
                new Tree(
                        token("class", ThoriumLexer.CLASS),
                        token("Identifier", ThoriumLexer.IDENTIFIER),
                        token("{"),
                        token("}")
                ).serialize("classDef")
        ).isEqualTo("(classDef class Identifier { })");
    }

    @Test
    void emptyClassImplements() {
        Assertions.assertThat(
                new Tree(
                        token("class", ThoriumLexer.CLASS),
                        token("Identifier", ThoriumLexer.IDENTIFIER),
                        token(":"),
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("{"),
                        token("}")
                ).serialize("classDef")
        ).isEqualTo("(classDef class Identifier " +
                "(implementsSpec : (typeSpec (typeSpecSimple (fqIdentifier TA)))) { })");
    }

    @Test
    void emptyClassParameterizedType() {
        Assertions.assertThat(
                new Tree(
                        token("class", ThoriumLexer.CLASS),
                        token("Identifier", ThoriumLexer.IDENTIFIER),
                        token("["),
                        token("TA", ThoriumLexer.IDENTIFIER),
                        token("]"),
                        token("{"),
                        token("}")
                ).serialize("classDef")
        ).isEqualTo("(classDef class Identifier [ (typeParameter TA) ] { })");
    }

    @Test
    void emptyClassPublic() {
        Assertions.assertThat(
                new Tree(
                        token("public", ThoriumLexer.PUBLIC),
                        token("class", ThoriumLexer.CLASS),
                        token("Identifier", ThoriumLexer.IDENTIFIER),
                        token("{"),
                        token("}")
                ).serialize("classDef")
        ).isEqualTo("(classDef public class Identifier { })");
    }

    @Test
    void nonEmptyClass() {
        Assertions.assertThat(
                new Tree(
                        token("public", ThoriumLexer.PUBLIC),
                        token("class", ThoriumLexer.CLASS),
                        token("Identifier", ThoriumLexer.IDENTIFIER),
                        token("{"),
                        token("val", ThoriumLexer.VAL),
                        token("Identifier", ThoriumLexer.IDENTIFIER),
                        token(":"),
                        token("T", ThoriumLexer.IDENTIFIER),
                        token(";"),
                        token("method", ThoriumLexer.IDENTIFIER),
                        token("("),
                        token(")"),
                        token("{"),
                        token("}"),
                        token("}")
                ).serialize("classDef")
        ).isEqualTo("(classDef public class Identifier { " +
                "(attributeDef val Identifier : (typeSpec (typeSpecSimple (fqIdentifier T))) ;) " +
                "(methodDef method ( ) { }) " +
                "})"
        );
    }
}
