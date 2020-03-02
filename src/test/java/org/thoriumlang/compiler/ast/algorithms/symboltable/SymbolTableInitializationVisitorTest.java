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
package org.thoriumlang.compiler.ast.algorithms.symboltable;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.ast.AST;
import org.thoriumlang.compiler.ast.context.Relatives;
import org.thoriumlang.compiler.ast.nodes.Attribute;
import org.thoriumlang.compiler.ast.nodes.BooleanValue;
import org.thoriumlang.compiler.ast.nodes.Class;
import org.thoriumlang.compiler.ast.nodes.DirectAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.FunctionValue;
import org.thoriumlang.compiler.ast.nodes.IdentifierValue;
import org.thoriumlang.compiler.ast.nodes.IndirectAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.Method;
import org.thoriumlang.compiler.ast.nodes.MethodCallValue;
import org.thoriumlang.compiler.ast.nodes.MethodSignature;
import org.thoriumlang.compiler.ast.nodes.Mode;
import org.thoriumlang.compiler.ast.nodes.NestedValue;
import org.thoriumlang.compiler.ast.nodes.NewAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.Node;
import org.thoriumlang.compiler.ast.nodes.NodeId;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.nodes.NoneValue;
import org.thoriumlang.compiler.ast.nodes.NumberValue;
import org.thoriumlang.compiler.ast.nodes.Parameter;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.ast.nodes.Statement;
import org.thoriumlang.compiler.ast.nodes.StringValue;
import org.thoriumlang.compiler.ast.nodes.Type;
import org.thoriumlang.compiler.ast.nodes.TypeParameter;
import org.thoriumlang.compiler.ast.nodes.TypeSpecFunction;
import org.thoriumlang.compiler.ast.nodes.TypeSpecInferred;
import org.thoriumlang.compiler.ast.nodes.TypeSpecIntersection;
import org.thoriumlang.compiler.ast.nodes.TypeSpecSimple;
import org.thoriumlang.compiler.ast.nodes.TypeSpecUnion;
import org.thoriumlang.compiler.ast.nodes.Use;
import org.thoriumlang.compiler.ast.nodes.Visibility;
import org.thoriumlang.compiler.ast.visitor.RelativesInjectionVisitor;
import org.thoriumlang.compiler.ast.visitor.Visitor;
import org.thoriumlang.compiler.symbols.DefaultSymbolTable;
import org.thoriumlang.compiler.symbols.SymbolTable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class SymbolTableInitializationVisitorTest {
    private static NodeIdGenerator nodeIdGenerator = new NodeIdGenerator();

    private SymbolTableInitializationVisitor visitor;

    @BeforeEach
    void setup() {
        visitor = new SymbolTableInitializationVisitor(new DefaultSymbolTable());
    }

    @Test
    void root() {
        Root root = injectParents(new Root(
                nodeIdGenerator.next(),
                "namespace",
                Collections.emptyList(),
                new Type(
                        nodeIdGenerator.next(),
                        Visibility.NAMESPACE,
                        "Type",
                        Collections.emptyList(),
                        new TypeSpecSimple(nodeIdGenerator.next(), "type", Collections.emptyList()),
                        Collections.emptyList()
                )
        ));

        visitor.visit(root);

        Assertions.assertThat(root.getContext().get(SymbolTable.class))
                .isPresent();
    }

    @Test
    void use() {
        Root root = injectParents(new Root(
                nodeIdGenerator.next(),
                "namespace",
                Collections.singletonList(new Use(nodeIdGenerator.next(), "use")),
                new Type(
                        nodeIdGenerator.next(),
                        Visibility.NAMESPACE,
                        "Type",
                        Collections.emptyList(),
                        new TypeSpecSimple(nodeIdGenerator.next(), "type", Collections.emptyList()),
                        Collections.emptyList()
                )
        ));

        visitor.visit(root);

        Assertions.assertThat(root.getUses().get(0).getContext().get(SymbolTable.class))
                .get()
                .isSameAs(root.getContext()
                        .get(SymbolTable.class)
                        .orElseThrow(() -> new IllegalStateException("no symbol table found"))
                );
    }

    @Test
    void type() {
        Root root = injectParents(new Root(
                nodeIdGenerator.next(),
                "namespace",
                Collections.emptyList(),
                new Type(
                        nodeIdGenerator.next(),
                        Visibility.NAMESPACE,
                        "Type",
                        Collections.emptyList(),
                        new TypeSpecSimple(nodeIdGenerator.next(), "type", Collections.emptyList()),
                        Collections.emptyList()
                )
        ));

        visitor.visit(root);

        Assertions.assertThat(root.getTopLevelNode().getContext().get(SymbolTable.class))
                .get()
                .matches(st ->
                        st.parent() == root.getContext()
                                .get(SymbolTable.class)
                                .orElseThrow(() -> new IllegalStateException("no symbol table found"))
                );
    }

    @Test
    void type_typeParameter() {
        Type type = new Type(
                nodeIdGenerator.next(),
                Visibility.NAMESPACE,
                "Type",
                Collections.singletonList(new TypeParameter(nodeIdGenerator.next(), "typeParameter")),
                new TypeSpecSimple(nodeIdGenerator.next(), "type", Collections.emptyList()),
                Collections.emptyList()
        );
        Root root = injectParents(new Root(
                nodeIdGenerator.next(),
                "namespace",
                Collections.emptyList(),
                type
        ));

        visitor.visit(root);

        Assertions.assertThat(type.getTypeParameters().get(0).getContext().get(SymbolTable.class))
                .get()
                .matches(st ->
                        st == type.getContext()
                                .get(SymbolTable.class)
                                .orElseThrow(() -> new IllegalStateException("no symbol table found"))
                );
    }

    @Test
    void type_superType() {
        Type type = new Type(
                nodeIdGenerator.next(),
                Visibility.NAMESPACE,
                "Type",
                Collections.emptyList(),
                new TypeSpecSimple(nodeIdGenerator.next(), "type", Collections.emptyList()),
                Collections.emptyList()
        );
        Root root = injectParents(new Root(
                nodeIdGenerator.next(),
                "namespace",
                Collections.emptyList(),
                type
        ));

        visitor.visit(root);

        Assertions.assertThat(type.getSuperType().getContext().get(SymbolTable.class))
                .get()
                .matches(st ->
                        st == type.getContext()
                                .get(SymbolTable.class)
                                .orElseThrow(() -> new IllegalStateException("no symbol table found"))
                );
    }

    @Test
    void type_methodSignature() {
        Type type = new Type(
                nodeIdGenerator.next(),
                Visibility.NAMESPACE,
                "Type",
                Collections.emptyList(),
                new TypeSpecSimple(nodeIdGenerator.next(), "type", Collections.emptyList()),
                Collections.singletonList(
                        new MethodSignature(
                                nodeIdGenerator.next(),
                                Visibility.NAMESPACE,
                                "methodName",
                                Collections.emptyList(),
                                Collections.emptyList(),
                                new TypeSpecSimple(nodeIdGenerator.next(), "type", Collections.emptyList())
                        )
                )
        );
        Root root = injectParents(new Root(
                nodeIdGenerator.next(),
                "namespace",
                Collections.emptyList(),
                type
        ));

        visitor.visit(root);

        Assertions.assertThat(type.getMethods().get(0).getContext().get(SymbolTable.class))
                .get()
                .matches(st ->
                        st.parent() == type.getContext()
                                .get(SymbolTable.class)
                                .orElseThrow(() -> new IllegalStateException("no symbol table found"))
                );
    }

    @Test
    void clazz() {
        Root root = injectParents(new Root(
                nodeIdGenerator.next(),
                "namespace",
                Collections.emptyList(),
                new Class(
                        nodeIdGenerator.next(),
                        Visibility.NAMESPACE,
                        "Class",
                        Collections.emptyList(),
                        new TypeSpecSimple(nodeIdGenerator.next(), "type", Collections.emptyList()),
                        Collections.emptyList(),
                        Collections.emptyList()
                )
        ));

        visitor.visit(root);

        Assertions.assertThat(root.getTopLevelNode().getContext().get(SymbolTable.class))
                .get()
                .matches(st ->
                        st.parent() == root.getContext()
                                .get(SymbolTable.class)
                                .orElseThrow(() -> new IllegalStateException("no symbol table found"))
                );
    }

    @Test
    void clazz_typeParameter() {
        Class clazz = new Class(
                nodeIdGenerator.next(),
                Visibility.NAMESPACE,
                "Class",
                Collections.singletonList(new TypeParameter(nodeIdGenerator.next(), "T")),
                new TypeSpecSimple(nodeIdGenerator.next(), "type", Collections.emptyList()),
                Collections.emptyList(),
                Collections.emptyList()
        );
        Root root = injectParents(new Root(
                nodeIdGenerator.next(),
                "namespace",
                Collections.emptyList(),
                clazz
        ));

        visitor.visit(root);

        Assertions.assertThat(clazz.getTypeParameters().get(0).getContext().get(SymbolTable.class))
                .get()
                .matches(st ->
                        st == clazz.getContext()
                                .get(SymbolTable.class)
                                .orElseThrow(() -> new IllegalStateException("no symbol table found"))
                );
    }

    @Test
    void clazz_superType() {
        Class clazz = new Class(
                nodeIdGenerator.next(),
                Visibility.NAMESPACE,
                "Class",
                Collections.emptyList(),
                new TypeSpecSimple(nodeIdGenerator.next(), "type", Collections.emptyList()),
                Collections.emptyList(),
                Collections.emptyList()
        );
        Root root = injectParents(new Root(
                nodeIdGenerator.next(),
                "namespace",
                Collections.emptyList(),
                clazz
        ));

        visitor.visit(root);

        Assertions.assertThat(clazz.getSuperType().getContext().get(SymbolTable.class))
                .get()
                .matches(st ->
                        st == clazz.getContext()
                                .get(SymbolTable.class)
                                .orElseThrow(() -> new IllegalStateException("no symbol table found"))
                );
    }

    @Test
    void clazz_method() {
        Class clazz = new Class(
                nodeIdGenerator.next(),
                Visibility.NAMESPACE,
                "Class",
                Collections.emptyList(),
                new TypeSpecSimple(nodeIdGenerator.next(), "type", Collections.emptyList()),
                Collections.singletonList(new Method(
                        nodeIdGenerator.next(),
                        new MethodSignature(
                                nodeIdGenerator.next(),
                                Visibility.NAMESPACE,
                                "name",
                                Collections.emptyList(),
                                Collections.emptyList(),
                                new TypeSpecSimple(nodeIdGenerator.next(), "type", Collections.emptyList())
                        ),
                        Collections.emptyList()
                )),
                Collections.emptyList()
        );
        Root root = injectParents(new Root(
                nodeIdGenerator.next(),
                "namespace",
                Collections.emptyList(),
                clazz
        ));

        visitor.visit(root);

        Assertions.assertThat(clazz.getMethods().get(0).getContext().get(SymbolTable.class))
                .get()
                .matches(st ->
                        st.parent().parent() == clazz.getContext()
                                .get(SymbolTable.class)
                                .orElseThrow(() -> new IllegalStateException("no symbol table found"))
                );
    }

    @Test
    void clazz_attribute() {
        Class clazz = new Class(
                nodeIdGenerator.next(),
                Visibility.NAMESPACE,
                "Class",
                Collections.emptyList(),
                new TypeSpecSimple(nodeIdGenerator.next(), "type", Collections.emptyList()),
                Collections.emptyList(),
                Collections.singletonList(
                        new Attribute(
                                nodeIdGenerator.next(),
                                "attribute",
                                new TypeSpecSimple(nodeIdGenerator.next(), "type", Collections.emptyList()),
                                new NoneValue(nodeIdGenerator.next()),
                                Mode.VAL
                        )
                )
        );
        Root root = injectParents(new Root(
                nodeIdGenerator.next(),
                "namespace",
                Collections.emptyList(),
                clazz
        ));

        visitor.visit(root);

        Assertions.assertThat(clazz.getAttributes().get(0).getContext().get(SymbolTable.class))
                .get()
                .matches(st ->
                        st.parent() == clazz.getContext()
                                .get(SymbolTable.class)
                                .orElseThrow(() -> new IllegalStateException("no symbol table found"))
                );
    }

    @Test
    void method_methodSignature() {
        Method method = injectParents(new Method(
                nodeIdGenerator.next(),
                new MethodSignature(
                        nodeIdGenerator.next(),
                        Visibility.NAMESPACE,
                        "method",
                        Collections.emptyList(),
                        Collections.emptyList(),
                        new TypeSpecSimple(nodeIdGenerator.next(), "Type", Collections.emptyList())
                ),
                Collections.emptyList()
        ));

        method.getContext().put(Relatives.class, new Relatives(method, new Relatives(parent())));

        visitor.visit(method);

        Assertions.assertThat(method.getSignature().getContext().get(SymbolTable.class))
                .get()
                .matches(st ->
                        st == method.getContext()
                                .get(SymbolTable.class)
                                .orElseThrow(() -> new IllegalStateException("no symbol table found"))
                                // signature's symbol table is the parent of the method's symbol table
                                // this is because the method's symbol table is the method's body symbol table
                                .parent()
                );
    }

    @Test
    void method_body() {
        Method method = injectParents(new Method(
                nodeIdGenerator.next(),
                new MethodSignature(
                        nodeIdGenerator.next(),
                        Visibility.NAMESPACE,
                        "method",
                        Collections.emptyList(),
                        Collections.emptyList(),
                        new TypeSpecSimple(nodeIdGenerator.next(), "Type", Collections.emptyList())
                ),
                Collections.singletonList(
                        new Statement(nodeIdGenerator.next(), new NoneValue(nodeIdGenerator.next()), true)
                )
        ));

        method.getContext().put(Relatives.class, new Relatives(method, new Relatives(parent())));

        visitor.visit(method);

        Assertions.assertThat(method.getStatements().get(0).getContext().get(SymbolTable.class))
                .get()
                .matches(st ->
                        st == method.getContext()
                                .get(SymbolTable.class)
                                .orElseThrow(() -> new IllegalStateException("no symbol table found"))
                );
    }

    @Test
    void parameter() {
        Node parent = parent();
        Parameter parameter = injectParents(new Parameter(
                nodeIdGenerator.next(),
                "name",
                new TypeSpecSimple(nodeIdGenerator.next(), "Type",
                        Collections.emptyList())
        ));

        parameter.getContext().put(Relatives.class, new Relatives(parameter, new Relatives(parent)));

        visitor.visit(parameter);

        Assertions.assertThat(parameter.getContext().get(SymbolTable.class))
                .get()
                .matches(st ->
                        st == parent.getContext()
                                .get(SymbolTable.class)
                                .orElseThrow(() -> new IllegalStateException("no symbol table found"))
                );
    }

    @Test
    void typeSpecIntersection() {
        Node parent = parent();
        TypeSpecIntersection typeSpec = new TypeSpecIntersection(
                nodeIdGenerator.next(),
                Collections.emptyList()
        );

        typeSpec.getContext().put(Relatives.class, new Relatives(typeSpec, new Relatives(parent)));

        visitor.visit(typeSpec);

        Assertions.assertThat(typeSpec.getContext().get(SymbolTable.class))
                .get()
                .matches(st ->
                        st == parent.getContext()
                                .get(SymbolTable.class)
                                .orElseThrow(() -> new IllegalStateException("no symbol table found"))
                );
    }

    @Test
    void typeSpecUnion() {
        Node parent = parent();
        TypeSpecUnion typeSpec = new TypeSpecUnion(
                nodeIdGenerator.next(),
                Collections.emptyList()
        );

        typeSpec.getContext().put(Relatives.class, new Relatives(typeSpec, new Relatives(parent)));

        visitor.visit(typeSpec);

        Assertions.assertThat(typeSpec.getContext().get(SymbolTable.class))
                .get()
                .matches(st ->
                        st == parent.getContext()
                                .get(SymbolTable.class)
                                .orElseThrow(() -> new IllegalStateException("no symbol table found"))
                );
    }

    @Test
    void typeSpecSimple() {
        Node parent = parent();
        TypeSpecSimple typeSpec = new TypeSpecSimple(
                nodeIdGenerator.next(),
                "Type",
                Collections.emptyList()
        );

        typeSpec.getContext().put(Relatives.class, new Relatives(typeSpec, new Relatives(parent)));

        visitor.visit(typeSpec);

        Assertions.assertThat(typeSpec.getContext().get(SymbolTable.class))
                .get()
                .matches(st ->
                        st == parent.getContext()
                                .get(SymbolTable.class)
                                .orElseThrow(() -> new IllegalStateException("no symbol table found"))
                );
    }

    @Test
    void typeSpecFunction() {
        Node parent = parent();
        TypeSpecFunction typeSpec = injectParents(new TypeSpecFunction(
                nodeIdGenerator.next(),
                Collections.emptyList(),
                new TypeSpecSimple(nodeIdGenerator.next(), "Type", Collections.emptyList())
        ));

        typeSpec.getContext().put(Relatives.class, new Relatives(typeSpec, new Relatives(parent)));

        visitor.visit(typeSpec);

        Assertions.assertThat(typeSpec.getContext().get(SymbolTable.class))
                .get()
                .matches(st ->
                        st == parent.getContext()
                                .get(SymbolTable.class)
                                .orElseThrow(() -> new IllegalStateException("no symbol table found"))
                );
    }

    @Test
    void typeSpecInferred() {
        Node parent = parent();
        TypeSpecInferred typeSpec = injectParents(new TypeSpecInferred(nodeIdGenerator.next()));

        typeSpec.getContext().put(Relatives.class, new Relatives(typeSpec, new Relatives(parent)));

        visitor.visit(typeSpec);

        Assertions.assertThat(typeSpec.getContext().get(SymbolTable.class))
                .get()
                .matches(st ->
                        st == parent.getContext()
                                .get(SymbolTable.class)
                                .orElseThrow(() -> new IllegalStateException("no symbol table found"))
                );
    }

    @Test
    void stringValue() {
        Node parent = parent();
        StringValue value = injectParents(new StringValue(
                nodeIdGenerator.next(),
                "value"
        ));

        value.getContext().put(Relatives.class, new Relatives(value, new Relatives(parent)));

        visitor.visit(value);

        Assertions.assertThat(value.getContext().get(SymbolTable.class))
                .get()
                .matches(st ->
                        st == parent.getContext()
                                .get(SymbolTable.class)
                                .orElseThrow(() -> new IllegalStateException("no symbol table found"))
                );
    }

    @Test
    void numberValue() {
        Node parent = parent();
        NumberValue value = injectParents(new NumberValue(
                nodeIdGenerator.next(),
                "1"
        ));

        value.getContext().put(Relatives.class, new Relatives(value, new Relatives(parent)));

        visitor.visit(value);

        Assertions.assertThat(value.getContext().get(SymbolTable.class))
                .get()
                .matches(st ->
                        st == parent.getContext()
                                .get(SymbolTable.class)
                                .orElseThrow(() -> new IllegalStateException("no symbol table found"))
                );
    }

    @Test
    void booleanValue() {
        Node parent = parent();
        BooleanValue value = injectParents(new BooleanValue(
                nodeIdGenerator.next(),
                true
        ));

        value.getContext().put(Relatives.class, new Relatives(value, new Relatives(parent)));

        visitor.visit(value);

        Assertions.assertThat(value.getContext().get(SymbolTable.class))
                .get()
                .matches(st ->
                        st == parent.getContext()
                                .get(SymbolTable.class)
                                .orElseThrow(() -> new IllegalStateException("no symbol table found"))
                );
    }

    @Test
    void noneValue() {
        Node parent = parent();
        NoneValue value = injectParents(new NoneValue(nodeIdGenerator.next()));

        value.getContext().put(Relatives.class, new Relatives(value, new Relatives(parent)));

        visitor.visit(value);

        Assertions.assertThat(value.getContext().get(SymbolTable.class))
                .get()
                .matches(st ->
                        st == parent.getContext()
                                .get(SymbolTable.class)
                                .orElseThrow(() -> new IllegalStateException("no symbol table found"))
                );
    }

    @Test
    void identifierValue() {
        Node parent = parent();
        IdentifierValue value = injectParents(new IdentifierValue(
                nodeIdGenerator.next(),
                "string"
        ));

        value.getContext().put(Relatives.class, new Relatives(value, new Relatives(parent)));

        visitor.visit(value);

        Assertions.assertThat(value.getContext().get(SymbolTable.class))
                .get()
                .matches(st ->
                        st == parent.getContext()
                                .get(SymbolTable.class)
                                .orElseThrow(() -> new IllegalStateException("no symbol table found"))
                );
    }

    @Test
    void newAssignmentValue() {
        Node parent = parent();
        NewAssignmentValue value = injectParents(new NewAssignmentValue(
                nodeIdGenerator.next(),
                "string",
                new TypeSpecSimple(nodeIdGenerator.next(), "Type", Collections.emptyList()),
                new NoneValue(nodeIdGenerator.next()),
                Mode.VAL
        ));

        value.getContext().put(Relatives.class, new Relatives(value, new Relatives(parent)));

        visitor.visit(value);

        Assertions.assertThat(value.getContext().get(SymbolTable.class))
                .get()
                .matches(st ->
                        st == parent.getContext()
                                .get(SymbolTable.class)
                                .orElseThrow(() -> new IllegalStateException("no symbol table found"))
                );
    }

    @Test
    void directAssignmentValue() {
        Node parent = parent();
        DirectAssignmentValue value = injectParents(new DirectAssignmentValue(
                nodeIdGenerator.next(),
                "string",
                new NoneValue(nodeIdGenerator.next())
        ));

        value.getContext().put(Relatives.class, new Relatives(value, new Relatives(parent)));

        visitor.visit(value);

        Assertions.assertThat(value.getContext().get(SymbolTable.class))
                .get()
                .matches(st ->
                        st == parent.getContext()
                                .get(SymbolTable.class)
                                .orElseThrow(() -> new IllegalStateException("no symbol table found"))
                );
    }

    @Test
    void indirectAssignmentValue() {
        Node parent = parent();
        IndirectAssignmentValue value = injectParents(new IndirectAssignmentValue(
                nodeIdGenerator.next(),
                new NoneValue(nodeIdGenerator.next()),
                "string",
                new NoneValue(nodeIdGenerator.next())
        ));

        value.getContext().put(Relatives.class, new Relatives(value, new Relatives(parent)));

        visitor.visit(value);

        Assertions.assertThat(value.getContext().get(SymbolTable.class))
                .get()
                .matches(st ->
                        st == parent.getContext()
                                .get(SymbolTable.class)
                                .orElseThrow(() -> new IllegalStateException("no symbol table found"))
                );
    }

    @Test
    void methodCallValue() {
        Node parent = parent();
        MethodCallValue value = injectParents(new MethodCallValue(
                nodeIdGenerator.next(),
                "methosName",
                Collections.emptyList(),
                Collections.emptyList()
        ));

        value.getContext().put(Relatives.class, new Relatives(value, new Relatives(parent)));

        visitor.visit(value);

        Assertions.assertThat(value.getContext().get(SymbolTable.class))
                .get()
                .matches(st ->
                        st == parent.getContext()
                                .get(SymbolTable.class)
                                .orElseThrow(() -> new IllegalStateException("no symbol table found"))
                );
    }

    @Test
    void nestedValue() {
        Node parent = parent();
        NestedValue value = injectParents(new NestedValue(
                nodeIdGenerator.next(),
                new NoneValue(nodeIdGenerator.next()),
                new NoneValue(nodeIdGenerator.next())
        ));

        value.getContext().put(Relatives.class, new Relatives(value, new Relatives(parent)));

        visitor.visit(value);

        Assertions.assertThat(value.getContext().get(SymbolTable.class))
                .get()
                .matches(st ->
                        st == parent.getContext()
                                .get(SymbolTable.class)
                                .orElseThrow(() -> new IllegalStateException("no symbol table found"))
                );
    }

    @Test
    void functionValue() {
        Node parent = parent();
        FunctionValue value = injectParents(new FunctionValue(
                nodeIdGenerator.next(),
                Collections.emptyList(), Collections.emptyList(),
                new TypeSpecSimple(nodeIdGenerator.next(), "type", Collections.emptyList()),
                Collections.emptyList()
        ));

        value.getContext().put(Relatives.class, new Relatives(value, new Relatives(parent)));

        visitor.visit(value);

        Assertions.assertThat(value.getContext().get(SymbolTable.class))
                .get()
                .matches(st ->
                        st.parent().parent() == parent.getContext()
                                .get(SymbolTable.class)
                                .orElseThrow(() -> new IllegalStateException("no symbol table found"))
                );
    }

    @Test
    void statement_method() {
        List<Statement> statements = Arrays.asList(
                new Statement(nodeIdGenerator.next(), new NoneValue(nodeIdGenerator.next()), false),
                new Statement(nodeIdGenerator.next(), new NoneValue(nodeIdGenerator.next()), false)
        );

        Method method = injectParents(new Method(
                nodeIdGenerator.next(),
                new MethodSignature(
                        nodeIdGenerator.next(),
                        Visibility.NAMESPACE,
                        "methodName",
                        Collections.emptyList(),
                        Collections.emptyList(),
                        new TypeSpecSimple(nodeIdGenerator.next(), "type", Collections.emptyList())
                ),
                statements
        ));

        method.getContext().put(Relatives.class, new Relatives(method, new Relatives(parent())));

        visitor.visit(method);

        Assertions.assertThat(method.getStatements().get(0).getContext().get(SymbolTable.class))
                .get()
                .matches(st ->
                        st == method.getContext()
                                .get(SymbolTable.class)
                                .orElseThrow(() -> new IllegalStateException("no symbol table found"))
                );
        Assertions.assertThat(method.getStatements().get(1).getContext().get(SymbolTable.class))
                .get()
                .matches(st ->
                        st == method.getContext()
                                .get(SymbolTable.class)
                                .orElseThrow(() -> new IllegalStateException("no symbol table found"))
                );
    }

    @Test
    void statement_functionValue() {
        List<Statement> statements = Arrays.asList(
                new Statement(nodeIdGenerator.next(), new NoneValue(nodeIdGenerator.next()), false),
                new Statement(nodeIdGenerator.next(), new NoneValue(nodeIdGenerator.next()), false)
        );

        FunctionValue functionValue = injectParents(new FunctionValue(
                nodeIdGenerator.next(),
                Collections.emptyList(),
                Collections.emptyList(),
                new TypeSpecSimple(nodeIdGenerator.next(), "type", Collections.emptyList()),
                statements
        ));

        functionValue.getContext().put(Relatives.class, new Relatives(functionValue, new Relatives(parent())));

        visitor.visit(functionValue);

        Assertions.assertThat(functionValue.getStatements().get(0).getContext().get(SymbolTable.class))
                .get()
                .matches(st ->
                        st == functionValue.getContext()
                                .get(SymbolTable.class)
                                .orElseThrow(() -> new IllegalStateException("no symbol table found"))
                );
        Assertions.assertThat(functionValue.getStatements().get(1).getContext().get(SymbolTable.class))
                .get()
                .matches(st ->
                        st == functionValue.getContext()
                                .get(SymbolTable.class)
                                .orElseThrow(() -> new IllegalStateException("no symbol table found"))
                );
    }

    @Test
    void full() throws IOException {
        Root root = new AST(
                SymbolTableInitializationVisitorTest.class.getResourceAsStream(
                        "/org/thoriumlang/compiler/ast/algorithms/symboltable/simple.th"
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
                                SymbolTableInitializationVisitorTest.class.getResourceAsStream(
                                        "/org/thoriumlang/compiler/ast/algorithms/symboltable/simple.tables"
                                )
                        )
                ).lines().collect(Collectors.joining("\n"))
        );
    }

    private Node parent() {
        return new Parent(nodeIdGenerator.next())
                .getContext()
                .put(SymbolTable.class, new DefaultSymbolTable())
                .getNode();
    }

    @SuppressWarnings("unchecked") // we're sure about what we return: it's the same object as what we get as input
    private <T extends Node> T injectParents(T node) {
        return (T) node.accept(new RelativesInjectionVisitor());
    }

    private static class Parent extends Node {
        Parent(NodeId nodeId) {
            super(nodeId);
        }

        @Override
        public <T> T accept(Visitor<? extends T> visitor) {
            return null;
        }
    }
}
