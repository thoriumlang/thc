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
import org.thoriumlang.compiler.ast.nodes.FunctionValue;
import org.thoriumlang.compiler.ast.nodes.IdentifierValue;
import org.thoriumlang.compiler.ast.nodes.IndirectAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.Method;
import org.thoriumlang.compiler.ast.nodes.MethodCallValue;
import org.thoriumlang.compiler.ast.nodes.MethodSignature;
import org.thoriumlang.compiler.ast.nodes.Mode;
import org.thoriumlang.compiler.ast.nodes.NestedValue;
import org.thoriumlang.compiler.ast.nodes.NewAssignmentValue;
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
import org.thoriumlang.compiler.ast.nodes.Value;
import org.thoriumlang.compiler.ast.nodes.Visibility;

import java.util.Collections;

class IdentityVisitorTest {
    private NodeIdGenerator nodeIdGenerator;

    @BeforeEach
    void setup() {
        this.nodeIdGenerator = new NodeIdGenerator();
    }

    @Test
    void visitRoot_type() {
        Root root = new Root(
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
        );
        Assertions.assertThat(root.accept(visitor()))
                .isSameAs(root);
    }

    @Test
    void visitRoot_clazz() {
        Root root = new Root(
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
        );
        Assertions.assertThat(root.accept(visitor()))
                .isSameAs(root);
    }

    @Test
    void visitType() {
        Type type = new Type(
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
        );
        Assertions.assertThat(type.accept(visitor()))
                .isSameAs(type);
    }

