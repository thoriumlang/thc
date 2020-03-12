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
package org.thoriumlang.compiler.ast.api;

import org.assertj.core.util.Strings;
import org.thoriumlang.compiler.SourceToAST;
import org.thoriumlang.compiler.ast.AST;
import org.thoriumlang.compiler.ast.algorithms.symbolicnamechecking.SymbolicNameChecker;
import org.thoriumlang.compiler.ast.algorithms.symboltable.SymbolTableInitializer;
import org.thoriumlang.compiler.ast.algorithms.typechecking.TypeChecker;
import org.thoriumlang.compiler.ast.nodes.Node;
import org.thoriumlang.compiler.collections.Lists;
import org.thoriumlang.compiler.input.SourceFiles;
import org.thoriumlang.compiler.input.loaders.TypeLoader;
import org.thoriumlang.compiler.symbols.AliasSymbol;
import org.thoriumlang.compiler.symbols.Name;
import org.thoriumlang.compiler.symbols.Symbol;
import org.thoriumlang.compiler.symbols.SymbolTable;
import org.thoriumlang.compiler.symbols.SymbolicName;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

public final class Helper {
    private static final String BASEDIR = "/org/thoriumlang/compiler/ast/api/";

    private final SymbolTableInitializer symbolTableInitializer;
    private final TypeChecker typeChecker;
    private final SymbolicNameChecker symbolicNameChecker;

    private final TypeLoader typeLoader = new TypeLoader() {
        @Override
        public Optional<Symbol> load(Name name, Node node) {
            if (name.getFullName().equals("org.thoriumlang.Object")) {
                return Optional.of(new SymbolicName(node));
            }
            if (name.getFullName().equals("org.thoriumlang.None")) {
                return Optional.of(new SymbolicName(node));
            }

            System.out.println(String.format("Loading %s", name));

            AST ast = fromResource(filename(name.getFullName()), namespace(name.getFullName()));

            ast.root();
            return Optional.of(new AliasSymbol(node, name.getSimpleName()));
        }

        private String filename(String fqName) {
            return BASEDIR + "/" + fqName.replace(".", "/") + ".th";
        }

        private String namespace(String name) {
            return Strings.join(Lists.withoutLast(Arrays.asList(name.split("\\.")))).with(".");
        }
    };

    private Helper() {
        this.symbolTableInitializer = new SymbolTableInitializer(new SymbolTable());
        this.typeChecker = new TypeChecker(Collections.singletonList(typeLoader));
        this.symbolicNameChecker = new SymbolicNameChecker();
    }

    public static CompilationUnit main() {
        try {
            return new CompilationUnit(new Helper().mainAST());
        }
        catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private AST mainAST() throws URISyntaxException {
        SourceFiles sources = new SourceFiles(
                Paths.get(Helper.class.getResource(BASEDIR).toURI()),
                p -> "Main.th".equals(p.getFileName().toString())
        );
        return new SourceToAST(sources, new SymbolTable()).apply(sources.sources().get(0));
    }

    private AST fromResource(String resource, String namespace) {
        return new AST(
                Helper.class.getResourceAsStream(resource),
                namespace,
                Arrays.asList(
                        symbolTableInitializer,
                        typeChecker,
                        symbolicNameChecker
                )
        );
    }

}
