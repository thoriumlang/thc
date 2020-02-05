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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.ast.AST;
import org.thoriumlang.compiler.ast.algorithms.CompilationError;
import org.thoriumlang.compiler.ast.algorithms.symboltable.SymbolTableInitializationVisitor;
import org.thoriumlang.compiler.ast.nodes.Class;
import org.thoriumlang.compiler.ast.nodes.Method;
import org.thoriumlang.compiler.ast.nodes.MethodSignature;
import org.thoriumlang.compiler.ast.nodes.Node;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.nodes.NoneValue;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.ast.nodes.Type;
import org.thoriumlang.compiler.ast.nodes.TypeParameter;
import org.thoriumlang.compiler.ast.nodes.TypeSpecSimple;
import org.thoriumlang.compiler.ast.nodes.Use;
import org.thoriumlang.compiler.ast.nodes.Visibility;
import org.thoriumlang.compiler.ast.visitor.RelativesInjectionVisitor;
import org.thoriumlang.compiler.symbols.JavaClass;
import org.thoriumlang.compiler.symbols.JavaInterface;
import org.thoriumlang.compiler.symbols.Symbol;
import org.thoriumlang.compiler.symbols.SymbolTable;
import org.thoriumlang.compiler.symbols.ThoriumType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

class TypeDiscoveryVisitorTest {
    private static JavaRuntimeClassLoader classLoader = name -> name.equals(String.class.getName()) ?
            Optional.of(String.class) :
            name.equals("java.util.List") ?
                    Optional.of(List.class)
                    : Optional.empty();
    private static NodeIdGenerator nodeIdGenerator = new NodeIdGenerator();

    private TypeDiscoveryVisitor visitor;

    @BeforeEach
    void setup() {
        visitor = new TypeDiscoveryVisitor(classLoader);
    }

    @Test
    void fullTable() throws IOException {
        Root root = new AST(
                TypeDiscoveryVisitorTest.class.getResourceAsStream(
                        "/org/thoriumlang/compiler/ast/algorithms/typechecking/simple.th"
                ),
                "namespace"
        ).root();

        visitor.visit(root);

        Assertions.assertThat(
                root.getContext()
                        .get(SymbolTable.class)
                        .orElseThrow(() -> new IllegalStateException("no symbol table found"))
                        .toString()

        ).isEqualTo(
                new BufferedReader(
                        new InputStreamReader(
                                TypeDiscoveryVisitorTest.class.getResourceAsStream(
                                        "/org/thoriumlang/compiler/ast/algorithms/typechecking/simple.types"
                                )
                        )
                ).lines().collect(Collectors.joining("\n"))
        );
    }

    @Test
    void use_class() {
        Root root = injectSymbolTable(injectParents(new Root(
                nodeIdGenerator.next(),
                "namespace",
                Collections.singletonList(
                        new Use(
                                nodeIdGenerator.next(),
                                "java.lang.String"
                        )
                ),
                new Type(
                        nodeIdGenerator.next(),
                        Visibility.NAMESPACE,
                        "type",
                        Collections.emptyList(),
                        new TypeSpecSimple(nodeIdGenerator.next(), "type", Collections.emptyList()),
                        Collections.emptyList()
                )
        )));

        Assertions.assertThat(visitor.visit(root))
                .isEmpty();
        Assertions.assertThat(getSymbol(root, "String"))
                .get()
                .isInstanceOf(JavaClass.class);
    }

    @Test
    void use_interface() {
        Root root = injectSymbolTable(injectParents(new Root(
                nodeIdGenerator.next(),
                "namespace",
                Collections.singletonList(
                        new Use(
                                nodeIdGenerator.next(),
                                "java.util.List"
                        )
                ),
                new Type(
                        nodeIdGenerator.next(),
                        Visibility.NAMESPACE,
                        "type",
                        Collections.emptyList(),
                        new TypeSpecSimple(nodeIdGenerator.next(), "type", Collections.emptyList()),
                        Collections.emptyList()
                )
        )));

        Assertions.assertThat(visitor.visit(root))
                .isEmpty();
        Assertions.assertThat(getSymbol(root, "List"))
                .get()
                .isInstanceOf(JavaInterface.class);
    }

