package org.thoriumlang.compiler.it;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.api.Compiler;
import org.thoriumlang.compiler.api.NoopCompilationListener;
import org.thoriumlang.compiler.api.errors.CompilationError;
import org.thoriumlang.compiler.input.Source;
import org.thoriumlang.compiler.input.SourceFiles;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompilerTest {
    @Test
    void error_lexer() throws URISyntaxException, IOException {
        SourceFiles sourceFiles = sourceFiles("CompilerTest_error_lexer.th");

        List<String> compilationErrors = new ArrayList<>();
        Compiler compiler = new Compiler(new CompilationListener(compilationErrors), Collections.emptyList());

        compiler.compile(sourceFiles);

        Assertions.assertThat(compilationErrors)
                .filteredOn(new Condition<>(e -> e.startsWith("[SyntaxError]"), null))
                .hasSize(1)
                .have(new Condition<>(
                                e -> e.contains("token recognition error at: '~' (2)")
                                        && e.contains("CompilerTest_error_lexer.th"),
                                null
                        )
                );
    }

    @Test
    void error_lexerOfDependency() throws URISyntaxException, IOException {
        SourceFiles sourceFiles = sourceFiles("CompilerTest_error_lexerOfDependency.th");

        List<String> compilationErrors = new ArrayList<>();
        Compiler compiler = new Compiler(new CompilationListener(compilationErrors), Collections.emptyList());

        compiler.compile(sourceFiles);

        Assertions.assertThat(compilationErrors)
                .filteredOn(new Condition<>(e -> e.startsWith("[SyntaxError]"), null))
                .hasSize(1)
                .have(new Condition<>(
                                e -> e.contains("token recognition error at: '~' (2)")
                                        && e.contains("CompilerTest_error_lexer.th"),
                                null
                        )
                );
    }

    private SourceFiles sourceFiles(String fileName) throws IOException, URISyntaxException {
        return new SourceFiles(
                Files.find(
                        Paths.get(CompilerTest.class.getResource("/org/thoriumlang/compiler/it/").toURI()),
                        999,
                        (p, bfa) -> p.getFileName().toString().equals(fileName)
                )
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException(fileName + " not found")),
                p -> true
        );
    }

    @Test
    void error_parser() throws IOException, URISyntaxException {
        SourceFiles sourceFiles = sourceFiles("CompilerTest_error_parser.th");

        List<String> compilationErrors = new ArrayList<>();
        Compiler compiler = new Compiler(new CompilationListener(compilationErrors), Collections.emptyList());

        compiler.compile(sourceFiles);

        Assertions.assertThat(compilationErrors)
                .filteredOn(new Condition<>(e -> e.startsWith("[SyntaxError]"), null))
                .hasSize(1)
                .have(new Condition<>(
                                e -> e.contains("missing IDENTIFIER at '{' (1)")
                                        && e.contains("CompilerTest_error_parser.th"),
                                null
                        )
                );
    }

    @Test
    void error_parserOfDependency() throws IOException, URISyntaxException {
        SourceFiles sourceFiles = sourceFiles("CompilerTest_error_parserOfDependency.th");

        List<String> compilationErrors = new ArrayList<>();
        Compiler compiler = new Compiler(new CompilationListener(compilationErrors), Collections.emptyList());

        compiler.compile(sourceFiles);

        Assertions.assertThat(compilationErrors)
                .filteredOn(new Condition<>(e -> e.startsWith("[SyntaxError]"), null))
                .hasSize(1)
                .haveAtLeastOne(new Condition<>(
                                e -> e.contains("missing IDENTIFIER at '{' (1)")
                                        && e.contains("CompilerTest_error_parser.th"),
                                null
                        )
                );
    }

    @Test
    void error_semantic() throws IOException, URISyntaxException {
        SourceFiles sourceFiles = sourceFiles("CompilerTest_error_semantic.th");

        List<String> compilationErrors = new ArrayList<>();
        Compiler compiler = new Compiler(new CompilationListener(compilationErrors), Collections.emptyList());

        compiler.compile(sourceFiles);

        Assertions.assertThat(compilationErrors)
                .filteredOn(new Condition<>(e -> e.startsWith("[SemanticError]"), null))
                .hasSize(1)
                .haveAtLeastOne(new Condition<>(
                                e -> e.contains("symbol not found: SuperType (1)")
                                        && e.contains("CompilerTest_error_semantic.th"),
                                null
                        )
                );
    }

    @Test
    void error_semanticOfDependency() throws IOException, URISyntaxException {
        SourceFiles sourceFiles = sourceFiles("CompilerTest_error_semanticOfDependency.th");

        List<String> compilationErrors = new ArrayList<>();
        Compiler compiler = new Compiler(new CompilationListener(compilationErrors), Collections.emptyList());

        compiler.compile(sourceFiles);

        Assertions.assertThat(compilationErrors)
                .filteredOn(new Condition<>(e -> e.startsWith("[SemanticError]"), null))
                .hasSize(1)
                .haveAtLeastOne(new Condition<>(
                                e -> e.contains("symbol not found: SuperType (1)")
                                        && e.contains("CompilerTest_error_semantic.th"),
                                null
                        )
                );
    }

    private static class CompilationListener extends NoopCompilationListener {
        private final List<String> compilationErrors;

        public CompilationListener(List<String> compilationErrors) {
            this.compilationErrors = compilationErrors;
        }

        @Override
        public void onError(Source source, CompilationError error) {
            compilationErrors.add(String.format("[%s] %s: %s",
                    error.getClass().getSimpleName(), source.toString(), error.toString()
            ));
        }
    }
}
