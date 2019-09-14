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
import org.thoriumlang.compiler.ast.Method;
import org.thoriumlang.compiler.ast.MethodSignature;
import org.thoriumlang.compiler.ast.Statement;
import org.thoriumlang.compiler.ast.TypeSpecInferred;
import org.thoriumlang.compiler.ast.Visibility;
import org.thoriumlang.compiler.collections.Lists;

import java.util.Collections;
import java.util.stream.Collectors;

public class MethodDefVisitor extends ThoriumBaseVisitor<Method> {
    public static final MethodDefVisitor INSTANCE = new MethodDefVisitor();

    private MethodDefVisitor() {
        // nothing
    }

    @Override
    public Method visitMethodDef(ThoriumParser.MethodDefContext ctx) {
        return new Method(
                new MethodSignature(
                        visibility(ctx),
                        ctx.IDENTIFIER().getSymbol().getText(),
                        ctx.typeParameterDef() == null ?
                                Collections.emptyList() :
                                ctx.typeParameterDef().accept(TypeParameterDefVisitor.INSTANCE),
                        ctx.methodParameterDef().stream()
                                .map(p -> p.accept(MethodParameterVisitor.INSTANCE))
                                .collect(Collectors.toList()),
                        ctx.typeSpec() == null ?
                                TypeSpecInferred.INSTANCE :
                                ctx.typeSpec().accept(TypeSpecVisitor.INSTANCE)
                ),
                Lists.append(
                        Lists.withoutLast(ctx.statement()).stream()
                                .map(s -> s.accept(StatementVisitor.INSTANCE_FOR_NOT_LAST))
                                .collect(Collectors.toList()),
                        Lists.last(ctx.statement())
                                .map(s -> s.accept(StatementVisitor.INSTANCE_FOR_LAST))
                                .orElse(Statement.NONE_LAST_STATEMENT)
                )
        );
    }

    private Visibility visibility(ThoriumParser.MethodDefContext ctx) {
        return ctx.visibility == null ?
                Visibility.PRIVATE :
                Visibility.valueOf(ctx.visibility.getText().toUpperCase());
    }
}
