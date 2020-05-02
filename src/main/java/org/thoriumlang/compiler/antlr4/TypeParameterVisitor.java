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
import org.thoriumlang.compiler.antlr.ThoriumBaseVisitor;
import org.thoriumlang.compiler.antlr.ThoriumParser;
import org.thoriumlang.compiler.ast.SourcePositionProvider;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.nodes.TypeParameter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class TypeParameterVisitor extends ThoriumBaseVisitor<List<TypeParameter>> {
    private final NodeIdGenerator nodeIdGenerator;
    private final SourcePositionProvider<Token> sourcePositionProvider;

    TypeParameterVisitor(
            NodeIdGenerator nodeIdGenerator,
            SourcePositionProvider<Token> sourcePositionProvider
    ) {
        this.nodeIdGenerator = nodeIdGenerator;
        this.sourcePositionProvider = sourcePositionProvider;
    }

    @Override
    public List<TypeParameter> visitTypeParameter(ThoriumParser.TypeParameterContext ctx) {
        if (ctx.IDENTIFIER() == null) {
            return Collections.emptyList();
        }
        return ctx.IDENTIFIER().stream()
                .map(i -> sourcePositionProvider.provide(
                        new TypeParameter(
                                nodeIdGenerator.next(),
                                i.getSymbol().getText()
                        ),
                        i.getSymbol(),
                        i.getSymbol()
                ))
                .collect(Collectors.toList());
    }
}
