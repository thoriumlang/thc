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

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenSource;
import org.thoriumlang.compiler.antlr.ThoriumLexer;
import org.thoriumlang.compiler.antlr.ThoriumParser;
import org.thoriumlang.compiler.antlr4.LexerErrorListener;
import org.thoriumlang.compiler.antlr4.ParserErrorListener;
import org.thoriumlang.compiler.antlr4.RootVisitor;
import org.thoriumlang.compiler.api.errors.CompilationError;
import org.thoriumlang.compiler.api.errors.SemanticError;
import org.thoriumlang.compiler.api.errors.SyntaxError;
import org.thoriumlang.compiler.ast.algorithms.Algorithm;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.ast.visitor.RelativesInjectionVisitor;
import org.thoriumlang.compiler.ast.visitor.SymbolTableInitializationVisitor;
import org.thoriumlang.compiler.ast.visitor.TypeFlatteningVisitor;
import org.thoriumlang.compiler.collections.Lists;
import org.thoriumlang.compiler.data.Pair;
import org.thoriumlang.compiler.symbols.Name;
import org.thoriumlang.compiler.symbols.SymbolTable;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AST {
    private final InputStream inputStream;
    private final String namespace; // TODO create a Namespace  (/!\ we use Name for some namespaces values)
    private final List<Algorithm> algorithms;
    private final NodeIdGenerator nodeIdGenerator;
    private final SymbolTable symbolTable;

    private boolean parsed = false;
    private Root root;
    private List<CompilationError> errors;

    public AST(InputStream inputStream, String namespace, NodeIdGenerator nodeIdGenerator, List<Algorithm> algorithms, SymbolTable symbolTable) {
        this.inputStream = Objects.requireNonNull(inputStream, "inputStream cannot be null");
        this.namespace = Objects.requireNonNull(namespace, "namespace cannot be null");
        this.algorithms = Objects.requireNonNull(algorithms, "algorithms cannot be null");
        this.nodeIdGenerator = Objects.requireNonNull(nodeIdGenerator, "nodeIdGenerator cannot be null");
        this.symbolTable = symbolTable;
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

            Pair<ThoriumParser.RootContext, List<SyntaxError>> parsingResult = new Parser().parse(inputStream);

            if (!parsingResult.right().isEmpty()) {
                errors = new ArrayList<>(parsingResult.right());
                return this;
            }

            Pair<Root, List<SemanticError>> algorithmsResult = applyAlgorithm(
                    algorithms.iterator(),
                    new Pair<>(
                            (Root) parsingResult.left()
                                    .accept(new RootVisitor(nodeIdGenerator, namespace))
                                    .accept(new TypeFlatteningVisitor(nodeIdGenerator))
                                    .accept(new RelativesInjectionVisitor())
                                    .accept(new SymbolTableInitializationVisitor(
                                            findLocalTable(symbolTable, new Name(namespace).getParts()))
                                    ),
                            Collections.emptyList()
                    )
            );

            errors = new ArrayList<>(algorithmsResult.right());
            root = algorithmsResult.left();
        }

        return this;
    }

    private Pair<Root, List<SemanticError>> applyAlgorithm(Iterator<Algorithm> currentAlgorithm,
                                                           Pair<Root, List<SemanticError>> state) {
        if (!currentAlgorithm.hasNext()) {
            return state;
        }

        Pair<Root, List<SemanticError>> result = currentAlgorithm.next().walk(state.left());

        return applyAlgorithm(
                currentAlgorithm,
                new Pair<>(result.left(), Lists.merge(state.right(), result.right()))
        );
    }


    private SymbolTable findLocalTable(SymbolTable symbolTable, List<String> namespaces) {
        if (namespaces.isEmpty()) {
            return symbolTable;
        }
        ArrayList<String> newNamespaces = new ArrayList<>(namespaces);
        return findLocalTable(
                symbolTable.createScope(newNamespaces.remove(0)),
                newNamespaces
        );
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

        private Pair<ThoriumParser.RootContext, List<SyntaxError>> parse(InputStream inputStream) {
            return new Pair<>(parser(lexer(inputStream)).root(), errors);
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
