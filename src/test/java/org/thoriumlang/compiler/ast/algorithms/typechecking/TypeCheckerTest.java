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
import org.thoriumlang.compiler.ast.AST;
import org.thoriumlang.compiler.ast.algorithms.CompilationError;
import org.thoriumlang.compiler.ast.algorithms.symboltable.SymbolTableInitializer;
import org.thoriumlang.compiler.symbols.SymbolTable;

import java.io.IOException;
import java.util.Collections;

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
                                        "namespace",
                                        Collections.singletonList(new SymbolTableInitializer(new SymbolTable()))
                                ).root()
                        )
                        .stream()
                        .map(CompilationError::toString)
        ).containsExactly(
                "symbol not found: org.thoriumlang.compiler.ast.algorithms.typechecking.TypeCheckerTest (3)",
                "symbol not found: UnknownSupertype (6)",
                "symbol not found: UnknownAttributeValType (7)",
                "symbol not found: UnknownAttributeTypeParameterType (7)",
                "symbol not found: UnknownFunctionValueParameterType1 (11)",
                "symbol not found: UnknownParameterType (15)",
                "symbol not found: UnknownMethodTypeParameterType (16)",
                "symbol not found: UnknownReturnType (19)",
                "symbol not found: UnknownValType (23)",
                "symbol not found: UnknownFunctionValueParameterType2 (28)"
        );
    }
}
