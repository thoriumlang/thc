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
import org.thoriumlang.compiler.ast.algorithms.typechecking.RTJarJavaRuntimeClassLoader;
import org.thoriumlang.compiler.ast.algorithms.typechecking.ThoriumRTClassLoader;
import org.thoriumlang.compiler.ast.algorithms.typechecking.TypeChecker;
import org.thoriumlang.compiler.symbols.Name;
import org.thoriumlang.compiler.symbols.SymbolTable;

import java.util.Arrays;
import java.util.function.Function;

public class SourceToAST implements Function<Source, AST> {
    @Override
    public AST apply(Source source) {
        SymbolTable symbolTable = new SymbolTable();

        return source.ast(
                Arrays.asList(
                        new SymbolTableInitializer(symbolTable),
                        new TypeChecker(Arrays.asList(
                                new ThoriumRTClassLoader(),
                                new RTJarJavaRuntimeClassLoader()
                        )),
                        new SymbolicNameChecker()
                )
        );
    }
}
