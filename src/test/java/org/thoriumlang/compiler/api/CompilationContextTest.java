package org.thoriumlang.compiler.api;

import io.vavr.control.Either;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.api.errors.CompilationError;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.ast.nodes.Type;
import org.thoriumlang.compiler.ast.nodes.TypeSpecSimple;
import org.thoriumlang.compiler.ast.nodes.Visibility;

import java.io.InputStream;
import java.util.Collections;

class CompilationContextTest {
    private static final CompilationListener listener = new NoopCompilationListener();
    private static final Root root = new RootStub();

    @Test
    void constructor_ast() {
        Assertions.assertThatThrownBy(() -> new CompilationContext(null, listener))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("ast cannot be null");
    }

    @Test
    void constructor_listener() {
        Assertions.assertThatThrownBy(() -> new CompilationContext(Either.left(Collections.emptyList()), null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("listener cannot be null");
    }

    @Test
    void root() {
        Assertions.assertThat(
                new CompilationContext(Either.right(root), listener)
                        .root()
                        .orElseThrow(() -> new IllegalStateException("no root found"))
        ).isSameAs(root);
    }

    @Test
    void error() {
        Assertions.assertThat(new CompilationContext(
                Either.left(Collections.singletonList(new CompilationError() {
                })),
                listener
        ).errors()).hasSize(1);
    }

    @Test
    void listener() {
        Assertions.assertThat(new CompilationContext(Either.right(root), listener).listener())
                .isSameAs(listener);
    }

    @Test
    void put_get() {
        CompilationContext context = new CompilationContext(Either.right(root), listener);
        context.put(String.class, "String");

        Assertions.assertThat(context.get(String.class))
                .get()
                .isEqualTo("String");

        Assertions.assertThat(context.get(Long.class))
                .isEmpty();
    }

    private static class RootStub extends Root {
        private static final NodeIdGenerator nodeIdGenerator = new NodeIdGenerator();

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
        public int read() {
            return 0;
        }
    }
}