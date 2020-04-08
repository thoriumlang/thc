package org.thoriumlang.compiler.ast.api;

import com.google.common.collect.Sets;
import org.thoriumlang.compiler.ast.api.unsafe.TypeSpec;
import org.thoriumlang.compiler.ast.nodes.TypeSpecIntersection;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class TypeSpecIntersectionType implements Type {
    private final TypeSpecIntersection node;

    public TypeSpecIntersectionType(TypeSpecIntersection node) {
        this.node = Objects.requireNonNull(node, "node cannot be null");
    }

    @Override
    public String getName() {
        return node.getTypes().stream()
                .map(t -> new TypeSpec(t).getType().getName())
                .sorted()
                .collect(Collectors.joining(" | ", "(", ")"));
    }

    @Override
    public Set<Method> getMethods() {
        List<Set<Method>> methodsList = node.getTypes().stream()
                .map(t -> new TypeSpec(t).getType().getMethods())
                .collect(Collectors.toList());

        return methodsList.stream()
                .reduce(methodsList.get(0), Sets::intersection);
    }

    @Override
    public String toString() {
        return getName();
    }
}
