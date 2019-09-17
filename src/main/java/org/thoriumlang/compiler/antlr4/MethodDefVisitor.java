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
import org.thoriumlang.compiler.ast.NodeIdGenerator;
import org.thoriumlang.compiler.ast.TypeSpecInferred;
import org.thoriumlang.compiler.ast.Visibility;
import org.thoriumlang.compiler.collections.Lists;

import java.util.Collections;
import java.util.stream.Collectors;

class MethodDefVisitor extends ThoriumBaseVisitor<Method> {
    private final NodeIdGenerator nodeIdGenerator;
    private final TypeParameterDefVisitor typeParameterDefVisitor;
    private final MethodParameterVisitor methodParameterVisitor;
    private final TypeSpecVisitor typeSpecVisitor;
    private final StatementVisitor statementVisitorForNotLast;
    private final StatementVisitor statementVisitorForLast;

    MethodDefVisitor(NodeIdGenerator nodeIdGenerator,
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
    public Method visitMethodDef(ThoriumParser.MethodDefContext ctx) {
        return new Method(
                nodeIdGenerator.next(),
                new MethodSignature(
                        nodeIdGenerator.next(),
                        visibility(ctx),
                        ctx.IDENTIFIER().getSymbol().getText(),
                        ctx.typeParameterDef() == null ?
                                Collections.emptyList() :
                                ctx.typeParameterDef().accept(typeParameterDefVisitor),
                        ctx.methodParameterDef().stream()
                                .map(p -> p.accept(methodParameterVisitor))
                                .collect(Collectors.toList()),
                        ctx.typeSpec() == null ?
                                new TypeSpecInferred(nodeIdGenerator.next()) :
                                ctx.typeSpec().accept(typeSpecVisitor)
                ),
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

    private Visibility visibility(ThoriumParser.MethodDefContext ctx) {
        return ctx.visibility == null ?
                Visibility.PRIVATE :
                Visibility.valueOf(ctx.visibility.getText().toUpperCase());
    }
}
