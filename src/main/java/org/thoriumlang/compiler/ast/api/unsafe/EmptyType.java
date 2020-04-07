package org.thoriumlang.compiler.ast.api.unsafe;

import org.thoriumlang.compiler.ast.api.Method;
import org.thoriumlang.compiler.ast.api.Type;

import java.util.Collections;
import java.util.Set;

public class EmptyType implements Type {
    @Override
    public String getName() {
        return "[empty]";
    }

    @Override
    public Set<Method> getMethods() {
        return Collections.emptySet();
    }
}
