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
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.ast.AST;
import org.thoriumlang.compiler.ast.algorithms.CompilationError;
import org.thoriumlang.compiler.ast.algorithms.symboltable.SymbolTableInitializer;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.symbols.SymbolTable;
import org.thoriumlang.compiler.testsupport.SymbolTableDumpingVisitor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.stream.Collectors;

class SymbolicNameCheckerTest {
    @Test
    void walk() {
        SymbolTable rootSymbolTable = new SymbolTable();
        Root root = new AST(
                SymbolicNameCheckerTest.class.getResourceAsStream(
                        "/org/thoriumlang/compiler/ast/algorithms/symbolicnamechecking/simple.th"
                ),
                "namespace",
                new NodeIdGenerator(),
                Collections.singletonList(
                        new SymbolTableInitializer(
                                rootSymbolTable//.createScope("namespace")
                        )
                )
        ).root();

        Assertions.assertThat(
                new SymbolicNameChecker()
                        .walk(root)
                        .stream()
                        .map(CompilationError::toString)
        ).containsExactly(
                "symbol already defined: someU (9)",
                "symbol not found: otherValue (10)",
                "symbol already defined: method1 (14)",
                "symbol already defined: p2 (14)",
                "symbol already defined: someVar2 (17)",
                "symbol not found: p3 (22)",
                "symbol not found: i (26)",
                "symbol not found: add (26)",
                "symbol not found: add (26)",
                "symbol not found: i (26)",
                "symbol not found: add (27)",
                "symbol not found: y (32)",
                "symbol already defined: method2 (37)",
                "symbol not found: p0 (38)",
                "symbol not found: null (38)",
                "symbol already defined: someValue (41)"
        );

        Assertions.assertThat(
                String.join("\n", rootSymbolTable.accept(new SymbolTableDumpingVisitor(true)))
        )
                .isEqualTo(
                        new BufferedReader(
                                new InputStreamReader(
                                        SymbolicNameCheckerTest.class.getResourceAsStream(
                                                "/org/thoriumlang/compiler/ast/algorithms/symbolicnamechecking/simple.symbols"
                                        )
                                )
                        ).lines().collect(Collectors.joining("\n"))
                );
    }
}
