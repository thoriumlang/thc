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
import org.thoriumlang.compiler.ast.Root;
import org.thoriumlang.compiler.ast.Use;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class RootVisitor extends ThoriumBaseVisitor<Root> {
    private final String namespace;
    private final NodeIdGenerator nodeIdGenerator;
    private final TypeDefVisitor typeDefVisitor;
    private final ClassDefVisitor classDefVisitor;
    private final UseVisitor useVisitor;

    public RootVisitor(NodeIdGenerator nodeIdGenerator, String namespace) {
        TypeSpecVisitor typeSpecVisitor = new TypeSpecVisitor(
                nodeIdGenerator,
                new FqIdentifierVisitor()
        );
        TypeParameterDefVisitor typeParameterDefVisitor = new TypeParameterDefVisitor(
                nodeIdGenerator
        );
        MethodParameterVisitor methodParameterVisitor = new MethodParameterVisitor(
                nodeIdGenerator,
                typeSpecVisitor
        );
        StatementVisitor statementVisitorForNotLast = new StatementVisitor(nodeIdGenerator, false);
        StatementVisitor statementVisitorForLast = new StatementVisitor(nodeIdGenerator, true);
        ValueVisitor valueVisitor = new ValueVisitor(
                nodeIdGenerator,
                typeParameterDefVisitor,
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
                new MethodSignatureVisitor(
                        nodeIdGenerator,
                        methodParameterVisitor,
                        typeSpecVisitor,
                        typeParameterDefVisitor
                ),
                typeParameterDefVisitor,
                typeSpecVisitor
        );
        this.classDefVisitor = new ClassDefVisitor(
                nodeIdGenerator,
                new MethodDefVisitor(
                        nodeIdGenerator,
                        typeParameterDefVisitor,
                        methodParameterVisitor,
                        typeSpecVisitor,
                        statementVisitorForNotLast,
                        statementVisitorForLast
                ),
                new AttributeDefVisitor(
                        nodeIdGenerator,
                        typeSpecVisitor,
                        valueVisitor
                ),
                typeParameterDefVisitor,
                typeSpecVisitor
        );
        this.useVisitor = new UseVisitor(nodeIdGenerator);
    }

    @Override
    public Root visitRoot(ThoriumParser.RootContext ctx) {
        if (ctx.typeDef() != null) {
            return new Root(
                    nodeIdGenerator.next(),
                    namespace,
                    visitUse(ctx),
                    ctx.typeDef().accept(typeDefVisitor)
            );
        }
        if (ctx.classDef() != null) {
            return new Root(
                    nodeIdGenerator.next(),
                    namespace,
                    visitUse(ctx),
                    ctx.classDef().accept(classDefVisitor)
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
