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
import org.thoriumlang.compiler.ast.nodes.BooleanValue;
import org.thoriumlang.compiler.ast.nodes.DirectAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.FunctionValue;
import org.thoriumlang.compiler.ast.nodes.IdentifierValue;
import org.thoriumlang.compiler.ast.nodes.IndirectAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.MethodCallValue;
import org.thoriumlang.compiler.ast.nodes.Mode;
import org.thoriumlang.compiler.ast.nodes.NestedValue;
import org.thoriumlang.compiler.ast.nodes.NewAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.nodes.NoneValue;
import org.thoriumlang.compiler.ast.nodes.NumberValue;
import org.thoriumlang.compiler.ast.nodes.Reference;
import org.thoriumlang.compiler.ast.nodes.Statement;
import org.thoriumlang.compiler.ast.nodes.StringValue;
import org.thoriumlang.compiler.ast.nodes.TypeSpec;
import org.thoriumlang.compiler.ast.nodes.TypeSpecInferred;
import org.thoriumlang.compiler.ast.nodes.Value;
import org.thoriumlang.compiler.collections.Lists;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class ValueVisitor extends ThoriumBaseVisitor<Value> {
    private final NodeIdGenerator nodeIdGenerator;
    private final SourcePositionProvider<Token> sourcePositionProvider;
    private final TypeParameterVisitor typeParameterVisitor;
    private final MethodParameterVisitor methodParameterVisitor;
    private final TypeSpecVisitor typeSpecVisitor;
    private final StatementVisitor statementVisitorForNotLast;
    private final StatementVisitor statementVisitorForLast;

    ValueVisitor(
            NodeIdGenerator nodeIdGenerator,
            SourcePositionProvider<Token> sourceSourcePositionProvider,
            TypeParameterVisitor typeParameterVisitor,
            MethodParameterVisitor methodParameterVisitor,
            TypeSpecVisitor typeSpecVisitor,
            StatementVisitor statementVisitorForNotLast,
            StatementVisitor statementVisitorForLast) {
        this.nodeIdGenerator = nodeIdGenerator;
        this.sourcePositionProvider = sourceSourcePositionProvider;
        this.typeParameterVisitor = typeParameterVisitor;
        this.methodParameterVisitor = methodParameterVisitor;
        this.typeSpecVisitor = typeSpecVisitor;
        this.statementVisitorForNotLast = statementVisitorForNotLast;
        this.statementVisitorForLast = statementVisitorForLast;
    }

    @Override
    public Value visitValue(ThoriumParser.ValueContext ctx) {
        if (ctx.value() != null) {
            return ctx.value().accept(this);
        }

        if (ctx.indirectValue() != null) {
            return ctx.indirectValue().accept(this);
        }

        if (ctx.directValue() != null) {
            return ctx.directValue().accept(this);
        }

        if (ctx.assignmentValue() != null) {
            return ctx.assignmentValue().accept(this);
        }

        if (ctx.functionValue() != null) {
            return ctx.functionValue().accept(this);
        }

        throw new IllegalStateException("No actual value found");
    }

    @Override
    public Value visitFunctionValue(ThoriumParser.FunctionValueContext ctx) {
        return sourcePositionProvider.provide(
                new FunctionValue(
                        nodeIdGenerator.next(),
                        ctx.typeParameter() == null ?
                                Collections.emptyList() :
                                ctx.typeParameter().accept(typeParameterVisitor),
                        ctx.methodParameter().stream()
                                .map(p -> p.accept(methodParameterVisitor))
                                .collect(Collectors.toList()),
                        ctx.typeSpec() == null ?
                                sourcePositionProvider.provide(
                                        new TypeSpecInferred(nodeIdGenerator.next()),
                                        ctx.start
                                ) :
                                ctx.typeSpec().accept(typeSpecVisitor),
                        ctx.value() != null ?
                                Collections.singletonList(
                                        sourcePositionProvider.provide(
                                                new Statement(
                                                        nodeIdGenerator.next(),
                                                        ctx.value().accept(this), true
                                                ),
                                                ctx.start
                                        )
                                ) :
                                Lists.append(
                                        Lists.withoutLast(ctx.statement()).stream()
                                                .map(s -> s.accept(statementVisitorForNotLast))
                                                .collect(Collectors.toList()),
                                        Lists.last(ctx.statement())
                                                .map(s -> s.accept(statementVisitorForLast))
                                                .orElse(statementVisitorForLast.none(ctx.start))
                                )
                ),
                ctx.start
        );
    }

    @Override
    public Value visitAssignmentValue(ThoriumParser.AssignmentValueContext ctx) {
        if (ctx.indirectValue() != null) {
            return sourcePositionProvider.provide(
                    new IndirectAssignmentValue(
                            nodeIdGenerator.next(),
                            ctx.indirectValue().accept(this),
                            sourcePositionProvider.provide(
                                    new Reference(
                                            nodeIdGenerator.next(),
                                            ctx.identifier.getText(),
                                            false
                                    ),
                                    ctx.start
                            ),
                            ctx.value().accept(this)
                    ),
                    ctx.start
            );
        }
        if (ctx.VAR() != null) {
            return sourcePositionProvider.provide(
                    new NewAssignmentValue(
                            nodeIdGenerator.next(),
                            ctx.varName.getText(),
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
                                    ctx.value().accept(this),
                            Mode.VAR
                    ),
                    ctx.start
            );
        }
        if (ctx.VAL() != null) {
            return sourcePositionProvider.provide(
                    new NewAssignmentValue(
                            nodeIdGenerator.next(),
                            ctx.valName.getText(),
                            ctx.typeSpec() == null ?
                                    sourcePositionProvider.provide(
                                            new TypeSpecInferred(nodeIdGenerator.next()),
                                            ctx.start
                                    ) :
                                    ctx.typeSpec().accept(typeSpecVisitor),
                            ctx.value().accept(this),
                            Mode.VAL
                    ),
                    ctx.start
            );
        }
        return sourcePositionProvider.provide(
                new DirectAssignmentValue(
                        nodeIdGenerator.next(),
                        sourcePositionProvider.provide(
                                new Reference(
                                        nodeIdGenerator.next(),
                                        ctx.identifier.getText(),
                                        false
                                ),
                                ctx.start
                        ),
                        ctx.value().accept(this)
                ),
                ctx.start
        );
    }

    @Override
    public Value visitDirectValue(ThoriumParser.DirectValueContext ctx) {
        if (ctx.NUMBER() != null) {
            return sourcePositionProvider.provide(
                    new NumberValue(
                            nodeIdGenerator.next(),
                            ctx.NUMBER().getSymbol().getText()
                    ),
                    ctx.start
            );
        }

        if (ctx.STRING() != null) {
            String str = ctx.STRING().getSymbol().getText();
            return sourcePositionProvider.provide(
                    new StringValue(
                            nodeIdGenerator.next(),
                            str.substring(1, str.length() - 1)
                    ),
                    ctx.start
            );
        }

        if (ctx.TRUE() != null) {
            return sourcePositionProvider.provide(
                    new BooleanValue(nodeIdGenerator.next(), true),
                    ctx.start
            );
        }

        if (ctx.FALSE() != null) {
            return sourcePositionProvider.provide(
                    new BooleanValue(nodeIdGenerator.next(), false),
                    ctx.start
            );
        }

        if (ctx.NONE() != null) {
            return sourcePositionProvider.provide(
                    new NoneValue(nodeIdGenerator.next()),
                    ctx.start
            );
        }

        throw new IllegalStateException("Value is none of [NUMBER, STRING, TRUE, FALSE, NONE]");
    }

    @Override
    public Value visitIndirectValue(ThoriumParser.IndirectValueContext ctx) {
        if (ctx.THIS() != null) {
            return sourcePositionProvider.provide(
                    new IdentifierValue(
                            nodeIdGenerator.next(),
                            sourcePositionProvider.provide(
                                    new Reference(
                                            nodeIdGenerator.next(),
                                            "this",
                                            false
                                    ),
                                    ctx.start
                            )
                    ),
                    ctx.start
            );
        }

        if (ctx.indirectValue() != null) {
            return isMethodCall(ctx) ?
                    sourcePositionProvider.provide(
                            new NestedValue(
                                    nodeIdGenerator.next(),
                                    ctx.indirectValue().accept(this),
                                    methodCall(ctx)
                            ),
                            ctx.start
                    ) :
                    sourcePositionProvider.provide(
                            new NestedValue(
                                    nodeIdGenerator.next(),
                                    ctx.indirectValue().accept(this),
                                    identifier(ctx)
                            ),
                            ctx.start
                    );
        }

        if (ctx.directValue() != null) {
            return isMethodCall(ctx) ?
                    sourcePositionProvider.provide(
                            new NestedValue(
                                    nodeIdGenerator.next(),
                                    ctx.directValue().accept(this),
                                    methodCall(ctx)
                            ),
                            ctx.start
                    ) :
                    sourcePositionProvider.provide(
                            new NestedValue(
                                    nodeIdGenerator.next(),
                                    ctx.directValue().accept(this),
                                    identifier(ctx)
                            ),
                            ctx.start
                    );
        }

        if (ctx.IDENTIFIER() != null) {
            return isMethodCall(ctx) ?
                    methodCall(ctx) :
                    identifier(ctx);
        }

        throw new IllegalStateException("Value is none of [THIS, IDENTIFIER, directValue, indirectValue]");
    }

    private boolean isMethodCall(ThoriumParser.IndirectValueContext ctx) {
        return ctx.methodName != null;
    }

    private MethodCallValue methodCall(ThoriumParser.IndirectValueContext ctx) {
        return sourcePositionProvider.provide(
                new MethodCallValue(
                        nodeIdGenerator.next(),
                        sourcePositionProvider.provide(
                                new Reference(nodeIdGenerator.next(), ctx.IDENTIFIER().getSymbol().getText(), true),
                                ctx.start
                        ),
                        typeArguments(ctx),
                        methodArguments(ctx)
                ),
                ctx.start
        );
    }

    private List<TypeSpec> typeArguments(ThoriumParser.IndirectValueContext ctx) {
        return ctx.typeArguments() == null ?
                Collections.emptyList() :
                ctx.typeArguments().typeSpec().stream()
                        .map(ts -> ts.accept(typeSpecVisitor))
                        .collect(Collectors.toList());
    }

    private List<Value> methodArguments(ThoriumParser.IndirectValueContext ctx) {
        return ctx.methodArguments() == null ?
                Collections.emptyList() :
                ctx.methodArguments().value().stream()
                        .map(v -> v.accept(this))
                        .collect(Collectors.toList());
    }

    private IdentifierValue identifier(ThoriumParser.IndirectValueContext ctx) {
        return sourcePositionProvider.provide(
                new IdentifierValue(
                        nodeIdGenerator.next(),
                        sourcePositionProvider.provide(
                                new Reference(
                                        nodeIdGenerator.next(),
                                        ctx.IDENTIFIER().getSymbol().getText(),
                                        false
                                ),
                                ctx.start
                        )
                ),
                ctx.start
        );
    }
}
