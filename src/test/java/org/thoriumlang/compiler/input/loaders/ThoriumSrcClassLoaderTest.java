package org.thoriumlang.compiler.input.loaders;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.api.Compiler;
import org.thoriumlang.compiler.api.NoopCompilationListener;
import org.thoriumlang.compiler.api.errors.CompilationError;
import org.thoriumlang.compiler.ast.AST;
import org.thoriumlang.compiler.ast.nodes.Node;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.ast.nodes.TopLevelNode;
import org.thoriumlang.compiler.ast.nodes.Type;
import org.thoriumlang.compiler.ast.nodes.TypeSpecSimple;
import org.thoriumlang.compiler.ast.nodes.Visibility;
import org.thoriumlang.compiler.ast.visitor.NodesMatchingVisitor;
import org.thoriumlang.compiler.ast.visitor.RelativesInjectionVisitor;
import org.thoriumlang.compiler.ast.visitor.SymbolTableInitializationVisitor;
import org.thoriumlang.compiler.ast.visitor.Visitor;
import org.thoriumlang.compiler.input.Source;
import org.thoriumlang.compiler.input.Sources;
import org.thoriumlang.compiler.symbols.Name;
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
        new SymbolTableInitializationVisitor(rootSymbolTable).visit(root);

        Optional<Symbol> symbol = new ThoriumSrcClassLoader(
                new SourcesStub(root.getTopLevelNode()),
                new Compiler(new NoopCompilationListener(), Collections.emptyList())
        ).load(new Name("package.TypeName"), root.getTopLevelNode());

        Assertions.assertThat(symbol)
                .get()
                .extracting(Symbol::getDefiningNode)
                .isSameAs(root.getTopLevelNode());

        Set<SymbolTable> rootSymbolTablesFromRoot = new NodesMatchingVisitor(n -> true).visit(root).stream()
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

        Optional<Symbol> symbol = new ThoriumSrcClassLoader(
                new SourcesStub(),
                new Compiler(new NoopCompilationListener(), Collections.emptyList())
        ).load(new Name("package.TypeName"), node);

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
        public Optional<Source> load(Name name) {
            if (topLevel == null) {
                return Optional.empty();
            }
            return Optional.of((nodeIdGenerator, symbolTable, algorithms) ->
                    new AST(new InputStreamStub(), "namespace", new NodeIdGenerator(), algorithms, symbolTable) {
                        @Override
                        public AST parse() {
                            // nothing to parse, the AST is already built
                            return this;
                        }

                        @Override
                        public Optional<Root> root() {
                            Root root = new Root(
                                    nodeIdGenerator.next(),
                                    "namespace",
                                    Collections.emptyList(),
                                    topLevel
                            );

                            return Optional.of(root);
                        }

                        @Override
                        public List<CompilationError> errors() {
                            return Collections.emptyList();
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
