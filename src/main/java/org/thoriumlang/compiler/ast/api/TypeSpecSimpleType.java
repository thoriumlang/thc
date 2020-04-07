package org.thoriumlang.compiler.ast.api;

import org.thoriumlang.compiler.ast.api.unsafe.Types;
import org.thoriumlang.compiler.ast.nodes.TypeSpecSimple;
import org.thoriumlang.compiler.symbols.Name;
import org.thoriumlang.compiler.symbols.SymbolTable;

import java.util.Objects;
import java.util.Set;

public class TypeSpecSimpleType implements Type {
    private final TypeSpecSimple node;

    public TypeSpecSimpleType(TypeSpecSimple node) {
        this.node = Objects.requireNonNull(node, "node cannot be null");
    }

    @Override
    public String getName() {
        return node.getType();
    }

    @Override
    public Set<Method> getMethods() {
        return Types.find(node.getContext().require(SymbolTable.class), new Name(node.getType()))
                .map(Type::getMethods)
                .orElseThrow(() -> new IllegalStateException("type " + node.getType() + " not found"));
    }
}
