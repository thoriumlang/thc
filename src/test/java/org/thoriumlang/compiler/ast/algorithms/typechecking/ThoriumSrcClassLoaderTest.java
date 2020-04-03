package org.thoriumlang.compiler.ast.algorithms.typechecking;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.ast.AST;
import org.thoriumlang.compiler.ast.algorithms.Algorithm;
import org.thoriumlang.compiler.ast.algorithms.NodesMatching;
import org.thoriumlang.compiler.ast.algorithms.symboltable.SymbolTableInitializer;
import org.thoriumlang.compiler.ast.nodes.Node;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.ast.nodes.TopLevelNode;
import org.thoriumlang.compiler.ast.nodes.Type;
import org.thoriumlang.compiler.ast.nodes.TypeSpecSimple;
import org.thoriumlang.compiler.ast.nodes.Visibility;
import org.thoriumlang.compiler.ast.visitor.RelativesInjectionVisitor;
import org.thoriumlang.compiler.ast.visitor.Visitor;
import org.thoriumlang.compiler.input.Source;
import org.thoriumlang.compiler.input.Sources;
import org.thoriumlang.compiler.symbols.Symbol;
import org.thoriumlang.compiler.symbols.SymbolTable;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

class ThoriumSrcClassLoaderTest {
    private static final NodeIdGenerator nodeIdGenerator = new NodeIdGenerator();

    @Test
    void load_success() {
        Root root = new Root(
                nodeIdGenerator.next(),
                "namespace",
                Collections.emptyList(),
                new Type(
                        nodeIdGenerator.next(),
                        Visibility.NAMESPACE,
                        "Type",
                        Collections.emptyList(),
                        new TypeSpecSimple(nodeIdGenerator.next(), "Object", Collections.emptyList()),
                        Collections.emptyList()
                )
        );

        SymbolTable rootSymbolTable = new SymbolTable();
        new RelativesInjectionVisitor().visit(root);
        new SymbolTableInitializer(rootSymbolTable).walk(root);

        Optional<Symbol> symbol = new ThoriumSrcClassLoader(new SourcesStub(root.getTopLevelNode()))
                .load("package.TypeName", root.getTopLevelNode());

        Assertions.assertThat(symbol)
                .get()
                .extracting(Symbol::getNode)
                .isSameAs(root.getTopLevelNode());

        Set<SymbolTable> rootSymbolTablesFromRoot = new NodesMatching(n -> true).visit(root).stream()
                .map(n -> n.getContext().require(SymbolTable.class).root())
                .collect(Collectors.toSet());

        Assertions.assertThat(rootSymbolTablesFromRoot)
                .containsExactly(rootSymbolTable);
    }

    @Test
    void load_failure() {
        Node node = new Node(new NodeIdGenerator().next()) {
            @Override
            public <T> T accept(Visitor<? extends T> visitor) {
                return null;
            }
        };

        Optional<Symbol> symbol = new ThoriumSrcClassLoader(new SourcesStub())
                .load("package.TypeName", node);

        Assertions.assertThat(symbol)
                .isEmpty();
    }

    private static class SourcesStub implements Sources {
        private final TopLevelNode topLevel;

        private SourcesStub(TopLevelNode topLevel) {
            this.topLevel = topLevel;
        }

        private SourcesStub() {
            this(null);
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
                                public Root root() {
                                    Root root = new Root(
                                            nodeIdGenerator.next(),
                                            "namespace",
                                            Collections.emptyList(),
                                            topLevel
                                    );

                                    new SymbolTableInitializer(topLevel.getContext().require(SymbolTable.class))
                                            .walk(root);

                                    return root;
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
