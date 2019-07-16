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
package org.thoriumlang.compiler.it;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.thoriumlang.compiler.SourceFile;
import org.thoriumlang.compiler.SourceFiles;
import org.thoriumlang.compiler.ast.AST;
import org.thoriumlang.compiler.output.th.ThWalker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@Tag("integration")
class IntegrationTest {
    @ParameterizedTest
    @ValueSource(strings = {
            "/org/thoriumlang/compiler/tests/type",
            "/org/thoriumlang/compiler/tests/use"
    })
    void ast(String path) throws IOException, URISyntaxException {
        SourceFile sourceFile = sourceFile(path);
        Assertions
                .assertThat(
                        new AST(sourceFile.inputStream(), sourceFile.namespace()).root()
                                .toString())
                .isEqualTo(
                        new BufferedReader(
                                new InputStreamReader(
                                        IntegrationTest.class.getResourceAsStream(path + ".out.ast")
                                )
                        )
                                .lines()
                                .collect(Collectors.joining("\n"))
                );
    }

    private SourceFile sourceFile(String path) throws URISyntaxException, IOException {
        return new SourceFiles(
                Paths.get(IntegrationTest.class.getResource("/").toURI()),
                (p, bfa) -> p.getFileName().toString().equals(sourceFilename(path))
        ).files().get(0);
    }

    private String sourceFilename(String path) {
        return path.substring(path.lastIndexOf("/") + 1) + ".th";
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/org/thoriumlang/compiler/tests/type",
            "/org/thoriumlang/compiler/tests/use"
    })
    void thorium(String path) throws IOException, URISyntaxException {
        SourceFile sourceFile = sourceFile(path);
        Assertions
                .assertThat(
                        new ThWalker(
                                new AST(sourceFile.inputStream(), sourceFile.namespace()).root()
                        ).walk()
                )
                .isEqualTo(
                        new BufferedReader(
                                new InputStreamReader(
                                        IntegrationTest.class.getResourceAsStream(path + ".out.th")
                                )
                        )
                                .lines()
                                .collect(Collectors.joining("\n"))
                );
    }
}