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

import org.thoriumlang.compiler.ast.AST;
import org.thoriumlang.compiler.ast.algorithms.Algorithm;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class SourceFile implements Source {
    private final String namespace;
    private final Path path;

    public SourceFile(String namespace, Path path) {
        this.namespace = namespace;
        this.path = path;
    }

    @Override
    public AST ast(List<Algorithm> algorithms) {
        return new AST(
                inputStream(),
                namespace(),
                algorithms
        );
    }

    @Override
    public AST ast() {
        return ast(Collections.emptyList());
    }

    private InputStream inputStream() {
        try {
            return Files.newInputStream(path);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String namespace() {
        return namespace;
    }


    @Override
    public String toString() {
        return namespace + " :: " + path.toString();
    }
}
