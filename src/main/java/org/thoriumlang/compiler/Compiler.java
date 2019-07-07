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
package org.thoriumlang.compiler;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.thoriumlang.compiler.antlr.ThoriumLexer;
import org.thoriumlang.compiler.antlr.ThoriumParser;
import org.thoriumlang.compiler.antlr4.RootVisitor;
import org.thoriumlang.compiler.ast.Root;
import org.thoriumlang.compiler.output.th.Configuration;
import org.thoriumlang.compiler.output.th.Walker;

import java.io.IOException;
import java.util.Optional;

public class Compiler {
    public static void main(String[] args) throws IOException {
        new Compiler().compile();
    }

    private void compile() throws IOException {
        ThoriumParser parser = new ThoriumParser(
                new CommonTokenStream(
                        new ThoriumLexer(
                                CharStreams.fromStream(
                                        Compiler.class.getResourceAsStream("/org/thoriumlang/compiler/examples/type.th")
                                )
                        )
                )
        );

        RootVisitor rootVisitor = new RootVisitor();
        Root r = rootVisitor.visit(parser.root());
        System.out.println(r);

        Walker walker = new Walker(r, new Configuration() {
            @Override
            public boolean expandOptional() {
                return Boolean.valueOf(
                        Optional.ofNullable(System.getProperty("compiler.output.th.expandOptional"))
                                .orElse("false")
                );
            }
        });
        System.out.println(walker.walk());
    }
}