    @Test
    void use_notFound() {
        Root root = injectSymbolTable(injectParents(new Root(
                nodeIdGenerator.next(),
                "namespace",
                Collections.singletonList(
                        new Use(
                                nodeIdGenerator.next(),
                                "notFound"
                        )
                ),
                new Type(
                        nodeIdGenerator.next(),
                        Visibility.NAMESPACE,
                        "type",
                        Collections.emptyList(),
                        new TypeSpecSimple(nodeIdGenerator.next(), "type", Collections.emptyList()),
                        Collections.emptyList()
                )
        )));

        Assertions.assertThat(visitor.visit(root).stream()
                .map(CompilationError::toString))
                .isNotEmpty()
                .containsExactly("symbol not found: notFound");
        Assertions.assertThat(getSymbol(root, "notFound"))
                .isEmpty();
    }

    @Test
    void use_aliased() {
        Root root = injectSymbolTable(injectParents(new Root(
                nodeIdGenerator.next(),
                "namespace",
                Collections.singletonList(
                        new Use(
                                nodeIdGenerator.next(),
                                "java.lang.String",
                                "JavaString"
                        )
                ),
                new Type(
                        nodeIdGenerator.next(),
                        Visibility.NAMESPACE,
                        "type",
                        Collections.emptyList(),
                        new TypeSpecSimple(nodeIdGenerator.next(), "type", Collections.emptyList()),
                        Collections.emptyList()
                )
        )));

        Assertions.assertThat(visitor.visit(root))
                .isEmpty();
        Assertions.assertThat(getSymbol(root, "JavaString"))
                .get()
                .isInstanceOf(JavaClass.class);
    }

    @Test
    void methodSignature() {
        MethodSignature methodSignature = new MethodSignature(
                nodeIdGenerator.next(),
                Visibility.NAMESPACE,
                "methodName",
                Collections.singletonList(new TypeParameter(nodeIdGenerator.next(), "TypeParameter")),
                Collections.emptyList(),
                new TypeSpecSimple(nodeIdGenerator.next(), "ReturnType", Collections.emptyList())
        );

        Root root = injectSymbolTable(injectParents(
                new Root(
                        nodeIdGenerator.next(),
                        "namespace",
                        Collections.emptyList(),
                        new Type(
                                nodeIdGenerator.next(),
                                Visibility.NAMESPACE,
                                "type",
                                Collections.emptyList(),
                                new TypeSpecSimple(
                                        nodeIdGenerator.next(),
                                        "type",
                                        Collections.emptyList()
                                ),
                                Collections.singletonList(methodSignature)
                        )
                )
        ));

        Assertions.assertThat(visitor.visit(root))
                .isEmpty();
        Assertions.assertThat(getSymbol(methodSignature, "TypeParameter"))
                .get()
                .isInstanceOf(ThoriumType.class);
    }

    @Test
    void method() {
        Method method = new Method(
                nodeIdGenerator.next(),
                new MethodSignature(
                        nodeIdGenerator.next(),
                        Visibility.NAMESPACE,
                        "methodName",
                        Collections.singletonList(new TypeParameter(nodeIdGenerator.next(), "TypeParameter")),
                        Collections.emptyList(),
                        new TypeSpecSimple(nodeIdGenerator.next(), "ReturnType", Collections.emptyList())
                ),
                Collections.emptyList()
        );
        Root root = injectSymbolTable(injectParents(new Root(
                nodeIdGenerator.next(),
                "namespace",
                Collections.emptyList(),
                new Class(
                        nodeIdGenerator.next(),
                        Visibility.NAMESPACE,
                        "class",
                        Collections.emptyList(),
                        new TypeSpecSimple(
                                nodeIdGenerator.next(),
                                "type",
                                Collections.emptyList()
                        ),
                        Collections.singletonList(method),
                        Collections.emptyList()
                )
        )));

        Assertions.assertThat(visitor.visit(root))
                .isEmpty();

        Assertions.assertThat(getSymbol(method, "TypeParameter"))
                .get()
                .isInstanceOf(ThoriumType.class);
    }

