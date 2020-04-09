package org.thoriumlang.compiler.api;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.ast.AST;
import org.thoriumlang.compiler.ast.algorithms.CompilationError;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.ast.nodes.Type;
import org.thoriumlang.compiler.ast.nodes.TypeSpecSimple;
import org.thoriumlang.compiler.ast.nodes.Visibility;
import org.thoriumlang.compiler.input.Source;
import org.thoriumlang.compiler.testsupport.NodeStub;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

class CompilationContextTest {
    private static final CompilationListener listener = new CompilationListener() {
        @Override
        public void onCompilationStarted(int sourcesCount) {
        }

        @Override
        public void onCompilationFinished() {
        }

        @Override
        public void onCompilationProgress(float progress) {
        }

        @Override
        public void onSourceStarted(Source source) {
        }

        @Override
        public void onSourceFinished(Source source, CompilationContext context) {
        }

        @Override
        public void onError(Source source, CompilationError error) {
        }

        @Override
        public void onEvent(Event event) {
        }
    };
    private static final Root root = new RootStub();
    private static AST ast = new AST(
            new InputStreamStub(), "", new NodeIdGenerator(), Collections.emptyList()
    ) {
        @Override
        public Root root() {
            return root;
        }

        @Override
        public List<CompilationError> errors() {
            return Collections.singletonList(new CompilationError("message", new NodeStub()));
        }
    };

    @Test
    void constructor_ast() {
        Assertions.assertThatThrownBy(() -> new CompilationContext(null, listener))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("ast cannot be null");
    }

    @Test
    void constructor_listener() {
        Assertions.assertThatThrownBy(() -> new CompilationContext(ast, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("listener cannot be null");
    }

    @Test
    void root() {
        Assertions.assertThat(new CompilationContext(ast, listener).root())
                .isSameAs(root);
    }

    @Test
    void error() {
        Assertions.assertThat(new CompilationContext(ast, listener).errors())
                .hasSize(1);
    }

    @Test
    void listener() {
        Assertions.assertThat(new CompilationContext(ast, listener).listener())
                .isSameAs(listener);
    }

    @Test
    void put_get() {
        CompilationContext context = new CompilationContext(ast, listener);
        context.put(String.class, "String");

        Assertions.assertThat(context.get(String.class))
                .get()
                .isEqualTo("String");

        Assertions.assertThat(context.get(Long.class))
                .isEmpty();
    }

    private static class RootStub extends Root {
        private static NodeIdGenerator nodeIdGenerator = new NodeIdGenerator();

        public RootStub() {
            super(
                    nodeIdGenerator.next(),
                    "",
                    Collections.emptyList(),
                    new Type(
                            new NodeIdGenerator().next(),
                            Visibility.NAMESPACE,
                            "Type",
                            Collections.emptyList(),
                            new TypeSpecSimple(nodeIdGenerator.next(), "Type", Collections.emptyList()),
                            Collections.emptyList()
                    )
            );
        }
    }

    private static class InputStreamStub extends InputStream {
        @Override
        public int read() throws IOException {
            return 0;
        }
    }
}