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

import java.io.IOException;

class SymbolicNameCheckerTest {
    @Test
    void walk() throws IOException {
        Assertions.assertThat(
                new SymbolicNameChecker()
                        .walk(
                                new AST(
                                        SymbolicNameCheckerTest.class.getResourceAsStream(
                                                "/org/thoriumlang/compiler/ast/algorithms/typechecking/simple.th"
                                        ),
                                        "namespace"
                                ).root()
                        )
                        .stream()
                        .map(CompilationError::toString)
        ).isEmpty(); // TODO this should not be empty
    }
}