    @Test
    void type() {
        MethodSignature methodSignature = new MethodSignature(
                nodeIdGenerator.next(),
                Visibility.NAMESPACE,
                "methodName",
                Collections.singletonList(new TypeParameter(nodeIdGenerator.next(), "MethodTypeParameter")),
                Collections.emptyList(),
                new TypeSpecSimple(nodeIdGenerator.next(), "ReturnType", Collections.emptyList())
        );
        Root root = injectSymbolTable(injectParents(new Root(
                        nodeIdGenerator.next(),
                        "namespace",
                        Collections.emptyList(),
                        new Type(
                                nodeIdGenerator.next(),
                                Visibility.NAMESPACE,
                                "TypeName",
                                Collections.singletonList(new TypeParameter(nodeIdGenerator.next(), "TypeParameter")),
                                new TypeSpecSimple(nodeIdGenerator.next(), "SuperType", Collections.emptyList()),
                                Collections.singletonList(methodSignature)
                        )
                )
        ));

        Assertions.assertThat(visitor.visit(root))
                .isEmpty();

        Assertions.assertThat(getSymbol(root.getTopLevelNode(), "TypeName"))
                .get()
                .isInstanceOf(ThoriumType.class);
        Assertions.assertThat(getSymbol(root.getTopLevelNode(), "TypeParameter"))
                .get()
                .isInstanceOf(ThoriumType.class);
        Assertions.assertThat(getSymbol(root.getTopLevelNode(), "MethodTypeParameter"))
                .isEmpty();
        Assertions.assertThat(getSymbol(methodSignature, "MethodTypeParameter"))
                .get()
                .isInstanceOf(ThoriumType.class);
    }

    @Test
    void type_alreadyDefined() {
        Root root = injectSymbolTable(injectParents(new Root(
                nodeIdGenerator.next(),
                "namespace",
                Collections.emptyList(),
                new Type(
                        nodeIdGenerator.next(),
                        Visibility.NAMESPACE,
                        "TypeName",
                        Collections.singletonList(
                                new TypeParameter(nodeIdGenerator.next(), "TypeParameter")),
                        new TypeSpecSimple(nodeIdGenerator.next(), "SuperType",
                                Collections.emptyList()),
                        Collections.emptyList()
                )
        )));

        putSymbol(
                root.getTopLevelNode(),
                new JavaClass("TypeName", new NoneValue(nodeIdGenerator.next()), String.class)
        );

        Assertions.assertThat(visitor.visit(root).stream()
                .map(CompilationError::toString))
                .isNotEmpty()
                .containsExactly("symbol already defined: TypeName");

        Assertions.assertThat(getSymbol(root.getTopLevelNode(), "TypeName"))
                .get()
                .isInstanceOf(JavaClass.class);
    }

    @Test
    void clazz() {
        Method method = new Method(
                nodeIdGenerator.next(),
                new MethodSignature(
                        nodeIdGenerator.next(),
                        Visibility.NAMESPACE,
                        "methodName",
                        Collections.singletonList(new TypeParameter(nodeIdGenerator.next(), "MethodTypeParameter")),
                        Collections.emptyList(),
                        new TypeSpecSimple(nodeIdGenerator.next(), "ReturnType", Collections.emptyList())
                ),
                Collections.emptyList()
        );
        Root root = injectSymbolTable(injectParents(new Root(
                nodeIdGenerator.next(),
                "ns",
                Collections.emptyList(),
                new Class(
                        nodeIdGenerator.next(),
                        Visibility.NAMESPACE,
                        "ClassName",
                        Collections.singletonList(new TypeParameter(nodeIdGenerator.next(), "TypeParameter")),
                        new TypeSpecSimple(nodeIdGenerator.next(), "SuperType", Collections.emptyList()),
                        Collections.singletonList(
                                method
                        ),
                        Collections.emptyList()
                )
        )));

        Assertions.assertThat(visitor.visit(root))
                .isEmpty();

        Assertions.assertThat(getSymbol(root.getTopLevelNode(), "ClassName"))
                .get()
                .isInstanceOf(ThoriumType.class);
        Assertions.assertThat(getSymbol(root.getTopLevelNode(), "TypeParameter"))
                .get()
                .isInstanceOf(ThoriumType.class);
        Assertions.assertThat(getSymbol(root.getTopLevelNode(), "MethodTypeParameter"))
                .isEmpty();
        Assertions.assertThat(getSymbol(method, "MethodTypeParameter"))
                .get()
                .isInstanceOf(ThoriumType.class);
    }

