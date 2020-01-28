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
package org.thoriumlang.compiler.ast.visitor;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.antlr.ThoriumLexer;
import org.thoriumlang.compiler.antlr.ThoriumParser;
import org.thoriumlang.compiler.antlr4.RootVisitor;
import org.thoriumlang.compiler.ast.algorithms.NodesMatching;
import org.thoriumlang.compiler.ast.nodes.Attribute;
import org.thoriumlang.compiler.ast.nodes.Class;
import org.thoriumlang.compiler.ast.nodes.DirectAssignmentValue;
import org.thoriumlang.compiler.ast.context.Relatives;
import org.thoriumlang.compiler.ast.nodes.FunctionValue;
import org.thoriumlang.compiler.ast.nodes.IndirectAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.Method;
import org.thoriumlang.compiler.ast.nodes.MethodCallValue;
import org.thoriumlang.compiler.ast.nodes.MethodSignature;
import org.thoriumlang.compiler.ast.nodes.Mode;
import org.thoriumlang.compiler.ast.nodes.NestedValue;
import org.thoriumlang.compiler.ast.nodes.NewAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.Node;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.nodes.NoneValue;
import org.thoriumlang.compiler.ast.nodes.Parameter;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.ast.nodes.Statement;
import org.thoriumlang.compiler.ast.nodes.TopLevelNode;
import org.thoriumlang.compiler.ast.nodes.Type;
import org.thoriumlang.compiler.ast.nodes.TypeParameter;
import org.thoriumlang.compiler.ast.nodes.TypeSpec;
import org.thoriumlang.compiler.ast.nodes.TypeSpecFunction;
import org.thoriumlang.compiler.ast.nodes.TypeSpecIntersection;
import org.thoriumlang.compiler.ast.nodes.TypeSpecSimple;
import org.thoriumlang.compiler.ast.nodes.TypeSpecUnion;
import org.thoriumlang.compiler.ast.nodes.Use;
import org.thoriumlang.compiler.ast.nodes.Value;
import org.thoriumlang.compiler.ast.nodes.Visibility;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

class RelativesInjectionVisitorTest {
    private NodeIdGenerator nodeIdGenerator;
    private RelativesInjectionVisitor relativesInjectionVisitor;

    @BeforeEach
    void setup() {
        this.nodeIdGenerator = new NodeIdGenerator();
        this.relativesInjectionVisitor = new RelativesInjectionVisitor();
    }

    @Test
    void root_type() {
        Use use = new Use(nodeIdGenerator.next(), "use");
        TopLevelNode type = new Type(
                nodeIdGenerator.next(),
                Visibility.NAMESPACE,
                "TypeName",
                Collections.emptyList(),
                new TypeSpecSimple(nodeIdGenerator.next(), "Type", Collections.emptyList()),
                Collections.emptyList()
        );
        Root root = new Root(
                nodeIdGenerator.next(),
                "namespace",
                Collections.singletonList(use),
                type
        );

        relativesInjectionVisitor.visit(root);

        assertParent(use, root);
        assertParent(type, root);
    }

    private void assertParent(Node child, Node parent) {
        Assertions.assertThat(child.getContext().get(Relatives.class))
                .get()
                .matches(f -> f.parent().isPresent() && f.parent().get().node().getNodeId().equals(parent.getNodeId()));
    }

    @Test
    void root_class() {
        Use use = new Use(nodeIdGenerator.next(), "use");
        TopLevelNode clazz = new Class(
                nodeIdGenerator.next(),
                Visibility.NAMESPACE,
                "TypeName",
                Collections.emptyList(),
                new TypeSpecSimple(nodeIdGenerator.next(), "Type", Collections.emptyList()),
                Collections.emptyList(),
                Collections.emptyList()
        );
        Root root = new Root(
                nodeIdGenerator.next(),
                "namespace",
                Collections.singletonList(use),
                clazz
        );

        relativesInjectionVisitor.visit(root);

        assertParent(use, root);
        assertParent(clazz, root);
    }

