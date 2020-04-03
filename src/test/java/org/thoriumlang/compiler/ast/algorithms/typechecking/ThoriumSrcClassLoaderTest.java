package org.thoriumlang.compiler.ast.algorithms.typechecking;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.ast.AST;
import org.thoriumlang.compiler.ast.algorithms.Algorithm;
import org.thoriumlang.compiler.ast.nodes.*;
import org.thoriumlang.compiler.ast.visitor.Visitor;
import org.thoriumlang.compiler.input.Source;
import org.thoriumlang.compiler.input.Sources;
import org.thoriumlang.compiler.symbols.Symbol;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

class ThoriumSrcClassLoaderTest {
    private final Node node = new Node(new NodeIdGenerator().next()) {
        @Override
        public <T> T accept(Visitor<? extends T> visitor) {
            return null;
        }
    };

    @Test
    void load_success() {
        NodeIdGenerator nodeIdGenerator = new NodeIdGenerator();

        Type topLevel = new Type(
                nodeIdGenerator.next(),
                Visibility.NAMESPACE,
                "Type",
                Collections.emptyList(),
                new TypeSpecSimple(nodeIdGenerator.next(), "Object", Collections.emptyList()),
                Collections.emptyList()
        );

        Optional<Symbol> symbol = new ThoriumSrcClassLoader(new SourcesStub(nodeIdGenerator, topLevel))
                .load("package.TypeName", node);

        Assertions.assertThat(symbol)
                .get()
                .extracting(Symbol::getNode)
                .isSameAs(topLevel);
    }

    @Test
    void load_failure() {
        Optional<Symbol> symbol = new ThoriumSrcClassLoader(new SourcesStub())
                .load("package.TypeName", node);

        Assertions.assertThat(symbol)
                .isEmpty();
    }

    private static class SourcesStub implements Sources {
        private final NodeIdGenerator nodeIdGenerator;
        private final TopLevelNode topLevel;

        private SourcesStub(NodeIdGenerator nodeIdGenerator, TopLevelNode topLevel) {
            this.nodeIdGenerator = nodeIdGenerator;
            this.topLevel = topLevel;
        }

        private SourcesStub() {
            this(null, null);
        }

        @Override
        public List<Source> sources() {
            return Collections.emptyList();
        }

        @Override
        public Optional<Source> load(String name) {
            if (topLevel == null) {
                return Optional.empty();
            }
            return Optional.of(
                    new Source() {
                        @Override
                        public AST ast(List<Algorithm> algorithms) {
                            return new AST(new InputStreamStub(), "namespace", algorithms) {
                                @Override
                                public Root root() throws IOException {
                                    return new Root(
                                            nodeIdGenerator.next(),
                                            "namespace",
                                            Collections.emptyList(),
                                            topLevel
                                    );
                                }
                            };
                        }

                        @Override
                        public AST ast() {
                            return ast(Collections.emptyList());
                        }
                    }
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
