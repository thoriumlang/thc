package org.thoriumlang.compiler.api.errors;

import org.antlr.v4.runtime.RecognitionException;
import org.thoriumlang.compiler.ast.context.SourcePosition;

import java.util.Collections;

public class SyntaxError implements CompilationError {
    private static final SyntaxErrorFormatter DEFAULT_FORMATTER = new DefaultErrorFormatter();

    private final SourcePosition sourcePosition;
    private final String message;
    private final RecognitionException exception;

    public SyntaxError(String message, int line, int column, int charsCount, String errorLine,
                       RecognitionException exception) {
        this.sourcePosition = new SourcePosition(
                new SourcePosition.Position(line, column + 1),
                new SourcePosition.Position(line, column + 1 + charsCount),
                Collections.singletonList(errorLine)
        );
        this.message = message;
        this.exception = exception;
    }

    public String format(SyntaxErrorFormatter formatter) {
        return formatter.format(sourcePosition, message, exception);
    }

    @Override
    public String toString() {
        return format(DEFAULT_FORMATTER);
    }
}
