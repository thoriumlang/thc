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

import org.antlr.v4.runtime.tree.TerminalNode;
import org.thoriumlang.compiler.antlr.ThoriumBaseVisitor;
import org.thoriumlang.compiler.antlr.ThoriumParser;
import org.thoriumlang.compiler.ast.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TypeSpecVisitor extends ThoriumBaseVisitor<TypeSpec> {
    @Override
    public TypeSpec visitTypeSpec(ThoriumParser.TypeSpecContext ctx) {
        if (ctx.IDENTIFIER() != null) {
            return new TypeSpecSingle(ctx.IDENTIFIER().getSymbol().getText());
        }
        if (ctx.typeSpec() != null) {
            return ctx.typeSpec().accept(this);
        }
        if (ctx.typeSpecOptional() != null) {
            return ctx.typeSpecOptional().accept(this);
        }
        if (ctx.typeSpecUnion() != null) {
            return ctx.typeSpecUnion().accept(this);
        }
        if (ctx.typeSpecIntersection() != null) {
            return ctx.typeSpecIntersection().accept(this);
        }
        throw new IllegalStateException("Missing branch");
    }

    @Override
    public TypeSpec visitTypeSpecOptional(ThoriumParser.TypeSpecOptionalContext ctx) {
        if (ctx.IDENTIFIER() != null) {
            return new TypeSpecOptional(new TypeSpecSingle(ctx.IDENTIFIER().getSymbol().getText()));
        }

        if (ctx.typeSpec() != null) {
            return new TypeSpecOptional(ctx.typeSpec().accept(this));
        }

        if (ctx.typeSpecOptional() != null) {
            return ctx.typeSpecOptional().accept(this);
        }

        if (ctx.typeSpecUnion() != null) {
            return new TypeSpecOptional(ctx.typeSpecUnion().accept(this));
        }

        if (ctx.typeSpecIntersection() != null) {
            return new TypeSpecOptional(ctx.typeSpecIntersection().accept(this));
        }

        throw new IllegalStateException("Missing branch");
    }

    @Override
    public TypeSpec visitTypeSpecUnion(ThoriumParser.TypeSpecUnionContext ctx) {
        List<TypeSpec> types = visit(
                ctx.IDENTIFIER(),
                ctx.typeSpec(),
                ctx.typeSpecOptional()
        );
        types.addAll(
                ctx.typeSpecUnion().stream()
                        .map(t -> (TypeSpecUnion) t.accept(this))
                        .map(TypeSpecUnion::types)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList())
        );
        types.addAll(
                ctx.typeSpecIntersection().stream()
                        .map(t -> t.accept(this))
                        .collect(Collectors.toList())
        );

        return new TypeSpecUnion(types);
    }

    private List<TypeSpec> visit(List<TerminalNode> identifier,
            List<ThoriumParser.TypeSpecContext> typeSpecContexts,
            List<ThoriumParser.TypeSpecOptionalContext> typeSpecOptionalContexts) {
        List<TypeSpec> typeSpecs = new ArrayList<>();

        typeSpecs.addAll(
                identifier.stream()
                        .map(t -> new TypeSpecSingle(
                                t.getSymbol().getText()
                        ))
                        .collect(Collectors.toList())
        );
        typeSpecs.addAll(
                typeSpecContexts.stream()
                        .map(t -> t.accept(this))
                        .collect(Collectors.toList())
        );
        typeSpecs.addAll(
                typeSpecOptionalContexts.stream()
                        .map(t -> t.accept(this))
                        .collect(Collectors.toList())
        );

        return typeSpecs;
    }

    @Override
    public TypeSpec visitTypeSpecIntersection(ThoriumParser.TypeSpecIntersectionContext ctx) {
        List<TypeSpec> types = visit(
                ctx.IDENTIFIER(),
                ctx.typeSpec(),
                ctx.typeSpecOptional()
        );
        types.addAll(
                ctx.typeSpecIntersection().stream()
                        .map(t -> (TypeSpecIntersection) t.accept(this))
                        .map(TypeSpecIntersection::types)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList())
        );
        types.addAll(
                ctx.typeSpecUnion().stream()
                        .map(t -> t.accept(this))
                        .collect(Collectors.toList())
        );

        return new TypeSpecIntersection(types);
    }
}
