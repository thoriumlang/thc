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
import org.thoriumlang.compiler.ast.nodes.Method;
import org.thoriumlang.compiler.ast.nodes.MethodSignature;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.nodes.TypeSpecInferred;
import org.thoriumlang.compiler.ast.nodes.Visibility;
import org.thoriumlang.compiler.collections.Lists;

import java.util.Collections;
import java.util.stream.Collectors;

class MethodDefVisitor extends ThoriumBaseVisitor<Method> {
    private final NodeIdGenerator nodeIdGenerator;
    private final SourcePositionProvider<Token> sourcePositionProvider;
    private final TypeParameterVisitor typeParameterVisitor;
    private final MethodParameterVisitor methodParameterVisitor;
    private final TypeSpecVisitor typeSpecVisitor;
    private final StatementVisitor statementVisitorForNotLast;
    private final StatementVisitor statementVisitorForLast;

    MethodDefVisitor(
            NodeIdGenerator nodeIdGenerator,
            SourcePositionProvider<Token> sourcePositionProvider,
            TypeParameterVisitor typeParameterVisitor,
            MethodParameterVisitor methodParameterVisitor,
            TypeSpecVisitor typeSpecVisitor,
            StatementVisitor statementVisitorForNotLast,
            StatementVisitor statementVisitorForLast
    ) {
        this.nodeIdGenerator = nodeIdGenerator;
        this.sourcePositionProvider = sourcePositionProvider;
        this.typeParameterVisitor = typeParameterVisitor;
        this.methodParameterVisitor = methodParameterVisitor;
        this.typeSpecVisitor = typeSpecVisitor;
        this.statementVisitorForNotLast = statementVisitorForNotLast;
        this.statementVisitorForLast = statementVisitorForLast;
    }

    @Override
    public Method visitMethodDef(ThoriumParser.MethodDefContext ctx) {
        return sourcePositionProvider.provide(
                new Method(
                        nodeIdGenerator.next(),
                        sourcePositionProvider.provide(
                                new MethodSignature(
                                        nodeIdGenerator.next(),
                                        visibility(ctx),
                                        ctx.IDENTIFIER().getSymbol().getText(),
                                        ctx.typeParameter() == null ?
                                                Collections.emptyList() :
                                                ctx.typeParameter().accept(typeParameterVisitor),
                                        ctx.methodParameter().stream()
                                                .map(p -> p.accept(methodParameterVisitor))
                                                .collect(Collectors.toList()),
                                        ctx.typeSpec() == null ?
                                                sourcePositionProvider.provide(
                                                        new TypeSpecInferred(nodeIdGenerator.next()),
                                                        ctx.start,
                                                        ctx.stop
                                                ) :
                                                ctx.typeSpec().accept(typeSpecVisitor)
                                ),
                                ctx.start,
                                ctx.stop
                        ),
                        Lists.append(
                                Lists.withoutLast(ctx.statement()).stream()
                                        .map(s -> s.accept(statementVisitorForNotLast))
                                        .collect(Collectors.toList()),
                                Lists.last(ctx.statement())
                                        .map(s -> s.accept(statementVisitorForLast))
                                        .orElse(statementVisitorForLast.none(ctx.start))
                        )
                ),
                ctx.start,
                ctx.stop
        );
    }

    private Visibility visibility(ThoriumParser.MethodDefContext ctx) {
        return ctx.visibility == null ?
                Visibility.PRIVATE :
                Visibility.valueOf(ctx.visibility.getText().toUpperCase());
    }
}
