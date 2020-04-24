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
import org.thoriumlang.compiler.helpers.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class SymbolTable {
    private final String name;
    private final SymbolTable parent;
    private final Map<String, Symbol> symbols;
    /**
     * Maps a string such as "methodName" to a list of actual symbols such as ["methodName()", "methodName(String)"]
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
        if (isMethodName(name)) {
            String simpleSignature = simpleSignature(name);
            table.methodSymbols.putIfAbsent(simpleSignature, new ArrayList<>());
            table.methodSymbols.get(simpleSignature).add(symbol);
        }
    }

    /**
     * Transforms a method signature of the form methodName[...](A,B) to a simplified signature of the form
     * methodName(_,_).
     */
    private String simpleSignature(Name name) {
        String methodSignature = name.getSimpleName();
        return String.format("%s(%s)",
                methodSignature.substring(
                        0,
                        Strings.indexOfFirst(methodSignature, "[", "(")
                ),
                Arrays
                        .stream(
                                methodSignature
                                        .substring(methodSignature.indexOf("(") + 1, methodSignature.indexOf(")"))
                                        .split(",")
                        )
                        .filter(s -> !s.isEmpty())
                        .map(p -> "_")
                        .collect(Collectors.joining(","))
        );
    }

    private boolean isMethodName(Name name) {
        return name.getSimpleName().contains("(");
    }

    private SymbolTable findTable(Name name) {
        if (!name.isQualified()) {
            return this;
        }

        List<String> parts = new ArrayList<>(name.getParts());
        SymbolTable table = findRoot();

        while (parts.size() > 1) {
            table.scopes.putIfAbsent(parts.get(0), table.createScope(parts.get(0)));
            table = table.scopes.get(parts.get(0));
            parts.remove(0);
        }

        return table;
    }

    private SymbolTable findRoot() {
        SymbolTable table = this;
        while (!table.isRoot()) {
            table = table.parent;
        }
        return table;
    }

    private boolean isRoot() {
        return parent == null;
    }

    public List<Symbol> find(Name name) {
        return findTable(name).findLocal(new Name(name.getSimpleName()));
    }

    private List<Symbol> findLocal(Name name) {
        return isMethodName(name)
                ? findLocalMethod(simpleSignature(name))
                : findLocalVariable(name.getSimpleName());
    }

    private List<Symbol> findLocalMethod(String simpleSignature) {
        return methodSymbols.getOrDefault(
                simpleSignature,
                isRoot() ? Collections.emptyList() : parent.findLocalMethod(simpleSignature)
        );
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

    // FIXME wrong for overloaded methods
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
        if (isRoot()) {
            return this;
        }
        return enclosingScope().root();
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
