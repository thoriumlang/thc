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
import org.thoriumlang.compiler.antlr.ThoriumLexer;
import org.thoriumlang.compiler.antlr.ThoriumParser;
import org.thoriumlang.compiler.antlr4.ErrorListener;
import org.thoriumlang.compiler.antlr4.RootVisitor;
import org.thoriumlang.compiler.api.errors.CompilationError;
import org.thoriumlang.compiler.api.errors.SyntaxError;
import org.thoriumlang.compiler.ast.algorithms.Algorithm;
import org.thoriumlang.compiler.ast.visitor.SymbolTableInitializationVisitor;
import org.thoriumlang.compiler.ast.visitor.TypeFlatteningVisitor;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.ast.visitor.RelativesInjectionVisitor;
import org.thoriumlang.compiler.symbols.Name;
import org.thoriumlang.compiler.symbols.SymbolTable;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class AST implements SyntaxErrorListener {
    private final InputStream inputStream;
    private final String namespace; // TODO create a Namespace  (/!\ we use Name for some namespaces values)
    private final List<Algorithm> algorithms;
    private final NodeIdGenerator nodeIdGenerator;
    private final ErrorListener syntaxErrorListener;
    private final SymbolTable symbolTable;

    private boolean parsed = false;
    private Root root;
    private List<CompilationError> errors;

    public AST(InputStream inputStream, String namespace, NodeIdGenerator nodeIdGenerator, List<Algorithm> algorithms, SymbolTable symbolTable) {
        this.inputStream = Objects.requireNonNull(inputStream, "inputStream cannot be null");
        this.namespace = Objects.requireNonNull(namespace, "namespace cannot be null");
        this.algorithms = Objects.requireNonNull(algorithms, "algorithms cannot be null");
        this.nodeIdGenerator = Objects.requireNonNull(nodeIdGenerator, "nodeIdGenerator cannot be null");
        this.syntaxErrorListener = new ErrorListener(this);
        this.symbolTable = symbolTable;
    }

    public Optional<Root> root() {
        if (parsed) {
            return Optional.ofNullable(root);
        }

        synchronized (inputStream) {
            if (parsed) {
                return Optional.ofNullable(root);
            }

            parsed = true;
            errors = new ArrayList<>();

            ThoriumParser.RootContext rootContext = parser().root();

            if (!errors.isEmpty()) {
                // parsing failed, we cannot proceed...
                return Optional.empty();
            }

            root = (Root) new RootVisitor(nodeIdGenerator, namespace).visit(rootContext)
                    .accept(new TypeFlatteningVisitor(nodeIdGenerator))
                    .accept(new RelativesInjectionVisitor())
                    .accept(new SymbolTableInitializationVisitor(
                            findLocalTable(symbolTable, new Name(namespace).getParts()))
                    );


//            root = (Root) new RelativesInjectionVisitor().visit(
//                    new TypeFlattenedRoot(
//                            nodeIdGenerator,
//                            new RootVisitor(nodeIdGenerator, namespace).visit(
//                                    rootContext
//                            )
//                    ).root()
//            );

            errors.addAll(
                    algorithms.stream()
                            .map(a -> a.walk(root))
                            .flatMap(List::stream)
                            .collect(Collectors.toList())
            );
        }

        return Optional.ofNullable(root);
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

    public List<CompilationError> errors() {
        if (!parsed) {
            throw new IllegalStateException("error() called before root()");
        }
        return errors;
    }

    private ThoriumParser parser() {
        ThoriumParser parser = new ThoriumParser(
                new CommonTokenStream(
                        lexer()
                )
        );
        parser.removeErrorListeners();
        parser.addErrorListener(syntaxErrorListener);

        return parser;
    }

    private ThoriumLexer lexer() {
        try {
            ThoriumLexer lexer = new ThoriumLexer(
                    CharStreams.fromStream(
                            inputStream
                    )
            );
            lexer.removeErrorListeners();
            lexer.addErrorListener(syntaxErrorListener);
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
