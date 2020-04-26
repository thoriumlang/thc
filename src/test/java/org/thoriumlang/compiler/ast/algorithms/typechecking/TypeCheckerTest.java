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
import org.thoriumlang.compiler.api.CompilationContext;
import org.thoriumlang.compiler.api.Compiler;
import org.thoriumlang.compiler.api.NoopCompilationListener;
import org.thoriumlang.compiler.api.errors.CompilationError;
import org.thoriumlang.compiler.ast.AST;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.input.Source;
import org.thoriumlang.compiler.input.SourceFiles;
import org.thoriumlang.compiler.input.loaders.JavaRTClassLoader;
import org.thoriumlang.compiler.symbols.Name;
import org.thoriumlang.compiler.symbols.Symbol;
import org.thoriumlang.compiler.symbols.SymbolTable;
import org.thoriumlang.compiler.symbols.ThoriumType;
import org.thoriumlang.compiler.testsupport.SymbolsExtractionVisitor;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

class TypeCheckerTest {
    @Test
    void walk() {
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
                                        new NodeIdGenerator(),
                                        Collections.emptyList(),
                                        new SymbolTable()
                                ).root().orElseThrow(() -> new IllegalStateException("no root found"))
                        )
                        .stream()
                        .map(se -> se.format((line, column, message) -> String.format("%s (%d)", message, line)))
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
    void loadsThlibTypes() throws URISyntaxException {
        List<Optional<Root>> roots = new ArrayList<>();
        List<CompilationError> errors = new ArrayList<>();

        new Compiler(
                new NoopCompilationListener() {
                    @Override
                    public void onError(Source source, CompilationError error) {
                        errors.add(error);
                    }

                    @Override
                    public void onSourceFinished(Source source, CompilationContext context) {
                        roots.add(context.root());
                    }
                },
                Collections.emptyList()
        ).compile(
                new SourceFiles(
                        Paths.get(TypeCheckerTest.class.getResource("/org/thoriumlang/compiler/ast/algorithms").toURI()),
                        p -> p.endsWith("typechecking/Main_thlibTypes.th")
                )
        );

        Assertions.assertThat(roots.get(0))
                .isPresent();

        Assertions.assertThat(errors)
                .isEmpty();

        SymbolTable symbolTable = roots.get(0)
                .orElseThrow(() -> new IllegalStateException("no root found"))
                .getContext()
                .require(SymbolTable.class);

        Assertions.assertThat(symbolTable.find(new Name("typechecking.Main")))
                .hasSize(1);

        Assertions.assertThat(symbolTable.find(new Name("org.thoriumlang.Object")))
                .hasSize(1);
    }

    @Test
    void loadsJavalibTypes() throws URISyntaxException {
        List<Optional<Root>> roots = new ArrayList<>();
        List<CompilationError> errors = new ArrayList<>();

        new Compiler(
                new NoopCompilationListener() {
                    @Override
                    public void onError(Source source, CompilationError error) {
                        errors.add(error);
                    }

                    @Override
                    public void onSourceFinished(Source source, CompilationContext context) {
                        roots.add(context.root());
                    }
                },
                Collections.emptyList()
        ).compile(
                new SourceFiles(
                        Paths.get(TypeCheckerTest.class.getResource("/org/thoriumlang/compiler/ast/algorithms").toURI()),
                        p -> p.endsWith("typechecking/Main_javalibTypes.th")
                )
        );

        Assertions.assertThat(roots.get(0))
                .isPresent();

        Assertions.assertThat(errors)
                .isEmpty();

        SymbolTable symbolTable = roots.get(0)
                .orElseThrow(() -> new IllegalStateException("no root found"))
                .getContext()
                .require(SymbolTable.class);

        Assertions.assertThat(symbolTable.find(new Name("typechecking.Main")))
                .hasSize(1);

        Assertions.assertThat(symbolTable.find(new Name("java.lang.Object")))
                .hasSize(1);
    }

    @Test
    void loadsPackageTypes() throws URISyntaxException {
        List<Optional<Root>> roots = new ArrayList<>();
        List<CompilationError> errors = new ArrayList<>();

        new Compiler(
                new NoopCompilationListener() {
                    @Override
                    public void onError(Source source, CompilationError error) {
                        errors.add(error);
                    }

                    @Override
                    public void onSourceFinished(Source source, CompilationContext context) {
                        roots.add(context.root());
                    }
                },
                Collections.emptyList()
        ).compile(
                new SourceFiles(
                        Paths.get(TypeCheckerTest.class.getResource("/org/thoriumlang/compiler/ast/algorithms").toURI()),
                        p -> p.endsWith("typechecking/Main_packageType.th")
                )
        );

        Assertions.assertThat(roots.get(0))
                .isPresent();

        Assertions.assertThat(errors)
                .isEmpty();

        SymbolTable symbolTable = roots.get(0)
                .orElseThrow(() -> new IllegalStateException("no root found"))
                .getContext()
                .require(SymbolTable.class);

        Assertions.assertThat(symbolTable.find(new Name("typechecking.Main")))
                .hasSize(1);

        Assertions.assertThat(symbolTable.find(new Name("typechecking.CustomType")))
                .hasSize(1);

        Set<SymbolTable> rootSymbolTablesFromLoadedSymbols = symbolTable.root()
                .accept(new SymbolsExtractionVisitor())
                .stream()
                .filter(s -> s instanceof ThoriumType)
                .map(Symbol::getDefiningNode)
                .map(n -> n.getContext().require(SymbolTable.class).root())
                .collect(Collectors.toSet());

        Assertions.assertThat(rootSymbolTablesFromLoadedSymbols)
                .containsExactly(symbolTable.root());
    }
}
