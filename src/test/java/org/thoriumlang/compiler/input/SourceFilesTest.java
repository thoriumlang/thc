/*
 * Copyright 2020 Christophe Pollet
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

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.symbols.Name;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;

class SourceFilesTest {
    @Test
    void sources() throws URISyntaxException {
        SourceFiles sourceFiles = new SourceFiles(
                Paths.get(SourceFilesTest.class.getResource("/org/thoriumlang/compiler/").toURI()),
                p -> p.getFileName().toString().equals("class.th")
        );

        List<Source> sources = sourceFiles.sources();

        Assertions.assertThat(sources)
                .hasSize(1);

        Assertions.assertThat(sources.get(0).toString()).endsWith("/org/thoriumlang/compiler/tests/class.th");
    }

    @Test
    void load() throws URISyntaxException {
        SourceFiles sourceFiles = new SourceFiles(
                Paths.get(SourceFilesTest.class.getResource("/org/thoriumlang/compiler/").toURI()),
                p -> false
        );

        Assertions.assertThat(sourceFiles.sources())
                .isEmpty();

        sourceFiles.load(new Name("org.thoriumlang.compiler.tests.class"));
    }
}
