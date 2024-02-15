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

import org.antlr.v4.runtime.Token;
import org.thoriumlang.compiler.ast.SourcePositionProvider;
import org.thoriumlang.compiler.ast.context.SourcePosition;
import org.thoriumlang.compiler.ast.nodes.Node;

import java.util.Arrays;

public class Antlr4SourcePositionProvider implements SourcePositionProvider<Token> {
    @Override
    public <T extends Node> T provide(T node, Token firstToken, Token lastToken) {
        node.getContext().put(
                SourcePosition.class,
                new SourcePosition(
                        new SourcePosition.Position(
                                firstToken.getLine(),
                                firstToken.getCharPositionInLine() + 1
                        ),
                        new SourcePosition.Position(
                                lastToken.getLine(),
                                lastToken.getCharPositionInLine() + lastToken.getText().length() + 1
                        ),
                        Arrays.asList(firstToken.getInputStream().toString().split("\n"))
                                // FIXME error when file ends in \n
                                .subList(firstToken.getLine() - 1, lastToken.getLine())
                )
        );
        return node;
    }
}
