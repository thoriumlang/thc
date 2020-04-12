package org.thoriumlang.compiler.api;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.api.errors.CompilationError;
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
import org.thoriumlang.compiler.testsupport.NodeStub;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

class CompilerTest {
    @Test
    void constructor_listener() {
        Assertions.assertThatThrownBy(() -> new Compiler(null, Collections.singletonList(new Plugin())))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("listener cannot be null");
    }

    @Test
    void constructor_plugins() {
        Assertions.assertThatThrownBy(() -> new Compiler(new Listener(), null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("plugins cannot be null");
    }

    @Test
    void compile() {
        Listener listener = new Listener();
        Compiler compiler = new Compiler(listener, Collections.singletonList(new Plugin()));
        AST ast = ast();

        compiler.compile(new Sources() {
            @Override
            public List<Source> sources() {
                return Collections.singletonList((nodeIdGenerator, algorithms) -> ast);
            }

            @Override
            public Optional<Source> load(Name name) {
                return Optional.empty();
            }
        });

        Assertions.assertThat(listener.events)
                .containsExactly(
                        "onCompilationStarted:1",
                        "onSourceStarted",
                        "onError:ast (-1)",
                        "onEvent:plugin",
                        "onError:plugin (-1)",
                        "onSourceFinished",
                        "onCompilationProgress:1.0",
                        "onCompilationFinished"
                );

        Assertions.assertThat(listener.context)
                .isNotNull();

        Assertions.assertThat(
                listener.context.errors()
                        .stream()
                        .map(CompilationError::toString)
                        .collect(Collectors.toList())
        ).containsExactly("ast (-1)");

        Assertions.assertThat(listener.context.root())
                .isSameAs(ast.root());
    }

    private AST ast() {
        return new AST(new InputStreamStub(), "", new NodeIdGenerator(), Collections.emptyList()) {
            NodeIdGenerator nodeIdGenerator = new NodeIdGenerator();
            Root root = new Root(
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
            public Root root() {
                return root;
            }

            @Override
            public List<CompilationError> errors() {
                return Collections.singletonList(new CompilationError(
                        "ast",
                        new NodeStub()
                                .getContext()
                                .put(SourcePosition.class, new SourcePosition(-1, -1))
                                .getNode()
                ));
            }
        };
    }

    private static class Plugin implements org.thoriumlang.compiler.api.Plugin {
        @Override
        public List<CompilationError> execute(CompilationContext context) {
            context.listener().onEvent(new Event(String.class, "plugin"));
            return Collections.singletonList(new CompilationError(
                    "plugin",
                    new NodeStub()
                            .getContext()
                            .put(SourcePosition.class, new SourcePosition(-1, -1))
                            .getNode()
            ));
        }
    }

    private static class Listener implements CompilationListener {
        private List<String> events = new ArrayList<>();
        private List<CompilationError> errors = new ArrayList<>();
        private CompilationContext context;

        @Override
        public void onCompilationStarted(int sourcesCount) {
            events.add("onCompilationStarted:" + sourcesCount);
        }

        @Override
        public void onCompilationFinished() {
            events.add("onCompilationFinished");
        }

        @Override
        public void onCompilationProgress(float progress) {
            events.add("onCompilationProgress:" + progress);
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
            events.add("onError:" + error.toString());
            errors.add(error);
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
        public int read() throws IOException {
            return 0;
        }
    }
}