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
import org.antlr.v4.runtime.tree.TerminalNode;
import org.thoriumlang.compiler.antlr.ThoriumBaseVisitor;
import org.thoriumlang.compiler.antlr.ThoriumParser;
import org.thoriumlang.compiler.ast.SourcePositionProvider;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.nodes.Use;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class UseVisitor extends ThoriumBaseVisitor<List<Use>> {
    private final NodeIdGenerator nodeIdGenerator;
    private final SourcePositionProvider<Token> sourceSourcePositionProvider;

    UseVisitor(NodeIdGenerator nodeIdGenerator, SourcePositionProvider<Token> sourceSourcePositionProvider) {
        this.nodeIdGenerator = nodeIdGenerator;
        this.sourceSourcePositionProvider = sourceSourcePositionProvider;
    }

    @Override
    public List<Use> visitUse(ThoriumParser.UseContext ctx) {
        if (ctx.baseFqIdentifier != null) {
            if (ctx.star != null) {
                return Collections.singletonList(
                        sourceSourcePositionProvider.provide(
                                new Use(
                                        nodeIdGenerator.next(),
                                        fqIdentifier(ctx.baseFqIdentifier.IDENTIFIER()) + ".*"
                                ),
                                ctx.start
                        )
                );
            }

            if (ctx.useAs() != null && !ctx.useAs().isEmpty()) {
                String base = fqIdentifier(ctx.baseFqIdentifier.IDENTIFIER());
                return ctx.useAs().stream()
                        .map(u -> getUse(base, u))
                        .collect(Collectors.toList());
            }

            return Collections.singletonList(
                    sourceSourcePositionProvider.provide(
                            new Use(
                                    nodeIdGenerator.next(),
                                    fqIdentifier(ctx.baseFqIdentifier.IDENTIFIER())
                            ),
                            ctx.start
                    )
            );
        }

        if (ctx.useAs() != null && ctx.useAs().size() == 1) {
            return Collections.singletonList(
                    sourceSourcePositionProvider.provide(
                            new Use(
                                    nodeIdGenerator.next(),
                                    fqIdentifier(ctx.useAs().get(0).fqIdentifier().IDENTIFIER()),
                                    ctx.useAs().get(0).alias.getText()
                            ),
                            ctx.start
                    )
            );
        }

        throw new IllegalStateException("should never be here");
    }

    private String fqIdentifier(List<TerminalNode> parts) {
        return parts.stream()
                .map(e -> e.getSymbol().getText())
                .collect(Collectors.joining("."));
    }

    private Use getUse(String base, ThoriumParser.UseAsContext ctx) {
        if (ctx.alias != null) {
            return sourceSourcePositionProvider.provide(
                    new Use(
                            nodeIdGenerator.next(),
                            String.format("%s.%s",
                                    base,
                                    fqIdentifier(ctx.fqIdentifier().IDENTIFIER())
                            ),
                            ctx.alias.getText()
                    ),
                    ctx.start
            );
        }
        return sourceSourcePositionProvider.provide(
                new Use(
                        nodeIdGenerator.next(),
                        String.format("%s.%s",
                                base,
                                fqIdentifier(ctx.fqIdentifier().IDENTIFIER())
                        )
                ),
                ctx.start
        );
    }
}
