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
package org.thoriumlang.compiler.ast;

import io.vavr.control.Either;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenSource;
import org.thoriumlang.compiler.antlr.ThoriumLexer;
import org.thoriumlang.compiler.antlr.ThoriumParser;
import org.thoriumlang.compiler.antlr4.LexerErrorListener;
import org.thoriumlang.compiler.antlr4.ParserErrorListener;
import org.thoriumlang.compiler.antlr4.RootVisitor;
import org.thoriumlang.compiler.api.errors.CompilationError;
import org.thoriumlang.compiler.api.errors.SyntaxError;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.nodes.Root;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AST {
    private final InputStream inputStream;
    private final String namespace; // TODO create a Namespace  (/!\ we use Name for some namespaces values)
    private final NodeIdGenerator nodeIdGenerator;

    private boolean parsed = false;
    private Root root;
    private List<CompilationError> errors;

    public AST(InputStream inputStream, String namespace, NodeIdGenerator nodeIdGenerator) {
        this.inputStream = Objects.requireNonNull(inputStream, "inputStream cannot be null");
        this.namespace = Objects.requireNonNull(namespace, "namespace cannot be null");
        this.nodeIdGenerator = Objects.requireNonNull(nodeIdGenerator, "nodeIdGenerator cannot be null");
    }

    public AST parse() {
        if (parsed) {
            return this;
        }

        synchronized (inputStream) {
            if (parsed) {
                return this;
            }
            parsed = true;

            Either<List<SyntaxError>, ThoriumParser.RootContext> parsingResult = new Parser().parse(inputStream);

            if (parsingResult.isLeft()) {
                errors = new ArrayList<>(parsingResult.getLeft());
                return this;
            }

            errors = Collections.emptyList();
            root = parsingResult.get().accept(new RootVisitor(nodeIdGenerator, namespace));
        }

        return this;
    }

    public Optional<Root> root() {
        parse();
        return Optional.ofNullable(root);
    }

    public List<CompilationError> errors() {
        parse();
        return errors;
    }

    private static class Parser implements SyntaxErrorListener {
        private final List<SyntaxError> errors = new ArrayList<>();

        private Either<List<SyntaxError>, ThoriumParser.RootContext> parse(InputStream inputStream) {
            ThoriumParser.RootContext root = parser(lexer(inputStream)).root();
            if (errors.isEmpty()) {
                return Either.right(root);
            }
            return Either.left(errors);
        }

        private ThoriumParser parser(TokenSource lexer) {
            ThoriumParser parser = new ThoriumParser(
                    new CommonTokenStream(
                            lexer
                    )
            );
            parser.removeErrorListeners();
            parser.addErrorListener(new ParserErrorListener(this));
            return parser;
        }

        private ThoriumLexer lexer(InputStream inputStream) {
            try {
                ThoriumLexer lexer = new ThoriumLexer(
                        CharStreams.fromStream(
                                inputStream
                        )
                );
                lexer.removeErrorListeners();
                lexer.addErrorListener(new LexerErrorListener(this));
                return lexer;
            }
            catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }


        @Override
        public void onError(SyntaxError syntaxError) {
            errors.add(syntaxError);
        }
    }
}