    @Test
    void type() {
        TypeParameter typeParameter = new TypeParameter(nodeIdGenerator.next(), "TypeParameter");
        TypeSpec superType = new TypeSpecSimple(nodeIdGenerator.next(), "SuperType", Collections.emptyList());
        MethodSignature methodSignature = new MethodSignature(
                nodeIdGenerator.next(),
                Visibility.NAMESPACE,
                "methodName",
                Collections.emptyList(),
                Collections.emptyList(),
                new TypeSpecSimple(nodeIdGenerator.next(), "ReturnType", Collections.emptyList())
        );
        Type type = new Type(
                nodeIdGenerator.next(),
                Visibility.NAMESPACE,
                "TypeName",
                Collections.singletonList(typeParameter),
                superType,
                Collections.singletonList(methodSignature)
        );

        relativesInjectionVisitor.visit(type);

        assertParent(typeParameter, type);
        assertParent(superType, type);
        assertParent(methodSignature, type);
    }

    @Test
    void clazz() {
        TypeParameter typeParameter = new TypeParameter(nodeIdGenerator.next(), "TypeParameter");
        TypeSpec superType = new TypeSpecSimple(nodeIdGenerator.next(), "SuperType", Collections.emptyList());
        Method method = new Method(
                nodeIdGenerator.next(),
                new MethodSignature(
                        nodeIdGenerator.next(),
                        Visibility.NAMESPACE,
                        "methodName",
                        Collections.emptyList(),
                        Collections.emptyList(),
                        new TypeSpecSimple(nodeIdGenerator.next(), "ReturnType", Collections.emptyList())
                ),
                Collections.emptyList()
        );
        Attribute attribute = new Attribute(
                nodeIdGenerator.next(),
                "attributeName",
                new TypeSpecSimple(nodeIdGenerator.next(), "Type", Collections.emptyList()),
                new NoneValue(nodeIdGenerator.next()),
                Mode.VAR
        );
        Class clazz = new Class(
                nodeIdGenerator.next(),
                Visibility.NAMESPACE,
                "TypeName",
                Collections.singletonList(typeParameter),
                superType,
                Collections.singletonList(method),
                Collections.singletonList(attribute)
        );

        relativesInjectionVisitor.visit(clazz);

        assertParent(typeParameter, clazz);
        assertParent(superType, clazz);
        assertParent(method, clazz);
        assertParent(attribute, clazz);
    }

    @Test
    void methodSignature() {
        TypeParameter typeParameter = new TypeParameter(nodeIdGenerator.next(), "TP");
        Parameter parameter = new Parameter(
                nodeIdGenerator.next(),
                "parameterName",
                new TypeSpecSimple(nodeIdGenerator.next(), "Type", Collections.emptyList())
        );
        TypeSpec returnType = new TypeSpecSimple(nodeIdGenerator.next(), "Type", Collections.emptyList());
        MethodSignature methodSignature = new MethodSignature(
                nodeIdGenerator.next(),
                Visibility.NAMESPACE,
                "methodName",
                Collections.singletonList(typeParameter),
                Collections.singletonList(parameter),
                returnType
        );

        relativesInjectionVisitor.visit(methodSignature);

        assertParent(typeParameter, methodSignature);
        assertParent(parameter, methodSignature);
        assertParent(returnType, methodSignature);
    }

    @Test
    void method() {
        MethodSignature methodSignature = new MethodSignature(
                nodeIdGenerator.next(),
                Visibility.NAMESPACE,
                "methodName",
                Collections.emptyList(),
                Collections.emptyList(),
                new TypeSpecSimple(nodeIdGenerator.next(), "Type", Collections.emptyList())
        );
        Statement statement = new Statement(
                nodeIdGenerator.next(),
                new NoneValue(nodeIdGenerator.next()),
                true
        );
        Method method = new Method(
                nodeIdGenerator.next(),
                methodSignature,
                Collections.singletonList(statement)
        );

        relativesInjectionVisitor.visit(method);

        assertParent(methodSignature, method);
        assertParent(statement, method);
    }

    @Test
    void parameter() {
        TypeSpec type = new TypeSpecSimple(nodeIdGenerator.next(), "Type", Collections.emptyList());
        Parameter parameter = new Parameter(
                nodeIdGenerator.next(),
                "parameterName",
                type
        );

        relativesInjectionVisitor.visit(parameter);

        assertParent(type, parameter);
    }

