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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SymbolTable {
    private static final SymbolTable ROOT = new SymbolTable() {
        @Override
        public Optional<Symbol> find(String name) {
            return Optional.empty();
        }

        @Override
        public void put(String name, Symbol symbol) {
            throw new IllegalStateException("Cannot add symbol to root table");
        }
    };

    private final SymbolTable parentSymbolTable;
    private final Map<String, Symbol> symbols;

    public SymbolTable() {
        this(ROOT);
    }

    SymbolTable(SymbolTable parentSymbolTable) {
        this.parentSymbolTable = parentSymbolTable;
        this.symbols = new HashMap<>();
    }

    public Optional<Symbol> find(String name) {
        return Optional.ofNullable(symbols.getOrDefault(name, parentSymbolTable.find(name).orElse(null)));
    }

    public void put(String name, Symbol symbol) {
        symbols.put(name, symbol);
    }

    public SymbolTable createNestedTable() {
        return new SymbolTable(this);
    }
}
