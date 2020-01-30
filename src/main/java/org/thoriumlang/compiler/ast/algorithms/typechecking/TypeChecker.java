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

import org.thoriumlang.compiler.ast.algorithms.NodesMatching;
import org.thoriumlang.compiler.ast.nodes.Node;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.ast.nodes.TypeSpecSimple;
import org.thoriumlang.compiler.collections.Lists;
import org.thoriumlang.compiler.symbols.SymbolTable;

import java.util.List;
import java.util.stream.Collectors;

public class TypeChecker {
    public List<TypeCheckingError> walk(Root root) {
        List<TypeCheckingError> discoveryErrors = root
                .accept(
                        new TypeDiscoveryVisitor(
                                new RTJarJavaRuntimeClassLoader()
                        )
                );

        List<TypeCheckingError> typeNotFoundErrors =
                new NodesMatching(n -> n instanceof TypeSpecSimple).visit(root).stream()
                        .map(t -> (TypeSpecSimple) t)
                        .filter(t -> !getSymbolTable(t).find(t.getType()).isPresent())
                        .map(t -> new TypeCheckingError(String.format("symbol not found: %s", t.getType()), t))
                        .collect(Collectors.toList());

        return Lists.merge(
                discoveryErrors,
                typeNotFoundErrors
        );
    }

    private SymbolTable getSymbolTable(Node node) {
        return node.getContext()
                .get(SymbolTable.class)
                .orElseThrow(() -> new IllegalStateException("no symbol table found"));
    }
}
