/*
 * Copyright 2021 Christophe Pollet
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
package org.thoriumlang.compiler.testsupport;

import io.vavr.control.Either;
import org.thoriumlang.compiler.api.errors.CompilationError;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.nodes.Root;

import java.net.URISyntaxException;
import java.util.List;

public final class AstHelper {
    private AstHelper() {
    }

    public static Root from(Class<?> c, String fileName) {
        return from(c, fileName, new NodeIdGenerator());
    }

    public static Root from(Class<?> c, String fileName, NodeIdGenerator nodeIdGenerator) {
        try {
            Either<List<CompilationError>, Root> maybeRoot = SourceFilesHelper.from(c, fileName)
                    .sources()
                    .get(0)
                    .ast(nodeIdGenerator)
                    .parse();

            if (maybeRoot.isLeft()) {
                System.out.println(maybeRoot.getLeft());
            }

            return maybeRoot.get();
        }
        catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
