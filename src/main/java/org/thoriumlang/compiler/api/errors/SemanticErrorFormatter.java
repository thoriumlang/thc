package org.thoriumlang.compiler.api.errors;

import org.thoriumlang.compiler.ast.context.SourcePosition;

public interface SemanticErrorFormatter {
    String format(SourcePosition sourcePosition, String message);
}
