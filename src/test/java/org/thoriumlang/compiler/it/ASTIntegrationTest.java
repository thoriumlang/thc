/*
 * Copyright 2020 Christophe Pollet
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
package org.thoriumlang.compiler.it;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.ast.AST;
import org.thoriumlang.compiler.ast.algorithms.CompilationError;
import org.thoriumlang.compiler.ast.algorithms.symbolicnamechecking.SymbolicNameChecker;
import org.thoriumlang.compiler.ast.algorithms.typechecking.TypeChecker;
import org.thoriumlang.compiler.ast.nodes.Class;
import org.thoriumlang.compiler.ast.nodes.FunctionValue;
import org.thoriumlang.compiler.ast.nodes.Method;
import org.thoriumlang.compiler.ast.nodes.NewAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.Node;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.ast.nodes.Statement;
import org.thoriumlang.compiler.ast.nodes.Type;
import org.thoriumlang.compiler.ast.nodes.Use;
import org.thoriumlang.compiler.ast.visitor.BaseVisitor;
import org.thoriumlang.compiler.ast.visitor.FlatMapVisitor;
import org.thoriumlang.compiler.collections.Lists;
import org.thoriumlang.compiler.symbols.SymbolTable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Disabled
class ASTIntegrationTest {
    @Test
    void symbols() throws IOException {
        AST ast = new AST(
                ASTIntegrationTest.class.getResourceAsStream(
                        "/org/thoriumlang/compiler/ast/algorithms/typechecking/simple.th"
                ),
                "org.thoriumlang",
                Arrays.asList(
                        new TypeChecker(),
                        new SymbolicNameChecker()
                )
        );

        Root root = ast.root();

        List<CompilationError> errors = ast.errors();

        List<Node> nodes = root.accept(new BaseVisitor<List<Node>>() {
            @Override
            public List<Node> visit(Root node) {
                return Lists.merge(
                        node.getUses().stream()
                                .map(n -> n.accept(this))
                                .flatMap(List::stream)
                                .collect(Collectors.toList()),
                        node.getTopLevelNode().accept(this)
                );
            }

            @Override
            public List<Node> visit(Use node) {
                return Collections.singletonList(node);
            }

            @Override
            public List<Node> visit(Type node) {
                return Lists.merge(
                        new ArrayList<>(Collections.singletonList(node)),
                        new ArrayList<>(node.getTypeParameters()),
                        new ArrayList<>(node.getMethods())
                );
            }

            @Override
            public List<Node> visit(Class node) {
                return Lists.merge(
                        new ArrayList<>(Collections.singletonList(node)),
                        new ArrayList<>(node.getTypeParameters()),
                        new ArrayList<>(node.getAttributes()),
                        node.getMethods().stream()
                                .map(n -> n.accept(this))
                                .flatMap(List::stream)
                                .collect(Collectors.toList())
                );
            }

            @Override
            public List<Node> visit(Method node) {
                return Lists.merge(
                        new ArrayList<>(Collections.singletonList(node)),
                        new ArrayList<>(node.getSignature().getTypeParameters()),
                        new ArrayList<>(node.getSignature().getParameters()),
                        node.getStatements().stream()
                                .map(n -> n.accept(this))
                                .flatMap(List::stream)
                                .collect(Collectors.toList())
                );
            }

            @Override
            public List<Node> visit(Statement node) {
                return Optional.ofNullable(node.getValue().accept(this))
                        .orElse(Collections.emptyList());
            }

            @Override
            public List<Node> visit(NewAssignmentValue node) {
                return new ArrayList<>(Collections.singletonList(node));
            }

            @Override
            public List<Node> visit(FunctionValue node) {
                return Lists.merge(
                        new ArrayList<>(node.getParameters()),
                        new ArrayList<>(node.getTypeParameters()),
                        node.getStatements().stream()
                                .map(n -> n.accept(this))
                                .flatMap(List::stream)
                                .collect(Collectors.toList())
                );
            }
        });

        Set<SymbolTable> symbolTables = new HashSet<>(
                root.accept(
                        new FlatMapVisitor<>(
                                n -> Collections.singletonList(
                                        n.getContext()
                                                .get(SymbolTable.class)
                                                .orElseThrow(() -> new IllegalStateException("no symbol table found"))
                                )
                        )
                )
        );

        nodes.stream()
                .filter(n -> errors.stream().noneMatch(e -> e.getNode() == n))
                .forEach(
                        n -> {
                            Assertions.assertThat(
                                    symbolTables.stream()
                                            .anyMatch(t -> t.symbolsStream().anyMatch(s -> s.getNode() == n))
                            ).as("%s", n).isTrue();
                        }
                );
    }
}
