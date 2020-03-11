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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SymbolTableDumpingVisitor implements SymbolTableVisitor<List<String>> {
    private final boolean includeSymbols;

    public SymbolTableDumpingVisitor(boolean includeSymbols) {
        this.includeSymbols = includeSymbols;
    }

    public SymbolTableDumpingVisitor() {
        this(false);
    }

    @Override
    public List<String> visit(
            String name,
            SymbolTable symbolTable,
            Map<String, Symbol> symbols,
            Map<String, SymbolTable> scopes
    ) {
        return Lists.merge(
                Stream.of(symbolTable.toString())
                        .filter(e -> !e.isEmpty())
                        .collect(Collectors.toList()),
                includeSymbols ?
                        symbols.entrySet().stream()
                                .sorted(Map.Entry.comparingByKey())
                                .map(e -> "  " + e.getKey() + " -> " + e.getValue())
                                .collect(Collectors.toList()) :
                        Collections.emptyList(),
                scopes.values().stream()
                        .sorted(Comparator.comparing(SymbolTable::toString))
                        .map(s -> s.accept(this))
                        .flatMap(List::stream)
                        .map(s -> "  " + s)
                        .collect(Collectors.toList())
        );
    }
}
