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

import org.thoriumlang.compiler.api.CompilationContext;
import org.thoriumlang.compiler.api.CompilationListener;
import org.thoriumlang.compiler.api.Compiler;
import org.thoriumlang.compiler.api.Event;
import org.thoriumlang.compiler.api.errors.CompilationError;
import org.thoriumlang.compiler.input.Source;
import org.thoriumlang.compiler.input.SourceFiles;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Arrays;

@SuppressWarnings("squid:S106")
public class Main {
    public static void main(String[] args) throws URISyntaxException {
        new Main().compile();
    }

    private void compile() throws URISyntaxException {
        CompilationListener listener = new CompilationListener() {
            @Override
            public void onCompilationStarted() {
                System.out.println(String.format("Compilation started"));
            }

            @Override
            public void onCompilationFinished() {
                System.out.println("Compilation finished");
            }

            @Override
            public void onSourceStarted(Source source) {
                System.out.println(String.format("Processing %s", source));
            }

            @Override
            public void onSourceFinished(Source source, CompilationContext context) {
                context.get(NodesCountPlugin.Count.class).ifPresent(
                        c -> System.out.println(String.format("Processed %d nodes", c.getCount()))
                );
            }

            @Override
            public void onError(Source source, CompilationError error) {
                System.err.println(error);
            }

            @Override
            public void onEvent(Event event) {
                event.payload(CustomEventPlugin.Payload.class).ifPresent(p -> System.out.println(p.value()));
            }

        };

        new Compiler(listener, Arrays.asList(
                new CustomEventPlugin(),
                new NodesCountPlugin(),
                new HtmlOutputPlugin()
        )).compile(new SourceFiles(Paths.get(Main.class.getResource("/").toURI())));
    }
}
