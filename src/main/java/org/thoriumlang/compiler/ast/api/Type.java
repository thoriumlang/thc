package org.thoriumlang.compiler.ast.api;

import java.util.Set;

public interface Type {
    String getName();

    /**
     * Returns all method defined in this type or in its supertype(s)
     *
     * @return the list of all methods
     */
    Set<Method> getMethods();
}
