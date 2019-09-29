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

import org.thoriumlang.compiler.ast.algorithms.typechecking.TypeChecker;
import org.thoriumlang.compiler.ast.algorithms.typechecking.TypeCheckingError;
import org.thoriumlang.compiler.ast.nodes.AST;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.symbols.SymbolTable;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;

@SuppressWarnings({"squid:S106", "squid:S00112"})
public class Compiler {
    public static void main(String[] args) throws IOException, URISyntaxException {
        new Compiler().compile();
    }

    private void compile() throws IOException, URISyntaxException {
        new SourceFiles(
                Paths.get(Compiler.class.getResource("/").toURI())
        ).files().forEach(f -> {
            try {
                Root root = new AST(f.inputStream(), f.namespace()).root();
                List<TypeCheckingError> typeCheckingError = new TypeChecker().walk(root);

                typeCheckingError.forEach(System.out::println);
                root.getContext().get(SymbolTable.class).ifPresent(System.out::println);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
