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
import org.thoriumlang.compiler.ast.nodes.NoneValue;
import org.thoriumlang.compiler.ast.nodes.Statement;

class StatementVisitor extends ThoriumBaseVisitor<Statement> {
    private final NodeIdGenerator nodeIdGenerator;
    private final SourcePositionProvider<Token> sourcePositionProvider;
    private final boolean last;
    private ValueVisitor valueVisitor;

    StatementVisitor(
            NodeIdGenerator nodeIdGenerator,
            SourcePositionProvider<Token> sourcePositionProvider,
            boolean last
    ) {
        this.nodeIdGenerator = nodeIdGenerator;
        this.sourcePositionProvider = sourcePositionProvider;
        this.last = last;
    }

    void setValueVisitor(ValueVisitor valueVisitor) {
        this.valueVisitor = valueVisitor;
    }

    @Override
    public Statement visitStatement(ThoriumParser.StatementContext ctx) {
        if (valueVisitor == null) {
            throw new IllegalStateException("value visitor not set; call setValueVisitor()");
        }
        return sourcePositionProvider.provide(
                new Statement(
                        nodeIdGenerator.next(),
                        ctx.value().accept(valueVisitor),
                        last || isReturnStatement(ctx)
                ),
                ctx.start,
                ctx.stop
        );
    }

    private boolean isReturnStatement(ThoriumParser.StatementContext ctx) {
        return ctx.RETURN() != null;
    }

    public Statement none(Token start) {
        return sourcePositionProvider.provide(
                new Statement(
                        nodeIdGenerator.next(),
                        sourcePositionProvider.provide(
                                new NoneValue(nodeIdGenerator.next()),
                                start,
                                start
                        ),
                        true
                ),
                start,
                start
        );
    }
}
