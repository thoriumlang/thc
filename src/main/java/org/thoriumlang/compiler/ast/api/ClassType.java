package org.thoriumlang.compiler.ast.api;

import org.thoriumlang.compiler.ast.api.unsafe.TypeSpec;
import org.thoriumlang.compiler.collections.Sets;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassType implements Type {
    private final org.thoriumlang.compiler.ast.nodes.Class node;

    public ClassType(org.thoriumlang.compiler.ast.nodes.Class node) {
        this.node = Objects.requireNonNull(node, "node cannot be null");
    }

    @Override
    public String getName() {
        return node.getName();
    }

    @Override
    public Set<Method> getMethods() {
        return Sets.merge(
                getSupertype().getMethods(),
                node.getMethods().stream()
                        .map(m -> new Method(m.getSignature()))
                        .collect(Collectors.toSet())
        );
    }

    private Type getSupertype() {
        return new TypeSpec(node.getSuperType()).getType();
    }
}
