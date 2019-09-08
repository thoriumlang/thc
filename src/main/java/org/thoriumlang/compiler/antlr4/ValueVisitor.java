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
import org.thoriumlang.compiler.ast.BooleanValue;
import org.thoriumlang.compiler.ast.IdentifierValue;
import org.thoriumlang.compiler.ast.MethodCallValue;
import org.thoriumlang.compiler.ast.NestedValue;
import org.thoriumlang.compiler.ast.NoneValue;
import org.thoriumlang.compiler.ast.NumberValue;
import org.thoriumlang.compiler.ast.StringValue;
import org.thoriumlang.compiler.ast.TypeSpec;
import org.thoriumlang.compiler.ast.Value;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ValueVisitor extends ThoriumBaseVisitor<Value> {
    public static final ValueVisitor INSTANCE = new ValueVisitor();

    private ValueVisitor() {
        // nothing
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
            ctx.assignmentValue().accept(this);
        }

        throw new IllegalStateException("No actual value found");
    }

    @Override
    public Value visitAssignmentValue(ThoriumParser.AssignmentValueContext ctx) {
        return NoneValue.INSTANCE;
    }

    @Override
    public Value visitDirectValue(ThoriumParser.DirectValueContext ctx) {
        if (ctx.NUMBER() != null) {
            return new NumberValue(
                    ctx.NUMBER().getSymbol().getText()
            );
        }

        if (ctx.STRING() != null) {
            String str = ctx.STRING().getSymbol().getText();
            return new StringValue(
                    str.substring(1, str.length() - 1)
            );
        }

        if (ctx.TRUE() != null) {
            return BooleanValue.TRUE;
        }

        if (ctx.FALSE() != null) {
            return BooleanValue.FALSE;
        }

        if (ctx.NONE() != null) {
            return NoneValue.INSTANCE;
        }

        throw new IllegalStateException("Value is none of [NUMBER, STRING, TRUE, FALSE, NONE]");
    }

    @Override
    public Value visitIndirectValue(ThoriumParser.IndirectValueContext ctx) {
        if (ctx.THIS() != null) {
            return IdentifierValue.THIS;
        }

        if (ctx.indirectValue() != null) {
            return isMethodCall(ctx) ?
                    new NestedValue(
                            ctx.indirectValue().accept(this),
                            methodCall(ctx)
                    ) :
                    new NestedValue(
                            ctx.indirectValue().accept(this),
                            identifier(ctx)
                    );
        }

        if (ctx.directValue() != null) {
            return isMethodCall(ctx) ?
                    new NestedValue(
                            ctx.directValue().accept(this),
                            methodCall(ctx)
                    ) :
                    new NestedValue(
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
                ctx.IDENTIFIER().getSymbol().getText(),
                typeArguments(ctx),
                methodArguments(ctx)
        );
    }

    private List<TypeSpec> typeArguments(ThoriumParser.IndirectValueContext ctx) {
        return ctx.typeArguments() == null ?
                Collections.emptyList() :
                ctx.typeArguments().typeSpec().stream()
                        .map(ts -> ts.accept(TypeSpecVisitor.INSTANCE))
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
        return new IdentifierValue(ctx.IDENTIFIER().getSymbol().getText());
    }
}