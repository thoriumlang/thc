package org.thoriumlang.compiler.api;

import org.thoriumlang.compiler.SourceToAST;
import org.thoriumlang.compiler.ast.AST;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.input.Source;
import org.thoriumlang.compiler.input.Sources;
import org.thoriumlang.compiler.symbols.SymbolTable;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Compiler {
    private final List<Plugin> plugins;
    private final CompilationListener listener;

    public Compiler(CompilationListener listener, List<Plugin> plugins) {
        this.listener = Objects.requireNonNull(listener, "listener cannot be null");
        this.plugins = Objects.requireNonNull(plugins, "plugins cannot be null");
    }

    public void compile(Sources sources) {
        // TODO optimize: if a top level is already known reuse it

        List<Source> sourcesToCompile = sources.sources();
        AtomicInteger sourcesProcessed = new AtomicInteger(0);

        listener.onCompilationStarted(sourcesToCompile.size());

        sourcesToCompile.forEach(source -> {
            listener.onSourceStarted(source);

            AST ast = new SourceToAST(
                    new NodeIdGenerator(),
                    sources,
                    new SymbolTable(),
                    listener
            ).convert(source);

            CompilationContext context = new CompilationContext(ast, listener);

            plugins.stream()
                    .map(p -> p.execute(context))
                    .flatMap(List::stream)
                    .collect(Collectors.toList())
                    .forEach(e -> listener.onError(source, e));

            listener.onSourceFinished(source, context);
            listener.onCompilationProgress((float) sourcesProcessed.incrementAndGet() / sourcesToCompile.size());
        });

        listener.onCompilationFinished();
    }
}
