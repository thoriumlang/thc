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
import org.thoriumlang.compiler.ast.nodes.Class;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.nodes.TypeParameter;
import org.thoriumlang.compiler.ast.nodes.TypeSpec;
import org.thoriumlang.compiler.ast.nodes.Visibility;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class ClassDefVisitor extends ThoriumBaseVisitor<Class> {
    private final NodeIdGenerator nodeIdGenerator;
    private final MethodDefVisitor methodDefVisitor;
    private final AttributeDefVisitor attributeDefVisitor;
    private final TypeParameterVisitor typeParameterVisitor;
    private final TypeSpecVisitor typeSpecVisitor;

    ClassDefVisitor(NodeIdGenerator nodeIdGenerator, MethodDefVisitor methodDefVisitor,
            AttributeDefVisitor attributeDefVisitor,
            TypeParameterVisitor typeParameterVisitor,
            TypeSpecVisitor typeSpecVisitor) {
        this.nodeIdGenerator = nodeIdGenerator;
        this.methodDefVisitor = methodDefVisitor;
        this.attributeDefVisitor = attributeDefVisitor;
        this.typeParameterVisitor = typeParameterVisitor;
        this.typeSpecVisitor = typeSpecVisitor;
    }

    @Override
    public Class visitClassDef(ThoriumParser.ClassDefContext ctx) {
        return new Class(
                nodeIdGenerator.next(),
                visibility(ctx),
                ctx.IDENTIFIER().getSymbol().getText(),
                typeParameters(ctx.typeParameter()),
                implementsSpec(ctx.implementsSpec()),
                ctx.methodDef() == null ?
                        Collections.emptyList() :
                        ctx.methodDef().stream()
                                .map(m -> m.accept(methodDefVisitor))
                                .collect(Collectors.toList()),
                ctx.attributeDef() == null ?
                        Collections.emptyList() :
                        ctx.attributeDef().stream()
                                .map(a -> a.accept(attributeDefVisitor))
                                .collect(Collectors.toList())
        );
    }

    private Visibility visibility(ThoriumParser.ClassDefContext ctx) {
        return ctx.visibility == null ?
                Visibility.NAMESPACE :
                Visibility.valueOf(ctx.visibility.getText().toUpperCase());
    }

    private List<TypeParameter> typeParameters(ThoriumParser.TypeParameterContext ctx) {
        return (ctx == null || ctx.IDENTIFIER() == null) ?
                Collections.emptyList() :
                ctx.accept(typeParameterVisitor);
    }

    private TypeSpec implementsSpec(ThoriumParser.ImplementsSpecContext ctx) {
        return ctx == null ?
                typeSpecVisitor.object() :
                ctx.typeSpec().accept(typeSpecVisitor);
    }
}
