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
package org.thoriumlang.compiler.ast.visitor;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import java.util.Collections;

class CopyVisitorTest {
    private NodeIdGenerator nodeIdGenerator;

    @BeforeEach
    void setup() {
        this.nodeIdGenerator = new NodeIdGenerator();
    }

    @Test
    void visitUse() {
        Node node = new Use(nodeIdGenerator.next(), "from", "to")
                .getContext()
                .put(Object.class, new Object())
                .getNode();

        makeAssertions(node);
    }

    private void makeAssertions(Node node) {
        Assertions.assertThat(node.accept(visitor()))
                .isEqualTo(node)
                .isNotSameAs(node)
                .extracting(Node::getContext)
                .extracting(c -> c.get(Object.class))
                .isEqualTo(node.getContext().get(Object.class));
    }

    @Test
    void visitRoot_type() {
        Node node = new Root(
                nodeIdGenerator.next(),
                "namespace",
                Collections.emptyList(), // FIXME
                new Type(
                        nodeIdGenerator.next(),
                        Visibility.PRIVATE,
                        "name",
                        Collections.emptyList(),
                        new TypeSpecSimple(
                                nodeIdGenerator.next(),
                                "Object",
                                Collections.emptyList()
                        ),
                        Collections.emptyList()
                )
        )
                .getContext()
                .put(Object.class, new Object())
                .getNode();

        makeAssertions(node);
    }


    @Test
    void visitRoot_clazz() {
        Node node = new Root(
                nodeIdGenerator.next(),
                "namespace",
                Collections.emptyList(), // FIXME
                new Class(
                        nodeIdGenerator.next(),
                        Visibility.PRIVATE,
                        "name",
                        Collections.emptyList(),
                        new TypeSpecSimple(
                                nodeIdGenerator.next(),
                                "Object",
                                Collections.emptyList()
                        ),
                        Collections.emptyList(),
                        Collections.emptyList()
                )
        )
                .getContext()
                .put(Object.class, new Object())
                .getNode();

        makeAssertions(node);
    }

    @Test
    void visitType() {
        Node node = new Type(
                nodeIdGenerator.next(),
                Visibility.NAMESPACE,
                "name",
                Collections.singletonList(new TypeParameter(nodeIdGenerator.next(), "T")),
                new TypeSpecSimple(
                        nodeIdGenerator.next(),
                        "Object",
                        Collections.emptyList()
                ),
                Collections.emptyList()
        )
                .getContext()
                .put(Object.class, new Object())
                .getNode();

        makeAssertions(node);
    }

    @Test
    void visitClass() {
        Node node = new Class(
                nodeIdGenerator.next(),
                Visibility.NAMESPACE,
                "name",
                Collections.singletonList(new TypeParameter(nodeIdGenerator.next(), "T")),
                new TypeSpecSimple(
                        nodeIdGenerator.next(),
                        "Object",
                        Collections.emptyList()
                ),
                Collections.singletonList(
                        new Method(
                                nodeIdGenerator.next(),
                                new MethodSignature(
                                        nodeIdGenerator.next(),
                                        Visibility.NAMESPACE,
                                        "method",
                                        Collections.emptyList(),
                                        Collections.emptyList(),
                                        new TypeSpecSimple(
                                                nodeIdGenerator.next(),
                                                "None",
                                                Collections.emptyList()
                                        )
                                ),
                                Collections.singletonList(
                                        new Statement(
                                                nodeIdGenerator.next(),
                                                new BooleanValue(nodeIdGenerator.next(), true),
                                                false
                                        )
                                )
                        )
                ),
                Collections.singletonList(
                        new Attribute(
                                nodeIdGenerator.next(),
                                "attribute",
                                new TypeSpecSimple(
                                        nodeIdGenerator.next(),
                                        "None",
                                        Collections.emptyList()
                                ),
                                new NoneValue(nodeIdGenerator.next()),
                                Mode.VAL
                        )
                )
        )
                .getContext()
                .put(Object.class, new Object())
                .getNode();

        makeAssertions(node);
    }

