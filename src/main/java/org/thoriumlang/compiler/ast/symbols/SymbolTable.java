package org.thoriumlang.compiler.ast.symbols;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SymbolTable {
    private static final SymbolTable EMPTY_TABLE = new EmptySymbolTable();
    private final String name;
    private final Map<String, NodeRef> symbols;
    private final SymbolTable parent;
    private final List<SymbolTable> children;

    private SymbolTable(String name, SymbolTable parent, Map<String, NodeRef> symbols) {
        this.name = name;
        this.symbols = new HashMap<>(symbols);
        this.parent = parent;
        this.children = new ArrayList<>();
    }

    public SymbolTable(String name) {
        this(name, EMPTY_TABLE, new HashMap<>());
    }

    public SymbolTable() {
        this("root");
    }

    public Optional<NodeRef> findInCurrentScope(String symbolName) {
        return Optional.ofNullable(symbols.get(symbolName));
    }

    public void put(String name, NodeRef sourceNode) {
        symbols.put(name, sourceNode);
    }

    /**
     * Returns true iff the symbol is already defined in the current scope. To know if the symbol is defined in some
     * enclosing scope, use {@link #inScope(String)} instead.
     *
     * @param name the symbol name.
     * @return true iff the symbol is defined in the current scope.
     */
    public boolean inCurrentScope(String name) {
        return symbols.containsKey(name);
    }

    public boolean inScope(String name) {
        return inCurrentScope(name) || parent.inScope(name);
    }

    public SymbolTable addChild(SymbolTable childTable) {
        SymbolTable childTableWithParent = childTable.withParent(this);
        children.add(childTableWithParent);
        return childTableWithParent;
    }

    private SymbolTable withParent(SymbolTable parent) {
        if (this.parent != EMPTY_TABLE) {
            throw new IllegalStateException("Cannot change parent");
        }
        return new SymbolTable(name, parent, symbols);
    }

    private static class EmptySymbolTable extends SymbolTable {
        public EmptySymbolTable() {
            super("EMPTY");
        }

        @Override
        public Optional<NodeRef> findInCurrentScope(String symbolName) {
            return Optional.empty();
        }

        @Override
        public void put(String name, NodeRef sourceNode) {
            throw new IllegalStateException();
        }

        @Override
        public boolean inCurrentScope(String name) {
            return false;
        }

        @Override
        public boolean inScope(String name) {
            return false;
        }

        @Override
        public SymbolTable addChild(SymbolTable childTable) {
            throw new IllegalStateException();
        }
    }
}
