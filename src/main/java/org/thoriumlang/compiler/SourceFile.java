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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class SourceFile {
    private final String namespace;
    private final Path path;

    public SourceFile(String namespace, Path path) {
        this.namespace = namespace;
        this.path = path;
    }

    @Override
    public String toString() {
        return namespace + " :: " + path.toString();
    }

    public InputStream inputStream() throws IOException {
        return Files.newInputStream(path);
    }

    public String namespace() {
        return namespace;
    }
}
