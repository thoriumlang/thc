package org.thoriumlang.compiler.api;

import org.thoriumlang.compiler.ast.AST;
import org.thoriumlang.compiler.ast.algorithms.symbolicnamechecking.SymbolicNameChecker;
import org.thoriumlang.compiler.ast.algorithms.typechecking.TypeChecker;
import org.thoriumlang.compiler.ast.algorithms.typeinference.TypeResolver;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.input.Source;
import org.thoriumlang.compiler.input.Sources;
import org.thoriumlang.compiler.input.loaders.JavaRTClassLoader;
import org.thoriumlang.compiler.input.loaders.ThoriumRTClassLoader;
import org.thoriumlang.compiler.input.loaders.ThoriumSrcClassLoader;
import org.thoriumlang.compiler.symbols.SymbolTable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Compiler {
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
        listener.onCompilationStarted();

        sources.sources().forEach(source -> compile(sources, source));

        listener.onCompilationFinished();
    }

    public AST compile(Sources sources, Source source) {
        if (compiledSources.containsKey(source)) {
            return compiledSources.get(source);
        }
        listener.onSourceStarted(source);

        AST ast = source.ast(
                nodeIdGenerator,
                symbolTable,
                Arrays.asList(
                        new TypeChecker(
                                Arrays.asList(
                                        new ThoriumSrcClassLoader(sources, this),
                                        new ThoriumRTClassLoader(),
                                        new JavaRTClassLoader()
                                )
                        ),
                        new SymbolicNameChecker()
                        // TODO add TypeResolver
                )
        ).parse();

        ast.errors().forEach(e -> listener.onError(source, e));

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
}
