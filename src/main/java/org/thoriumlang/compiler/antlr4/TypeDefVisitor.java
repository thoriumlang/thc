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
import org.thoriumlang.compiler.ast.Type;
import org.thoriumlang.compiler.ast.TypeParameter;
import org.thoriumlang.compiler.ast.TypeSpec;
import org.thoriumlang.compiler.ast.Visibility;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class TypeDefVisitor extends ThoriumBaseVisitor<Type> {
    private final NodeIdGenerator nodeIdGenerator;
    private final MethodSignatureVisitor methodSignatureVisitor;
    private final TypeParameterDefVisitor typeParameterDefVisitor;
    private final TypeSpecVisitor typeSpecVisitor;

    TypeDefVisitor(NodeIdGenerator nodeIdGenerator,
            MethodSignatureVisitor methodSignatureVisitor,
            TypeParameterDefVisitor typeParameterDefVisitor,
            TypeSpecVisitor typeSpecVisitor) {
        this.nodeIdGenerator = nodeIdGenerator;
        this.methodSignatureVisitor = methodSignatureVisitor;
        this.typeParameterDefVisitor = typeParameterDefVisitor;
        this.typeSpecVisitor = typeSpecVisitor;
    }

    @Override
    public Type visitTypeDef(ThoriumParser.TypeDefContext ctx) {
        return new Type(
                nodeIdGenerator.next(),
                visibility(ctx),
                ctx.IDENTIFIER().getSymbol().getText(),
                typeParameters(ctx.typeParameterDef()),
                implementsSpec(ctx.implementsSpec()),
                ctx.methodSignature().stream()
                        .map(method -> method.accept(methodSignatureVisitor))
                        .collect(Collectors.toList())
        );
    }

    private Visibility visibility(ThoriumParser.TypeDefContext ctx) {
        return ctx.visibility == null ?
                Visibility.NAMESPACE :
                Visibility.valueOf(ctx.visibility.getText().toUpperCase());
    }

    private List<TypeParameter> typeParameters(ThoriumParser.TypeParameterDefContext ctx) {
        if (ctx == null || ctx.IDENTIFIER() == null) {
            return Collections.emptyList();
        }
        return ctx.accept(typeParameterDefVisitor);
    }

    private TypeSpec implementsSpec(ThoriumParser.ImplementsSpecContext ctx) {
        if (ctx == null) {
            return typeSpecVisitor.object();
        }
        return ctx.typeSpec().accept(typeSpecVisitor);
    }
}