    @Test
    void clazz_alreadyDefined() {
        Root root = injectSymbolTable(injectParents(new Root(
                nodeIdGenerator.next(),
                "namespace",
                Collections.emptyList(),
                new Class(
                        nodeIdGenerator.next(),
                        Visibility.NAMESPACE,
                        "ClassName",
                        Collections.singletonList(
                                new TypeParameter(nodeIdGenerator.next(), "TypeParameter")),
                        new TypeSpecSimple(nodeIdGenerator.next(), "SuperType",
                                Collections.emptyList()),
                        Collections.emptyList(),
                        Collections.emptyList()
                )
        )));

        putSymbol(
                root.getTopLevelNode(),
                new JavaClass("ClassName", new NoneValue(nodeIdGenerator.next()), String.class)
        );

        Assertions.assertThat(visitor.visit(root).stream()
                .map(CompilationError::toString))
                .isNotEmpty()
                .containsExactly("symbol already defined: ClassName");

        Assertions.assertThat(getSymbol(root.getTopLevelNode(), "ClassName"))
                .get()
                .isInstanceOf(JavaClass.class);
    }

    @Test
    void root_type() {
        Root root = injectSymbolTable(injectParents(new Root(
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
                        Collections.emptyList(),
                        new TypeSpecSimple(nodeIdGenerator.next(), "SuperType", Collections.emptyList()),
                        Collections.emptyList()
                )
        )));

        Assertions.assertThat(visitor.visit(root))
                .isEmpty();

        Assertions.assertThat(getSymbol(root, "String"))
                .get()
                .isInstanceOf(JavaClass.class);
        Assertions.assertThat(getSymbol(root, "TypeName"))
                .get()
                .isInstanceOf(ThoriumType.class);
    }

    @Test
    void root_class() {
        Root node = injectSymbolTable(injectParents(new Root(
                nodeIdGenerator.next(),
                "namespace",
                Collections.singletonList(new Use(
                        nodeIdGenerator.next(),
                        "java.lang.String"
                )),
                new Class(
                        nodeIdGenerator.next(),
                        Visibility.NAMESPACE,
                        "ClassName",
                        Collections.emptyList(),
                        new TypeSpecSimple(nodeIdGenerator.next(), "SuperType", Collections.emptyList()),
                        Collections.emptyList(),
                        Collections.emptyList()
                )
        )));

        Assertions.assertThat(visitor.visit(node))
                .isEmpty();

        Assertions.assertThat(getSymbol(node, "String"))
                .get()
                .isInstanceOf(JavaClass.class);
        Assertions.assertThat(getSymbol(node, "ClassName"))
                .get()
                .isInstanceOf(ThoriumType.class);
    }


    // FIXME implement tests for missing type params (FunctionValue) in both attributes and stmts


    @SuppressWarnings("unchecked") // we're sure about what we return: it's the same object as what we get as input
    private <T extends Node> T injectParents(T node) {
        return (T) node.accept(new RelativesInjectionVisitor());
    }

    @SuppressWarnings("unchecked") // we're sure about what we return: it's the same object as what we get as input
    private <T extends Node> T injectSymbolTable(T node) {
        return (T) node.accept(new SymbolTableInitializationVisitor());
    }

    private Optional<Symbol> getSymbol(Node node, String name) {
        return node
                .getContext()
                .get(SymbolTable.class)
                .orElseThrow(IllegalStateException::new)
                .find(name);
    }

    private void putSymbol(Node node, Symbol symbol) {
        node
                .getContext()
                .get(SymbolTable.class)
                .orElseThrow(IllegalStateException::new)
                .put(symbol);
    }
}
