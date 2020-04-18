package org.thoriumlang.compiler.ast.algorithms.typeinference;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.thoriumlang.compiler.api.errors.SemanticError;
import org.thoriumlang.compiler.ast.AST;
import org.thoriumlang.compiler.ast.algorithms.symbolicnamechecking.SymbolicNameChecker;
import org.thoriumlang.compiler.ast.algorithms.typechecking.TypeChecker;
import org.thoriumlang.compiler.ast.nodes.Attribute;
import org.thoriumlang.compiler.ast.nodes.NewAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.Node;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.ast.nodes.TypeSpec;
import org.thoriumlang.compiler.ast.nodes.TypeSpecInferred;
import org.thoriumlang.compiler.ast.nodes.TypeSpecIntersection;
import org.thoriumlang.compiler.ast.nodes.TypeSpecSimple;
import org.thoriumlang.compiler.ast.visitor.BaseVisitor;
import org.thoriumlang.compiler.ast.visitor.NodesMatchingVisitor;
import org.thoriumlang.compiler.ast.visitor.PredicateVisitor;
import org.thoriumlang.compiler.input.loaders.ThoriumRTClassLoader;
import org.thoriumlang.compiler.input.loaders.TypeLoader;
import org.thoriumlang.compiler.symbols.Name;
import org.thoriumlang.compiler.symbols.Symbol;
import org.thoriumlang.compiler.symbols.SymbolTable;
import org.thoriumlang.compiler.symbols.SymbolicName;
import org.thoriumlang.compiler.testsupport.SymbolsExtractionVisitor;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class TypeResolverTest {
    public static final String TEST_FILES_PATH = "/org/thoriumlang/compiler/ast/algorithms/typeinference/";

    private Root loadRoot(Path filePath) {
        try {
            AST ast = new AST(
                    Files.newInputStream(filePath),
                    "namespace",
                    new NodeIdGenerator(),
                    Arrays.asList(
                            new TypeChecker(Collections.singletonList(new TypeLoader() {
                                @Override
                                public Optional<Symbol> load(Name name, Node triggerNode) {
                                    // TODO implement or remove depending on the actual need
                                    return Optional.empty();
                                }
                            })),
                            new SymbolicNameChecker()
                    ),
                    new SymbolTable()
            );
            return ast.root().orElseThrow(() -> new IllegalStateException("no root found: " + ast.errors().get(0)));
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @TestFactory
    Stream<DynamicTest> valuesCases() throws URISyntaxException {
        extractTypeFromNodeName("a_BooleanOrInteger");
        final String FILENAME = "inferValues.th";
        return loadRoot(
                Paths.get(ThoriumRTClassLoader.class.getResource(TEST_FILES_PATH + FILENAME).toURI())
        )
                .getContext()
                .require(SymbolTable.class)
                .accept(new SymbolsExtractionVisitor()).stream()
                .filter(s -> s instanceof SymbolicName)
                .map(s -> (SymbolicName) s)
                .map(SymbolicName::getDefiningNode)
                .filter(n -> n.accept(new PredicateVisitor() {
                    @Override
                    public Boolean visit(NewAssignmentValue node) {
                        return true;
                    }

                    @Override
                    public Boolean visit(Attribute node) {
                        return true;
                    }
                })) // we have all NewAssignmentValue and Attribute nodes
                .map(node -> DynamicTest.dynamicTest(
                        FILENAME + " / " + getName(node),
                        () -> {
                            Assertions.assertThat(node.getContext().get(TypeSpec.class))
                                    .get()
                                    .extracting(this::typeSpecToString)
                                    .isEqualTo(extractTypeFromNodeName(getName(node)));
                        }
                ));
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
        });
    }

    private String typeSpecToString(TypeSpec typeSpec) {
        return typeSpec.accept(new BaseVisitor<String>() {
            @Override
            public String visit(TypeSpecIntersection node) {
                return node.getTypes().stream()
                        .map(t -> t.accept(this))
                        .collect(Collectors.joining(" | ", "(", ")"));
            }

            @Override
            public String visit(TypeSpecSimple node) {
                return node.toString();
            }
        });
    }

    private String extractTypeFromNodeName(String name) {
        Matcher matcher = Pattern
                .compile("[^_]+_([^_]+)_?.*?")
                .matcher(name);

        return matcher.find()
                ? expandOr(matcher.group(1))
                : "";
    }

    private String expandOr(String typeName) {
        Matcher matcher = Pattern
                .compile("^([a-zA-Z]+)Or([a-zA-Z]+)$")
                .matcher(typeName);

        return matcher.find()
                ? String.format("(%s | %s)", matcher.group(1), matcher.group(2))
                : typeName;
    }

    @TestFactory
    Stream<DynamicTest> languageConstructCases() throws URISyntaxException, IOException {
        Path directory = Paths.get(
                TypeResolverTest.class.getResource(TEST_FILES_PATH).toURI()
        );

        return Files
                .find(
                        directory,
                        999,
                        (p, bfa) -> p.getFileName().toString().matches("^infer[a-zA-Z0-9]+\\.th$")
                )
                .map(file -> DynamicTest.dynamicTest(
                        file.getFileName().toString().replaceFirst("\\.th$", ""),
                        () -> assertsOn(file)
                ));
    }

    private void assertsOn(Path file) {
        Root root = loadRoot(file);
        List<SemanticError> errors = new TypeResolver().walk(root);

        Assertions.assertThat(errors.stream().map(SemanticError::toString).collect(Collectors.toList()))
                .isEmpty();

        Assertions.assertThat(root.accept(new NodesMatchingVisitor(n ->
                n instanceof TypeSpecInferred && !n.getContext().contains(TypeSpec.class)
        ))).isEmpty();
    }
}