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
class VariableTest {
    @Test
    void implicitConstantWithoutType() {
        Assertions.assertThat(
                new Tree(
                        token("name", ThoriumLexer.IDENTIFIER),
                        token("="),
                        token("1", ThoriumLexer.NUMBER),
                        token(";")
                ).serialize("constOrVarDef")
        ).isEqualTo("(constOrVarDef name = (expression 1) ;)");
    }

    @Test
    void implicitConstantWitType() {
        Assertions.assertThat(
                new Tree(
                        token("name", ThoriumLexer.IDENTIFIER),
                        token(":"),
                        token("type", ThoriumLexer.IDENTIFIER),
                        token("="),
                        token("1", ThoriumLexer.NUMBER),
                        token(";")
                ).serialize("constOrVarDef")
        ).isEqualTo("(constOrVarDef name : (typeSpec type) = (expression 1) ;)");
    }

    @Test
    void explicitConstantWithoutType() {
        Assertions.assertThat(
                new Tree(
                        token("val", ThoriumLexer.VAL),
                        token("name", ThoriumLexer.IDENTIFIER),
                        token("="),
                        token("1", ThoriumLexer.NUMBER),
                        token(";")
                ).serialize("constOrVarDef")
        ).isEqualTo("(constOrVarDef val name = (expression 1) ;)");
    }

    @Test
    void explicitConstantWithType() {
        Assertions.assertThat(
                new Tree(
                        token("val", ThoriumLexer.VAL),
                        token("name", ThoriumLexer.IDENTIFIER),
                        token(":"),
                        token("type", ThoriumLexer.IDENTIFIER),
                        token("="),
                        token("1", ThoriumLexer.NUMBER),
                        token(";")
                ).serialize("constOrVarDef")
        ).isEqualTo("(constOrVarDef val name : (typeSpec type) = (expression 1) ;)");
    }

    @Test
    void explicitVariableWithoutTypeWithoutValue() {
        Assertions.assertThat(
                new Tree(
                        token("var", ThoriumLexer.VAR),
                        token("name", ThoriumLexer.IDENTIFIER),
                        token(";")
                ).serialize("constOrVarDef")
        ).isEqualTo("(constOrVarDef var name ;)");
    }

    @Test
    void explicitVariableWithTypeWithoutValue() {
        Assertions.assertThat(
                new Tree(
                        token("var", ThoriumLexer.VAR),
                        token("name", ThoriumLexer.IDENTIFIER),
                        token(":"),
                        token("type", ThoriumLexer.IDENTIFIER),
                        token(";")
                ).serialize("constOrVarDef")
        ).isEqualTo("(constOrVarDef var name : (typeSpec type) ;)");
    }

    @Test
    void explicitVariableWithoutTypeWithValue() {
        Assertions.assertThat(
                new Tree(
                        token("var", ThoriumLexer.VAR),
                        token("name", ThoriumLexer.IDENTIFIER),
                        token("="),
                        token("1", ThoriumLexer.NUMBER),
                        token(";")
                ).serialize("constOrVarDef")
        ).isEqualTo("(constOrVarDef var name = (expression 1) ;)");
    }

    @Test
    void explicitVariableWithTypeWithValue() {
        Assertions.assertThat(
                new Tree(
                        token("var", ThoriumLexer.VAR),
                        token("name", ThoriumLexer.IDENTIFIER),
                        token(":"),
                        token("type", ThoriumLexer.IDENTIFIER),
                        token("="),
                        token("1", ThoriumLexer.NUMBER),
                        token(";")
                ).serialize("constOrVarDef")
        ).isEqualTo("(constOrVarDef var name : (typeSpec type) = (expression 1) ;)");
    }
}
