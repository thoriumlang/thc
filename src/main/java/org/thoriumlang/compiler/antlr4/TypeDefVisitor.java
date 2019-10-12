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
import org.thoriumlang.compiler.ast.nodes.Type;
import org.thoriumlang.compiler.ast.nodes.TypeParameter;
import org.thoriumlang.compiler.ast.nodes.TypeSpec;
import org.thoriumlang.compiler.ast.nodes.Visibility;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class TypeDefVisitor extends ThoriumBaseVisitor<Type> {
    private final NodeIdGenerator nodeIdGenerator;
    private final SourcePositionProvider<Token> sourcePositionProvider;
    private final MethodSignatureVisitor methodSignatureVisitor;
    private final TypeParameterVisitor typeParameterVisitor;
    private final TypeSpecVisitor typeSpecVisitor;

    TypeDefVisitor(
            NodeIdGenerator nodeIdGenerator,
            SourcePositionProvider<Token> sourcePositionProvider,
            MethodSignatureVisitor methodSignatureVisitor,
            TypeParameterVisitor typeParameterVisitor,
            TypeSpecVisitor typeSpecVisitor) {
        this.nodeIdGenerator = nodeIdGenerator;
        this.sourcePositionProvider = sourcePositionProvider;
        this.methodSignatureVisitor = methodSignatureVisitor;
        this.typeParameterVisitor = typeParameterVisitor;
        this.typeSpecVisitor = typeSpecVisitor;
    }

    @Override
    public Type visitTypeDef(ThoriumParser.TypeDefContext ctx) {
        return sourcePositionProvider.provide(
                new Type(
                        nodeIdGenerator.next(),
                        visibility(ctx),
                        ctx.IDENTIFIER().getSymbol().getText(),
                        typeParameters(ctx.typeParameter()),
                        implementsSpec(ctx),
                        ctx.methodSignature().stream()
                                .map(method -> method.accept(methodSignatureVisitor))
                                .collect(Collectors.toList())
                ),
                ctx.start
        );
    }

    private Visibility visibility(ThoriumParser.TypeDefContext ctx) {
        return ctx.visibility == null ?
                Visibility.NAMESPACE :
                Visibility.valueOf(ctx.visibility.getText().toUpperCase());
    }

    private List<TypeParameter> typeParameters(ThoriumParser.TypeParameterContext ctx) {
        if (ctx == null || ctx.IDENTIFIER() == null) {
            return Collections.emptyList();
        }
        return ctx.accept(typeParameterVisitor);
    }

    private TypeSpec implementsSpec(ThoriumParser.TypeDefContext ctx) {
        if (ctx.implementsSpec() == null) {
            return typeSpecVisitor.object(ctx.start);
        }
        return ctx.implementsSpec().typeSpec().accept(typeSpecVisitor);
    }
}
