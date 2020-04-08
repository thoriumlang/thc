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
import org.thoriumlang.compiler.ast.algorithms.CompilationError;
import org.thoriumlang.compiler.collections.Lists;
import org.thoriumlang.compiler.input.Source;
import org.thoriumlang.compiler.input.SourceFiles;
import org.thoriumlang.compiler.output.html.HtmlWalker;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings({"squid:S106", "squid:S00112"})
public class Main {
    public static void main(String[] args) throws URISyntaxException {
        new Main().compile();
    }

    private void compile() throws URISyntaxException {
        new Compiler(
                new CompilationListener() {
                    @Override
                    public void compilationStarted(int sourcesCount) {
                        System.out.println(String.format("About to compile %d sources", sourcesCount));
                    }

                    @Override
                    public void compilationFinished() {

                    }

                    @Override
                    public void compilationProgress(float progress) {
                        System.out.println(String.format("progress: %f", progress));
                    }

                    @Override
                    public void sourceStarted(Source source) {
                        System.out.println(String.format("Processing %s", source));
                    }

                    @Override
                    public void sourceFinished(Source source, AST ast) {
                        try {
                            System.out.println(String.format(
                                    "Processed %d nodes",
                                    ast.root().getContext().require(NodesCountPlugin.class.getName(), Integer.class)
                            ));

                            // TODO put that block as a "plugin"
                            ast.root().getContext().put("compilationErrors", Map.class, ast.errors()
                                    .stream()
                                    .collect(Collectors.toMap(
                                            CompilationError::getNode,
                                            Collections::singletonList,
                                            Lists::merge
                                    )));

                            new FileOutputStream("/tmp/" + ast.root().getTopLevelNode().getName() + ".html").write(
                                    new HtmlWalker(ast.root()).walk().getBytes()
                            );
                        }
                        catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    }

                    @Override
                    public void emitError(Source source, CompilationError error) {
                        System.out.println(error);
                    }
                },
                Collections.singletonList(new NodesCountPlugin())
        ).compile(
                new SourceFiles(
                        Paths.get(Main.class.getResource("/").toURI())
                )
        );
    }
}
