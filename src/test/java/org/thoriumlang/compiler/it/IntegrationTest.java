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

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.thoriumlang.compiler.SourceToAST;
import org.thoriumlang.compiler.ast.algorithms.CompilationError;
import org.thoriumlang.compiler.ast.algorithms.NodesMatching;
import org.thoriumlang.compiler.ast.algorithms.typechecking.TypeChecker;
import org.thoriumlang.compiler.ast.context.SourcePosition;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.collections.Lists;
import org.thoriumlang.compiler.input.Source;
import org.thoriumlang.compiler.input.SourceFiles;
import org.thoriumlang.compiler.input.Sources;
import org.thoriumlang.compiler.output.html.HtmlWalker;
import org.thoriumlang.compiler.output.th.ThWalker;
import org.thoriumlang.compiler.symbols.Name;
import org.thoriumlang.compiler.symbols.SymbolTable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Tag("integration")
class IntegrationTest {
    @ParameterizedTest
    @ValueSource(strings = {
            "/org/thoriumlang/compiler/tests/type",
            "/org/thoriumlang/compiler/tests/type2",
            "/org/thoriumlang/compiler/tests/use",
            "/org/thoriumlang/compiler/tests/class",
            "/org/thoriumlang/compiler/tests/ClassMethods",
            "/org/thoriumlang/compiler/tests/ClassAttributes",
            "/org/thoriumlang/compiler/tests/FunctionsAsValues"
    })
    void ast(String path) throws IOException, URISyntaxException {
        Source source = source(path, this::sourceFilename);
        Assertions
                .assertThat(
                        source
                                .ast()
                                .root()
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

    private Source source(String path, Function<String, String> filenameGenerator) throws URISyntaxException {
        String folder = path.substring(0, path.lastIndexOf("/"));
        String filename = path.substring(path.lastIndexOf("/") + 1);

        return new SourceFiles(
                Paths.get(IntegrationTest.class.getResource(folder).toURI()),
                p -> p.getFileName().toString().equals(filenameGenerator.apply(filename))
        ).sources().get(0);
    }

    private String sourceFilename(String path) {
        return path.substring(path.lastIndexOf("/") + 1) + ".th";
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/org/thoriumlang/compiler/tests/type",
            "/org/thoriumlang/compiler/tests/type2",
            "/org/thoriumlang/compiler/tests/use",
            "/org/thoriumlang/compiler/tests/class",
            "/org/thoriumlang/compiler/tests/ClassMethods",
            "/org/thoriumlang/compiler/tests/ClassAttributes",
            "/org/thoriumlang/compiler/tests/FunctionsAsValues"
    })
    void thorium(String path) throws IOException, URISyntaxException {
        Source source = source(path, this::sourceFilename);
        Assertions
                .assertThat(
                        new ThWalker(
                                source.ast().root()
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

    @ParameterizedTest
    @ValueSource(strings = {
            "/org/thoriumlang/compiler/tests/type",
            "/org/thoriumlang/compiler/tests/type2",
            "/org/thoriumlang/compiler/tests/use",
            "/org/thoriumlang/compiler/tests/class",
            "/org/thoriumlang/compiler/tests/ClassMethods",
            "/org/thoriumlang/compiler/tests/ClassAttributes",
            "/org/thoriumlang/compiler/tests/FunctionsAsValues"
    })
    void generatedThorium(String path) throws IOException, URISyntaxException {
        Source sourceFile = source(path, this::generatedSourceFilename);
        Assertions
                .assertThat(
                        new ThWalker(
                                sourceFile.ast().root()
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

    private String generatedSourceFilename(String path) {
        return path.substring(path.lastIndexOf("/") + 1) + ".out.th";
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/org/thoriumlang/compiler/tests/type",
            "/org/thoriumlang/compiler/tests/type2",
            "/org/thoriumlang/compiler/tests/use",
            "/org/thoriumlang/compiler/tests/class",
            "/org/thoriumlang/compiler/tests/ClassMethods",
            "/org/thoriumlang/compiler/tests/ClassAttributes",
            "/org/thoriumlang/compiler/tests/FunctionsAsValues",
            "/org/thoriumlang/compiler/ast/algorithms/typechecking/Main_packageType"
    })
    @DisabledIfSystemProperty(named = "skipHtml", matches = "true")
    void html(String path) throws Exception {
        HttpResponse<String> uniResponse = Unirest.post("http://localhost:8888/")
                .header("User-Agent",
                        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.101 Safari/537.36")
                .header("Content-Type", "text/html; charset=UTF-8")
                .queryString("out", "gnu")
                .body(generateHtmlDocument(path))
                .asString();
        Assertions.assertThat(uniResponse.getBody())
                .isEmpty();
    }

    private String generateHtmlDocument(String path) throws IOException, URISyntaxException {
        Source source = source(path, this::sourceFilename);

        Root root = new SourceToAST(new Sources() {
            @Override
            public List<Source> sources() {
                return Collections.emptyList();
            }

            @Override
            public Optional<Source> load(Name name) {
                return Optional.empty();
            }
        }, new SymbolTable()).apply(source).root();

        root.getContext()
                .put(
                        "compilationErrors",
                        Map.class,
                        new TypeChecker(
                                Collections.singletonList(
                                        (name, node) -> Optional.empty()
                                )
                        ).walk(root).stream()
                                .collect(Collectors.toMap(
                                        CompilationError::getNode,
                                        Collections::singletonList,
                                        Lists::merge
                                ))
                );

        return new HtmlWalker(
                root
        ).walk();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/org/thoriumlang/compiler/tests/type",
            "/org/thoriumlang/compiler/tests/type2",
            "/org/thoriumlang/compiler/tests/use",
            "/org/thoriumlang/compiler/tests/class",
            "/org/thoriumlang/compiler/tests/ClassMethods",
            "/org/thoriumlang/compiler/tests/ClassAttributes",
            "/org/thoriumlang/compiler/tests/FunctionsAsValues"
    })
    void sourceLocation_normalizedSource(String path) throws IOException, URISyntaxException {
        Source source = source(path, this::generatedSourceFilename);
        Assertions
                .assertThat(
                        new NodesMatching(n -> !n.getContext().get(SourcePosition.class).isPresent())
                                .visit(source.ast().root())
                )
                .isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/org/thoriumlang/compiler/tests/type",
            "/org/thoriumlang/compiler/tests/type2",
            "/org/thoriumlang/compiler/tests/use",
            "/org/thoriumlang/compiler/tests/class",
            "/org/thoriumlang/compiler/tests/ClassMethods",
            "/org/thoriumlang/compiler/tests/ClassAttributes",
            "/org/thoriumlang/compiler/tests/FunctionsAsValues"
    })
    void sourceLocation_rawSource(String path) throws IOException, URISyntaxException {
        Source source = source(path, this::sourceFilename);
        Assertions
                .assertThat(
                        new NodesMatching(n -> !n.getContext().get(SourcePosition.class).isPresent())
                                .visit(source.ast().root())
                )
                .isEmpty();
    }
}