    @Test
    void attribute() {
        TypeSpec type = new TypeSpecSimple(nodeIdGenerator.next(), "T", Collections.emptyList());
        Value value = new NoneValue(nodeIdGenerator.next());
        Attribute attribute = new Attribute(
                nodeIdGenerator.next(),
                "identifier",
                type,
                value,
                Mode.VAR
        );

        relativesInjectionVisitor.visit(attribute);

        assertParent(type, attribute);
        assertParent(value, attribute);
    }

    @Test
    void statement() {
        Value value = new NoneValue(nodeIdGenerator.next());
        Statement statement = new Statement(
                nodeIdGenerator.next(),
                value,
                false
        );

        relativesInjectionVisitor.visit(statement);

        assertParent(value, statement);
    }

    @Test
    void typeSpecSimple() {
        TypeSpec argument = new TypeSpecSimple(nodeIdGenerator.next(), "TA", Collections.emptyList());
        TypeSpecSimple typeSpecSimple = new TypeSpecSimple(
                nodeIdGenerator.next(),
                "T",
                Collections.singletonList(argument)
        );

        relativesInjectionVisitor.visit(typeSpecSimple);

        assertParent(argument, typeSpecSimple);
    }

    @Test
    void typeSpecUnion() {
        TypeSpec type = new TypeSpecSimple(nodeIdGenerator.next(), "TA", Collections.emptyList());
        TypeSpecUnion typeSpecUnion = new TypeSpecUnion(
                nodeIdGenerator.next(),
                Collections.singletonList(type)
        );

        relativesInjectionVisitor.visit(typeSpecUnion);

        assertParent(type, typeSpecUnion);
    }

    @Test
    void typeSpecIntersection() {
        TypeSpec type = new TypeSpecSimple(nodeIdGenerator.next(), "TA", Collections.emptyList());
        TypeSpecIntersection typeSpecIntersection = new TypeSpecIntersection(
                nodeIdGenerator.next(),
                Collections.singletonList(type)
        );

        relativesInjectionVisitor.visit(typeSpecIntersection);

        assertParent(type, typeSpecIntersection);
    }

    @Test
    void typeSpecFunction() {
        TypeSpec argument = new TypeSpecSimple(nodeIdGenerator.next(), "T1", Collections.emptyList());
        TypeSpec returnType = new TypeSpecSimple(nodeIdGenerator.next(), "T2", Collections.emptyList());
        TypeSpecFunction typeSpecFunction = new TypeSpecFunction(
                nodeIdGenerator.next(),
                Collections.singletonList(argument),
                returnType
        );

        relativesInjectionVisitor.visit(typeSpecFunction);

        assertParent(argument, typeSpecFunction);
        assertParent(returnType, typeSpecFunction);
    }

    @Test
    void newAssignmentValue() {
        TypeSpec typeSpec = new TypeSpecSimple(nodeIdGenerator.next(), "T1", Collections.emptyList());
        Value value = new NoneValue(nodeIdGenerator.next());
        NewAssignmentValue newAssignmentValue = new NewAssignmentValue(
                nodeIdGenerator.next(),
                "id",
                typeSpec,
                value,
                Mode.VAR
        );

        relativesInjectionVisitor.visit(newAssignmentValue);

        assertParent(typeSpec, newAssignmentValue);
        assertParent(value, newAssignmentValue);
    }

    @Test
    void indirectAssignmentValue() {
        Value value1 = new NoneValue(nodeIdGenerator.next());
        Value value2 = new NoneValue(nodeIdGenerator.next());
        IndirectAssignmentValue indirectAssignmentValue = new IndirectAssignmentValue(
                nodeIdGenerator.next(),
                value1,
                "id",
                value2
        );

        relativesInjectionVisitor.visit(indirectAssignmentValue);

        assertParent(value1, indirectAssignmentValue);
        assertParent(value2, indirectAssignmentValue);
    }

