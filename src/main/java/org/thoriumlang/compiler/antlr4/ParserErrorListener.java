package org.thoriumlang.compiler.antlr4;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.thoriumlang.compiler.api.errors.SyntaxError;
import org.thoriumlang.compiler.ast.SyntaxErrorListener;

import java.util.Collections;
import java.util.List;

public class ParserErrorListener extends BaseErrorListener {
    private final SyntaxErrorListener listener;

    public ParserErrorListener(SyntaxErrorListener listener) {
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
                String.format("%s%s", msg, parseStack((Parser) recognizer)),
                line,
                charPositionInLine,
                Math.max(1, ((Token) offendingSymbol).getStopIndex() - ((Token) offendingSymbol).getStartIndex() + 1),
                ((CommonTokenStream) recognizer.getInputStream())
                        .getTokenSource()
                        .getInputStream()
                        .toString()
                        .split("\n")[line - 1],
                e
        ));
    }

    private String parseStack(Parser recognizer) {
        if (System.getProperty("thc.parser.error.printStack", "false").equalsIgnoreCase("true")) {
            List<String> stack = recognizer.getRuleInvocationStack();
            Collections.reverse(stack);
            return String.format("%nparser stack: %s", stack);

        }

        return "";
    }
}
