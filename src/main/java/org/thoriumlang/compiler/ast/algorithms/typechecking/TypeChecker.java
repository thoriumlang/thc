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

import org.thoriumlang.compiler.api.errors.SemanticError;
import org.thoriumlang.compiler.api.errors.SymbolNotFoundError;
import org.thoriumlang.compiler.ast.algorithms.Algorithm;
import org.thoriumlang.compiler.ast.nodes.Node;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.ast.nodes.TypeSpecSimple;
import org.thoriumlang.compiler.ast.visitor.NodesMatchingVisitor;
import org.thoriumlang.compiler.collections.Lists;
import org.thoriumlang.compiler.data.Maybe;
import org.thoriumlang.compiler.data.Pair;
import org.thoriumlang.compiler.input.loaders.TypeLoader;
import org.thoriumlang.compiler.symbols.AliasSymbol;
import org.thoriumlang.compiler.symbols.Name;
import org.thoriumlang.compiler.symbols.Symbol;
import org.thoriumlang.compiler.symbols.SymbolTable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class TypeChecker implements Algorithm, TypeLoader { // TODO rename to TypeLoader
    private final NodeIdGenerator nodeIdGenerator;
    private final List<TypeLoader> typeLoaders;

    public TypeChecker(NodeIdGenerator nodeIdGenerator, List<TypeLoader> typeLoaders) {
        this.nodeIdGenerator = nodeIdGenerator;
        this.typeLoaders = Objects.requireNonNull(typeLoaders, "typeLoaders cannot be null");
    }

    @Override
    public Pair<Root, List<SemanticError>> walk(Root root) {
        List<SemanticError> discoveryErrors = root
                .accept(
                        new TypeDiscoveryVisitor(
                                root.getNamespace(),
                                this
                        )
                );

        // TODO move to TypeDiscoveryVisitor
        List<SemanticError> typeNotFoundErrors = new NodesMatchingVisitor(n -> n instanceof TypeSpecSimple)
                .visit(root).stream()
                .map(t -> (TypeSpecSimple) t)
                .filter(t -> t.getContext().require(SymbolTable.class).find(new Name(t.getType())).isEmpty())
                .map(t -> {
                    Name name = new Name(t.getType(), root.getNamespace()); // TODO make sure t is not qualified

                    if (load(name, t, t.getContext().require(SymbolTable.class))) {
                        return null;
                    }

                    return new SymbolNotFoundError(t, t.getType());
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Maybe<Root, List<SemanticError>> qualifyingResult = new TypeQualifyingVisitor(nodeIdGenerator).visit(root);

        return new Pair<>(root, Lists.merge(
                discoveryErrors,
                typeNotFoundErrors,
                qualifyingResult.isFailure() ? qualifyingResult.error() : Collections.emptyList()
        ));
    }

    private boolean load(Name name, Node node, SymbolTable symbolTable) {
        Optional<Symbol> symbol = load(name, node);

        if (symbol.isPresent()) {
            symbolTable.put(name, symbol.get());

            symbolTable               // [body]
                    .enclosingScope() // type or class
                    .put(new Name(name.getSimpleName()), new AliasSymbol(node, name.getFullName()));
            return true;
        }

        return false;
    }

    @Override
    public Optional<Symbol> load(Name name, Node triggerNode) {
        return typeLoaders.stream()
                .map(loader -> loader.load(name, triggerNode))
                .filter(Optional::isPresent)
                .findFirst()
                .orElse(Optional.empty());
    }
}
