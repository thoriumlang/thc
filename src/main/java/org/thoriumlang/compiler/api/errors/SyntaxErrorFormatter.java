package org.thoriumlang.compiler.api.errors;

import org.antlr.v4.runtime.RecognitionException;
import org.thoriumlang.compiler.ast.context.SourcePosition;

public interface SyntaxErrorFormatter {
    String format(SourcePosition sourcePosition, String message, RecognitionException exception);
}