    @Test
    void visitClass() {
        Class clazz = new Class(
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
                                Mode.VAR
                        )
                )
        );
        Assertions.assertThat(clazz.accept(visitor()))
                .isSameAs(clazz);
    }

    @Test
    void visitTypeIntersection() {
        TypeSpecIntersection typeSpec = new TypeSpecIntersection(
                nodeIdGenerator.next(),
                Collections.singletonList(
                        new TypeSpecSimple(
                                nodeIdGenerator.next(),
                                "type",
                                Collections.emptyList()
                        )
                )
        );
        Assertions.assertThat(typeSpec.accept(visitor()))
                .isSameAs(typeSpec);
    }

    @Test
    void visitTypeUnion() {
        TypeSpecUnion typeSpec = new TypeSpecUnion(
                nodeIdGenerator.next(),
                Collections.singletonList(
                        new TypeSpecSimple(
                                nodeIdGenerator.next(),
                                "type",
                                Collections.emptyList()
                        )
                )
        );
        Assertions.assertThat(typeSpec.accept(visitor()))
                .isSameAs(typeSpec);
    }

    @Test
    void visitTypeSingle() {
        TypeSpecSimple typeSpec = new TypeSpecSimple(
                nodeIdGenerator.next(),
                "type",
                Collections.emptyList()
        );
        Assertions.assertThat(typeSpec.accept(visitor()))
                .isSameAs(typeSpec);
    }

    @Test
    void visitTypeFunction() {
        TypeSpecFunction typeSpec = new TypeSpecFunction(
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
        );
        Assertions.assertThat(typeSpec.accept(visitor()))
                .isSameAs(typeSpec);
    }

    @Test
    void visitTypeInferred() {
        TypeSpecInferred typeSpec = new TypeSpecInferred(nodeIdGenerator.next());
        Assertions.assertThat(typeSpec.accept(visitor()))
                .isSameAs(typeSpec);
    }

    @Test
    void visitMethodSignature() {
        MethodSignature methodSignature = new MethodSignature(
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
        );
        Assertions.assertThat(methodSignature.accept(visitor()))
                .isSameAs(methodSignature);
    }

    @Test
    void visitParameter() {
        Parameter parameter = new Parameter(
                nodeIdGenerator.next(),
                "name",
                new TypeSpecSimple(
                        nodeIdGenerator.next(),
                        "type",
                        Collections.emptyList()
                )
        );
        Assertions.assertThat(parameter.accept(visitor()))
                .isSameAs(parameter);
    }

    @Test
    void visitTypeParameter() {
        TypeParameter parameter = new TypeParameter(nodeIdGenerator.next(), "name");
        Assertions.assertThat(parameter.accept(visitor()))
                .isSameAs(parameter);
    }

    @Test
    void visitStringValue() {
        Value value = new StringValue(nodeIdGenerator.next(), "value");
        Assertions.assertThat(value.accept(visitor()))
                .isSameAs(value);
    }

    @Test
    void visitNumberValue() {
        Value value = new NumberValue(nodeIdGenerator.next(), "1");
        Assertions.assertThat(value.accept(visitor()))
                .isSameAs(value);
    }

    @Test
    void visitBooleanValue_true() {
        Value value = new BooleanValue(nodeIdGenerator.next(), true);
        Assertions.assertThat(value.accept(visitor()))
                .isSameAs(value);
    }

    @Test
    void visitBooleanValue_false() {
        Value value = new BooleanValue(nodeIdGenerator.next(), false);
        Assertions.assertThat(value.accept(visitor()))
                .isSameAs(value);
    }

    @Test
    void visitNoneValue() {
        Value value = new NoneValue(nodeIdGenerator.next());
        Assertions.assertThat(value.accept(visitor()))
                .isSameAs(value);
    }


    @Test
    void visitIdentifierValue() {
        Value value = new IdentifierValue(nodeIdGenerator.next(), "id");
        Assertions.assertThat(value.accept(visitor()))
                .isSameAs(value);
    }

    @Test
    void visitNewAssignmentValue_val() {
        Value value = new NewAssignmentValue(
                nodeIdGenerator.next(),
                "identifier",
                new TypeSpecSimple(nodeIdGenerator.next(), "T", Collections.emptyList()),
                new NoneValue(nodeIdGenerator.next()),
                Mode.VAL
        );
        Assertions.assertThat(value.accept(visitor()))
                .isSameAs(value);
    }

    @Test
    void visitNewAssignmentValue_var() {
        Value value = new NewAssignmentValue(
                nodeIdGenerator.next(),
                "identifier",
                new TypeSpecSimple(nodeIdGenerator.next(), "T", Collections.emptyList()),
                new NoneValue(nodeIdGenerator.next()),
                Mode.VAR
        );
        Assertions.assertThat(value.accept(visitor()))
                .isSameAs(value);
    }

    @Test
    void visitIndirectAssignmentValue() {
        Value value = new IndirectAssignmentValue(
                nodeIdGenerator.next(),
                new NoneValue(nodeIdGenerator.next()),
                "identifier",
                new NoneValue(nodeIdGenerator.next())
        );
        Assertions.assertThat(value.accept(visitor()))
                .isSameAs(value);
    }

    @Test
    void visitMethodCallValue() {
        Value value = new MethodCallValue(
                nodeIdGenerator.next(),
                "identifier",
                Collections.emptyList(),
                Collections.emptyList()
        );
        Assertions.assertThat(value.accept(visitor()))
                .isSameAs(value);
    }

    @Test
    void visitNestedValue() {
        Value value = new NestedValue(
                nodeIdGenerator.next(),
                new BooleanValue(nodeIdGenerator.next(), true),
                new BooleanValue(nodeIdGenerator.next(), false)
        );
        Assertions.assertThat(value.accept(visitor()))
                .isSameAs(value);
    }

    @Test
    void visitFunctionValue() {
        Value value = new FunctionValue(
                nodeIdGenerator.next(),
                Collections.emptyList(),
                Collections.emptyList(),
                new TypeSpecSimple(nodeIdGenerator.next(), "None", Collections.emptyList()),
                Collections.emptyList()
        );
        Assertions.assertThat(value.accept(visitor()))
                .isSameAs(value);
    }

    @Test
    void visitStatement() {
        Statement statement = new Statement(
                nodeIdGenerator.next(),
                new BooleanValue(nodeIdGenerator.next(), true),
                false
        );
        Assertions.assertThat(statement.accept(visitor()))
                .isSameAs(statement);
    }

    @Test
    void visitMethod() {
        Method method = new Method(
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
        );
        Assertions.assertThat(method.accept(visitor()))
                .isSameAs(method);
    }

    @Test
    void visitAttribute_var() {
        Attribute attribute = new Attribute(
                nodeIdGenerator.next(),
                "identifier",
                new TypeSpecSimple(
                        nodeIdGenerator.next(),
                        "None",
                        Collections.emptyList()
                ),
                new NoneValue(nodeIdGenerator.next()),
                Mode.VAR
        );
        Assertions.assertThat(attribute.accept(visitor()))
                .isSameAs(attribute);
    }

    @Test
    void visitAttribute_val() {
        Attribute attribute = new Attribute(
                nodeIdGenerator.next(),
                "identifier",
                new TypeSpecSimple(
                        nodeIdGenerator.next(),
                        "None",
                        Collections.emptyList()
                ),
                new NoneValue(nodeIdGenerator.next()),
                Mode.VAL
        );
        Assertions.assertThat(attribute.accept(visitor()))
                .isSameAs(attribute);
    }

    private IdentityVisitor visitor() {
        return new IdentityVisitor() {
        };
    }
}
