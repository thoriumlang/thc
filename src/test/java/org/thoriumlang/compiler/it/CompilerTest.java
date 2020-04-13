package org.thoriumlang.compiler.it;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.api.Compiler;
import org.thoriumlang.compiler.api.NoopCompilationListener;
import org.thoriumlang.compiler.api.errors.CompilationError;
import org.thoriumlang.compiler.collections.Lists;
import org.thoriumlang.compiler.input.Source;
import org.thoriumlang.compiler.input.SourceFiles;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CompilerTest {
    @Test
    void error_lexer() throws URISyntaxException {
        SourceFiles sourceFiles = sourceFiles("CompilerTest_error_lexer.th");

        List<String> compilationErrors = new ArrayList<>();
        Compiler compiler = new Compiler(new CompilationErrorListener(compilationErrors), Collections.emptyList());

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
    void error_lexerOfDependency() throws URISyntaxException {
        SourceFiles sourceFiles = sourceFiles("CompilerTest_error_lexerOfDependency.th");

        List<String> compilationErrors = new ArrayList<>();
        Compiler compiler = new Compiler(new CompilationErrorListener(compilationErrors), Collections.emptyList());

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

    private SourceFiles sourceFiles(String... fileName) throws URISyntaxException {
        return new SourceFiles(
                Paths.get(CompilerTest.class.getResource("/org/thoriumlang/compiler/it/").toURI()),
                p -> Arrays.asList(fileName).contains(p.getFileName().toString())
        );
    }

    @Test
    void error_parser() throws URISyntaxException {
        SourceFiles sourceFiles = sourceFiles("CompilerTest_error_parser.th");

        List<String> compilationErrors = new ArrayList<>();
        Compiler compiler = new Compiler(new CompilationErrorListener(compilationErrors), Collections.emptyList());

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
    void error_parserOfDependency() throws URISyntaxException {
        SourceFiles sourceFiles = sourceFiles("CompilerTest_error_parserOfDependency.th");

        List<String> compilationErrors = new ArrayList<>();
        Compiler compiler = new Compiler(new CompilationErrorListener(compilationErrors), Collections.emptyList());

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
    void error_semantic() throws URISyntaxException {
        SourceFiles sourceFiles = sourceFiles("CompilerTest_error_semantic.th");

        List<String> compilationErrors = new ArrayList<>();
        Compiler compiler = new Compiler(new CompilationErrorListener(compilationErrors), Collections.emptyList());

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
    void error_semanticOfDependency() throws URISyntaxException {
        SourceFiles sourceFiles = sourceFiles("CompilerTest_error_semanticOfDependency.th");

        List<String> compilationErrors = new ArrayList<>();
        Compiler compiler = new Compiler(new CompilationErrorListener(compilationErrors), Collections.emptyList());

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
    void compile_reusesKnownTopLevels() throws URISyntaxException {
        SourceFiles sourceFiles = sourceFiles(
                "CompilerTest_compile_reusesKnownTopLevels_1.th",
                "CompilerTest_compile_reusesKnownTopLevels_2.th"
        );


        List<String> sourceStarted = new ArrayList<>();
        Compiler compiler = new Compiler(new CompilationSourceStartedListener(sourceStarted), Collections.emptyList());

        compiler.compile(
                new SourceFiles(
                        Paths.get(CompilerTest.class.getResource("/org/thoriumlang/compiler/it/").toURI())
                ) {
                    @Override
                    public List<Source> sources() {
                        return Lists.merge(sourceFiles.sources(), sourceFiles.sources());
                    }
                }
        );

        Assertions.assertThat(sourceStarted)
                .hasSize(3)
                .haveAtLeastOne(new Condition<>(s -> s.endsWith("CompilerTest_compile_reusesKnownTopLevels_1.th"), null))
                .haveAtLeastOne(new Condition<>(s -> s.endsWith("CompilerTest_compile_reusesKnownTopLevels_2.th"), null))
                .haveAtLeastOne(new Condition<>(s -> s.endsWith("CompilerTest_compile_reusesKnownTopLevels.th"), null));
    }

    private static class CompilationErrorListener extends NoopCompilationListener {
        private final List<String> compilationErrors;

        public CompilationErrorListener(List<String> compilationErrors) {
            this.compilationErrors = compilationErrors;
        }

        @Override
        public void onError(Source source, CompilationError error) {
            compilationErrors.add(String.format("[%s] %s: %s",
                    error.getClass().getSimpleName(), source.toString(), error.toString()
            ));
        }
    }

    private static class CompilationSourceStartedListener extends NoopCompilationListener {
        private final List<String> sourceStarted;

        private CompilationSourceStartedListener(List<String> sourceStarted) {
            this.sourceStarted = sourceStarted;
        }

        @Override
        public void onSourceStarted(Source source) {
            sourceStarted.add(source.toString());
        }
    }
}
