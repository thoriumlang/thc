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

@SuppressWarnings("squid:S1192")
@Tag("parser")
class MethodSignatureTest {
    public static void main(String[] args) {
        ThoriumLexer.VOCABULARY.getLiteralName(5);
    }

    @Test
    void defaultMethodSignature() {
        Assertions.assertThat(
                new Tree(
                        new TokenStub("methodName", ThoriumLexer.IDENTIFIER),
                        new TokenStub("("),
                        new TokenStub(")"),
                        new TokenStub(":"),
                        new TokenStub("typeName", ThoriumLexer.IDENTIFIER)
                ).serialize("methodSignature")
        ).isEqualTo("(methodSignature methodName ( ) : (typeSpec typeName))");
    }

    @Test
    void privateMethodSignature() {
        Assertions.assertThat(
                new Tree(
                        new TokenStub("private", ThoriumLexer.PRIVATE),
                        new TokenStub("methodName", ThoriumLexer.IDENTIFIER),
                        new TokenStub("("),
                        new TokenStub(")"),
                        new TokenStub(":"),
                        new TokenStub("typeName", ThoriumLexer.IDENTIFIER)
                ).serialize("methodSignature")
        ).isEqualTo("(methodSignature private methodName ( ) : (typeSpec typeName))");
    }

    @Test
    void namespaceMethodSignature() {
        Assertions.assertThat(
                new Tree(
                        new TokenStub("namespace", ThoriumLexer.NAMESPACE),
                        new TokenStub("methodName", ThoriumLexer.IDENTIFIER),
                        new TokenStub("("),
                        new TokenStub(")"),
                        new TokenStub(":"),
                        new TokenStub("typeName", ThoriumLexer.IDENTIFIER)
                ).serialize("methodSignature")
        ).isEqualTo("(methodSignature namespace methodName ( ) : (typeSpec typeName))");
    }

    @Test
    void publicMethodSignature() {
        Assertions.assertThat(
                new Tree(
                        new TokenStub("public", ThoriumLexer.PUBLIC),
                        new TokenStub("methodName", ThoriumLexer.IDENTIFIER),
                        new TokenStub("("),
                        new TokenStub(")"),
                        new TokenStub(":"),
                        new TokenStub("typeName", ThoriumLexer.IDENTIFIER)
                ).serialize("methodSignature")
        ).isEqualTo("(methodSignature public methodName ( ) : (typeSpec typeName))");
    }

    @Test
    void methodSignatureWithParameter() {
        Assertions.assertThat(
                new Tree(
                        new TokenStub("public", ThoriumLexer.PUBLIC),
                        new TokenStub("methodName", ThoriumLexer.IDENTIFIER),
                        new TokenStub("("),
                        new TokenStub("id", ThoriumLexer.IDENTIFIER),
                        new TokenStub(":"),
                        new TokenStub("type", ThoriumLexer.IDENTIFIER),
                        new TokenStub(")"),
                        new TokenStub(":"),
                        new TokenStub("typeName", ThoriumLexer.IDENTIFIER)
                ).serialize("methodSignature")
        ).isEqualTo("(methodSignature public methodName ( id : (typeSpec type) ) : (typeSpec typeName))");
    }

    @Test
    void methodSignatureWithParameters() {
        Assertions.assertThat(
                new Tree(
                        new TokenStub("public", ThoriumLexer.PUBLIC),
                        new TokenStub("methodName", ThoriumLexer.IDENTIFIER),
                        new TokenStub("("),
                        new TokenStub("id", ThoriumLexer.IDENTIFIER),
                        new TokenStub(":"),
                        new TokenStub("type", ThoriumLexer.IDENTIFIER),
                        new TokenStub(","),
                        new TokenStub("id", ThoriumLexer.IDENTIFIER),
                        new TokenStub(":"),
                        new TokenStub("type", ThoriumLexer.IDENTIFIER),
                        new TokenStub(","),
                        new TokenStub("id", ThoriumLexer.IDENTIFIER),
                        new TokenStub(":"),
                        new TokenStub("type", ThoriumLexer.IDENTIFIER),
                        new TokenStub(")"),
                        new TokenStub(":"),
                        new TokenStub("typeName", ThoriumLexer.IDENTIFIER)
                ).serialize("methodSignature")
        ).isEqualTo("(methodSignature public methodName ( id : (typeSpec type) , id : (typeSpec type) , id : (typeSpec type) ) : (typeSpec typeName))");
    }
}
