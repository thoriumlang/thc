package org.thoriumlang.compiler.antlr4;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.thoriumlang.compiler.api.errors.SyntaxError;
import org.thoriumlang.compiler.ast.SyntaxErrorListener;

public class LexerErrorListener extends BaseErrorListener {
    private final SyntaxErrorListener listener;

    public LexerErrorListener(SyntaxErrorListener listener) {
        this.listener = listener;
    }

    @Override
    public void syntaxError(
            Recognizer<?, ?> recognizer,
            Object offendingSymbol,
            int line,
            int charPositionInLine,
            String msg,
            RecognitionException e
    ) throws ParseCancellationException {
        listener.onError(new SyntaxError(
                msg,
                line,
                charPositionInLine,
                1,
                recognizer.getInputStream()
                        .toString()
                        .split("\n")[line - 1],
                e
        ));
    }
}
