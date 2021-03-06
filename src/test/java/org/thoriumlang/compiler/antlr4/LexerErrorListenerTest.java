package org.thoriumlang.compiler.antlr4;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.antlr.ThoriumLexer;
import org.thoriumlang.compiler.api.errors.CompilationError;
import org.thoriumlang.compiler.ast.SyntaxErrorListener;

import java.util.ArrayList;
import java.util.List;

class LexerErrorListenerTest {
    @Test
    void lexer_defaultConfiguration() {
        final List<CompilationError> errors = new ArrayList<>();
        SyntaxErrorListener listener = errors::add;

        ThoriumLexer lexer = new ThoriumLexer(CharStreams.fromString("type Type {~}"));
        lexer.removeErrorListeners();
        lexer.addErrorListener(new LexerErrorListener(listener));

        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        tokenStream.fill();

        Assertions.assertThat(tokenStream.getTokens())
                .hasSize(5); // they are: ['type', 'Type', '{', '}', <EOF>]

        Assertions.assertThat(errors)
                .hasSize(1)
                .extracting(Object::toString)
                .containsExactly(
                        "token recognition error at: '~'\n  1. type Type {~}\n                ^\non line 1, column 12"
                );
    }

    @Test
    void lexer_keepAllTokens() {
        final List<CompilationError> errors = new ArrayList<>();
        SyntaxErrorListener listener = errors::add;

        ThoriumLexer lexer = new ThoriumLexer(
                CharStreams.fromString("type Type {~}"),
                new DefaultLexerConfiguration() {
                    @Override
                    public boolean keepAllTokens() {
                        return true;
                    }
                }
        );
        lexer.removeErrorListeners();
        lexer.addErrorListener(new LexerErrorListener(listener));

        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        tokenStream.fill();

        Assertions.assertThat(tokenStream.getTokens())
                .hasSize(8); // they are: ['type', ' ', 'Type', ' ', '{', '~', '}', <EOF>]

        Assertions.assertThat(errors)
                .hasSize(0);
    }
}
