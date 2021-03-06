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
import org.thoriumlang.compiler.ast.context.SourcePosition;
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
import org.thoriumlang.compiler.ast.visitor.NodesMatchingVisitor;
import org.thoriumlang.compiler.ast.visitor.RelativesInjectionVisitor;
import org.thoriumlang.compiler.ast.visitor.SymbolTableInitializationVisitor;
import org.thoriumlang.compiler.collections.Lists;
import org.thoriumlang.compiler.input.loaders.TypeLoader;
import org.thoriumlang.compiler.symbols.AliasSymbol;
import org.thoriumlang.compiler.symbols.JavaClass;
import org.thoriumlang.compiler.symbols.JavaInterface;
import org.thoriumlang.compiler.symbols.Name;
import org.thoriumlang.compiler.symbols.Symbol;
import org.thoriumlang.compiler.symbols.SymbolTable;
import org.thoriumlang.compiler.symbols.ThoriumType;
import org.thoriumlang.compiler.testsupport.ExternalString;
import org.thoriumlang.compiler.testsupport.SymbolTableDumpingVisitor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

class TypeDiscoveryVisitorTest {
    private static final TypeLoader typeLoader = (name, node) -> {
        if (name.getFullName().equals(String.class.getName())) {
            return Optional.of(new JavaClass(node, String.class));
        }
        if (name.getFullName().equals(List.class.getName())) {
            return Optional.of(new JavaInterface(node, List.class));
        }
        if (name.getFullName().equals(Long.class.getName())) {
            return Optional.of(new JavaInterface(node, Long.class));
        }
        if (name.getFullName().equals(Integer.class.getName())) {
            return Optional.of(new JavaInterface(node, Integer.class));
        }
        return Optional.empty();
    };

    private static final NodeIdGenerator nodeIdGenerator = new NodeIdGenerator();

    private TypeDiscoveryVisitor visitor;

    @BeforeEach
    void setup() {
        visitor = new TypeDiscoveryVisitor("namespace", typeLoader);
    }

    @Test
    void fullTable() {
        // TODO refactor, it's to big to be easily understandable
        SymbolTable rootSymbolTable = new SymbolTable();
        Root root = new AST(
                TypeDiscoveryVisitorTest.class.getResourceAsStream(
                        "/org/thoriumlang/compiler/ast/algorithms/typechecking/Main_discovery.th"
                ),
                "namespace",
                new NodeIdGenerator(),
                Collections.emptyList(),
                rootSymbolTable
        ).root().orElseThrow(() -> new IllegalStateException("no root found"));

        visitor.visit(root);

        Assertions
                .assertThat(
                        String.join("\n", rootSymbolTable.accept(new SymbolTableDumpingVisitor(true)))
                )
                .isEqualTo(ExternalString.fromClasspath(
                        "/org/thoriumlang/compiler/ast/algorithms/typechecking/Main_discovery.types"
                ));
    }

    @Test
    void use_class() {
        Root root = injectSourcePosition(injectSymbolTable(injectParents(new Root(
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
        ))));

        Assertions.assertThat(visitor.visit(root))
                .isEmpty();
        Assertions.assertThat(getSymbol(root, "String"))
                .get()
                .isInstanceOf(AliasSymbol.class);
    }

    @Test
    void use_interface() {
        Root root = injectSourcePosition(injectSymbolTable(injectParents(new Root(
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
        ))));

        Assertions.assertThat(visitor.visit(root))
                .isEmpty();
        Assertions.assertThat(getSymbol(root, "List"))
                .get()
                .isInstanceOf(AliasSymbol.class);
    }

    @Test
    void use_notFound() {
        Root root = injectSourcePosition(injectSymbolTable(injectParents(new Root(
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
        ))));

        Assertions.assertThat(visitor.visit(root).stream()
                .map(se -> se.format((sp, message) -> String.format("%s (%d)", message, sp.getStartLine())))
        )
                .isNotEmpty()
                .containsExactly("symbol not found: notFound (1)");
        Assertions.assertThat(getSymbol(root, "notFound"))
                .isEmpty();
    }

    @Test
    void use_aliased() {
        Root root = injectSourcePosition(injectSymbolTable(injectParents(new Root(
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
        ))));

        Assertions.assertThat(visitor.visit(root))
                .isEmpty();
        Assertions.assertThat(getSymbol(root, "JavaString"))
                .get()
                .isInstanceOf(AliasSymbol.class);
    }

