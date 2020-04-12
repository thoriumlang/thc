package org.thoriumlang.compiler.antlr4;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.antlr.ThoriumLexer;
import org.thoriumlang.compiler.api.errors.CompilationError;
import org.thoriumlang.compiler.ast.SyntaxErrorListener;

import java.util.ArrayList;
import java.util.List;

class ErrorListenerTest {
    @Test
    void lexer() {
        final List<CompilationError> errors = new ArrayList<>();
        SyntaxErrorListener listener = errors::add;

        ThoriumLexer lexer = new ThoriumLexer(CharStreams.fromString("~?"));
        lexer.removeErrorListeners();
        lexer.addErrorListener(new ErrorListener(listener));

        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        tokenStream.fill();

        Assertions.assertThat(tokenStream.getTokens())
                .hasSize(2) // because of EOF token at the end
                .haveAtLeastOne(new Condition<>((Token t) -> t.getText().equals("?"), null));

        Assertions.assertThat(errors)
                .hasSize(1)
                .haveAtLeastOne(new Condition<>((CompilationError e) -> e.toString().equals("token recognition error at: '~' (1)"), null));
    }
}
