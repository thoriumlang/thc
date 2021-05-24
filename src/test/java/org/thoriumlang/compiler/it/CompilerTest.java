package org.thoriumlang.compiler.it;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.api.Compiler;
import org.thoriumlang.compiler.api.NoopCompilationListener;
import org.thoriumlang.compiler.api.errors.CompilationError;
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
                .hasOnlyOneElementSatisfying(s -> Assertions.assertThat(s)
                        .contains(
                                "CompilerTest_error_lexer.th",
                                "token recognition error at: '~'",
                                "line 2, column 5"
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
                .hasOnlyOneElementSatisfying(s -> Assertions.assertThat(s)
                        .contains(
                                "CompilerTest_error_parser.th",
                                "missing IDENTIFIER at '{'",
                                "line 1"
                        )
                );
    }

    private static class CompilationErrorListener extends NoopCompilationListener {
        private final List<String> compilationErrors;

        public CompilationErrorListener(List<String> compilationErrors) {
            this.compilationErrors = compilationErrors;
        }

        @Override
        public void onError(Source source, CompilationError error) {
            compilationErrors.add(String.format("[%s] %s:%n%s",
                    error.getClass().getSimpleName(), source.toString(), error.toString()
            ));
        }
    }
}
