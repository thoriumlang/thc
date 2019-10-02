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

@Tag("parser")
class MethodDefTest {
    @Test
    void defaultMethodDef() {
        Assertions.assertThat(
                new Tree(
                        token("methodName", ThoriumLexer.IDENTIFIER),
                        token("("),
                        token(")"),
                        token("{"),
                        token("}")
                ).serialize("methodDef")
        ).isEqualTo("(methodDef methodName ( ) { })");
    }

    @Test
    void privateMethodDef() {
        Assertions.assertThat(
                new Tree(
                        token("private", ThoriumLexer.PRIVATE),
                        token("methodName", ThoriumLexer.IDENTIFIER),
                        token("("),
                        token(")"),
                        token("{"),
                        token("}")
                ).serialize("methodDef")
        ).isEqualTo("(methodDef private methodName ( ) { })");
    }

    @Test
    void namespaceMethodDef() {
        Assertions.assertThat(
                new Tree(
                        token("namespace", ThoriumLexer.NAMESPACE),
                        token("methodName", ThoriumLexer.IDENTIFIER),
                        token("("),
                        token(")"),
                        token("{"),
                        token("}")
                ).serialize("methodDef")
        ).isEqualTo("(methodDef namespace methodName ( ) { })");
    }

    @Test
    void publicMethodDef() {
        Assertions.assertThat(
                new Tree(
                        token("public", ThoriumLexer.PUBLIC),
                        token("methodName", ThoriumLexer.IDENTIFIER),
                        token("("),
                        token(")"),
                        token("{"),
                        token("}")
                ).serialize("methodDef")
        ).isEqualTo("(methodDef public methodName ( ) { })");
    }

    @Test
    void methodDefWithParameters() {
        Assertions.assertThat(
                new Tree(
                        token("methodName", ThoriumLexer.IDENTIFIER),
                        token("("),
                        token("id1", ThoriumLexer.IDENTIFIER),
                        token(":"),
                        token("type1", ThoriumLexer.IDENTIFIER),
                        token(","),
                        token("id2", ThoriumLexer.IDENTIFIER),
                        token(":"),
                        token("type2", ThoriumLexer.IDENTIFIER),
                        token(")"),
                        token("{"),
                        token("}")
                ).serialize("methodDef")
        ).isEqualTo("(methodDef methodName ( " +
                "(methodParameter id1 : (typeSpec (typeSpecSimple (fqIdentifier type1)))) , " +
                "(methodParameter id2 : (typeSpec (typeSpecSimple (fqIdentifier type2)))) " +
                ") { })");
    }

    @Test
    void methodDefWithTypeParameters() {
        Assertions.assertThat(
                new Tree(
                        token("methodName", ThoriumLexer.IDENTIFIER),
                        token("["),
                        token("T", ThoriumLexer.IDENTIFIER),
                        token(","),
                        token("U", ThoriumLexer.IDENTIFIER),
                        token("]"),
                        token("("),
                        token(")"),
                        token("{"),
                        token("}")
                ).serialize("methodDef")
        ).isEqualTo("(methodDef methodName [ (typeParameter T , U) ] ( ) { })");
    }

    @Test
    void methodDefWithReturnType() {
        Assertions.assertThat(
                new Tree(
                        token("methodName", ThoriumLexer.IDENTIFIER),
                        token("("),
                        token(")"),
                        token(":"),
                        token("typeName", ThoriumLexer.IDENTIFIER),
                        token("{"),
                        token("}")
                ).serialize("methodDef")
        ).isEqualTo("(methodDef methodName ( ) : (typeSpec (typeSpecSimple (fqIdentifier typeName))) { })");
    }

    @Test
    void nonEmptyMethodDef() {
        Assertions.assertThat(
                new Tree(
                        token("methodName", ThoriumLexer.IDENTIFIER),
                        token("("),
                        token(")"),
                        token("{"),
                        token("return", ThoriumLexer.RETURN),
                        token("Something", ThoriumLexer.IDENTIFIER),
                        token("("),
                        token(")"),
                        token("."),
                        token("method", ThoriumLexer.IDENTIFIER),
                        token("("),
                        token("true", ThoriumLexer.TRUE),
                        token(")"),
                        token(";"),
                        token("}")
                ).serialize("methodDef")
        ).isEqualTo("(methodDef methodName ( ) { " +
                "(statement return (value (indirectValue (indirectValue Something ( )) " +
                ". method ( (methodArguments (value (directValue true))) ))) ;) " +
                "})");
    }
}
