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

import org.thoriumlang.compiler.input.SourceFiles;

import java.net.URISyntaxException;
import java.nio.file.Path;

public final class SourceFilesHelper {
    private SourceFilesHelper() {

    }

    public static SourceFiles from(Class<?> c, String file) throws URISyntaxException {
        return new SourceFiles(
                Path.of(
                        c.getResource(
                                "/" + Path.of(
                                        c.getPackageName().replace(".", "/")
                                ).toString()
                        ).toURI()
                ),
                path -> path.getFileName().toString().equals(file)
        );
    }
}