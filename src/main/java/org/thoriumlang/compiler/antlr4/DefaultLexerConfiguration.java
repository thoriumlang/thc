package org.thoriumlang.compiler.antlr4;

public class DefaultLexerConfiguration implements LexerConfiguration {
    @Override
    public boolean keepAllTokens() {
        return false;
    }
}
