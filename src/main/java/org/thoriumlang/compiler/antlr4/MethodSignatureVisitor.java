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
import org.thoriumlang.compiler.ast.nodes.MethodSignature;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.nodes.TypeParameter;
import org.thoriumlang.compiler.ast.nodes.Visibility;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class MethodSignatureVisitor extends ThoriumBaseVisitor<MethodSignature> {
    private final NodeIdGenerator nodeIdGenerator;
    private final MethodParameterVisitor methodParameterVisitor;
    private final TypeSpecVisitor typeSpecVisitor;
    private final TypeParameterDefVisitor typeParameterDefVisitor;

    MethodSignatureVisitor(NodeIdGenerator nodeIdGenerator,
            MethodParameterVisitor methodParameterVisitor,
            TypeSpecVisitor typeSpecVisitor,
            TypeParameterDefVisitor typeParameterDefVisitor) {
        this.nodeIdGenerator = nodeIdGenerator;
        this.methodParameterVisitor = methodParameterVisitor;
        this.typeSpecVisitor = typeSpecVisitor;
        this.typeParameterDefVisitor = typeParameterDefVisitor;
    }

    @Override
    public MethodSignature visitMethodSignature(ThoriumParser.MethodSignatureContext ctx) {

        return new MethodSignature(
                nodeIdGenerator.next(),
                visibility(ctx),
                ctx.name.getText(),
                typeParameters(ctx.typeParameterDef()),
                ctx.methodParameterDef().stream()
                        .map(p -> p.accept(methodParameterVisitor))
                        .collect(Collectors.toList()),
                ctx.returnType.accept(typeSpecVisitor)
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
        return ctx.accept(typeParameterDefVisitor);
    }
}
