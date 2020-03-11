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
package org.thoriumlang.compiler.antlr4;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenSource;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.ast.context.SourcePosition;
import org.thoriumlang.compiler.ast.nodes.Node;
import org.thoriumlang.compiler.ast.nodes.NodeId;
import org.thoriumlang.compiler.ast.visitor.Visitor;

class Antlr4SourcePositionProviderTest {
    @Test
    void provide() {
        Antlr4SourcePositionProvider provider = new Antlr4SourcePositionProvider();
        Node node = new NodeStub();
        Token token = new TokenStub();

        provider.provide(node, token);

        Assertions.assertThat(node.getContext().require(SourcePosition.class))
                .extracting(SourcePosition::toString)
                .isEqualTo("1:3");

    }

    private static class NodeStub extends Node {
        private NodeStub() {
            super(new NodeId(1L));
        }

        @Override
        public <T> T accept(Visitor<? extends T> visitor) {
            return null;
        }
    }

    private static class TokenStub implements Token {
        @Override
        public String getText() {
            return null;
        }

        @Override
        public int getType() {
            return 0;
        }

        @Override
        public int getLine() {
            return 1;
        }

        @Override
        public int getCharPositionInLine() {
            return 2;
        }

        @Override
        public int getChannel() {
            return 0;
        }

        @Override
        public int getTokenIndex() {
            return 0;
        }

        @Override
        public int getStartIndex() {
            return 0;
        }

        @Override
        public int getStopIndex() {
            return 0;
        }

        @Override
        public TokenSource getTokenSource() {
            return null;
        }

        @Override
        public CharStream getInputStream() {
            return null;
        }
    }
}
