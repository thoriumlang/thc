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
package org.thoriumlang.compiler.ast.algorithms.symboltable;

import org.thoriumlang.compiler.api.errors.CompilationError;
import org.thoriumlang.compiler.ast.algorithms.Algorithm;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.symbols.Name;
import org.thoriumlang.compiler.symbols.SymbolTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SymbolTableInitializer implements Algorithm {
    private final SymbolTable rootSymbolTable;

    public SymbolTableInitializer(SymbolTable rootSymbolTable) {
        this.rootSymbolTable = rootSymbolTable;
    }

    @Override
    public List<CompilationError> walk(Root root) {
        root.accept(
                new SymbolTableInitializationVisitor(
                        findLocalTable(
                                rootSymbolTable,
                                new Name(root.getNamespace()).getParts()
                        )
                )
        );
        return Collections.emptyList();
    }

    private SymbolTable findLocalTable(SymbolTable symbolTable, List<String> namespaces) {
        if (namespaces.isEmpty()) {
            return symbolTable;
        }
        ArrayList<String> newNamespaces = new ArrayList<>(namespaces);
        return findLocalTable(
                symbolTable.createScope(newNamespaces.remove(0)),
                newNamespaces
        );
    }
}
