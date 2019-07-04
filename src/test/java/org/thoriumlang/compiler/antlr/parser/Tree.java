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
package org.thoriumlang.compiler.antlr.parser;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ListTokenSource;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.assertj.core.api.Assertions;
import org.thoriumlang.compiler.antlr.ThoriumParser;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

public class Tree {
    private final List<Token> tokens;

    public Tree(Token... tokens) {
        this.tokens = Arrays.asList(tokens);
    }

    public String serialize(String ruleName) {
        ThoriumParser p = new ThoriumParser(
                new CommonTokenStream(
                        new ListTokenSource(tokens)
                )
        );

        p.addErrorListener(new ANTLRErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                Assertions.fail("syntaxError");
            }

            @Override
            public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact, BitSet ambigAlts, ATNConfigSet configs) {
                Assertions.fail("reportAmbiguity");
            }

            @Override
            public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex, BitSet conflictingAlts, ATNConfigSet configs) {
                Assertions.fail("reportAttemptingFullContext");
            }

            @Override
            public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction, ATNConfigSet configs) {
                Assertions.fail("reportContextSensitivity");
            }
        });

        try {
            try {
                return ((ParserRuleContext) (ThoriumParser.class.getMethod(ruleName).invoke(p))).toStringTree(p);
            } catch (InvocationTargetException e) {
                if (e.getCause() instanceof AssertionError) {
                    throw (AssertionError) e.getCause();
                }
                Assertions.fail(e.getMessage());
                return null;
            }
        } catch (NoSuchMethodException | IllegalAccessException e) {
            Assertions.fail(String.format("'%s' is not a valid rule name", ruleName));
            return null;
        }
    }

    public String serialize() {
        return serialize("root");
    }
}
