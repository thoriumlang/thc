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
import org.thoriumlang.compiler.ast.NodeIdGenerator;
import org.thoriumlang.compiler.ast.NoneValue;
import org.thoriumlang.compiler.ast.Statement;

class StatementVisitor extends ThoriumBaseVisitor<Statement> {
    private final NodeIdGenerator nodeIdGenerator;
    private final boolean last;
    private ValueVisitor valueVisitor;

    StatementVisitor(NodeIdGenerator nodeIdGenerator, boolean last) {
        this.nodeIdGenerator = nodeIdGenerator;
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
        return new Statement(
                nodeIdGenerator.next(),
                ctx.value().accept(valueVisitor),
                last || isReturnStatement(ctx)
        );
    }

    private boolean isReturnStatement(ThoriumParser.StatementContext ctx) {
        return ctx.RETURN() != null;
    }

    public Statement none() {
        return new Statement(
                nodeIdGenerator.next(),
                new NoneValue(nodeIdGenerator.next()),
                true
        );
    }
}