    @Test
    void use_duplicateAlias() {
        Root root = injectSourcePosition(injectSymbolTable(injectParents(new Root(
                nodeIdGenerator.next(),
                "namespace",
                Arrays.asList(
                        new Use(
                                nodeIdGenerator.next(),
                                "java.lang.Long",
                                "Number"
                        ),
                        new Use(
                                nodeIdGenerator.next(),
                                "java.lang.Integer",
                                "Number"
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
        ))));

        Assertions.assertThat(visitor.visit(root).stream()
                .map(se -> se.format((sp, message) -> String.format("%s (%d)", message, sp.getStartLine())))
        )
                .isNotEmpty()
                .containsExactly("symbol already defined: Number (1)");
        Assertions.assertThat(getSymbol(root, "Number"))
                .get()
                .isInstanceOf(AliasSymbol.class);
    }

    @Test
    void use_duplicateAliasedUse() {
        Root root = injectSourcePosition(injectSymbolTable(injectParents(new Root(
                nodeIdGenerator.next(),
                "namespace",
                Arrays.asList(
                        new Use(
                                nodeIdGenerator.next(),
                                "java.lang.String",
                                "JavaString1"
                        ),
                        new Use(
                                nodeIdGenerator.next(),
                                "java.lang.String",
                                "JavaString2"
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
        ))));

        Assertions.assertThat(visitor.visit(root))
                .isEmpty();
        Assertions.assertThat(getSymbol(root, "JavaString1"))
                .get()
                .isInstanceOf(AliasSymbol.class);
        Assertions.assertThat(getSymbol(root, "JavaString2"))
                .get()
                .isInstanceOf(AliasSymbol.class);
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

        Root root = injectSourcePosition(injectSymbolTable(injectParents(
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
        )));

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
        Root root = injectSourcePosition(injectSymbolTable(injectParents(new Root(
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
        ))));

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
        Root root = injectSourcePosition(injectSymbolTable(injectParents(new Root(
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
        )));

        Assertions.assertThat(visitor.visit(root))
                .isEmpty();

        Assertions.assertThat(getSymbol(root.getTopLevelNode(), "TypeName"))
                .get()
                .isInstanceOf(AliasSymbol.class);
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
        Root root = injectSourcePosition(injectSymbolTable(injectParents(new Root(
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
        ))));

        putSymbol(
                "TypeName",
                root.getTopLevelNode(),
                new JavaClass(new NoneValue(nodeIdGenerator.next()), String.class)
        );

        Assertions.assertThat(visitor.visit(root).stream()
                .map(se -> se.format((sp, message) -> String.format("%s (%d)", message, sp.getStartLine())))
        )
                .isNotEmpty()
                .containsExactly("symbol already defined: TypeName (1)");

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
        Root root = injectSourcePosition(injectSymbolTable(injectParents(new Root(
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
        ))));

        Assertions.assertThat(visitor.visit(root))
                .isEmpty();

        Assertions.assertThat(getSymbol(root.getTopLevelNode(), "ClassName"))
                .get()
                .isInstanceOf(AliasSymbol.class);
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
        Root root = injectSourcePosition(injectSymbolTable(injectParents(new Root(
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
        ))));

        putSymbol(
                "ClassName",
                root.getTopLevelNode(),
                new JavaClass(new NoneValue(nodeIdGenerator.next()), String.class)
        );

        Assertions.assertThat(visitor.visit(root).stream()
                .map(se -> se.format((sp, message) -> String.format("%s (%d)", message, sp.getStartLine())))
        )
                .isNotEmpty()
                .containsExactly("symbol already defined: ClassName (1)");

        Assertions.assertThat(getSymbol(root.getTopLevelNode(), "ClassName"))
                .get()
                .isInstanceOf(JavaClass.class);
    }

    @Test
    void root_type() {
        Root root = injectSourcePosition(injectSymbolTable(injectParents(new Root(
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
        ))));

        Assertions.assertThat(visitor.visit(root))
                .isEmpty();

        Assertions.assertThat(getSymbol(root, "String"))
                .get()
                .isInstanceOf(AliasSymbol.class);
        Assertions.assertThat(getSymbol(root, "TypeName"))
                .get()
                .isInstanceOf(AliasSymbol.class);
    }

    @Test
    void root_class() {
        Root node = injectSourcePosition(injectSymbolTable(injectParents(new Root(
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
        ))));

        Assertions.assertThat(visitor.visit(node))
                .isEmpty();

        Assertions.assertThat(getSymbol(node, "String"))
                .get()
                .isInstanceOf(AliasSymbol.class);
        Assertions.assertThat(getSymbol(node, "ClassName"))
                .get()
                .isInstanceOf(AliasSymbol.class);
    }


    // FIXME implement tests for missing type params (FunctionValue) in both attributes and stmts


    private Root injectSourcePosition(Root node) {
        new NodesMatchingVisitor(n -> true)
                .visit(node)
                .forEach(n -> n.getContext().put(
                        SourcePosition.class,
                        new SourcePosition(
                                new SourcePosition.Position(1, 1),
                                new SourcePosition.Position(1, 1),
                                Collections.singletonList("")
                        )
                ));

        return node;
    }

    private Root injectParents(Root node) {
        return (Root) node.accept(new RelativesInjectionVisitor());
    }

    private Root injectSymbolTable(Root node) {
        return (Root) new SymbolTableInitializationVisitor(new SymbolTable()).visit(node);
    }

    private Optional<Symbol> getSymbol(Node node, String name) {
        return Lists.get(node.getContext().require(SymbolTable.class).find(new Name(name)), 0);
    }

    private void putSymbol(String name, Node node, Symbol symbol) {
        node
                .getContext()
                .require(SymbolTable.class)
                .put(new Name(name), symbol);
    }
}
