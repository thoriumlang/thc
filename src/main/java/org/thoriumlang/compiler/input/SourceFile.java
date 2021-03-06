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
package org.thoriumlang.compiler.input;

import org.thoriumlang.compiler.ast.AST;
import org.thoriumlang.compiler.ast.algorithms.Algorithm;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.symbols.SymbolTable;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class SourceFile implements Source {
    private final String namespace;
    private final Path path;

    public SourceFile(String namespace, Path path) {
        this.namespace = namespace;
        this.path = path;
    }

    @Override
    public AST ast(NodeIdGenerator nodeIdGenerator, SymbolTable symbolTable, List<Algorithm> algorithms) {
        return new AST(
                inputStream(),
                namespace(),
                nodeIdGenerator,
                algorithms,
                symbolTable
        );
    }

    private InputStream inputStream() {
        try {
            return Files.newInputStream(path);
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String namespace() {
        return namespace;
    }


    @Override
    public String toString() {
        return path.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SourceFile that = (SourceFile) o;
        return path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}
