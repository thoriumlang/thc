package org.thoriumlang.compiler.testsupport;

import org.thoriumlang.compiler.collections.Lists;
import org.thoriumlang.compiler.symbols.Symbol;
import org.thoriumlang.compiler.symbols.SymbolTable;
import org.thoriumlang.compiler.symbols.SymbolTableVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SymbolsExtractionVisitor implements SymbolTableVisitor<List<Symbol>> {
    @Override
    public List<Symbol> visit(String name, SymbolTable symbolTable, Map<String, Symbol> symbols, Map<String, SymbolTable> scopes) {
        return Lists.merge(
                new ArrayList<>(symbols.values()),
                scopes.values().stream()
                        .map(s -> s.accept(this))
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList())
        );
    }
}
