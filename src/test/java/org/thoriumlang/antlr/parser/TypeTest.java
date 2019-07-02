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

@Tag("parser")
class TypeTest {
    @Test
    void emptyType() {
        Assertions.assertThat(
                new Tree(
                        new TokenStub("type", ThoriumLexer.TYPE),
                        new TokenStub("Identifier", ThoriumLexer.IDENTIFIER),
                        new TokenStub("{", ThoriumLexer.LBRACE),
                        new TokenStub("}", ThoriumLexer.RBRACE)
                ).serialize()
        ).isEqualTo("(root (typeDef type Identifier { }))");
    }
}
