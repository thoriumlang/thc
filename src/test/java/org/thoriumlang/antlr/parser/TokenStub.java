/*
 * Copyright 2019 Christophe Pollet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thoriumlang.antlr.parser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenSource;
import org.thoriumlang.antlr.ThoriumLexer;

public class TokenStub implements Token {
    private final String text;
    private final int type;

    public TokenStub(String text, int type) {
        this.text = text;
        this.type = type;
    }

    public TokenStub(int type) {
        this("", type);
    }

    public TokenStub(String litteral) {
        this(litteral, findTokenType(litteral));
    }

    public static TokenStub token(String text, int type) {
        return new TokenStub(text, type);
    }

    public static TokenStub token(String text) {
        return new TokenStub(text);
    }

    public static TokenStub token(int type) {
        return new TokenStub(type);
    }

    private static int findTokenType(String literal) {
        for (int i = 0; i < ThoriumLexer.VOCABULARY.getMaxTokenType(); i++) {
            if (("'" + literal + "'").equals(ThoriumLexer.VOCABULARY.getLiteralName(i))) {
                return i;
            }
        }
        throw new IllegalArgumentException(String.format("'%s' is not a valid token literal", literal));
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public int getLine() {
        return 0;
    }

    @Override
    public int getCharPositionInLine() {
        return 0;
    }

    @Override
    public int getChannel() {
        return 0;
    }

    @Override
    public int getTokenIndex() {
        return 0;
    }

    @Override
    public int getStartIndex() {
        return 0;
    }

    @Override
    public int getStopIndex() {
        return 0;
    }

    @Override
    public TokenSource getTokenSource() {
        return null;
    }

    @Override
    public CharStream getInputStream() {
        return null;
    }
}
