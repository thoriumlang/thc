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
package org.thoriumlang.compiler.input.loaders;

import org.thoriumlang.compiler.ast.algorithms.typechecking.TypeLoader;
import org.thoriumlang.compiler.ast.nodes.Node;
import org.thoriumlang.compiler.symbols.JavaClass;
import org.thoriumlang.compiler.symbols.JavaInterface;
import org.thoriumlang.compiler.symbols.Symbol;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.Optional;

public class JavaRTClassLoader implements TypeLoader {
    private final URLClassLoader classLoader;

    @SuppressWarnings("squid:S00112") // we want to throw Error here
    public JavaRTClassLoader() {
        try {
            classLoader = new URLClassLoader(
                    new URL[]{
                            Paths.get(
                                    System.getProperty("sun.boot.class.path"),
                                    "rt.jar"
                            ).toUri().toURL()
                    },
                    null
            );
        } catch (MalformedURLException e) {
            throw new Error(e);
        }
    }

    @Override
    public Optional<Symbol> load(String name, Node node) {
        try {
            Class<?> clazz = classLoader.loadClass(name);

            return Optional.of(
                    clazz.isInterface() ?
                            new JavaInterface(node, clazz) :
                            new JavaClass(node, clazz)
            );
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }
}
