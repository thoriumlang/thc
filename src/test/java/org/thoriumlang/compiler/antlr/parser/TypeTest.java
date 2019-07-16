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
class TypeTest {
    @Test
    void emptyType() {
        Assertions.assertThat(
                new Tree(
                        token("type", ThoriumLexer.TYPE),
                        token("Identifier", ThoriumLexer.IDENTIFIER),
                        token("{"),
                        token("}")
                ).serialize()
        ).isEqualTo("(root (typeDef type Identifier { }))");
    }

    @Test
    void nonEmptyType() {
        Assertions.assertThat(
                new Tree(
                        token("type", ThoriumLexer.TYPE),
                        token("Identifier", ThoriumLexer.IDENTIFIER),
                        token("{"),
                        token("public", ThoriumLexer.PUBLIC),
                        token("fibonacci", ThoriumLexer.IDENTIFIER),
                        token("("),
                        token("n", ThoriumLexer.IDENTIFIER),
                        token(":"),
                        token("Integer", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token(":"),
                        token("Integer", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("None", ThoriumLexer.IDENTIFIER),
                        token(";"),
                        token("public", ThoriumLexer.PUBLIC),
                        token("square", ThoriumLexer.IDENTIFIER),
                        token("("),
                        token("n", ThoriumLexer.IDENTIFIER),
                        token(":"),
                        token("Integer", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token(":"),
                        token("Integer", ThoriumLexer.IDENTIFIER),
                        token("|"),
                        token("None", ThoriumLexer.IDENTIFIER),
                        token(";"),
                        token("}")
                ).serialize()
        ).isEqualTo("(root (typeDef type Identifier { " +
                "(methodSignature public fibonacci ( (methodParameterDef n : (typeSpec (fqIdentifier Integer))) ) : (typeSpec (typeSpecIntersection (fqIdentifier Integer) | (fqIdentifier None)))) ; " +
                "(methodSignature public square ( (methodParameterDef n : (typeSpec (fqIdentifier Integer))) ) : (typeSpec (typeSpecIntersection (fqIdentifier Integer) | (fqIdentifier None)))) ; " +
                "}))"
        );
    }
}
