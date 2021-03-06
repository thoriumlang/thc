package org.thoriumlang.compiler.api;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.api.errors.CompilationError;
import org.thoriumlang.compiler.api.errors.SemanticError;
import org.thoriumlang.compiler.api.errors.SymbolNotFoundError;
import org.thoriumlang.compiler.api.errors.SyntaxError;
import org.thoriumlang.compiler.ast.AST;
import org.thoriumlang.compiler.ast.context.SourcePosition;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.ast.nodes.Type;
import org.thoriumlang.compiler.ast.nodes.TypeSpecSimple;
import org.thoriumlang.compiler.ast.nodes.Visibility;
import org.thoriumlang.compiler.input.Source;
import org.thoriumlang.compiler.input.Sources;
import org.thoriumlang.compiler.symbols.Name;
import org.thoriumlang.compiler.symbols.SymbolTable;
import org.thoriumlang.compiler.testsupport.NodeStub;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

class CompilerTest {
    @Test
    void constructor_listener() {
        Assertions.assertThatThrownBy(() -> new Compiler(null, Collections.singletonList(new PluginStub())))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("listener cannot be null");
    }

    @Test
    void constructor_plugins() {
        Assertions.assertThatThrownBy(() -> new Compiler(new ListenerStub(), null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("plugins cannot be null");
    }

    @Test
    void compile() {
        ListenerStub listener = new ListenerStub();
        Compiler compiler = new Compiler(listener, Collections.singletonList(new PluginStub()));
        AST ast = ast();

        compiler.compile(new Sources() {
            @Override
            public List<Source> sources() {
                return Collections.singletonList((nodeIdGenerator, symbolTable, algorithms) -> ast);
            }

            @Override
            public Optional<Source> load(Name name) {
                return Optional.empty();
            }
        });

        Assertions.assertThat(listener.events)
                .containsExactly(
                        "onCompilationStarted",
                        "onSourceStarted",
                        "onError:symbol not found: ast (-1)",
                        "onEvent:plugin",
                        "onError:symbol not found: plugin (-1)",
                        "onSourceFinished",
                        "onCompilationFinished"
                );

        Assertions.assertThat(listener.context)
                .isNotNull();

        Assertions.assertThat(
                listener.context.errors()
                        .stream()
                        .filter(e -> e instanceof SemanticError)
                        .map(e -> (SemanticError) e)
                        .map(e -> e.format((sp, message) -> String.format("%s (%d)", message, sp.getStartLine())))
                        .map(Object::toString)
                        .collect(Collectors.toList())
        ).containsExactly("symbol not found: ast (-1)");

        Assertions.assertThat(listener.context.root().orElseThrow(() -> new IllegalStateException("no root found")))
                .isSameAs(ast.root().orElseThrow(() -> new IllegalStateException("no root found")));
    }

    private AST ast() {
        return new AST(
                new InputStreamStub(),
                "",
                new NodeIdGenerator(),
                Collections.emptyList(),
                new SymbolTable()
        ) {
            private final NodeIdGenerator nodeIdGenerator = new NodeIdGenerator();
            private final Root root = new Root(
                    nodeIdGenerator.next(),
                    "",
                    Collections.emptyList(),
                    new Type(
                            nodeIdGenerator.next(),
                            Visibility.NAMESPACE,
                            "Type",
                            Collections.emptyList(),
                            new TypeSpecSimple(nodeIdGenerator.next(), "Type", Collections.emptyList()),
                            Collections.emptyList()
                    )
            );

            @Override
            public AST parse() {
                // nothing to parse, the AST is already built
                return this;
            }

            @Override
            public Optional<Root> root() {
                return Optional.of(root);
            }

            @Override
            public List<CompilationError> errors() {
                return Collections.singletonList(new SymbolNotFoundError(
                        new NodeStub()
                                .getContext()
                                .put(
                                        SourcePosition.class,
                                        new SourcePosition(
                                                new SourcePosition.Position(-1, -1),
                                                new SourcePosition.Position(-1, -1),
                                                Collections.singletonList("")
                                        )
                                )
                                .getNode(),
                        "ast"
                ));
            }
        };
    }

    private static class PluginStub implements Plugin {
        @Override
        public List<CompilationError> execute(CompilationContext context) {
            context.listener().onEvent(new Event(String.class, "plugin"));
            return Collections.singletonList(new SymbolNotFoundError(
                    new NodeStub()
                            .getContext()
                            .put(
                                    SourcePosition.class,
                                    new SourcePosition(
                                            new SourcePosition.Position(-1, -1),
                                            new SourcePosition.Position(-1, -1),
                                            Collections.singletonList("")
                                    )
                            )
                            .getNode(),
                    "plugin"
            ));
        }
    }

    private static class ListenerStub implements CompilationListener {
        private final List<String> events = new ArrayList<>();
        private CompilationContext context;

        @Override
        public void onCompilationStarted() {
            events.add("onCompilationStarted");
        }

        @Override
        public void onCompilationFinished() {
            events.add("onCompilationFinished");
        }

        @Override
        public void onSourceStarted(Source source) {
            events.add("onSourceStarted");
        }

        @Override
        public void onSourceFinished(Source source, CompilationContext context) {
            events.add("onSourceFinished");
            this.context = context;
        }

        @Override
        public void onError(Source source, CompilationError error) {
            if (error instanceof SemanticError) {
                events.add(
                        String.format("onError:%s",
                                ((SemanticError) error)
                                        .format((sp, message) -> String.format("%s (%d)", message, sp.getStartLine()))
                        )
                );
            }
            if (error instanceof SyntaxError) {
                events.add(
                        String.format("onError:%s",
                                ((SyntaxError) error)
                                        .format((sourcePosition, message, exception) ->
                                                String.format("%s (%d)", message, sourcePosition.getStartLine()))
                        )
                );
            }
        }

        @Override
        public void onEvent(Event event) {
            String payload = event
                    .payload(String.class)
                    .orElseThrow(() -> new IllegalStateException("payload expected"));
            events.add("onEvent:" + payload);
        }
    }

    private static class InputStreamStub extends InputStream {
        @Override
        public int read() {
            return 0;
        }
    }
}