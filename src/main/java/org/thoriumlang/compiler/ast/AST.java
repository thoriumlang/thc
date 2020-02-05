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
import org.thoriumlang.compiler.antlr4.RootVisitor;
import org.thoriumlang.compiler.ast.algorithms.Algorithm;
import org.thoriumlang.compiler.ast.algorithms.CompilationError;
import org.thoriumlang.compiler.ast.algorithms.symboltable.SymbolTableInitializationVisitor;
import org.thoriumlang.compiler.ast.algorithms.typeflattening.TypeFlattenedRoot;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.ast.visitor.RelativesInjectionVisitor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AST {
    private final InputStream inputStream;
    private final String namespace;
    private final List<Algorithm> algorithms;
    private final NodeIdGenerator nodeIdGenerator;

    private Root root;
    private List<CompilationError> errors;

    public AST(InputStream inputStream, String namespace, List<Algorithm> algorithms) {
        this.inputStream = inputStream;
        this.namespace = namespace;
        this.algorithms = algorithms;
        this.nodeIdGenerator = new NodeIdGenerator();
    }

    public AST(InputStream inputStream, String namespace) {
        this(inputStream, namespace, Collections.emptyList());
    }

    public Root root() throws IOException {
        if (root != null) {
            return root;
        }

        synchronized (inputStream) {
            if (root == null) {
                root = (Root) new SymbolTableInitializationVisitor().visit(
                        (Root) new RelativesInjectionVisitor().visit(
                                new TypeFlattenedRoot(
                                        nodeIdGenerator,
                                        new RootVisitor(nodeIdGenerator, namespace).visit(
                                                parser().root()
                                        )
                                ).root()
                        )
                );
            }

            errors = algorithms.stream()
                    .map(a -> a.walk(root))
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
        }

        return root;
    }

    public List<CompilationError> errors() {
        if (root == null) {
            throw new IllegalStateException("error() called before root()");
        }
        return errors;
    }

    private ThoriumParser parser() throws IOException {
        return new ThoriumParser(
                new CommonTokenStream(
                        new ThoriumLexer(
                                CharStreams.fromStream(
                                        inputStream
                                )
                        )
                )
        );
    }
}
