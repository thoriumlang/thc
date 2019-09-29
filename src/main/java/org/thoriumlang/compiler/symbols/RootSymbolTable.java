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
package org.thoriumlang.compiler.symbols;

import java.util.Optional;

public class RootSymbolTable implements SymbolTable {
    @Override
    public String fqName() {
        return "";
    }

    @Override
    public Optional<Symbol> find(String name) {
        return Optional.empty();
    }

    @Override
    public void put(String name, Symbol symbol) {
        throw new IllegalStateException("Cannot add a symbol to an empty symbol table");
    }

    @Override
    public DefaultSymbolTable createNestedTable(String name) {
        throw new IllegalStateException("Cannot add a nested symbol table to an empty symbol table");
    }
}
