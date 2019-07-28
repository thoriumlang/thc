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
package org.thoriumlang.compiler.antlr.lexer;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.assertj.core.api.Assertions;
import org.thoriumlang.compiler.antlr.ThoriumLexer;

import java.util.ArrayList;
import java.util.BitSet;
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
        lexer.addErrorListener(new ANTLRErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
                    int charPositionInLine, String msg, RecognitionException e) {
                Assertions.fail("syntaxError");
            }

            @Override
            public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact,
                    BitSet ambigAlts, ATNConfigSet configs) {
                Assertions.fail("reportAmbiguity");
            }

            @Override
            public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex,
                    BitSet conflictingAlts, ATNConfigSet configs) {
                Assertions.fail("reportAttemptingFullContext");
            }

            @Override
            public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex,
                    int prediction, ATNConfigSet configs) {
                Assertions.fail("reportContextSensitivity");
            }
        });
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
