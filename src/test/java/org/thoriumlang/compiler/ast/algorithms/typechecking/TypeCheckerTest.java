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
import org.thoriumlang.compiler.SourceToAST;
import org.thoriumlang.compiler.ast.AST;
import org.thoriumlang.compiler.ast.algorithms.CompilationError;
import org.thoriumlang.compiler.ast.algorithms.symboltable.SymbolTableInitializer;
import org.thoriumlang.compiler.input.Source;
import org.thoriumlang.compiler.input.SourceFiles;
import org.thoriumlang.compiler.input.loaders.JavaRTClassLoader;
import org.thoriumlang.compiler.symbols.Name;
import org.thoriumlang.compiler.symbols.Symbol;
import org.thoriumlang.compiler.symbols.SymbolTable;
import org.thoriumlang.compiler.symbols.ThoriumType;
import org.thoriumlang.compiler.testsupport.SymbolsExtractionVisitor;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

class TypeCheckerTest {
    @Test
    void walk() throws IOException {
        Assertions.assertThat(
                new TypeChecker(
                        Collections.singletonList(
                                new JavaRTClassLoader()
                        )
                )
                        .walk(
                                new AST(
                                        TypeCheckerTest.class.getResourceAsStream(
                                                "/org/thoriumlang/compiler/ast/algorithms/typechecking/Main_discovery.th"
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

    @Test
    void loadsThlibTypes() throws URISyntaxException, IOException {
        SourceFiles sources = new SourceFiles(
                Paths.get(TypeCheckerTest.class.getResource("/org/thoriumlang/compiler/ast/algorithms").toURI()),
                p -> p.endsWith("typechecking/Main_thlibTypes.th")
        );
        Source source = sources.sources().get(0);

        AST ast = new SourceToAST(sources).apply(source);

        ast.root();

        Assertions.assertThat(ast.errors())
                .isEmpty();

        SymbolTable symbolTable = ast.root().getContext().require(SymbolTable.class);

        Assertions.assertThat(symbolTable.find(new Name("typechecking.Main")))
                .isPresent();

        Assertions.assertThat(symbolTable.find(new Name("org.thoriumlang.Object")))
                .isPresent();
    }

    @Test
    void loadsJavalibTypes() throws URISyntaxException, IOException {
        SourceFiles sources = new SourceFiles(
                Paths.get(TypeCheckerTest.class.getResource("/org/thoriumlang/compiler/ast/algorithms").toURI()),
                p -> p.endsWith("typechecking/Main_javalibTypes.th")
        );
        Source source = sources.sources().get(0);

        AST ast = new SourceToAST(sources).apply(source);

        ast.root();

        Assertions.assertThat(ast.errors())
                .isEmpty();

        SymbolTable symbolTable = ast.root().getContext().require(SymbolTable.class);

        Assertions.assertThat(symbolTable.find(new Name("typechecking.Main")))
                .isPresent();

        Assertions.assertThat(symbolTable.find(new Name("java.lang.Object")))
                .isPresent();
    }

    @Test
    void loadsPackageTypes() throws URISyntaxException, IOException {
        SourceFiles sources = new SourceFiles(
                Paths.get(TypeCheckerTest.class.getResource("/org/thoriumlang/compiler/ast/algorithms").toURI()),
                p -> p.endsWith("typechecking/Main_packageType.th")
        );
        Source source = sources.sources().get(0);

        SymbolTable rootSymbolTable = new SymbolTable();
        AST ast = new SourceToAST(sources, rootSymbolTable).apply(source);

        ast.root();

        Assertions.assertThat(ast.errors())
                .isEmpty();

        SymbolTable symbolTable = ast.root().getContext().require(SymbolTable.class);

        Assertions.assertThat(symbolTable.find(new Name("typechecking.Main")))
                .isPresent();

        Assertions.assertThat(symbolTable.find(new Name("typechecking.CustomType")))
                .isPresent();

        Set<SymbolTable> rootSymbolTablesFromLoadedSymbols = rootSymbolTable
                .accept(new SymbolsExtractionVisitor())
                .stream()
                .filter(s -> s instanceof ThoriumType)
                .map(Symbol::getNode)
                .map(n -> n.getContext().require(SymbolTable.class).root())
                .collect(Collectors.toSet());

        Assertions.assertThat(rootSymbolTablesFromLoadedSymbols)
                .containsExactly(rootSymbolTable);
    }
}
