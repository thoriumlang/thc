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

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.ast.nodes.Attribute;
import org.thoriumlang.compiler.ast.nodes.BooleanValue;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// import org.thoriumlang.compiler.ast.nodes.Class;

class FlatMapVisitorTest {
    private NodeIdGenerator nodeIdGenerator;

    @BeforeEach
    void setup() {
        this.nodeIdGenerator = new NodeIdGenerator();
    }

    @Test
    void use() {
        Node node = new Use(nodeIdGenerator.next(), "from", "to");

        List<Class> result = node.accept(visitor());

        Assertions.assertThat(result)
                .containsExactly(Use.class);
    }

    @Test
    void stringValue() {
        Node node = new StringValue(nodeIdGenerator.next(), "string");

        List<Class> result = node.accept(visitor());

        Assertions.assertThat(result)
                .containsExactly(StringValue.class);
    }

    @Test
    void numberValue() {
        Node node = new NumberValue(nodeIdGenerator.next(), "1234");

        List<Class> result = node.accept(visitor());

        Assertions.assertThat(result)
                .containsExactly(NumberValue.class);
    }

    @Test
    void booleanValue() {
        Node node = new BooleanValue(nodeIdGenerator.next(), true);

        List<Class> result = node.accept(visitor());

        Assertions.assertThat(result)
                .containsExactly(BooleanValue.class);
    }

    @Test
    void noneValue() {
        Node node = new NoneValue(nodeIdGenerator.next());

        List<Class> result = node.accept(visitor());

        Assertions.assertThat(result)
                .containsExactly(NoneValue.class);
    }

    @Test
    void nestedValue() {
        Node node = new NestedValue(
                nodeIdGenerator.next(),
                new NoneValue(nodeIdGenerator.next()),
                new StringValue(nodeIdGenerator.next(), "string")
        );

        List<Class> result = node.accept(visitor());

        Assertions.assertThat(result)
                .containsExactly(
                        NestedValue.class,
                        NoneValue.class,
                        StringValue.class
                );
    }

    @Test
    void identifierValue() {
        Node node = new IdentifierValue(nodeIdGenerator.next(), "value");

        List<Class> result = node.accept(visitor());

        Assertions.assertThat(result)
                .containsExactly(IdentifierValue.class);
    }

    @Test
    void typeSpecInferred() {
        Node node = new TypeSpecInferred(nodeIdGenerator.next());

        List<Class> result = node.accept(visitor());

        Assertions.assertThat(result)
                .containsExactly(TypeSpecInferred.class);
    }

    @Test
    void typeParameter() {
        Node node = new TypeParameter(nodeIdGenerator.next(), "name");

        List<Class> result = node.accept(visitor());

        Assertions.assertThat(result)
                .containsExactly(TypeParameter.class);
    }

    @Test
    void typeSpecSimple() {
        Node node = new TypeSpecSimple(
                nodeIdGenerator.next(),
                "type",
                Collections.singletonList(new TypeSpecSimple(
                        nodeIdGenerator.next(), "type2", Collections.emptyList()
                ))
        );

        List<Class> result = node.accept(visitor());

        Assertions.assertThat(result)
                .containsExactly(
                        TypeSpecSimple.class,
                        TypeSpecSimple.class
                );
    }

    @Test
    void typeSpecIntersection() {
        Node node = new TypeSpecIntersection(
                nodeIdGenerator.next(),
                Arrays.asList(
                        new TypeSpecSimple(
                                nodeIdGenerator.next(), "type", Collections.emptyList()
                        ),
                        new TypeSpecInferred(nodeIdGenerator.next())
                )
        );

        List<Class> result = node.accept(visitor());

        Assertions.assertThat(result)
                .containsExactly(
                        TypeSpecIntersection.class,
                        TypeSpecSimple.class,
                        TypeSpecInferred.class
                );
    }

    @Test
    void typeSpecUnion() {
        Node node = new TypeSpecUnion(
                nodeIdGenerator.next(),
                Arrays.asList(
                        new TypeSpecSimple(
                                nodeIdGenerator.next(), "type", Collections.emptyList()
                        ),
                        new TypeSpecInferred(nodeIdGenerator.next())
                )
        );

        List<Class> result = node.accept(visitor());

        Assertions.assertThat(result)
                .containsExactly(
                        TypeSpecUnion.class,
                        TypeSpecSimple.class,
                        TypeSpecInferred.class
                );
    }

    @Test
    void typeSpecFunction() {
        Node node = new TypeSpecFunction(
                nodeIdGenerator.next(),
                Collections.singletonList(
                        new TypeSpecSimple(
                                nodeIdGenerator.next(), "type", Collections.emptyList()
                        )
                ),
                new TypeSpecInferred(nodeIdGenerator.next())
        );

        List<Class> result = node.accept(visitor());

        Assertions.assertThat(result)
                .containsExactly(
                        TypeSpecFunction.class,
                        TypeSpecSimple.class,
                        TypeSpecInferred.class
                );
    }


