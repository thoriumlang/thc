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
package org.thoriumlang.compiler.symbols;

import org.thoriumlang.compiler.collections.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SymbolTable {
    private final String name;
    private final SymbolTable parent;
    private final Map<String, Symbol> symbols;
    /**
     * Maps a string such as "methodName(_)" to a list of actual symbols such as ["methodName(Number)", "methodName(String)"]
     */
    private final Map<String, List<Symbol>> methodSymbols;
    private final Map<String, SymbolTable> scopes;

    private SymbolTable(String name, SymbolTable parent) {
        this.name = name;
        this.parent = parent;
        this.symbols = new HashMap<>();
        this.methodSymbols = new HashMap<>();
        this.scopes = new HashMap<>();
    }

    public SymbolTable() {
        this("", null);
    }

    public void put(Name name, Symbol symbol) {
        SymbolTable table = findTable(name);
        table.symbols.put(name.getSimpleName(), symbol);
        if (name.isMethod()) {
            String simpleSignature = name.getNormalizedSimpleName();
            table.methodSymbols.putIfAbsent(simpleSignature, new ArrayList<>());
            table.methodSymbols.get(simpleSignature).add(symbol);
        }
    }

    private SymbolTable findTable(Name name) {
        if (!name.isQualified()) {
            return this;
        }

        List<String> parts = new ArrayList<>(name.getParts());
        SymbolTable table = root();

        while (parts.size() > 1) {
            table.scopes.putIfAbsent(parts.get(0), table.createScope(parts.get(0)));
            table = table.scopes.get(parts.get(0));
            parts.remove(0);
        }

        return table;
    }

    public List<Symbol> find(Name name) {
        return findTable(name).findLocal(name);
    }

    private List<Symbol> findLocal(Name name) {
        return name.isMethod()
                ? findLocalMethod(name.getNormalizedSimpleName())
                : findLocalVariable(name.getNormalizedSimpleName());
    }

    private List<Symbol> findLocalMethod(String simpleSignature) {
        return methodSymbols.getOrDefault(
                simpleSignature,
                isRoot() ? Collections.emptyList() : parent.findLocalMethod(simpleSignature)
        );
    }

    private boolean isRoot() {
        return parent == null;
    }

    private List<Symbol> findLocalVariable(String name) {
        return Optional
                .ofNullable(
                        symbols.getOrDefault(
                                name,
                                isRoot() ? null : Lists.get(parent.findLocalVariable(name), 0).orElse(null)
                        )
                )
                .map(Collections::singletonList)
                .orElse(Collections.emptyList());
    }

    public boolean inScope(Name name) {
        if (name.isQualified()) {
            return false;
        }
        return symbols.containsKey(name.getSimpleName());
    }

    public SymbolTable createScope(String name) {
        SymbolTable symbolTable = new SymbolTable(name, this);
        scopes.putIfAbsent(name, symbolTable);
        return scopes.get(name);
    }

    public SymbolTable enclosingScope() {
        if (isRoot()) {
            throw new IllegalStateException("root has no enclosing scope");
        }
        return parent;
    }

    public SymbolTable root() {
        SymbolTable table = this;
        while (!table.isRoot()) {
            table = table.parent;
        }
        return table;
    }

    public <T> T accept(SymbolTableVisitor<? extends T> visitor) {
        return visitor.visit(name, this, symbols, scopes);
    }

    @Override
    public String toString() {
        if (isRoot()) {
            return "";
        }
        if (parent.isRoot()) {
            return String.format("%s", name);
        }
        return String.format("%s.%s", parent.toString(), name);
    }
}
