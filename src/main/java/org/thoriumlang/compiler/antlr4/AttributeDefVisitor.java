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
import org.thoriumlang.compiler.ast.nodes.Attribute;
import org.thoriumlang.compiler.ast.nodes.Mode;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.nodes.NoneValue;
import org.thoriumlang.compiler.ast.nodes.TypeSpecInferred;

class AttributeDefVisitor extends ThoriumBaseVisitor<Attribute> {
    private final NodeIdGenerator nodeIdGenerator;
    private final SourcePositionProvider<Token> sourcePositionProvider;
    private final TypeSpecVisitor typeSpecVisitor;
    private final ValueVisitor valueVisitor;

    AttributeDefVisitor(
            NodeIdGenerator nodeIdGenerator,
            SourcePositionProvider<Token> sourcePositionProvider,
            TypeSpecVisitor typeSpecVisitor,
            ValueVisitor valueVisitor
    ) {
        this.nodeIdGenerator = nodeIdGenerator;
        this.sourcePositionProvider = sourcePositionProvider;
        this.typeSpecVisitor = typeSpecVisitor;
        this.valueVisitor = valueVisitor;
    }

    @Override
    public Attribute visitAttributeDef(ThoriumParser.AttributeDefContext ctx) {
        if (ctx.VAR() != null) {
            return sourcePositionProvider.provide(
                    new Attribute(
                            nodeIdGenerator.next(),
                            ctx.IDENTIFIER().getSymbol().getText(),
                            ctx.typeSpec().accept(typeSpecVisitor),
                            ctx.value() == null ?
                                    sourcePositionProvider.provide(
                                            new NoneValue(nodeIdGenerator.next()),
                                            ctx.start
                                    ) :
                                    ctx.value().accept(valueVisitor),
                            Mode.VAR
                    ),
                    ctx.start
            );
        }

        return sourcePositionProvider.provide(
                new Attribute(
                        nodeIdGenerator.next(),
                        ctx.IDENTIFIER().getSymbol().getText(),
                        ctx.typeSpec() == null ?
                                sourcePositionProvider.provide(
                                        new TypeSpecInferred(nodeIdGenerator.next()),
                                        ctx.start
                                ) :
                                ctx.typeSpec().accept(typeSpecVisitor),
                        ctx.value() == null ?
                                sourcePositionProvider.provide(
                                        new NoneValue(nodeIdGenerator.next()),
                                        ctx.start
                                ) :
                                ctx.value().accept(valueVisitor),
                        Mode.VAL
                ),
                ctx.start
        );
    }
}
