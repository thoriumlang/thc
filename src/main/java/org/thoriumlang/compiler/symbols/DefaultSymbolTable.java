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
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultSymbolTable implements SymbolTable {
    private final int hashCode;
    private final String name;
    private final SymbolTable parentSymbolTable;
    private final Set<DefaultSymbolTable> childrenSymbolTables;
    private final Map<String, Symbol> symbols;

    DefaultSymbolTable(String name, SymbolTable parentSymbolTable) {
        this.hashCode = new Random().nextInt();
        this.name = name;
        this.parentSymbolTable = parentSymbolTable;
        this.symbols = new HashMap<>();
        this.childrenSymbolTables = new HashSet<>();
    }

    public DefaultSymbolTable() {
        this("root", new RootSymbolTable());
    }

    @Override
    public String fqName() {
        if (parentSymbolTable.fqName().isEmpty()) {
            return String.format("%s", name);
        }
        return String.format("%s.%s", parentSymbolTable.fqName(), name);
    }

    @Override
    public Optional<Symbol> find(String name) {
        return Optional.ofNullable(symbols.getOrDefault(name, parentSymbolTable.find(name).orElse(null)));
    }

    @Override
    public void put(String name, Symbol symbol) {
        symbols.put(name, symbol);
    }

    @Override
    public DefaultSymbolTable createNestedTable(String name) {
        DefaultSymbolTable childTable = new DefaultSymbolTable(name, this);
        childrenSymbolTables.add(childTable);
        return childTable;
    }

    @Override
    public String toString() {
        return String.format("%s:%s%s",
                fqName(),
                symbols.isEmpty() ?
                        "" :
                        symbols.entrySet().stream()
                                .map(e -> String.format("%s -> %s", e.getKey(), e.getValue()))
                                .collect(Collectors.joining("\n   ", "\n   ", "")),
                childrenSymbolTables.isEmpty() ?
                        "" :
                        childrenSymbolTables.stream()
                                .map(DefaultSymbolTable::toString)
                                .collect(Collectors.joining("\n", "\n", ""))
        );
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }
}
