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
import org.thoriumlang.compiler.ast.algorithms.FlattenedTypesRoot;

import java.io.IOException;
import java.io.InputStream;

public class AST {
    private final InputStream inputStream;

    public AST(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public Root root() throws IOException {
        return new FlattenedTypesRoot(
                new RootVisitor().visit(
                        parser().root()
                )
        ).root();
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
