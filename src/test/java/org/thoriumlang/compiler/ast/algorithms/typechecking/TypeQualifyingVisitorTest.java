package org.thoriumlang.compiler.ast.algorithms.typechecking;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.api.errors.SemanticError;
import org.thoriumlang.compiler.ast.AST;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.ast.nodes.Type;
import org.thoriumlang.compiler.ast.nodes.TypeSpecSimple;
import org.thoriumlang.compiler.ast.nodes.Visibility;
import org.thoriumlang.compiler.ast.visitor.NodesMatchingVisitor;
import org.thoriumlang.compiler.ast.visitor.PredicateVisitor;
import org.thoriumlang.compiler.data.Maybe;
import org.thoriumlang.compiler.input.loaders.TypeLoader;
import org.thoriumlang.compiler.symbols.SymbolTable;
import org.thoriumlang.compiler.symbols.ThoriumType;
import org.thoriumlang.compiler.testsupport.SourceProvider;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

class TypeQualifyingVisitorTest {
    private static final NodeIdGenerator nodeIdGenerator = new NodeIdGenerator();

    private static TypeLoader buildTypeLoader() {
        return (name, triggerNode) -> Optional.of(new ThoriumType(
                triggerNode,
                new Type(
                        nodeIdGenerator.next(),
                        Visibility.NAMESPACE,
                        name.getSimpleName(),
                        Collections.emptyList(),
                        new TypeSpecSimple(
                                nodeIdGenerator.next(),
                                "T",
                                Collections.emptyList()),
                        Collections.emptyList()
                )
        ));
    }

    private static void doAsserts(AST ast) {
        Assertions.assertThat(ast.errors()).isEmpty();
        Assertions.assertThat(ast.root()).isPresent();

        Root root = ast.root().orElseThrow(() -> new IllegalStateException("not root found"));
        new TypeDiscoveryVisitor(root.getNamespace(), buildTypeLoader()).visit(root);

        Maybe<Root, List<SemanticError>> result = new TypeQualifyingVisitor(nodeIdGenerator).visit(root);
        Assertions.assertThat(result.isSuccess()).isTrue();
        Root newRoot = result.value();

        List<String> nonCanonicalTypes = newRoot
                .accept(new NodesMatchingVisitor(n -> n.accept(new PredicateVisitor() {
                    @Override
                    public Boolean visit(TypeSpecSimple node) {
                        return true;
                    }
                })))
                .stream()
                .map(n -> (TypeSpecSimple) n)
                .map(TypeSpecSimple::getType)
                .filter(t -> t.equals("org.thoriumlang.T") || !t.startsWith("org.thoriumlang.") && !t.equals("T"))
                .collect(Collectors.toList());

        Assertions.assertThat(nonCanonicalTypes).isEmpty();
    }

    @Test
    void testClass() {
        doAsserts(
                SourceProvider.provide(TypeQualifyingVisitorTest.class, "MainClass.th").ast(
                        nodeIdGenerator,
                        new SymbolTable(),
                        Collections.emptyList()
                )
        );
    }

    @Test
    void testType() {
        doAsserts(
                SourceProvider.provide(TypeQualifyingVisitorTest.class, "MainType.th").ast(
                        nodeIdGenerator,
                        new SymbolTable(),
                        Collections.emptyList()
                )
        );
    }
}