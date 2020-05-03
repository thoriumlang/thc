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
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.symbols.SymbolTable;
import org.thoriumlang.compiler.testsupport.ExternalString;
import org.thoriumlang.compiler.testsupport.SymbolTableDumpingVisitor;

import java.util.Collections;

// TODO refactor, it's to big to be easily understandable
class SymbolicNameCheckerTest {
    @Test
    void walkClass() {
        SymbolTable rootSymbolTable = new SymbolTable();
        AST ast = new AST(
                SymbolicNameCheckerTest.class.getResourceAsStream(
                        "/org/thoriumlang/compiler/ast/algorithms/symbolicnamechecking/simple.th"
                ),
                "namespace",
                new NodeIdGenerator(),
                Collections.emptyList(),
                rootSymbolTable
        );
        Root root = ast.root().orElseThrow(() -> new IllegalStateException("no root found: " + ast.errors().get(0)));

        Assertions.assertThat(
                new SymbolicNameChecker()
                        .walk(root).right().stream()
                        .map(se -> se.format((sp, message) -> String.format("%s (%d)", message, sp.getStartLine())))
        ).containsExactly(
                "symbol already defined: someU (9)",
                "symbol not found: otherValue (10)",
                "symbol already defined: p2 (14)",
                "symbol already defined: someVar2 (17)",
                "symbol not found: p3 (22)",
                "symbol not found: i (26)",
                "symbol not found: i (26)",
                "symbol not found: y (32)",
                "symbol already defined: method2() (37)",
                "symbol not found: p0 (38)",
                "symbol not found: null (38)",
                "symbol not found: stuff (49)",
                "symbol not found: add(_) (26)",
                "symbol not found: add(_) (26)",
                "symbol not found: add(_) (27)"
        );

        Assertions.assertThat(
                String.join("\n", rootSymbolTable.accept(new SymbolTableDumpingVisitor(true)))
        ).isEqualTo(ExternalString.fromClasspath(
                "/org/thoriumlang/compiler/ast/algorithms/symbolicnamechecking/simple.symbols"
        ));
    }

    @Test
    void walkType() {
        SymbolTable rootSymbolTable = new SymbolTable();
        AST ast = new AST(
                SymbolicNameCheckerTest.class.getResourceAsStream(
                        "/org/thoriumlang/compiler/ast/algorithms/symbolicnamechecking/Type.th"
                ),
                "namespace",
                new NodeIdGenerator(),
                Collections.emptyList(),
                rootSymbolTable
        );
        Root root = ast.root().orElseThrow(() -> new IllegalStateException("no root found: " + ast.errors().get(0)));

        Assertions.assertThat(
                new SymbolicNameChecker()
                        .walk(root).right().stream()
                        .map(se -> se.format((sp, message) -> String.format("%s (%d)", message, sp.getStartLine())))
        ).containsExactly(
                "symbol already defined: method1() (3)"
        );

        Assertions.assertThat(
                String.join("\n", rootSymbolTable.accept(new SymbolTableDumpingVisitor(true)))
        ).isEqualTo(ExternalString.fromClasspath(
                "/org/thoriumlang/compiler/ast/algorithms/symbolicnamechecking/Type.symbols"
        ));
    }
}
