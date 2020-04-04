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
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class JsonIntegrationTest {
    private static void assertJsonEqual(File file, String expectedJson, String actualJson) throws Throwable {
        try {
            JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.NON_EXTENSIBLE);
        } catch (AssertionError e) {
            // store the actual value
            if (!Strings.isNullOrEmpty(System.getProperty("dumpdir"))) {
                try (
                        OutputStream out = new FileOutputStream(
                                System.getProperty("dumpdir")
                                        + File.separator
                                        + file.getName().replaceFirst("\\.th$", ".json"),
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
    Stream<DynamicTest> examples() throws URISyntaxException {
        File directory = new File(JsonIntegrationTest.class.getResource("/org/thoriumlang/compiler/it/").toURI());

        File[] files = Optional
                .ofNullable(directory.listFiles((dir, name) -> !name.startsWith("_") && name.endsWith(".th")))
                .orElseThrow(() -> new IllegalStateException("no .th files found"));

        return Arrays.stream(files)
                .map(file -> {
                    SourceFiles sources = new SourceFiles(
                            file.toPath(),
                            p -> p.equals(file.toPath())
                    );
                    Source sourceFile = sources.sources().get(0);

                    return DynamicTest.dynamicTest(
                            file.getName().replaceFirst("\\.th$", ""),
                            () -> assertJsonEqual(file, expectedJson(file), json(sources, sourceFile))
                    );
                });
    }

    private String json(Sources sources, Source source) {
        try {
            return new JsonAST(
                    new SourceToAST(sources, new SymbolTable()).apply(source)
            ).json();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String expectedJson(File sourceFile) {
        return Optional
                .ofNullable(JsonIntegrationTest.class.getResourceAsStream(
                        "/org/thoriumlang/compiler/it/" + sourceFile.getName().replaceFirst("\\.th$", ".json")
                ))
                .map(InputStreamReader::new)
                .map(BufferedReader::new)
                .map(BufferedReader::lines)
                .orElseGet(() -> Arrays.stream(new String[]{"{}"}))
                .collect(Collectors.joining("\n"));
    }
}
