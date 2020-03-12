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
package org.thoriumlang.compiler.ast.algorithms.typechecking;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.ast.nodes.Node;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.visitor.Visitor;
import org.thoriumlang.compiler.symbols.JavaClass;
import org.thoriumlang.compiler.symbols.JavaInterface;
import org.thoriumlang.compiler.symbols.Symbol;

import java.util.Optional;

class RTJarJavaRuntimeClassLoaderTest {
    private final Node node = new Node(new NodeIdGenerator().next()) {
        @Override
        public <T> T accept(Visitor<? extends T> visitor) {
            return null;
        }
    };

    @Test
    void find_success_class() {
        Optional<Symbol> clazz = new RTJarJavaRuntimeClassLoader().load("java.lang.String", node);
        Assertions.assertThat(clazz)
                .get()
                .isInstanceOf(JavaClass.class)
                .hasToString("(rt.jar: class java.lang.String)");
    }

    @Test
    void find_success_interface() {
        Optional<Symbol> clazz = new RTJarJavaRuntimeClassLoader().load("java.util.List", node);
        Assertions.assertThat(clazz)
                .get()
                .isInstanceOf(JavaInterface.class)
                .hasToString("(rt.jar: interface java.util.List)");
    }

    @Test
    void find_failure() {
        Optional<Symbol> clazz = new RTJarJavaRuntimeClassLoader().load("NonexistentClass", node);
        Assertions.assertThat(clazz)
                .isEmpty();
    }
}
