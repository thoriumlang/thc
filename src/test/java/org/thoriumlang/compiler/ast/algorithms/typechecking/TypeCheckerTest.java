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

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.ast.nodes.AST;

import java.io.IOException;

class TypeCheckerTest {
    @Test
    void walk() throws IOException {
        Assertions.assertThat(
                new TypeChecker()
                        .walk(
                                new AST(
                                        TypeCheckerTest.class.getResourceAsStream(
                                                "/org/thoriumlang/compiler/ast/algorithms/typechecking/simple.th"
                                        ),
                                        "namespace"
                                ).root()
                        )
                        .stream()
                        .map(TypeCheckingError::toString)
        ).containsExactly(
                "symbol not found: org.thoriumlang.compiler.ast.algorithms.typechecking.TypeCheckerTest",
                "symbol not found: UnknownSupertype",
                "symbol not found: UnknownAttributeValType",
                "symbol not found: UnknownParameterType",
                "symbol not found: UnknownTypeParameterType",
                "symbol not found: UnknownReturnType",
                "symbol not found: UnknownValType"
        );
    }
}
