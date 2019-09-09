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
import org.thoriumlang.compiler.ast.MethodSignature;
import org.thoriumlang.compiler.ast.TypeParameter;
import org.thoriumlang.compiler.ast.Visibility;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MethodSignatureVisitor extends ThoriumBaseVisitor<MethodSignature> {
    public static final MethodSignatureVisitor INSTANCE = new MethodSignatureVisitor();

    private MethodSignatureVisitor() {
        // nothing
    }

    @Override
    public MethodSignature visitMethodSignature(ThoriumParser.MethodSignatureContext ctx) {

        return new MethodSignature(
                visibility(ctx),
                ctx.name.getText(),
                typeParameters(ctx.typeParameterDef()),
                ctx.methodParameterDef().stream()
                        .map(p -> p.accept(MethodParameterVisitor.INSTANCE))
                        .collect(Collectors.toList()),
                ctx.returnType.accept(TypeSpecVisitor.INSTANCE)
        );
    }

    private Visibility visibility(ThoriumParser.MethodSignatureContext ctx) {
        return ctx.visibility == null ?
                Visibility.NAMESPACE :
                Visibility.valueOf(ctx.visibility.getText().toUpperCase());
    }

    private List<TypeParameter> typeParameters(ThoriumParser.TypeParameterDefContext ctx) {
        if (ctx == null || ctx.IDENTIFIER() == null) {
            return Collections.emptyList();
        }
        return ctx.accept(TypeParameterDefVisitor.INSTANCE);
    }
}
