package org.thoriumlang.compiler.api.errors;

import org.antlr.v4.runtime.RecognitionException;

public interface SyntaxErrorFormatter {
    String format(String errorLine, int line, int charPositionInLine, int charsCount, String message,
                  RecognitionException exception);
}
