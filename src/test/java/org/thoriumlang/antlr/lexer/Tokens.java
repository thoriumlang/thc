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
package org.thoriumlang.antlr.lexer;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.thoriumlang.antlr.ThoriumLexer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Tokens {
    private final String string;

    Tokens(String string) {
        this.string = string;
    }

    List<Token> parse() {
        CharStream cStream = CharStreams.fromString(string);
        ThoriumLexer lexer = new ThoriumLexer(cStream);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        tokenStream.fill();
        return Collections.unmodifiableList(
                withoutEof(tokenStream.getTokens())
        );
    }

    private List<Token> withoutEof(List<Token> tokens) {
        ArrayList<Token> tokenWithoutEof = new ArrayList<>(tokens);
        tokenWithoutEof.remove(tokens.size() - 1);
        return tokenWithoutEof;
    }
}
