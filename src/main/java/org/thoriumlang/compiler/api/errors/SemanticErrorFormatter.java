package org.thoriumlang.compiler.api.errors;

public interface SemanticErrorFormatter {
    String format(int line, int column, String message);
}
