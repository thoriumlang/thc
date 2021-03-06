package org.thoriumlang.compiler.antlr4;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.thoriumlang.compiler.antlr.ThoriumLexer;
import org.thoriumlang.compiler.antlr.ThoriumParser;
import org.thoriumlang.compiler.api.errors.CompilationError;
import org.thoriumlang.compiler.ast.SyntaxErrorListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class ParserErrorListenerTest {
    @AfterEach
    void resetDebug() {
        System.setProperty("thc.parser.error.printStack", "false");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "type Type {%" +
                    "mismatched input '<EOF>' expecting {'}', 'public', 'namespace', IDENTIFIER}\n" +
                    "  1. type Type {\n" +
                    "                ^\n" +
                    "on line 1, column 12",
            "type type Type {}%" +
                    "extraneous input 'type' expecting IDENTIFIER\n" +
                    "  1. type type Type {}\n" +
                    "          ^^^^\n" +
                    "on line 1, column 6",
            "type Type[T {}%" +
                    "missing ']' at '{'\n" +
                    "  1. type Type[T {}\n" +
                    "                 ^\n" +
                    "on line 1, column 13",
            "class Class { method() { other(\"String\" } }%" +
                    "no viable alternative at input 'other(\"String\"}'\n" +
                    "  1. class Class { method() { other(\"String\" } }\n" +
                    "                                             ^\n" +
                    "on line 1, column 41" +
                    "%" +
                    "mismatched input '}' expecting ')'\n" +
                    "  1. class Class { method() { other(\"String\" } }\n" +
                    "                                             ^\n" +
                    "on line 1, column 41",
            "type Type {} stuff%" +
                    "extraneous input 'stuff' expecting <EOF>\n" +
                    "  1. type Type {} stuff\n" +
                    "                  ^^^^^\n" +
                    "on line 1, column 14",
    })
    void parser_withoutDebug(String string) {
        final List<CompilationError> errors = new ArrayList<>();
        SyntaxErrorListener listener = errors::add;

        String[] parts = string.split("%");

        ThoriumParser parser = new ThoriumParser(
                new CommonTokenStream(
                        new ThoriumLexer(CharStreams.fromString(parts[0]))
                )
        );

        parser.removeErrorListeners();
        parser.addErrorListener(new ParserErrorListener(listener));

        parser.root();

        Assertions.assertThat(errors)
                .extracting(Object::toString)
                .containsExactly(
                        Arrays.asList(parts)
                                .subList(1, parts.length)
                                .toArray(new String[]{})
                );
    }

    @ParameterizedTest()
    @ValueSource(strings = {
            "type Type {%" +
                    "mismatched input '<EOF>' expecting {'}', 'public', 'namespace', IDENTIFIER}\n" +
                    "  1. type Type {\n" +
                    "                ^\n" +
                    "on line 1, column 12",
            "type type Type {}%" +
                    "extraneous input 'type' expecting IDENTIFIER\n" +
                    "  1. type type Type {}\n" +
                    "          ^^^^\n" +
                    "on line 1, column 6",
            "type Type[T {}%" +
                    "missing ']' at '{'\n" +
                    "  1. type Type[T {}\n" +
                    "                 ^\n" +
                    "on line 1, column 13",
            // no viable alternative
            "type Type {} stuff%" +
                    "extraneous input 'stuff' expecting <EOF>\n" +
                    "  1. type Type {} stuff\n" +
                    "                  ^^^^^\n" +
                    "on line 1, column 14",
    })
    void parser_withoutDebugKeepAllTokens(String string) {
        final List<CompilationError> errors = new ArrayList<>();
        SyntaxErrorListener listener = errors::add;

        String[] parts = string.split("%");

        ThoriumParser parser = new ThoriumParser(
                new CommonTokenStream(
                        new ThoriumLexer(
                                CharStreams.fromString(parts[0]),
                                new DefaultLexerConfiguration() {
                                    @Override
                                    public boolean keepAllTokens() {
                                        return true;
                                    }
                                }
                        )
                )
        );

        parser.removeErrorListeners();
        parser.addErrorListener(new ParserErrorListener(listener));

        parser.root();

        Assertions.assertThat(errors)
                .hasSize(1)
                .extracting(Object::toString)
                .containsExactly(parts[1]);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "type Type {%" +
                    "mismatched input '<EOF>' expecting {'}', 'public', 'namespace', IDENTIFIER}\n" +
                    "parser stack: [root, typeDef]\n" +
                    "  1. type Type {\n" +
                    "                ^\n" +
                    "on line 1, column 12",
            "type type Type {}%" +
                    "extraneous input 'type' expecting IDENTIFIER\n" +
                    "parser stack: [root, typeDef]\n" +
                    "  1. type type Type {}\n" +
                    "          ^^^^\n" +
                    "on line 1, column 6",
            "type Type[T {}%" +
                    "missing ']' at '{'\n" +
                    "parser stack: [root, typeDef]\n" +
                    "  1. type Type[T {}\n" +
                    "                 ^\n" +
                    "on line 1, column 13",
            "type Type {} stuff%" +
                    "extraneous input 'stuff' expecting <EOF>\n" +
                    "parser stack: [root]\n" +
                    "  1. type Type {} stuff\n" +
                    "                  ^^^^^\n" +
                    "on line 1, column 14",
            // no viable alternative
    })
    void parser_withDebug(String string) {
        System.setProperty("thc.parser.error.printStack", "true");
        final List<CompilationError> errors = new ArrayList<>();
        SyntaxErrorListener listener = errors::add;

        String[] parts = string.split("%");

        ThoriumParser parser = new ThoriumParser(
                new CommonTokenStream(
                        new ThoriumLexer(CharStreams.fromString(parts[0]))
                )
        );

        parser.removeErrorListeners();
        parser.addErrorListener(new ParserErrorListener(listener));

        parser.root();

        Assertions.assertThat(errors)
                .hasSize(1)
                .extracting(Object::toString)
                .containsExactly(parts[1]);
    }
}