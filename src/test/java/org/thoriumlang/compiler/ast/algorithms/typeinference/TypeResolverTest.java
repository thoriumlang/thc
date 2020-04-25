package org.thoriumlang.compiler.ast.algorithms.typeinference;

import com.google.common.collect.Maps;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.thoriumlang.compiler.api.errors.SemanticError;
import org.thoriumlang.compiler.ast.AST;
import org.thoriumlang.compiler.ast.algorithms.symbolicnamechecking.SymbolicNameChecker;
import org.thoriumlang.compiler.ast.nodes.Attribute;
import org.thoriumlang.compiler.ast.nodes.Method;
import org.thoriumlang.compiler.ast.nodes.NewAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.Node;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.ast.nodes.TypeSpec;
import org.thoriumlang.compiler.ast.nodes.TypeSpecInferred;
import org.thoriumlang.compiler.ast.nodes.TypeSpecIntersection;
import org.thoriumlang.compiler.ast.nodes.TypeSpecSimple;
import org.thoriumlang.compiler.ast.nodes.TypeSpecUnion;
import org.thoriumlang.compiler.ast.visitor.BaseVisitor;
import org.thoriumlang.compiler.ast.visitor.NodesMatchingVisitor;
import org.thoriumlang.compiler.ast.visitor.PredicateVisitor;
import org.thoriumlang.compiler.input.Source;
import org.thoriumlang.compiler.input.SourceFiles;
import org.thoriumlang.compiler.input.loaders.ThoriumRTClassLoader;
import org.thoriumlang.compiler.symbols.SymbolTable;
import org.thoriumlang.compiler.symbols.SymbolicName;
import org.thoriumlang.compiler.testsupport.SymbolsExtractionVisitor;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class TypeResolverTest {
    private static final String TEST_FILES_PATH = "/org/thoriumlang/compiler/ast/algorithms/typeinference/";
    private static final String ASSERT_MARKER = "^assert_";
    private static final NodeIdGenerator nodeIdGenerator = new NodeIdGenerator();

    @TestFactory
    Stream<DynamicTest> valuesCases() throws URISyntaxException {
        return new SourceFiles(
                Paths.get(ThoriumRTClassLoader.class.getResource(TEST_FILES_PATH).toURI()),
                p -> true
        )
                .sources()
                .stream()
                .flatMap(this::buildTestsStream);
    }

    private Stream<? extends DynamicTest> buildTestsStream(Source source) {
        AST ast = source.ast(
                nodeIdGenerator,
                new SymbolTable(),
                Collections.singletonList(new SymbolicNameChecker())
        );

        String fileName = source.toString().substring(source.toString().lastIndexOf('/') + 1);

        if (!ast.root().isPresent() || !ast.errors().isEmpty()) {
            return Stream.of(
                    DynamicTest.dynamicTest(
                            fileName + " / *",
                            () -> Assertions.fail(ast.errors().get(0).toString())
                    )
            );
        }

        Root root = ast.root().get();

        try {
            List<SemanticError> errors = new TypeResolver(nodeIdGenerator).walk(root);

            return root.getContext()
                    .require(SymbolTable.class)
                    .accept(new SymbolsExtractionVisitor()).stream()
                    .filter(s -> s instanceof SymbolicName)
                    .map(s -> (SymbolicName) s)
                    .map(SymbolicName::getDefiningNode)
                    .filter(n -> n.accept(new PredicateVisitor() {
                        @Override
                        public Boolean visit(NewAssignmentValue node) {
                            return node.getName().matches(ASSERT_MARKER + ".*");
                        }

                        @Override
                        public Boolean visit(Attribute node) {
                            return node.getName().matches(ASSERT_MARKER + ".*");
                        }

                        @Override
                        public Boolean visit(Method node) {
                            return node.getSignature().getName().matches(ASSERT_MARKER + ".*");
                        }
                    })) // we have all NewAssignmentValue and Attribute nodes
                    .map(node -> DynamicTest.dynamicTest(
                            fileName + " / " + getName(node),
                            () -> doAssert(root, errors, node)
                    ));
        }
        catch (Exception e) {
            return Stream.of(
                    DynamicTest.dynamicTest(
                            fileName + " / *",
                            () -> Assertions.fail(e.getMessage(), e)
                    )
            );
        }
    }

    private void doAssert(Root root, List<SemanticError> errors, Node node) {
        Assertions.assertThat(errors.stream().map(SemanticError::toString).collect(Collectors.toList()))
                .isEmpty();

//        Assertions.assertThat(root.accept(new NodesMatchingVisitor(n ->
//                // TODO this is not correct, all nodes should have a TypeSpec in the context
//                //  i.e. remove the instanceof restriction
//                n instanceof TypeSpecInferred && !n.getContext().contains(TypeSpec.class)
//        ))).isEmpty();

        Assertions.assertThat(node.getContext().get(TypeSpec.class))
                .get()
                .extracting(this::typeSpecToString)
                .isEqualTo(extractTypeFromNodeName(getName(node)));
    }

    private String getName(Node node) {
        return node.accept(new BaseVisitor<String>() {
            @Override
            public String visit(NewAssignmentValue node) {
                return node.getName();
            }

            @Override
            public String visit(Attribute node) {
                return node.getName();
            }

            @Override
            public String visit(Method node) {
                return node.getSignature().getName();
            }
        });
    }

    private String typeSpecToString(TypeSpec typeSpec) {
        return typeSpec.accept(new BaseVisitor<String>() {
            @Override
            public String visit(TypeSpecIntersection node) {
                return node.getTypes().stream()
                        .map(t -> t.accept(this))
                        .sorted()
                        .collect(Collectors.joining(" | "));
            }

            @Override
            public String visit(TypeSpecUnion node) {
                return node.getTypes().stream()
                        .map(t -> t.accept(this))
                        .sorted()
                        .collect(Collectors.joining(" & "));
            }

            @Override
            public String visit(TypeSpecSimple node) {
                return node.toString();
            }
        });
    }

    private String extractTypeFromNodeName(String name) {
        Matcher matcher = Pattern
                .compile(ASSERT_MARKER + "([^_]+)_?.*?")
                .matcher(name);

        return matcher.find()
                ? expandTypeString(matcher.group(1))
                : "";
    }

    private String expandTypeString(String typeName) {
        if (typeName.contains("Or") && typeName.contains("And")) {
            throw new UnsupportedOperationException("combining Or and And is not supported");
        }

        Map<String, String> predefined = Maps.asMap(
                new HashSet<>(Arrays.asList("String", "Number", "None", "Function", "Boolean", "Object")),
                e -> String.format("org.thoriumlang.%s[]", e)
        );

        String fqNames = typeName.replaceAll("DOT", ".");

        if (typeName.contains("Or")) {
            return Arrays.stream(fqNames.split("Or"))
                    .map(name -> predefined.getOrDefault(name, String.format("%s[]", name)))
                    .collect(Collectors.joining(" | "));
        }

        return Arrays.stream(fqNames.split("And"))
                .map(name -> predefined.getOrDefault(name, String.format("%s[]", name)))
                .collect(Collectors.joining(" & "));
    }
}