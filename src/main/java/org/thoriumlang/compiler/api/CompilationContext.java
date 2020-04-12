package org.thoriumlang.compiler.api;

import org.thoriumlang.compiler.api.errors.CompilationError;
import org.thoriumlang.compiler.ast.AST;
import org.thoriumlang.compiler.ast.nodes.Root;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class CompilationContext {
    private final AST ast;
    private final CompilationListener listener;
    private Map<Class<?>, Object> map;

    public CompilationContext(AST ast, CompilationListener listener) {
        this.ast = Objects.requireNonNull(ast, "ast cannot be null");
        this.listener = Objects.requireNonNull(listener, "listener cannot be null");
        this.map = new HashMap<>();
    }

    public Optional<Root> root() {
        return ast.root();
    }

    public List<CompilationError> errors() { // TODO shouldn't errors only be sent to CompilationListener.onError?
        return ast.errors();
    }

    public CompilationListener listener() {
        return listener;
    }

    @SuppressWarnings("unchecked") // we know it's the correct type thanks to put
    public <T> Optional<T> get(Class<T> key) {
        return Optional.ofNullable((T) map.get(key));
    }

    public <T> void put(Class<T> key, T value) {
        map.put(key, value);
    }
}
