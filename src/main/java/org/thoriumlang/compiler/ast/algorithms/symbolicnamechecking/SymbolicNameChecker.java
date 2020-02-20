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
package org.thoriumlang.compiler.ast.algorithms.symbolicnamechecking;

import org.thoriumlang.compiler.ast.algorithms.Algorithm;
import org.thoriumlang.compiler.ast.algorithms.CompilationError;
import org.thoriumlang.compiler.ast.algorithms.NodesMatching;
import org.thoriumlang.compiler.ast.context.SourcePosition;
import org.thoriumlang.compiler.ast.nodes.DirectAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.IdentifierValue;
import org.thoriumlang.compiler.ast.nodes.IndirectAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.MethodCallValue;
import org.thoriumlang.compiler.ast.nodes.NewAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.Node;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.ast.visitor.BaseVisitor;
import org.thoriumlang.compiler.collections.Lists;
import org.thoriumlang.compiler.symbols.SymbolTable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SymbolicNameChecker implements Algorithm {
    @Override
    public List<CompilationError> walk(Root root) {
        List<CompilationError> symbolDiscoveryErrors = root.accept(
                new SymbolicNameDiscoveryVisitor()
        );

        List<CompilationError> symbolNotFoundErrors = new NodesMatching(
                n -> Arrays.asList(
                        IdentifierValue.class,
                        DirectAssignmentValue.class,
                        IndirectAssignmentValue.class,
                        NewAssignmentValue.class,
                        MethodCallValue.class
                ).contains(n.getClass())
        )
                .visit(root)
                .stream()
                .map(n -> n.accept(new BaseVisitor<List<CompilationError>>() {
                    @Override
                    public List<CompilationError> visit(IdentifierValue node) {
                        if (!getSymbolTable(node).find(node.getValue()).isPresent()) {
                            return Collections.singletonList(error(node.getValue(), node));
                        }
                        return Collections.emptyList();
                    }

                    @Override
                    public List<CompilationError> visit(DirectAssignmentValue node) {
                        if (!getSymbolTable(node).find(node.getIdentifier()).isPresent()) {
                            return Collections.singletonList(error(node.getIdentifier(), node));
                        }
                        return Collections.emptyList();
                    }

                    @Override
                    public List<CompilationError> visit(IndirectAssignmentValue node) {
                        if (!getSymbolTable(node).find(node.getIdentifier()).isPresent()) {
                            return Collections.singletonList(error(node.getIdentifier(), node));
                        }
                        return Collections.emptyList();
                    }

                    @Override
                    public List<CompilationError> visit(NewAssignmentValue node) {
                        if (!getSymbolTable(node).find(node.getIdentifier()).isPresent()) {
                            return Collections.singletonList(error(node.getIdentifier(), node));
                        }
                        return Collections.emptyList();
                    }

                    @Override
                    public List<CompilationError> visit(MethodCallValue node) {
                        if (!getSymbolTable(node).find(node.getMethodName()).isPresent()) {
                            return Collections.singletonList(error(node.getMethodName(), node));
                        }
                        return Collections.emptyList();
                    }
                }))
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        return Lists.merge(symbolDiscoveryErrors, symbolNotFoundErrors);
    }

    private SymbolTable getSymbolTable(Node node) {
        return node.getContext()
                .get(SymbolTable.class)
                .orElseThrow(() -> new IllegalStateException("no symbol table found"));
    }

    private CompilationError error(String name, Node node) {
        return new CompilationError(String.format(
                "symbol not found: %s (%d)",
                name,
                node.getContext().get(SourcePosition.class)
                        .orElseThrow(() -> new IllegalStateException("no source position found"))
                        .getLine()
        ), node);
    }
}
