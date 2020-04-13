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

import org.thoriumlang.compiler.api.Compiler;
import org.thoriumlang.compiler.ast.AST;
import org.thoriumlang.compiler.ast.algorithms.symbolicnamechecking.SymbolicNameChecker;
import org.thoriumlang.compiler.ast.algorithms.typechecking.TypeChecker;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.input.Source;
import org.thoriumlang.compiler.input.Sources;
import org.thoriumlang.compiler.input.loaders.JavaRTClassLoader;
import org.thoriumlang.compiler.input.loaders.ThoriumRTClassLoader;
import org.thoriumlang.compiler.input.loaders.ThoriumSrcClassLoader;
import org.thoriumlang.compiler.symbols.SymbolTable;

import java.util.Arrays;

// TODO does it make sense to keep it?
public class SourceToAST {
    private final NodeIdGenerator nodeIdGenerator;
    private final Sources sources;
    private final SymbolTable symbolTable;
    private final Compiler compiler;

    public SourceToAST(NodeIdGenerator nodeIdGenerator, Sources sources, SymbolTable symbolTable, Compiler compiler) {
        this.nodeIdGenerator = nodeIdGenerator;
        this.sources = sources;
        this.symbolTable = symbolTable;
        this.compiler = compiler;
    }

    public AST convert(Source source) {
        AST ast = source.ast(
                nodeIdGenerator,
                symbolTable,
                Arrays.asList(
                        new TypeChecker(
                                Arrays.asList(
                                        new ThoriumSrcClassLoader(sources, compiler),
                                        new ThoriumRTClassLoader(),
                                        new JavaRTClassLoader()
                                )
                        ),
                        new SymbolicNameChecker()
                )
        );
        ast.root();

        ast.errors().forEach(e -> compiler.onError(source, e));

        return ast;
    }
}