    @Test
    void directAssignmentValue() {
        Value value1 = new NoneValue(nodeIdGenerator.next());
        DirectAssignmentValue directAssignmentValue = new DirectAssignmentValue(
                nodeIdGenerator.next(),
                "id",
                value1
        );

        relativesInjectionVisitor.visit(directAssignmentValue);

        assertParent(value1, directAssignmentValue);
    }

    @Test
    void methodCallValue() {
        TypeSpec typeArgument = new TypeSpecSimple(nodeIdGenerator.next(), "T1", Collections.emptyList());
        Value value = new NoneValue(nodeIdGenerator.next());
        MethodCallValue methodCallValue = new MethodCallValue(
                nodeIdGenerator.next(),
                "methodName",
                Collections.singletonList(typeArgument),
                Collections.singletonList(value)
        );

        relativesInjectionVisitor.visit(methodCallValue);

        assertParent(typeArgument, methodCallValue);
        assertParent(value, methodCallValue);
    }

    @Test
    void nestedValue() {
        Value value1 = new NoneValue(nodeIdGenerator.next());
        Value value2 = new NoneValue(nodeIdGenerator.next());
        NestedValue nestedValue = new NestedValue(
                nodeIdGenerator.next(),
                value1,
                value2
        );

        relativesInjectionVisitor.visit(nestedValue);

        assertParent(value1, nestedValue);
        assertParent(value2, nestedValue);
    }

    @Test
    void functionValue() {
        TypeParameter typeParameter = new TypeParameter(nodeIdGenerator.next(), "TP");
        Parameter parameter = new Parameter(
                nodeIdGenerator.next(),
                "n",
                new TypeSpecSimple(nodeIdGenerator.next(), "Type", Collections.emptyList())
        );
        TypeSpec returnType = new TypeSpecSimple(nodeIdGenerator.next(), "Type", Collections.emptyList());
        Statement statement = new Statement(nodeIdGenerator.next(), new NoneValue(nodeIdGenerator.next()), true);
        FunctionValue functionValue = new FunctionValue(
                nodeIdGenerator.next(),
                Collections.singletonList(typeParameter),
                Collections.singletonList(parameter),
                returnType,
                Collections.singletonList(statement)
        );

        relativesInjectionVisitor.visit(functionValue);

        assertParent(typeParameter, functionValue);
        assertParent(parameter, functionValue);
        assertParent(returnType, functionValue);
        assertParent(statement, functionValue);
    }

    @Test
    void allNodesHaveParent_class() throws IOException {
        Root root = new RootVisitor(nodeIdGenerator, "namespace").visit(
                new ThoriumParser(
                        new CommonTokenStream(
                                new ThoriumLexer(
                                        CharStreams.fromStream(
                                                RelativesInjectionVisitor.class.getResourceAsStream(
                                                        "/org/thoriumlang/compiler/ast/algorithms/typechecking/simple.th"
                                                )
                                        )
                                )
                        )
                ).root()
        );

        relativesInjectionVisitor.visit(root);

        List<Node> missingParents = new NodesMatching(
                n -> !n.getContext().get(Relatives.class).isPresent() && n != root
        ).visit(root);

        Assertions.assertThat(missingParents).isEmpty();
    }

    @Test
    void familyTree_isConsistent() throws IOException {
        Root root = new RootVisitor(nodeIdGenerator, "namespace").visit(
                new ThoriumParser(
                        new CommonTokenStream(
                                new ThoriumLexer(
                                        CharStreams.fromStream(
                                                RelativesInjectionVisitor.class.getResourceAsStream(
                                                        "/org/thoriumlang/compiler/ast/algorithms/typechecking/simple.th"
                                                )
                                        )
                                )
                        )
                ).root()
        );

        relativesInjectionVisitor.visit(root);

        Statement someStatement = ((Class) root.getTopLevelNode()).getMethods().get(0).getStatements().get(0);
        Assertions.assertThat(
                parent(
                        parent(
                                parent(
                                        someStatement)
                        )
                )
        ).isSameAs(root);
    }

    private Node parent(Node node) {
        return node.getContext()
                .get(Relatives.class)
                .orElseThrow(() -> new IllegalStateException("No family found for node " + node))
                .parent()
                .orElseThrow(() -> new IllegalStateException("No parent found for node" + node))
                .node();
    }
}
