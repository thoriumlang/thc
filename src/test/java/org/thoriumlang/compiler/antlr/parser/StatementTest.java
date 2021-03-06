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
class StatementTest {
    @Test
    void returnStatement() {
        Assertions.assertThat(
                new Tree(
                        token("return", ThoriumLexer.RETURN),
                        token("1", ThoriumLexer.NUMBER),
                        token(";")
                ).serialize("statement")
        ).isEqualTo("(statement return (value (directValue 1)) ;)");
    }

    @Test
    void expressionStatement() {
        Assertions.assertThat(
                new Tree(
                        token("1", ThoriumLexer.NUMBER),
                        token(";")
                ).serialize("statement")
        ).isEqualTo("(statement (value (directValue 1)) ;)");
    }
}
