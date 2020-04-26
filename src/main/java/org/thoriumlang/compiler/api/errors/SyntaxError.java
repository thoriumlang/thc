package org.thoriumlang.compiler.api.errors;

import com.google.common.base.Strings;
import org.antlr.v4.runtime.RecognitionException;

public class SyntaxError implements CompilationError {
    private final String message;
    private final int line;
    private final int column;
    private final int charsCount;
    private final String errorLine;
    private final RecognitionException exception;

    public SyntaxError(String message, int line, int column, int charsCount, String errorLine,
                       RecognitionException exception) {
        this.message = message;
        this.line = line;
        this.column = column;
        this.charsCount = charsCount;
        this.errorLine = errorLine;
        this.exception = exception;
    }

    public String format(SyntaxErrorFormatter formatter) {
        return formatter.format(errorLine, line, column, charsCount, message, exception);
    }

    @Override
    public String toString() {
        return String.format("%s%n%s%n%s%s%non line %d, column %d",
                message,
                errorLine,
                Strings.repeat(" ", column),
                Strings.repeat("^", charsCount),
                line,
                column
        );
    }
}
