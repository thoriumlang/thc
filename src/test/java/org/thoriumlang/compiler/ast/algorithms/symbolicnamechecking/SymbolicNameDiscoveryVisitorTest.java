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
package org.thoriumlang.compiler.ast.algorithms.symbolicnamechecking;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.ast.AST;
import org.thoriumlang.compiler.ast.algorithms.CompilationError;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.symbols.SymbolTable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

class SymbolicNameDiscoveryVisitorTest {
    private SymbolicNameDiscoveryVisitor visitor;

    @BeforeEach
    void setup() {
        visitor = new SymbolicNameDiscoveryVisitor();
    }

    @Test
    void fullTable() throws IOException {
        Root root = new AST(
                SymbolicNameDiscoveryVisitorTest.class.getResourceAsStream(
                        "/org/thoriumlang/compiler/ast/algorithms/symbolicnamechecking/simple.th"
                ),
                "namespace"
        ).root();

        List<CompilationError> errors = visitor.visit(root);

        Assertions.assertThat(
                errors.stream()
                        .map(CompilationError::toString)
                        .collect(Collectors.toList())
        ).containsExactly(
                "symbol already defined: someU (10)",
                "symbol already defined: p4 (20)",
                "symbol already defined: someVal2 (28)",
                "symbol already defined: someVar4 (29)",
                "symbol already defined: method2 (43)"
        );

        Assertions.assertThat(
                root.getContext()
                        .get(SymbolTable.class)
                        .orElseThrow(() -> new IllegalStateException("no symbol table found"))
                        .toString()

        ).isEqualTo(
                new BufferedReader(
                        new InputStreamReader(
                                SymbolicNameDiscoveryVisitorTest.class.getResourceAsStream(
                                        "/org/thoriumlang/compiler/ast/algorithms/symbolicnamechecking/simple.symbols"
                                )
                        )
                ).lines().collect(Collectors.joining("\n"))
        );
    }
}
