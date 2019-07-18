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
class MethodSignatureTest {
    @Test
    void defaultMethodSignature() {
        Assertions.assertThat(
                new Tree(
                        token("methodName", ThoriumLexer.IDENTIFIER),
                        token("("),
                        token(")"),
                        token(":"),
                        token("typeName", ThoriumLexer.IDENTIFIER)
                ).serialize("methodSignature")
        ).isEqualTo("(methodSignature methodName ( ) : (typeSpec (fqIdentifier typeName)))");
    }

    @Test
    void privateMethodSignature() {
        Assertions.assertThat(
                new Tree(
                        token("private", ThoriumLexer.PRIVATE),
                        token("methodName", ThoriumLexer.IDENTIFIER),
                        token("("),
                        token(")"),
                        token(":"),
                        token("typeName", ThoriumLexer.IDENTIFIER)
                ).serialize("methodSignature")
        ).isEqualTo("(methodSignature private methodName ( ) : (typeSpec (fqIdentifier typeName)))");
    }

    @Test
    void namespaceMethodSignature() {
        Assertions.assertThat(
                new Tree(
                        token("namespace", ThoriumLexer.NAMESPACE),
                        token("methodName", ThoriumLexer.IDENTIFIER),
                        token("("),
                        token(")"),
                        token(":"),
                        token("typeName", ThoriumLexer.IDENTIFIER)
                ).serialize("methodSignature")
        ).isEqualTo("(methodSignature namespace methodName ( ) : (typeSpec (fqIdentifier typeName)))");
    }

    @Test
    void publicMethodSignature() {
        Assertions.assertThat(
                new Tree(
                        token("public", ThoriumLexer.PUBLIC),
                        token("methodName", ThoriumLexer.IDENTIFIER),
                        token("("),
                        token(")"),
                        token(":"),
                        token("typeName", ThoriumLexer.IDENTIFIER)
                ).serialize("methodSignature")
        ).isEqualTo("(methodSignature public methodName ( ) : (typeSpec (fqIdentifier typeName)))");
    }

    @Test
    void methodSignatureWithParameter() {
        Assertions.assertThat(
                new Tree(
                        token("public", ThoriumLexer.PUBLIC),
                        token("methodName", ThoriumLexer.IDENTIFIER),
                        token("("),
                        token("id", ThoriumLexer.IDENTIFIER),
                        token(":"),
                        token("type", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token(":"),
                        token("typeName", ThoriumLexer.IDENTIFIER)
                ).serialize("methodSignature")
        ).isEqualTo(
                "(methodSignature public methodName ( (methodParameterDef id : (typeSpec (fqIdentifier type))) ) : (typeSpec (fqIdentifier typeName)))");
    }

    @Test
    void methodSignatureWithParameters() {
        Assertions.assertThat(
                new Tree(
                        token("public", ThoriumLexer.PUBLIC),
                        token("methodName", ThoriumLexer.IDENTIFIER),
                        token("("),
                        token("id1", ThoriumLexer.IDENTIFIER),
                        token(":"),
                        token("type1", ThoriumLexer.IDENTIFIER),
                        token(","),
                        token("id2", ThoriumLexer.IDENTIFIER),
                        token(":"),
                        token("type2", ThoriumLexer.IDENTIFIER),
                        token(","),
                        token("id3", ThoriumLexer.IDENTIFIER),
                        token(":"),
                        token("type3", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token(":"),
                        token("typeName", ThoriumLexer.IDENTIFIER)
                ).serialize("methodSignature")
        ).isEqualTo("(methodSignature public methodName ( " +
                "(methodParameterDef id1 : (typeSpec (fqIdentifier type1))) , " +
                "(methodParameterDef id2 : (typeSpec (fqIdentifier type2))) , " +
                "(methodParameterDef id3 : (typeSpec (fqIdentifier type3))) " +
                ") : (typeSpec (fqIdentifier typeName)))");
    }

    @Test
    void methodSignatureWithTypeParameter() {
        Assertions.assertThat(
                new Tree(
                        token("public", ThoriumLexer.PUBLIC),
                        token("methodName", ThoriumLexer.IDENTIFIER),
                        token("["),
                        token("T", ThoriumLexer.IDENTIFIER),
                        token("]"),
                        token("("),
                        token(")"),
                        token(":"),
                        token("typeName", ThoriumLexer.IDENTIFIER)
                ).serialize("methodSignature")
        ).isEqualTo(
                "(methodSignature public methodName [ (typeParameterDef T) ] ( ) : (typeSpec (fqIdentifier typeName)))");
    }

    @Test
    void methodSignatureWithTypeParameters() {
        Assertions.assertThat(
                new Tree(
                        token("public", ThoriumLexer.PUBLIC),
                        token("methodName", ThoriumLexer.IDENTIFIER),
                        token("["),
                        token("T", ThoriumLexer.IDENTIFIER),
                        token(","),
                        token("U", ThoriumLexer.IDENTIFIER),
                        token("]"),
                        token("("),
                        token(")"),
                        token(":"),
                        token("typeName", ThoriumLexer.IDENTIFIER)
                ).serialize("methodSignature")
        ).isEqualTo(
                "(methodSignature public methodName [ (typeParameterDef T , U) ] ( ) : (typeSpec (fqIdentifier typeName)))");
    }
}
