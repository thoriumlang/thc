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
package org.thoriumlang.compiler.it;

import com.google.common.base.Strings;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.thoriumlang.compiler.SourceToAST;
import org.thoriumlang.compiler.api.NoopCompilationListener;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.input.Source;
import org.thoriumlang.compiler.input.SourceFiles;
import org.thoriumlang.compiler.input.Sources;
import org.thoriumlang.compiler.symbols.SymbolTable;
import org.thoriumlang.compiler.testsupport.JsonAST;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class JsonIntegrationTest {
    private static void assertJsonEqual(Path file, String expectedJson, String actualJson) throws Throwable {
        try {
            JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.NON_EXTENSIBLE);
        }
        catch (AssertionError e) {
            // store the actual value
            if (!Strings.isNullOrEmpty(System.getProperty("dumpdir"))) {
                try (
                        OutputStream out = new FileOutputStream(
                                System.getProperty("dumpdir")
                                        + File.separator
                                        + file.getFileName().toString().replaceFirst("\\.th$", ".json"),
                                false
                        )
                ) {
                    out.write(actualJson.getBytes());
                    out.flush();
                }
            }

            // so we can see the diff
            Assertions.assertThat(actualJson).isEqualTo(expectedJson);
        }
    }

    @TestFactory
    Stream<DynamicTest> examples() throws URISyntaxException, IOException {
        Path directory = Paths.get(JsonIntegrationTest.class.getResource("/org/thoriumlang/compiler/it/").toURI());

        Stream<Path> files = Files.find(
                directory,
                999,
                (p, bfa) -> p.getFileName().toString().matches("^[a-z]+_[0-9]{3}\\.th$")
        );

        return files
                .map(file -> {
                    SourceFiles sources = new SourceFiles(
                            file,
                            p -> p.equals(file)
                    );
                    Source sourceFile = sources.sources().get(0);

                    return DynamicTest.dynamicTest(
                            file.getFileName().toString().replaceFirst("\\.th$", ""),
                            () -> assertJsonEqual(file, expectedJson(file), json(sources, sourceFile))
                    );
                });
    }

    private String json(Sources sources, Source source) {
        SymbolTable symbolTable = new SymbolTable();
        return new JsonAST(
                new SourceToAST(
                        new NodeIdGenerator(),
                        sources,
                        symbolTable,
                        new NoopCompilationListener() // TODO Fix if broken tests..
                ).convert(source)
        ).json();
    }

    private String expectedJson(Path sourceFile) {
        return Optional
                .ofNullable(JsonIntegrationTest.class.getResourceAsStream(
                        "/org/thoriumlang/compiler/it/" + sourceFile.getFileName().toString().replaceFirst("\\.th$", ".json")
                ))
                .map(InputStreamReader::new)
                .map(BufferedReader::new)
                .map(BufferedReader::lines)
                .orElseGet(() -> Arrays.stream(new String[]{"{}"}))
                .collect(Collectors.joining("\n"));
    }
}
