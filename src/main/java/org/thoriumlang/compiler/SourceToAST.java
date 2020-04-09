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
package org.thoriumlang.compiler;

import org.thoriumlang.compiler.ast.AST;
import org.thoriumlang.compiler.ast.algorithms.symbolicnamechecking.SymbolicNameChecker;
import org.thoriumlang.compiler.ast.algorithms.symboltable.SymbolTableInitializer;
import org.thoriumlang.compiler.ast.algorithms.typechecking.TypeChecker;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.input.Source;
import org.thoriumlang.compiler.input.Sources;
import org.thoriumlang.compiler.input.loaders.JavaRTClassLoader;
import org.thoriumlang.compiler.input.loaders.ThoriumRTClassLoader;
import org.thoriumlang.compiler.input.loaders.ThoriumSrcClassLoader;
import org.thoriumlang.compiler.symbols.SymbolTable;

import java.util.Arrays;

public class SourceToAST {
    private final NodeIdGenerator nodeIdGenerator;
    private final Sources sources;
    private final SymbolTable symbolTable;

    public SourceToAST(NodeIdGenerator nodeIdGenerator, Sources sources, SymbolTable symbolTable) {
        this.nodeIdGenerator = nodeIdGenerator;
        this.sources = sources;
        this.symbolTable = symbolTable;
    }

    public AST convert(Source source) {
        AST ast = source.ast(
                nodeIdGenerator,
                Arrays.asList(
                        new SymbolTableInitializer(symbolTable),
                        new TypeChecker(
                                Arrays.asList(
                                        new ThoriumSrcClassLoader(nodeIdGenerator, sources),
                                        new ThoriumRTClassLoader(),
                                        new JavaRTClassLoader()
                                )
                        ),
                        new SymbolicNameChecker()
                )
        );
        ast.root();

        if (!ast.errors().isEmpty()) {
            // TODO have Source expose some errorPrefix() to show it below:
            ast.errors().forEach(err -> System.err.println(String.format("--: %s", err)));
        }

        return ast;
    }
}
