package org.thoriumlang.compiler.api;

import io.vavr.control.Either;
import org.thoriumlang.compiler.api.errors.CompilationError;
import org.thoriumlang.compiler.ast.ASTFactory;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.input.Source;
import org.thoriumlang.compiler.input.Sources;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Compiler {
    private final List<Plugin> plugins;
    private final CompilationListener listener;
    private final Map<Source, Either<List<CompilationError>, Root>> compiledSources;
    private final NodeIdGenerator nodeIdGenerator;

    public Compiler(CompilationListener listener, List<Plugin> plugins) {
        this.listener = Objects.requireNonNull(listener, "listener cannot be null");
        this.plugins = Objects.requireNonNull(plugins, "plugins cannot be null");
        this.compiledSources = new HashMap<>();
        this.nodeIdGenerator = new NodeIdGenerator();
    }

    public void compile(Sources sources) {
        listener.onCompilationStarted();

        sources.sources().forEach(this::compile);

        listener.onCompilationFinished();
    }

    private void compile(Source source) {
        if (compiledSources.containsKey(source)) {
            return ;
        }
        listener.onSourceStarted(source);

        Either<List<CompilationError>, Root> ast = source.ast(nodeIdGenerator).parse();

        if (ast.isLeft()) {
            ast.getLeft().forEach(e -> listener.onError(source, e));
        }

        CompilationContext context = new CompilationContext(ast, listener);

        plugins.stream()
                .map(p -> p.execute(context))
                .flatMap(List::stream)
                .collect(Collectors.toList())
                .forEach(e -> listener.onError(source, e));

        compiledSources.put(source, ast);
        listener.onSourceFinished(source, context);
    }
}
