package org.thoriumlang.compiler.api;

import org.thoriumlang.compiler.SourceToAST;
import org.thoriumlang.compiler.api.errors.CompilationError;
import org.thoriumlang.compiler.ast.AST;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.input.Source;
import org.thoriumlang.compiler.input.Sources;
import org.thoriumlang.compiler.symbols.SymbolTable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Compiler implements CompilationListener {
    private final List<Plugin> plugins;
    private final CompilationListener listener;
    private final Map<Source, AST> compiledSources;
    private final NodeIdGenerator nodeIdGenerator;
    private final SymbolTable symbolTable;

    public Compiler(CompilationListener listener, List<Plugin> plugins) {
        this.listener = Objects.requireNonNull(listener, "listener cannot be null");
        this.plugins = Objects.requireNonNull(plugins, "plugins cannot be null");
        this.compiledSources = new HashMap<>();
        this.nodeIdGenerator = new NodeIdGenerator();
        this.symbolTable = new SymbolTable();
    }

    public void compile(Sources sources) {
        // TODO optimize: if a top level is already known reuse it

        listener.onCompilationStarted();

        sources.sources().forEach(source -> compile(sources, source));

        listener.onCompilationFinished();
    }

    public AST compile(Sources sources, Source source) {
        if (compiledSources.containsKey(source)) {
            return compiledSources.get(source);
        }
        listener.onSourceStarted(source);

        AST ast = new SourceToAST(
                nodeIdGenerator,
                sources,
                symbolTable,
                this
        ).convert(source);

        CompilationContext context = new CompilationContext(ast, listener);

        plugins.stream()
                .map(p -> p.execute(context))
                .flatMap(List::stream)
                .collect(Collectors.toList())
                .forEach(e -> listener.onError(source, e));

        compiledSources.put(source, ast);
        listener.onSourceFinished(source, context);

        return ast;
    }

    @Override
    public void onCompilationStarted() {
        listener.onCompilationStarted();
    }

    @Override
    public void onCompilationFinished() {
        listener.onCompilationFinished();
    }

    @Override
    public void onSourceStarted(Source source) {
        listener.onSourceStarted(source);
    }

    @Override
    public void onSourceFinished(Source source, CompilationContext context) {
        listener.onSourceFinished(source, context);
    }

    @Override
    public void onError(Source source, CompilationError error) {
        listener.onError(source, error);
    }

    @Override
    public void onEvent(Event event) {
        listener.onEvent(event);
    }
}
