package org.thoriumlang.compiler.api;

import org.thoriumlang.compiler.ast.AST;
import org.thoriumlang.compiler.ast.algorithms.CompilationError;
import org.thoriumlang.compiler.ast.nodes.Root;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompilationContext {
    private final AST ast;
    private final List<CompilationError> errors;
    private final CompilationListener listener;
    private Map<Class<?>, Object> map;

    public CompilationContext(AST ast, CompilationListener listener) {
        this.ast = ast;
        this.errors = new ArrayList<>(ast.errors());
        this.listener = listener;
        this.map = new HashMap<>();
    }

    public Root root() {
        return ast.root();
    }

    public List<CompilationError> errors() {
        return ast.errors();
    }

    public CompilationListener listener() {
        return listener;
    }

    @SuppressWarnings("unchecked") // we know it's the correct type thanks to put
    public <T> T get(Class<T> key) {
        return (T) map.get(key);
    }

    public <T> void put(Class<T> key, T value) {
        map.put(key, value);
    }
}
