package org.thoriumlang.compiler.api.errors;

public class SyntaxError implements CompilationError {
    private final String message;

    public SyntaxError(String message, int line) {
        this.message = String.format("%s (%d)", message, line);
    }

    @Override
    public String toString() {
        return message;
    }
}
