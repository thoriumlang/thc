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
class ExpressionTest {
    @Test
    void numberExpression() {
        Assertions.assertThat(
                new Tree(
                        token("1", ThoriumLexer.NUMBER)
                        ).serialize("expression")
        ).isEqualTo("(expression 1)");
    }

    @Test
    void stringExpression() {
        Assertions.assertThat(
                new Tree(
                        token("\"str\"", ThoriumLexer.STRING)
                ).serialize("expression")
        ).isEqualTo("(expression \"str\")");
    }

    @Test
    void booleanExpression() {
        Assertions.assertThat(
                new Tree(
                        token("true", ThoriumLexer.BOOLEAN)
                ).serialize("expression")
        ).isEqualTo("(expression true)");
    }

    @Test
    void noneExpression() {
        Assertions.assertThat(
                new Tree(
                        token("none", ThoriumLexer.NONE)
                ).serialize("expression")
        ).isEqualTo("(expression none)");
    }

    @Test
    void identifierExpression() {
        Assertions.assertThat(
                new Tree(
                        token("identifier", ThoriumLexer.IDENTIFIER)
                ).serialize("expression")
        ).isEqualTo("(expression identifier)");
    }

    @Test
    void parensExpression() {
        Assertions.assertThat(
                new Tree(
                        token("("),
                        token("identifier", ThoriumLexer.IDENTIFIER),
                        token(")")
                ).serialize("expression")
        ).isEqualTo("(expression ( (expression identifier) ))");
    }

    @Test
    void methodCallExpression() {
        Assertions.assertThat(
                new Tree(
                        token("a", ThoriumLexer.IDENTIFIER),
                        token("."),
                        token("m1",ThoriumLexer.IDENTIFIER ),
                        token("("),
                        token(")"),
                        token("?."),
                        token("m2",ThoriumLexer.IDENTIFIER ),
                        token("("),
                        token(")")
                ).serialize("expression")
        ).isEqualTo("(expression (expression (expression a) . m1 ( )) ?. m2 ( ))");
    }
}
