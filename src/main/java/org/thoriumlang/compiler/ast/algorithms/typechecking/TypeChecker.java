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
package org.thoriumlang.compiler.ast.algorithms.typechecking;

import org.thoriumlang.compiler.ast.algorithms.Algorithm;
import org.thoriumlang.compiler.ast.algorithms.CompilationError;
import org.thoriumlang.compiler.ast.algorithms.NodesMatching;
import org.thoriumlang.compiler.ast.context.SourcePosition;
import org.thoriumlang.compiler.ast.nodes.Node;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.ast.nodes.TypeSpecSimple;
import org.thoriumlang.compiler.collections.Lists;
import org.thoriumlang.compiler.symbols.Name;
import org.thoriumlang.compiler.symbols.Symbol;
import org.thoriumlang.compiler.symbols.SymbolTable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class TypeChecker implements Algorithm, TypeLoader {
    private final List<TypeLoader> typeLoaders;

    public TypeChecker(List<TypeLoader> typeLoaders) {
        this.typeLoaders = typeLoaders;
    }

    @Override
    public List<CompilationError> walk(Root root) {
        List<CompilationError> discoveryErrors = root
                .accept(
                        new TypeDiscoveryVisitor(
                                root.getNamespace(),
                                this
                        )
                );

        List<CompilationError> typeNotFoundErrors = new NodesMatching(n -> n instanceof TypeSpecSimple)
                .visit(root).stream()
                .map(t -> (TypeSpecSimple) t)
                .filter(t -> !getSymbolTable(t).find(new Name(t.getType())).isPresent())
                .map(t -> {
                    String fqName = t.getType().contains(".")
                            ? t.getType()
                            : root.getNamespace() + "." + t.getType();

                    if (load(fqName, t, root.getContext().require(SymbolTable.class))) {
                        return null;
                    }

                    return new CompilationError(String.format("symbol not found: %s (%d)",
                            t.getType(),
                            t.getContext().require(SourcePosition.class).getLine()
                    ), t);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return Lists.merge(
                discoveryErrors,
                typeNotFoundErrors
        );
    }

    private boolean load(String fqName, Node node, SymbolTable symbolTable) {
        Optional<Symbol> symbol = load(fqName, node);

        if (symbol.isPresent()) {
            symbolTable.put(new Name(fqName), symbol.get());
            return true;
        }

        return false;
    }

    @Override
    public Optional<Symbol> load(String fqName, Node node) {
        return typeLoaders.stream()
                .map(loader -> loader.load(fqName, node))
                .filter(Optional::isPresent)
                .findFirst()
                .orElse(Optional.empty());
    }

    private SymbolTable getSymbolTable(Node node) {
        return node.getContext()
                .get(SymbolTable.class)
                .orElseThrow(() -> new IllegalStateException("no symbol table found"));
    }
}
