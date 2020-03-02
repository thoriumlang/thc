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

import org.thoriumlang.compiler.ast.nodes.Node;

import java.util.ArrayList;
import java.util.Collection;
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
    private final List<DefaultSymbolTable> childrenSymbolTables;
    private final Map<String, Symbol> symbols;
    private final Node node;

    DefaultSymbolTable(Node node, String name, SymbolTable parentSymbolTable) {
        this.hashCode = new Random().nextInt();
        this.node = node;
        this.name = name;
        this.parentSymbolTable = parentSymbolTable;
        this.symbols = new HashMap<>();
        this.childrenSymbolTables = new ArrayList<>();
    }

    public DefaultSymbolTable() {
        this(null, "root", new RootSymbolTable());
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
        return Optional.ofNullable(symbols.get(name));
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
    public DefaultSymbolTable createScope(Node node, String name) {
        DefaultSymbolTable childTable = new DefaultSymbolTable(node, name, this);
        childrenSymbolTables.add(childTable);
        return childTable;
    }

    @Override
    public SymbolTable parent() {
        return parentSymbolTable;
    }

    @Override
    public Optional<Node> node() {
        return Optional.ofNullable(node);
    }

    @Override
    public String toString() {
        return String.join("\n", toStringInternal());

    }

    private List<String> toStringInternal() {
        List<String> lines = new ArrayList<>();

        lines.addAll(
                symbols.values().stream()
                        .map(Object::toString)
                        .map(s -> String.format("  %s", s))
                        .collect(Collectors.toList())
        );

        lines.addAll(
                childrenSymbolTables.stream()
                        .map(DefaultSymbolTable::toStringInternal)
                        .flatMap(Collection::stream)
                        .map(s -> String.format("  %s", s))
                        .collect(Collectors.toList())
        );

        lines.add(0, String.format("%s:", fqName()));

        return lines;
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
