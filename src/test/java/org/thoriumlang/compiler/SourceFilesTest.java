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
package org.thoriumlang.compiler;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

class SourceFilesTest {
    @Test
    void all() throws URISyntaxException, IOException {
        SourceFile sourceFile = new SourceFiles(
                Paths.get(SourceFilesTest.class.getResource("/").toURI()),
                (p, a) -> p.endsWith("org/thoriumlang/compiler/tests/class.th")
        ).files().get(0);

        Assertions.assertThat(sourceFile.namespace())
                .isEqualTo("org.thoriumlang.compiler.tests");
        Assertions.assertThat(sourceFile.filename())
                .isEqualTo("class.th");
    }
}
