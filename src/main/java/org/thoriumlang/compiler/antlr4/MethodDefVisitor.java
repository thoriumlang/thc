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
import org.thoriumlang.compiler.ast.TypeSpecSimple;
import org.thoriumlang.compiler.ast.Visibility;

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
                        Visibility.valueOf(ctx.visibility.getText().toUpperCase()),
                        ctx.IDENTIFIER().getSymbol().getText(),
                        ctx.typeParameterDef() == null ?
                                Collections.emptyList() :
                                ctx.typeParameterDef().accept(TypeParameterDefVisitor.INSTANCE),
                        ctx.methodParameterDef().stream()
                                .map(p -> p.accept(MethodParameterVisitor.INSTANCE))
                                .collect(Collectors.toList()),
                        ctx.typeSpec() == null ?
                                TypeSpecSimple.NONE :
                                ctx.typeSpec().accept(TypeSpecVisitor.INSTANCE)
                ),
                Collections.emptyList()
        );
    }
}