    @Test
    void parameter() {
        Node node = new Parameter(
                nodeIdGenerator.next(),
                "parameter",
                new TypeSpecInferred(nodeIdGenerator.next())
        );

        List<Class> result = node.accept(visitor());

        Assertions.assertThat(result)
                .containsExactly(
                        Parameter.class,
                        TypeSpecInferred.class
                );
    }

    @Test
    void statement() {
        Node node = new Statement(
                nodeIdGenerator.next(),
                new NoneValue(nodeIdGenerator.next()),
                false
        );

        List<Class> result = node.accept(visitor());

        Assertions.assertThat(result)
                .containsExactly(
                        Statement.class,
                        NoneValue.class
                );
    }

    @Test
    void attribute() {
        Node node = new Attribute(
                nodeIdGenerator.next(),
                "id",
                new TypeSpecInferred(nodeIdGenerator.next()),
                new NoneValue(nodeIdGenerator.next()),
                Mode.VAL
        );

        List<Class> result = node.accept(visitor());

        Assertions.assertThat(result)
                .containsExactly(
                        Attribute.class,
                        TypeSpecInferred.class,
                        NoneValue.class
                );
    }

    @Test
    void methodSignature() {
        Node node = new MethodSignature(
                nodeIdGenerator.next(),
                Visibility.NAMESPACE,
                "name",
                Collections.singletonList(new TypeParameter(nodeIdGenerator.next(), "typeParam")),
                Collections.singletonList(new Parameter(
                        nodeIdGenerator.next(),
                        "param",
                        new TypeSpecInferred(nodeIdGenerator.next())
                )),
                new TypeSpecSimple(nodeIdGenerator.next(), "type", Collections.emptyList())
        );

        List<Class> result = node.accept(visitor());

        Assertions.assertThat(result)
                .containsExactly(
                        MethodSignature.class,
                        TypeParameter.class,
                        Parameter.class,
                        TypeSpecInferred.class,
                        TypeSpecSimple.class
                );
    }

    @Test
    void methodCallValue() {
        Node node = new MethodCallValue(
                nodeIdGenerator.next(),
                "methodName",
                Collections.singletonList(new TypeSpecInferred(nodeIdGenerator.next())),
                Collections.singletonList(new NoneValue(nodeIdGenerator.next()))
        );

        List<Class> result = node.accept(visitor());

        Assertions.assertThat(result)
                .containsExactly(
                        MethodCallValue.class,
                        TypeSpecInferred.class,
                        NoneValue.class
                );
    }

    @Test
    void functionValue() {
        Node node = new FunctionValue(
                nodeIdGenerator.next(),
                Collections.singletonList(new TypeParameter(nodeIdGenerator.next(), "typeParameter")),
                Collections.singletonList(new Parameter(
                        nodeIdGenerator.next(),
                        "name",
                        new TypeSpecInferred(nodeIdGenerator.next())
                )),
                new TypeSpecSimple(nodeIdGenerator.next(), "type", Collections.emptyList()),
                Collections.singletonList(
                        new Statement(nodeIdGenerator.next(), new NoneValue(nodeIdGenerator.next()), false)
                )
        );

        List<Class> result = node.accept(visitor());

        Assertions.assertThat(result)
                .containsExactly(
                        FunctionValue.class,
                        TypeParameter.class,
                        Parameter.class,
                        TypeSpecInferred.class,
                        TypeSpecSimple.class,
                        Statement.class,
                        NoneValue.class
                );
    }

    @Test
    void method() {
        Node node = new Method(
                nodeIdGenerator.next(),
                new MethodSignature(
                        nodeIdGenerator.next(),
                        Visibility.NAMESPACE,
                        "name",
                        Collections.singletonList(new TypeParameter(nodeIdGenerator.next(), "typeParam")),
                        Collections.singletonList(new Parameter(
                                nodeIdGenerator.next(),
                                "param",
                                new TypeSpecInferred(nodeIdGenerator.next())
                        )),
                        new TypeSpecSimple(nodeIdGenerator.next(), "type", Collections.emptyList())
                ),
                Collections.singletonList(
                        new Statement(nodeIdGenerator.next(), new NoneValue(nodeIdGenerator.next()), false)
                )
        );

        List<Class> result = node.accept(visitor());

        Assertions.assertThat(result)
                .containsExactly(
                        Method.class,
                        MethodSignature.class,
                        TypeParameter.class,
                        Parameter.class,
                        TypeSpecInferred.class,
                        TypeSpecSimple.class,
                        Statement.class,
                        NoneValue.class
                );
    }

    @Test
    void newAssignmentValue() {
        Node node = new NewAssignmentValue(
                nodeIdGenerator.next(),
                "identifier",
                new TypeSpecInferred(nodeIdGenerator.next()),
                new NoneValue(nodeIdGenerator.next()),
                Mode.VAL
        );

        List<Class> result = node.accept(visitor());

        Assertions.assertThat(result)
                .containsExactly(
                        NewAssignmentValue.class,
                        TypeSpecInferred.class,
                        NoneValue.class
                );
    }

