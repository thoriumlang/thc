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
import org.thoriumlang.compiler.ast.Type;
import org.thoriumlang.compiler.ast.TypeParameter;
import org.thoriumlang.compiler.ast.TypeSpec;
import org.thoriumlang.compiler.ast.TypeSpecSingle;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TypeDefVisitor extends ThoriumBaseVisitor<Type> {
    private static final MethodSignatureVisitor methodSignatureVisitor = new MethodSignatureVisitor();

    @Override
    public Type visitTypeDef(ThoriumParser.TypeDefContext ctx) {
        return new Type(
                ctx.IDENTIFIER().getSymbol().getText(),
                typeParameters(ctx.typeParameterDef()),
                implementsSpec(ctx.implementsSpec()),
                ctx.methodSignature().stream()
                        .map(method -> method.accept(methodSignatureVisitor))
                        .collect(Collectors.toList())
        );
    }

    private List<TypeParameter> typeParameters(ThoriumParser.TypeParameterDefContext ctx) {
        if (ctx == null || ctx.IDENTIFIER() == null) {
            return Collections.emptyList();
        }
        return ctx.IDENTIFIER().stream()
                .map(i -> new TypeParameter(i.getSymbol().getText()))
                .collect(Collectors.toList());
    }

    private TypeSpec implementsSpec(ThoriumParser.ImplementsSpecContext ctx) {
        if (ctx == null) {
            return TypeSpecSingle.OBJECT;
        }
        return ctx.typeSpec().accept(new TypeSpecVisitor());
    }
}
