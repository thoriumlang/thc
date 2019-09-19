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
import org.thoriumlang.compiler.ast.Attribute;
import org.thoriumlang.compiler.ast.NodeIdGenerator;
import org.thoriumlang.compiler.ast.NoneValue;
import org.thoriumlang.compiler.ast.TypeSpecInferred;
import org.thoriumlang.compiler.ast.ValAttribute;
import org.thoriumlang.compiler.ast.VarAttribute;

class AttributeDefVisitor extends ThoriumBaseVisitor<Attribute> {
    private final NodeIdGenerator nodeIdGenerator;
    private final TypeSpecVisitor typeSpecVisitor;
    private final ValueVisitor valueVisitor;

    AttributeDefVisitor(NodeIdGenerator nodeIdGenerator, TypeSpecVisitor typeSpecVisitor,
            ValueVisitor valueVisitor) {
        this.nodeIdGenerator = nodeIdGenerator;
        this.typeSpecVisitor = typeSpecVisitor;
        this.valueVisitor = valueVisitor;
    }

    @Override
    public Attribute visitAttributeDef(ThoriumParser.AttributeDefContext ctx) {
        if (ctx.VAR() != null) {
            return new VarAttribute(
                    nodeIdGenerator.next(),
                    ctx.IDENTIFIER().getSymbol().getText(),
                    ctx.typeSpec().accept(typeSpecVisitor),
                    ctx.value() == null ?
                            new NoneValue(nodeIdGenerator.next()) :
                            ctx.value().accept(valueVisitor)
            );
        }

        return new ValAttribute(
                nodeIdGenerator.next(),
                ctx.IDENTIFIER().getSymbol().getText(),
                ctx.typeSpec() == null ?
                        new TypeSpecInferred(nodeIdGenerator.next()) :
                        ctx.typeSpec().accept(typeSpecVisitor),
                ctx.value() == null ?
                        new NoneValue(nodeIdGenerator.next()) :
                        ctx.value().accept(valueVisitor)
        );
    }
}