    @Test
    void visitTypeIntersection() {
        Node node = new TypeSpecIntersection(
                nodeIdGenerator.next(),
                Collections.singletonList(
                        new TypeSpecSimple(
                                nodeIdGenerator.next(),
                                "type",
                                Collections.emptyList()
                        )
                )
        )
                .getContext()
                .put(Object.class, new Object())
                .getNode();

        makeAssertions(node);
    }

    @Test
    void visitTypeUnion() {
        Node node = new TypeSpecUnion(
                nodeIdGenerator.next(),
                Collections.singletonList(
                        new TypeSpecSimple(
                                nodeIdGenerator.next(),
                                "type",
                                Collections.emptyList()
                        )
                )
        )
                .getContext()
                .put(Object.class, new Object())
                .getNode();

        makeAssertions(node);
    }

    @Test
    void visitTypeSingle() {
        Node node = new TypeSpecSimple(
                nodeIdGenerator.next(),
                "type",
                Collections.emptyList()
        )
                .getContext()
                .put(Object.class, new Object())
                .getNode();

        makeAssertions(node);
    }

    @Test
    void visitTypeFunction() {
        Node node = new TypeSpecFunction(
                nodeIdGenerator.next(),
                Collections.singletonList(
                        new TypeSpecSimple(
                                nodeIdGenerator.next(),
                                "None",
                                Collections.emptyList()
                        )
                ),
                new TypeSpecSimple(
                        nodeIdGenerator.next(),
                        "Object",
                        Collections.emptyList()
                )
        )
                .getContext()
                .put(Object.class, new Object())
                .getNode();

        makeAssertions(node);
    }

    @Test
    void visitTypeInferred() {
        Node node = new TypeSpecInferred(nodeIdGenerator.next())
                .getContext()
                .put(Object.class, new Object())
                .getNode();

        makeAssertions(node);
    }

    @Test
    void visitMethodSignature() {
        Node node = new MethodSignature(
                nodeIdGenerator.next(),
                Visibility.PRIVATE,
                "name",
                Collections.emptyList(),
                Collections.emptyList(),
                new TypeSpecSimple(
                        nodeIdGenerator.next(),
                        "type",
                        Collections.emptyList()
                )
        )
                .getContext()
                .put(Object.class, new Object())
                .getNode();

        makeAssertions(node);
    }

    @Test
    void visitParameter() {
        Node node = new Parameter(
                nodeIdGenerator.next(),
                "name",
                new TypeSpecSimple(
                        nodeIdGenerator.next(),
                        "type",
                        Collections.emptyList()
                )
        )
                .getContext()
                .put(Object.class, new Object())
                .getNode();

        makeAssertions(node);
    }

    @Test
    void visitTypeParameter() {
        Node node = new TypeParameter(nodeIdGenerator.next(), "name")
                .getContext()
                .put(Object.class, new Object())
                .getNode();

        makeAssertions(node);
    }

    @Test
    void visitStringValue() {
        Node node = new StringValue(nodeIdGenerator.next(), "value")
                .getContext()
                .put(Object.class, new Object())
                .getNode();

        makeAssertions(node);
    }

    @Test
    void visitNumberValue() {
        Node node = new NumberValue(nodeIdGenerator.next(), "1")
                .getContext()
                .put(Object.class, new Object())
                .getNode();

        makeAssertions(node);
    }

    @Test
    void visitBooleanValue_true() {
        Node node = new BooleanValue(nodeIdGenerator.next(), true)
                .getContext()
                .put(Object.class, new Object())
                .getNode();

        makeAssertions(node);
    }

    @Test
    void visitBooleanValue_false() {
        Node node = new BooleanValue(nodeIdGenerator.next(), false)
                .getContext()
                .put(Object.class, new Object())
                .getNode();

        makeAssertions(node);
    }

    @Test
    void visitNoneValue() {
        Node node = new NoneValue(nodeIdGenerator.next())
                .getContext()
                .put(Object.class, new Object())
                .getNode();

        makeAssertions(node);
    }


