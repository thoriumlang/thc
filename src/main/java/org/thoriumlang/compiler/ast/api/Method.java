package org.thoriumlang.compiler.ast.api;

import org.thoriumlang.compiler.ast.api.unsafe.TypeSpec;
import org.thoriumlang.compiler.ast.nodes.MethodSignature;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Method {
    private final MethodSignature node;

    public Method(MethodSignature node) {
        this.node = Objects.requireNonNull(node, "node cannot be null");
    }

    public String getName() {
        return node.getName();
    }

    public String toString() {
        return String.format("%s()", getName());
    }

    public Type getReturnType() {
        return new TypeSpec(node.getReturnType()).getType();
    }

    public List<Parameter> getParameters() {
        return node.getParameters().stream()
                .map(Parameter::new)
                .collect(Collectors.toList());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Method method = (Method) o;
        return toString().equals(method.toString());
    }
}
