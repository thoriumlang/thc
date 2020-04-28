package org.thoriumlang.compiler.antlr.lexer;

import org.antlr.v4.runtime.Token;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.antlr.ThoriumLexer;
import org.thoriumlang.compiler.antlr4.DefaultLexerConfiguration;

import java.util.List;

@Tag("lexer")
class CommentTest {
    @Test
    void singleLine() {
        Assertions.assertThat(new Tokens("// a comment", new DefaultLexerConfiguration() {
            @Override
            public boolean keepAllTokens() {
                return true;
            }
        }).parse())
                .hasSize(1)
                .element(0)
                .extracting(Token::getType, Token::getText)
                .containsExactly(ThoriumLexer.LINE_COMMENT, "// a comment");
    }

    @Test
    void multiLine() {
        List<Token> tokens = new Tokens("/* \n */ 1", new DefaultLexerConfiguration() {
            @Override
            public boolean keepAllTokens() {
                return true;
            }
        }).parse();

        Assertions.assertThat(tokens)
                .hasSize(3);

        Assertions.assertThat(tokens)
                .element(0)
                .extracting(Token::getType, Token::getText)
                .containsExactly(ThoriumLexer.BLOCK_COMMENT, "/* \n */");
        Assertions.assertThat(tokens)
                .element(1)
                .extracting(Token::getType, Token::getText)
                .containsExactly(ThoriumLexer.WS, " ");
        Assertions.assertThat(tokens)
                .element(2)
                .extracting(Token::getType, Token::getText)
                .containsExactly(ThoriumLexer.NUMBER, "1");
    }

    @Test
    void multiLineOpen() {
        Assertions.assertThat(new Tokens("/* \n ... 1", new DefaultLexerConfiguration() {
            @Override
            public boolean keepAllTokens() {
                return true;
            }
        }).parse())
                .hasSize(1)
                .element(0)
                .extracting(Token::getType, Token::getText)
                .containsExactly(ThoriumLexer.BLOCK_COMMENT, "/* \n ... 1");
    }
}