    @Test
    void visitIdentifierValue() {
        Node node = new IdentifierValue(nodeIdGenerator.next(), "id")
                .getContext()
                .put(Object.class, new Object())
                .getNode();

        makeAssertions(node);
    }

    @Test
    void visitAssignmentValue() {
        Node node = new NewAssignmentValue(
                nodeIdGenerator.next(),
                "identifier",
                new TypeSpecSimple(nodeIdGenerator.next(), "T", Collections.emptyList()),
                new NoneValue(nodeIdGenerator.next()),
                Mode.VAR
        )
                .getContext()
                .put(Object.class, new Object())
                .getNode();

        makeAssertions(node);
    }

    @Test
    void visitIndirectAssignmentValue() {
        Node node = new IndirectAssignmentValue(
                nodeIdGenerator.next(),
                new NoneValue(nodeIdGenerator.next()),
                "identifier",
                new NoneValue(nodeIdGenerator.next())
        )
                .getContext()
                .put(Object.class, new Object())
                .getNode();

        makeAssertions(node);
    }

    @Test
    void visitDirectAssignmentValue() {
        Node node = new DirectAssignmentValue(
                nodeIdGenerator.next(),
                "identifier",
                new NoneValue(nodeIdGenerator.next())
        )
                .getContext()
                .put(Object.class, new Object())
                .getNode();

        makeAssertions(node);
    }

    @Test
    void visitMethodCallValue() {
        Node node = new MethodCallValue(
                nodeIdGenerator.next(),
                "identifier",
                Collections.emptyList(),
                Collections.emptyList()
        )
                .getContext()
                .put(Object.class, new Object())
                .getNode();

        makeAssertions(node);
    }

    @Test
    void visitNestedValue() {
        Node node = new NestedValue(
                nodeIdGenerator.next(),
                new BooleanValue(nodeIdGenerator.next(), true),
                new BooleanValue(nodeIdGenerator.next(), false)
        )
                .getContext()
                .put(Object.class, new Object())
                .getNode();

        makeAssertions(node);
    }

    @Test
    void visitFunctionValue() {
        Node node = new FunctionValue(
                nodeIdGenerator.next(),
                Collections.emptyList(),
                Collections.emptyList(),
                new TypeSpecSimple(nodeIdGenerator.next(), "None", Collections.emptyList()),
                Collections.emptyList()
        )
                .getContext()
                .put(Object.class, new Object())
                .getNode();

        makeAssertions(node);
    }

    @Test
    void visitStatement() {
        Node node = new Statement(
                nodeIdGenerator.next(),
                new BooleanValue(nodeIdGenerator.next(), true),
                false
        )
                .getContext()
                .put(Object.class, new Object())
                .getNode();

        makeAssertions(node);
    }

    @Test
    void visitMethod() {
        Node node = new Method(
                nodeIdGenerator.next(),
                new MethodSignature(
                        nodeIdGenerator.next(),
                        Visibility.NAMESPACE,
                        "method",
                        Collections.emptyList(),
                        Collections.emptyList(),
                        new TypeSpecSimple(
                                nodeIdGenerator.next(),
                                "None",
                                Collections.emptyList()
                        )
                ),
                Collections.singletonList(new Statement(
                        nodeIdGenerator.next(),
                        new BooleanValue(nodeIdGenerator.next(), true),
                        false
                ))
        )
                .getContext()
                .put(Object.class, new Object())
                .getNode();

        makeAssertions(node);
    }

    @Test
    void visitAttribute() {
        Node node = new Attribute(
                nodeIdGenerator.next(),
                "identifier",
                new TypeSpecSimple(
                        nodeIdGenerator.next(),
                        "None",
                        Collections.emptyList()
                ),
                new NoneValue(nodeIdGenerator.next()),
                Mode.VAL
        )
                .getContext()
                .put(Object.class, new Object())
                .getNode();

        makeAssertions(node);
    }

    private CopyVisitor visitor() {
        return new CopyVisitor() {
        };
    }
}