    @Test
    void directAssignmentValue() {
        Node node = new DirectAssignmentValue(
                nodeIdGenerator.next(),
                "identifier",
                new NoneValue(nodeIdGenerator.next())
        );

        List<Class> result = node.accept(visitor());

        Assertions.assertThat(result)
                .containsExactly(
                        DirectAssignmentValue.class,
                        NoneValue.class
                );
    }

    @Test
    void indirectAssignmentValue() {
        Node node = new IndirectAssignmentValue(
                nodeIdGenerator.next(),
                new IdentifierValue(nodeIdGenerator.next(), "identifier"),
                "identifier",
                new NoneValue(nodeIdGenerator.next())
        );

        List<Class> result = node.accept(visitor());

        Assertions.assertThat(result)
                .containsExactly(
                        IndirectAssignmentValue.class,
                        IdentifierValue.class,
                        NoneValue.class
                );
    }

    @Test
    void type() {
        Node node = new Type(
                nodeIdGenerator.next(),
                Visibility.NAMESPACE,
                "Type",
                Collections.singletonList(new TypeParameter(nodeIdGenerator.next(), "name")),
                new TypeSpecInferred(nodeIdGenerator.next()),
                Collections.singletonList(new MethodSignature(
                        nodeIdGenerator.next(),
                        Visibility.NAMESPACE,
                        "name",
                        Collections.singletonList(new TypeParameter(
                                nodeIdGenerator.next(),
                                "name"
                        )),
                        Collections.singletonList(new Parameter(
                                nodeIdGenerator.next(),
                                "name",
                                new TypeSpecInferred(nodeIdGenerator.next())
                        )),
                        new TypeSpecInferred(nodeIdGenerator.next())
                ))
        );

        List<Class> result = node.accept(visitor());

        Assertions.assertThat(result)
                .containsExactly(
                        Type.class,
                        TypeParameter.class,
                        TypeSpecInferred.class,
                        MethodSignature.class,
                        TypeParameter.class,
                        Parameter.class,
                        TypeSpecInferred.class,
                        TypeSpecInferred.class
                );
    }

    @Test
    void clazz() {
        Node node = new org.thoriumlang.compiler.ast.nodes.Class(
                nodeIdGenerator.next(),
                Visibility.NAMESPACE,
                "Type",
                Collections.singletonList(new TypeParameter(nodeIdGenerator.next(), "name")),
                new TypeSpecInferred(nodeIdGenerator.next()),
                Collections.singletonList(new Method(
                        nodeIdGenerator.next(),
                        new MethodSignature(
                                nodeIdGenerator.next(),
                                Visibility.NAMESPACE,
                                "name",
                                Collections.singletonList(new TypeParameter(
                                        nodeIdGenerator.next(),
                                        "name"
                                )),
                                Collections.singletonList(new Parameter(
                                        nodeIdGenerator.next(),
                                        "name",
                                        new TypeSpecInferred(nodeIdGenerator.next())
                                )),
                                new TypeSpecInferred(nodeIdGenerator.next())
                        ),
                        Collections.emptyList()
                )),
                Collections.singletonList(new Attribute(
                        nodeIdGenerator.next(),
                        "identifier",
                        new TypeSpecInferred(nodeIdGenerator.next()),
                        new NoneValue(nodeIdGenerator.next()),
                        Mode.VAL
                ))
        );

        List<Class> result = node.accept(visitor());

        Assertions.assertThat(result)
                .containsExactly(
                        org.thoriumlang.compiler.ast.nodes.Class.class,
                        TypeParameter.class,
                        TypeSpecInferred.class,
                        Method.class,
                        MethodSignature.class,
                        TypeParameter.class,
                        Parameter.class,
                        TypeSpecInferred.class,
                        TypeSpecInferred.class,
                        Attribute.class,
                        TypeSpecInferred.class,
                        NoneValue.class
                );
    }

    @Test
    void root() {
        Node node = new Root(
                nodeIdGenerator.next(),
                "namespace",
                Collections.singletonList(new Use(nodeIdGenerator.next(), "from", "to")),
                new Type(
                        nodeIdGenerator.next(),
                        Visibility.NAMESPACE,
                        "Type",
                        Collections.emptyList(),
                        new TypeSpecInferred(nodeIdGenerator.next()),
                        Collections.emptyList()
                )
        );

        List<Class> result = node.accept(visitor());

        Assertions.assertThat(result)
                .containsExactly(
                        Root.class,
                        Use.class,
                        Type.class,
                        TypeSpecInferred.class
                );
    }

    private FlatMapVisitor<Class> visitor() {
        return new FlatMapVisitor<>(node -> Collections.singletonList(node.getClass()));
    }
}
