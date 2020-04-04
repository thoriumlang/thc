package org.thoriumlang.compiler.testsupport;

import org.thoriumlang.compiler.collections.Lists;
import org.thoriumlang.compiler.symbols.Symbol;
import org.thoriumlang.compiler.symbols.SymbolTable;
import org.thoriumlang.compiler.symbols.SymbolTableVisitor;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ScopesExtractionVisitor implements SymbolTableVisitor<List<SymbolTable>> {
    @Override
    public List<SymbolTable> visit(
            String name,
            SymbolTable symbolTable, Map<String, Symbol> symbols, Map<String, SymbolTable> scopes
    ) {
        return Lists.merge(
                Collections.singletonList(symbolTable),
                scopes.values().stream()
                        .map(s -> s.accept(this))
                        .flatMap(List::stream)
                        .sorted(Comparator.comparing(SymbolTable::toString))
                        .collect(Collectors.toList())
        );
    }

}