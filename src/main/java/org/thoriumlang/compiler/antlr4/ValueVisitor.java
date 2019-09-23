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

import org.thoriumlang.compiler.antlr.ThoriumBaseVisitor;
import org.thoriumlang.compiler.antlr.ThoriumParser;
import org.thoriumlang.compiler.ast.nodes.BooleanValue;
import org.thoriumlang.compiler.ast.nodes.FunctionValue;
import org.thoriumlang.compiler.ast.nodes.IdentifierValue;
import org.thoriumlang.compiler.ast.nodes.IndirectAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.MethodCallValue;
import org.thoriumlang.compiler.ast.nodes.NestedValue;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.nodes.NoneValue;
import org.thoriumlang.compiler.ast.nodes.NumberValue;
import org.thoriumlang.compiler.ast.nodes.Statement;
import org.thoriumlang.compiler.ast.nodes.StringValue;
import org.thoriumlang.compiler.ast.nodes.TypeSpec;
import org.thoriumlang.compiler.ast.nodes.TypeSpecInferred;
import org.thoriumlang.compiler.ast.nodes.ValAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.Value;
import org.thoriumlang.compiler.ast.nodes.VarAssignmentValue;
import org.thoriumlang.compiler.collections.Lists;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class ValueVisitor extends ThoriumBaseVisitor<Value> {
    private final NodeIdGenerator nodeIdGenerator;
    private final TypeParameterDefVisitor typeParameterDefVisitor;
    private final MethodParameterVisitor methodParameterVisitor;
    private final TypeSpecVisitor typeSpecVisitor;
    private final StatementVisitor statementVisitorForNotLast;
    private final StatementVisitor statementVisitorForLast;

    ValueVisitor(NodeIdGenerator nodeIdGenerator,
            TypeParameterDefVisitor typeParameterDefVisitor,
            MethodParameterVisitor methodParameterVisitor,
            TypeSpecVisitor typeSpecVisitor,
            StatementVisitor statementVisitorForNotLast,
            StatementVisitor statementVisitorForLast) {
        this.nodeIdGenerator = nodeIdGenerator;
        this.typeParameterDefVisitor = typeParameterDefVisitor;
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
        return new FunctionValue(
                nodeIdGenerator.next(),
                ctx.typeParameterDef() == null ?
                        Collections.emptyList() :
                        ctx.typeParameterDef().accept(typeParameterDefVisitor),
                ctx.methodParameterDef().stream()
                        .map(p -> p.accept(methodParameterVisitor))
                        .collect(Collectors.toList()),
                ctx.typeSpec() == null ?
                        new TypeSpecInferred(nodeIdGenerator.next()) :
                        ctx.typeSpec().accept(typeSpecVisitor),
                ctx.value() != null ?
                        Collections.singletonList(new Statement(
                                nodeIdGenerator.next(),
                                ctx.value().accept(this), true
                        )) :
                        Lists.append(
                                Lists.withoutLast(ctx.statement()).stream()
                                        .map(s -> s.accept(statementVisitorForNotLast))
                                        .collect(Collectors.toList()),
                                Lists.last(ctx.statement())
                                        .map(s -> s.accept(statementVisitorForLast))
                                        .orElse(statementVisitorForLast.none())
                        )
        );
    }

    @Override
    public Value visitAssignmentValue(ThoriumParser.AssignmentValueContext ctx) {
        if (ctx.indirectValue() != null) {
            return new IndirectAssignmentValue(
                    nodeIdGenerator.next(),
                    ctx.indirectValue().accept(this),
                    ctx.IDENTIFIER().getSymbol().getText(),
                    ctx.value().accept(this)
            );
        }
        if (ctx.VAR() != null) {
            return new VarAssignmentValue(
                    nodeIdGenerator.next(),
                    ctx.IDENTIFIER().getSymbol().getText(),
                    ctx.typeSpec() == null ?
                            new TypeSpecInferred(nodeIdGenerator.next()) :
                            ctx.typeSpec().accept(typeSpecVisitor),
                    ctx.value() == null ?
                            new NoneValue(nodeIdGenerator.next()) :
                            ctx.value().accept(this)
            );
        }
        return new ValAssignmentValue(
                nodeIdGenerator.next(),
                ctx.IDENTIFIER().getSymbol().getText(),
                ctx.typeSpec() == null ?
                        new TypeSpecInferred(nodeIdGenerator.next()) :
                        ctx.typeSpec().accept(typeSpecVisitor),
                ctx.value().accept(this)
        );
    }

    @Override
    public Value visitDirectValue(ThoriumParser.DirectValueContext ctx) {
        if (ctx.NUMBER() != null) {
            return new NumberValue(
                    nodeIdGenerator.next(),
                    ctx.NUMBER().getSymbol().getText()
            );
        }

        if (ctx.STRING() != null) {
            String str = ctx.STRING().getSymbol().getText();
            return new StringValue(
                    nodeIdGenerator.next(),
                    str.substring(1, str.length() - 1)
            );
        }

        if (ctx.TRUE() != null) {
            return new BooleanValue(nodeIdGenerator.next(), true);
        }

        if (ctx.FALSE() != null) {
            return new BooleanValue(nodeIdGenerator.next(), false);
        }

        if (ctx.NONE() != null) {
            return new NoneValue(nodeIdGenerator.next());
        }

        throw new IllegalStateException("Value is none of [NUMBER, STRING, TRUE, FALSE, NONE]");
    }

    @Override
    public Value visitIndirectValue(ThoriumParser.IndirectValueContext ctx) {
        if (ctx.THIS() != null) {
            return new IdentifierValue(nodeIdGenerator.next(), "this");
        }

        if (ctx.indirectValue() != null) {
            return isMethodCall(ctx) ?
                    new NestedValue(
                            nodeIdGenerator.next(),
                            ctx.indirectValue().accept(this),
                            methodCall(ctx)
                    ) :
                    new NestedValue(
                            nodeIdGenerator.next(),
                            ctx.indirectValue().accept(this),
                            identifier(ctx)
                    );
        }

        if (ctx.directValue() != null) {
            return isMethodCall(ctx) ?
                    new NestedValue(
                            nodeIdGenerator.next(),
                            ctx.directValue().accept(this),
                            methodCall(ctx)
                    ) :
                    new NestedValue(
                            nodeIdGenerator.next(),
                            ctx.directValue().accept(this),
                            identifier(ctx)
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
        return new MethodCallValue(
                nodeIdGenerator.next(),
                ctx.IDENTIFIER().getSymbol().getText(),
                typeArguments(ctx),
                methodArguments(ctx)
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
        return new IdentifierValue(
                nodeIdGenerator.next(),
                ctx.IDENTIFIER().getSymbol().getText()
        );
    }
}
