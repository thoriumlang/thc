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
import org.thoriumlang.compiler.ast.nodes.TypeSpec;
import org.thoriumlang.compiler.ast.nodes.TypeSpecFunction;
import org.thoriumlang.compiler.ast.nodes.TypeSpecIntersection;
import org.thoriumlang.compiler.ast.nodes.TypeSpecSimple;
import org.thoriumlang.compiler.ast.nodes.TypeSpecUnion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class TypeSpecVisitor extends ThoriumBaseVisitor<TypeSpec> {
    private final NodeIdGenerator nodeIdGenerator;
    private final SourcePositionProvider<Token> sourcePositionProvider;
    private final FqIdentifierVisitor fqIdentifierVisitor;

    TypeSpecVisitor(
            NodeIdGenerator nodeIdGenerator,
            SourcePositionProvider<Token> sourceSourcePositionProvider,
            FqIdentifierVisitor fqIdentifierVisitor) {
        this.nodeIdGenerator = nodeIdGenerator;
        this.sourcePositionProvider = sourceSourcePositionProvider;
        this.fqIdentifierVisitor = fqIdentifierVisitor;
    }

    @Override
    public TypeSpec visitTypeSpec(ThoriumParser.TypeSpecContext ctx) {
        if (ctx.typeSpecSimple() != null) {
            return ctx.typeSpecSimple().accept(this);
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
        if (ctx.typeSpecFunction() != null) {
            return ctx.typeSpecFunction().accept(this);
        }
        throw new IllegalStateException("Missing branch");
    }

    @Override
    public TypeSpec visitTypeSpecSimple(ThoriumParser.TypeSpecSimpleContext ctx) {
        return sourcePositionProvider.provide(
                new TypeSpecSimple(
                        nodeIdGenerator.next(),
                        ctx.fqIdentifier().accept(fqIdentifierVisitor),
                        visitArguments(ctx.typeArguments())
                ),
                ctx.start,
                ctx.stop
        );
    }

    private List<TypeSpec> visitArguments(ThoriumParser.TypeArgumentsContext ctx) {
        if (ctx == null) {
            return Collections.emptyList();
        }
        return ctx.typeSpec().stream()
                .map(e -> e.accept(this))
                .collect(Collectors.toList());
    }

    @Override
    public TypeSpec visitTypeSpecOptional(ThoriumParser.TypeSpecOptionalContext ctx) {
        if (ctx.typeSpecSimple() != null) {
            return sourcePositionProvider.provide(
                    new TypeSpecIntersection(
                            nodeIdGenerator.next(),
                            Arrays.asList(
                                    ctx.typeSpecSimple().accept(this),
                                    none(ctx.start)
                            )
                    ),
                    ctx.start,
                    ctx.stop
            );
        }

        if (ctx.typeSpec() != null) {
            return sourcePositionProvider.provide(
                    new TypeSpecIntersection(
                            nodeIdGenerator.next(),
                            Arrays.asList(
                                    ctx.typeSpec().accept(this),
                                    none(ctx.start)
                            )
                    ),
                    ctx.start,
                    ctx.stop
            );
        }

        if (ctx.typeSpecOptional() != null) {
            return ctx.typeSpecOptional().accept(this);
        }

        if (ctx.typeSpecUnion() != null) {
            return sourcePositionProvider.provide(
                    new TypeSpecIntersection(
                            nodeIdGenerator.next(),
                            Arrays.asList(
                                    ctx.typeSpecUnion().accept(this),
                                    none(ctx.start)
                            )
                    ),
                    ctx.start,
                    ctx.stop
            );
        }

        if (ctx.typeSpecIntersection() != null) {
            return sourcePositionProvider.provide(
                    new TypeSpecIntersection(
                            nodeIdGenerator.next(),
                            Arrays.asList(
                                    ctx.typeSpecIntersection().accept(this),
                                    none(ctx.start)
                            )
                    ),
                    ctx.start,
                    ctx.stop
            );
        }

        throw new IllegalStateException("Missing branch");
    }

    @Override
    public TypeSpec visitTypeSpecUnion(ThoriumParser.TypeSpecUnionContext ctx) {
        List<TypeSpec> types = visit(
                ctx.typeSpecSimple(),
                ctx.typeSpec(),
                ctx.typeSpecOptional()
        );
        types.addAll(
                ctx.typeSpecUnion().stream()
                        .map(t -> (TypeSpecUnion) t.accept(this))
                        .map(TypeSpecUnion::getTypes)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList())
        );
        types.addAll(
                ctx.typeSpecIntersection().stream()
                        .map(t -> t.accept(this))
                        .collect(Collectors.toList())
        );

        return sourcePositionProvider.provide(
                new TypeSpecUnion(
                        nodeIdGenerator.next(),
                        types
                ),
                ctx.start,
                ctx.stop
        );
    }

    private List<TypeSpec> visit(List<ThoriumParser.TypeSpecSimpleContext> specSimpleContexts,
            List<ThoriumParser.TypeSpecContext> typeSpecContexts,
            List<ThoriumParser.TypeSpecOptionalContext> typeSpecOptionalContexts) {
        List<TypeSpec> typeSpecs = new ArrayList<>();

        typeSpecs.addAll(
                specSimpleContexts.stream()
                        .map(t -> t.accept(this))
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
                ctx.typeSpecSimple(),
                ctx.typeSpec(),
                ctx.typeSpecOptional()
        );
        types.addAll(
                ctx.typeSpecIntersection().stream()
                        .map(t -> (TypeSpecIntersection) t.accept(this))
                        .map(TypeSpecIntersection::getTypes)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList())
        );
        types.addAll(
                ctx.typeSpecUnion().stream()
                        .map(t -> t.accept(this))
                        .collect(Collectors.toList())
        );

        return sourcePositionProvider.provide(
                new TypeSpecIntersection(
                        nodeIdGenerator.next(),
                        types
                ),
                ctx.start,
                ctx.stop
        );
    }

    @Override
    public TypeSpec visitTypeSpecFunction(ThoriumParser.TypeSpecFunctionContext ctx) {
        return sourcePositionProvider.provide(
                new TypeSpecFunction(
                        nodeIdGenerator.next(),
                        visitArguments(ctx.typeArguments()),
                        ctx.typeSpec().accept(this)
                ),
                ctx.start,
                ctx.stop
        );
    }

    public TypeSpecSimple object(Token token) {
        return sourcePositionProvider.provide(
                new TypeSpecSimple(
                        nodeIdGenerator.next(),
                        "org.thoriumlang.Object",
                        Collections.emptyList()
                ),
                token,
                token
        );
    }

    public TypeSpecSimple none(Token token) {
        return sourcePositionProvider.provide(
                new TypeSpecSimple(
                        nodeIdGenerator.next(),
                        "org.thoriumlang.None",
                        Collections.emptyList()
                ),
                token,
                token
        );
    }
}
