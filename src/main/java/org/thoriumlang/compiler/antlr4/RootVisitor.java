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
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.ast.nodes.Use;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class RootVisitor extends ThoriumBaseVisitor<Root> {
    private final Antlr4SourcePositionProvider sourcePositionProvider;
    private final String namespace;
    private final NodeIdGenerator nodeIdGenerator;
    private final TypeDefVisitor typeDefVisitor;
    private final ClassDefVisitor classDefVisitor;
    private final UseVisitor useVisitor;

    public RootVisitor(NodeIdGenerator nodeIdGenerator, String namespace) {
        this.sourcePositionProvider = new Antlr4SourcePositionProvider();

        TypeSpecVisitor typeSpecVisitor = new TypeSpecVisitor(
                nodeIdGenerator,
                sourcePositionProvider,
                new FqIdentifierVisitor()
        );
        TypeParameterVisitor typeParameterVisitor = new TypeParameterVisitor(
                nodeIdGenerator,
                sourcePositionProvider
        );
        MethodParameterVisitor methodParameterVisitor = new MethodParameterVisitor(
                nodeIdGenerator,
                sourcePositionProvider,
                typeSpecVisitor
        );
        StatementVisitor statementVisitorForNotLast = new StatementVisitor(
                nodeIdGenerator,
                sourcePositionProvider,
                false
        );
        StatementVisitor statementVisitorForLast = new StatementVisitor(
                nodeIdGenerator,
                sourcePositionProvider,
                true
        );
        ValueVisitor valueVisitor = new ValueVisitor(
                nodeIdGenerator,
                sourcePositionProvider,
                typeParameterVisitor,
                methodParameterVisitor,
                typeSpecVisitor,
                statementVisitorForNotLast,
                statementVisitorForLast
        );
        statementVisitorForNotLast.setValueVisitor(valueVisitor);
        statementVisitorForLast.setValueVisitor(valueVisitor);

        this.nodeIdGenerator = nodeIdGenerator;
        this.namespace = namespace;
        this.typeDefVisitor = new TypeDefVisitor(
                nodeIdGenerator,
                sourcePositionProvider,
                new MethodSignatureVisitor(
                        nodeIdGenerator,
                        sourcePositionProvider,
                        methodParameterVisitor,
                        typeSpecVisitor,
                        typeParameterVisitor
                ),
                typeParameterVisitor,
                typeSpecVisitor
        );
        this.classDefVisitor = new ClassDefVisitor(
                nodeIdGenerator,
                sourcePositionProvider,
                new MethodDefVisitor(
                        nodeIdGenerator,
                        sourcePositionProvider,
                        typeParameterVisitor,
                        methodParameterVisitor,
                        typeSpecVisitor,
                        statementVisitorForNotLast,
                        statementVisitorForLast
                ),
                new AttributeDefVisitor(
                        nodeIdGenerator,
                        sourcePositionProvider,
                        typeSpecVisitor,
                        valueVisitor
                ),
                typeParameterVisitor,
                typeSpecVisitor
        );
        this.useVisitor = new UseVisitor(nodeIdGenerator, sourcePositionProvider);
    }

    @Override
    public Root visitRoot(ThoriumParser.RootContext ctx) {
        if (ctx.typeDef() != null) {
            return sourcePositionProvider.provide(
                    new Root(
                            nodeIdGenerator.next(),
                            namespace,
                            visitUse(ctx),
                            ctx.typeDef().accept(typeDefVisitor)
                    ),
                    ctx.start,
                    ctx.stop
            );
        }
        if (ctx.classDef() != null) {
            return sourcePositionProvider.provide(new Root(
                            nodeIdGenerator.next(),
                            namespace,
                            visitUse(ctx),
                            ctx.classDef().accept(classDefVisitor)
                    ),
                    ctx.start,
                    ctx.stop
            );
        }
        throw new IllegalStateException("No root node found");
    }

    private List<Use> visitUse(ThoriumParser.RootContext ctx) {
        return ctx.use().stream()
                .map(u -> u.accept(useVisitor))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
