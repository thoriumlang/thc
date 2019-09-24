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
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.ast.nodes.Type;
import org.thoriumlang.compiler.ast.nodes.TypeParameter;
import org.thoriumlang.compiler.ast.nodes.TypeSpecSimple;
import org.thoriumlang.compiler.ast.nodes.Use;
import org.thoriumlang.compiler.ast.nodes.Visibility;
import org.thoriumlang.compiler.symbols.JavaClass;
import org.thoriumlang.compiler.symbols.JavaInterface;
import org.thoriumlang.compiler.symbols.Symbol;
import org.thoriumlang.compiler.symbols.SymbolTable;
import org.thoriumlang.compiler.symbols.ThoriumType;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

class TypeDiscoveryVisitorTest {
    private static JavaRuntimeClassLoader classLoader = name -> name.equals(String.class.getName()) ?
            Optional.of(String.class) :
            name.equals("java.util.List") ?
                    Optional.of(List.class)
                    : Optional.empty();
    private static NodeIdGenerator nodeIdGenerator = new NodeIdGenerator();

    @Test
    void use_class() {
        Use node = injectSymbolTable(new Use(
                nodeIdGenerator.next(),
                "java.lang.String"
        ));

        Assertions.assertThat(visitor().visit(node))
                .isEmpty();
        Assertions.assertThat(getSymbol(node, "String"))
                .get()
                .isInstanceOf(JavaClass.class);
    }

    @Test
    void use_interface() {
        Use node = injectSymbolTable(new Use(
                nodeIdGenerator.next(),
                "java.util.List"
        ));

        Assertions.assertThat(visitor().visit(node))
                .isEmpty();
        Assertions.assertThat(getSymbol(node, "List"))
                .get()
                .isInstanceOf(JavaInterface.class);
    }

    @Test
    void use_notFound() {
        Use node = injectSymbolTable(new Use(
                nodeIdGenerator.next(),
                "notFound"
        ));

        Assertions.assertThat(visitor().visit(node).stream()
                .map(TypeCheckingError::toString))
                .isNotEmpty()
                .containsExactly("symbol not found: notFound");
        Assertions.assertThat(getSymbol(node, "notFound"))
                .isEmpty();
    }

    @Test
    void use_aliased() {
        Use node = injectSymbolTable(new Use(
                nodeIdGenerator.next(),
                "java.lang.String",
                "JavaString"
        ));

        Assertions.assertThat(visitor().visit(node))
                .isEmpty();
        Assertions.assertThat(getSymbol(node, "JavaString"))
                .get()
                .isInstanceOf(JavaClass.class);
    }

    @Test
    void type() {
        Type node = injectSymbolTable(new Type(
                nodeIdGenerator.next(),
                Visibility.NAMESPACE,
                "TypeName",
                Collections.singletonList(new TypeParameter(nodeIdGenerator.next(), "TypeParameter")),
                new TypeSpecSimple(nodeIdGenerator.next(), "SuperType", Collections.emptyList()),
                Collections.emptyList()
        ));

        Assertions.assertThat(visitor().visit(node))
                .isEmpty();

        Assertions.assertThat(getSymbol(node, "TypeName"))
                .get()
                .isInstanceOf(ThoriumType.class);
        Assertions.assertThat(getSymbol(node, "TypeParameter"))
                .get()
                .isInstanceOf(ThoriumType.class);
    }

    @Test
    void type_alreadyDefined() {
        Type node = putSymbol(
                injectSymbolTable(
                        new Type(
                                nodeIdGenerator.next(),
                                Visibility.NAMESPACE,
                                "TypeName",
                                Collections.singletonList(new TypeParameter(nodeIdGenerator.next(), "TypeParameter")),
                                new TypeSpecSimple(nodeIdGenerator.next(), "SuperType", Collections.emptyList()),
                                Collections.emptyList()
                        )
                ),
                "TypeName",
                new JavaClass(String.class)
        );

        Assertions.assertThat(visitor().visit(node).stream()
                .map(TypeCheckingError::toString))
                .isNotEmpty()
                .containsExactly("symbol already defined: TypeName");

        Assertions.assertThat(getSymbol(node, "TypeName"))
                .get()
                .isInstanceOf(JavaClass.class);
    }

    @Test
    void root() {
        Root node = injectSymbolTable(new Root(
                nodeIdGenerator.next(),
                "namespace",
                Collections.singletonList(new Use(
                        nodeIdGenerator.next(),
                        "java.lang.String"
                )),
                new Type(
                        nodeIdGenerator.next(),
                        Visibility.NAMESPACE,
                        "TypeName",
                        Collections.singletonList(new TypeParameter(nodeIdGenerator.next(), "TypeParameter")),
                        new TypeSpecSimple(nodeIdGenerator.next(), "SuperType", Collections.emptyList()),
                        Collections.emptyList()
                )
        ));

        Assertions.assertThat(visitor().visit(node))
                .isEmpty();

        Assertions.assertThat(getSymbol(node, "String"))
                .get()
                .isInstanceOf(JavaClass.class);
        Assertions.assertThat(getSymbol(node, "TypeName"))
                .get()
                .isInstanceOf(ThoriumType.class);
        Assertions.assertThat(getSymbol(node, "TypeParameter"))
                .get()
                .isInstanceOf(ThoriumType.class);
    }

    @SuppressWarnings("unchecked") // we're sure about what we return: it's the same object as what we get as input
    private <T extends Node> T injectSymbolTable(T node) {
        return (T) node
                .getContext()
                .put(SymbolTable.class, new SymbolTable())
                .getNode();
    }

    private Optional<Symbol> getSymbol(Node node, String name) {
        return node
                .getContext()
                .get(SymbolTable.class)
                .orElseThrow(IllegalStateException::new)
                .find(name);
    }

    private <T extends Node> T putSymbol(T node, String name, Symbol symbol) {
        node
                .getContext()
                .get(SymbolTable.class)
                .orElseThrow(IllegalStateException::new)
                .put(name, symbol);

        return node;
    }

    private TypeDiscoveryVisitor visitor() {
        return new TypeDiscoveryVisitor(classLoader);
    }
}
