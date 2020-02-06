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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultSymbolTable implements SymbolTable {
    private final int hashCode;
    private final String name;
    private final SymbolTable parentSymbolTable;
    private final boolean blockBoundary;
    private final List<DefaultSymbolTable> childrenSymbolTables;
    private final Map<String, Symbol> symbols;

    private DefaultSymbolTable(String name, SymbolTable parentSymbolTable, boolean blockBoundary) {
        this.hashCode = new Random().nextInt();
        this.name = name;
        this.parentSymbolTable = parentSymbolTable;
        this.blockBoundary = blockBoundary;
        this.symbols = new HashMap<>();
        this.childrenSymbolTables = new ArrayList<>();
    }

    @SuppressWarnings("java:S2245")
    DefaultSymbolTable(String name, SymbolTable parentSymbolTable) {
        this(name, parentSymbolTable, false);
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
    public Optional<Symbol> findInScope(String name) {
        return Optional.ofNullable(
                symbols.getOrDefault(
                        name,
                        (blockBoundary ? null : parentSymbolTable.findInScope(name).orElse(null))
                )
        );
    }

    @Override
    public Stream<Symbol> symbolsStream() {
        return Stream.concat(
                symbols.values().stream(),
                parentSymbolTable.symbolsStream()
                        .filter(s -> !symbols.containsKey(s.getName()))
        );
    }

    @Override
    public void put(Symbol symbol) {
        symbols.put(symbol.getName(), symbol);
    }

    @Override
    public DefaultSymbolTable createScope(String name) {
        DefaultSymbolTable childTable = new DefaultSymbolTable(name, this, true);
        childrenSymbolTables.add(childTable);
        return childTable;
    }

    @Override
    public DefaultSymbolTable append(String name) {
        DefaultSymbolTable childTable = new DefaultSymbolTable(name, this);
        childrenSymbolTables.add(childTable);
        return childTable;
    }

    @Override
    public SymbolTable parent() {
        return parentSymbolTable;
    }

    @Override
    public String toString() {
        final String IDENT = "\n   ";
        return String.format("%s%s:%s%s",
                fqName(),
                blockBoundary ? " --" : "",
                symbols.isEmpty() ?
                        "" :
                        symbols.values().stream()
                                .map(Object::toString)
                                .collect(Collectors.joining(IDENT, IDENT, "")),
                childrenSymbolTables.isEmpty() ?
                        "" :
                        childrenSymbolTables.stream()
                                .map(DefaultSymbolTable::toString)
                                .map(s -> s.replace("\n", IDENT))
                                .collect(Collectors.joining("\n", IDENT, ""))
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
