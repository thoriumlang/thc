package org.thoriumlang.compiler.ast.api;

import org.thoriumlang.compiler.ast.api.unsafe.TypeSpec;
import org.thoriumlang.compiler.ast.nodes.TypeSpecUnion;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class TypeSpecUnionType implements Type {
    private final TypeSpecUnion node;

    public TypeSpecUnionType(TypeSpecUnion node) {
        this.node = Objects.requireNonNull(node, "node cannot be null");
    }

    @Override
    public String getName() {
        return node.getTypes().stream()
                .map(t -> new TypeSpec(t).getType().getName())
                .sorted()
                .collect(Collectors.joining(" & ", "(", ")"));
    }

    @Override
    public Set<Method> getMethods() {
        return node.getTypes().stream()
                .map(t -> new TypeSpec(t).getType().getMethods())
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return getName();
    }
}
