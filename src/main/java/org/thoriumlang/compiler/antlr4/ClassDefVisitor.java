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
import org.thoriumlang.compiler.ast.Class;
import org.thoriumlang.compiler.ast.TypeParameter;
import org.thoriumlang.compiler.ast.TypeSpec;
import org.thoriumlang.compiler.ast.TypeSpecSimple;
import org.thoriumlang.compiler.ast.Visibility;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class ClassDefVisitor extends ThoriumBaseVisitor<Class> {
    private final MethodDefVisitor methodDefVisitor;
    private final AttributeDefVisitor attributeDefVisitor;
    private final TypeParameterDefVisitor typeParameterDefVisitor;
    private final TypeSpecVisitor typeSpecVisitor;

    ClassDefVisitor(MethodDefVisitor methodDefVisitor,
            AttributeDefVisitor attributeDefVisitor,
            TypeParameterDefVisitor typeParameterDefVisitor,
            TypeSpecVisitor typeSpecVisitor) {
        this.methodDefVisitor = methodDefVisitor;
        this.attributeDefVisitor = attributeDefVisitor;
        this.typeParameterDefVisitor = typeParameterDefVisitor;
        this.typeSpecVisitor = typeSpecVisitor;
    }

    @Override
    public Class visitClassDef(ThoriumParser.ClassDefContext ctx) {
        return new Class(
                visibility(ctx),
                ctx.IDENTIFIER().getSymbol().getText(),
                typeParameters(ctx.typeParameterDef()),
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

    private List<TypeParameter> typeParameters(ThoriumParser.TypeParameterDefContext ctx) {
        return (ctx == null || ctx.IDENTIFIER() == null) ?
                Collections.emptyList() :
                ctx.accept(typeParameterDefVisitor);
    }

    private TypeSpec implementsSpec(ThoriumParser.ImplementsSpecContext ctx) {
        return ctx == null ?
                TypeSpecSimple.OBJECT :
                ctx.typeSpec().accept(typeSpecVisitor);
    }
}
