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
package org.thoriumlang.compiler.ast;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

class CopyVisitorTest {
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
                .isEqualTo(root)
                .isNotSameAs(root);
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
                .isEqualTo(root)
                .isNotSameAs(root);
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
                .isEqualTo(type)
                .isNotSameAs(type);
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
                                                BooleanValue.TRUE,
                                                false
                                        )
                                )
                        )
                ),
                Collections.singletonList(
                        new VarAttribute(
                                nodeIdGenerator.next(),
                                "attribute",
                                new TypeSpecSimple(
                                        nodeIdGenerator.next(),
                                        "None",
                                        Collections.emptyList()
                                ),
                                NoneValue.INSTANCE
                        )
                )
        );
        Assertions.assertThat(clazz.accept(visitor()))
                .isEqualTo(clazz)
                .isNotSameAs(clazz);
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
                .isEqualTo(typeSpec)
                .isNotSameAs(typeSpec);
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
                .isEqualTo(typeSpec)
                .isNotSameAs(typeSpec);
    }

    @Test
    void visitTypeSingle() {
        TypeSpecSimple typeSpec = new TypeSpecSimple(
                nodeIdGenerator.next(),
                "type",
                Collections.emptyList()
        );
        Assertions.assertThat(typeSpec.accept(visitor()))
                .isEqualTo(typeSpec)
                .isNotSameAs(typeSpec);
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
                .isEqualTo(typeSpec)
                .isNotSameAs(typeSpec);
    }

    @Test
    void visitTypeInferred() {
        TypeSpecInferred typeSpec = new TypeSpecInferred(nodeIdGenerator.next());
        Assertions.assertThat(typeSpec.accept(visitor()))
                .isEqualTo(typeSpec)
                .isNotSameAs(typeSpec);
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
                .isEqualTo(methodSignature)
                .isNotSameAs(methodSignature);
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
                .isEqualTo(parameter)
                .isNotSameAs(parameter);
    }

    @Test
    void visitTypeParameter() {
        TypeParameter parameter = new TypeParameter(nodeIdGenerator.next(), "name");
        Assertions.assertThat(parameter.accept(visitor()))
                .isEqualTo(parameter)
                .isNotSameAs(parameter);
    }

    @Test
    void visitStringValue() {
        Value value = new StringValue(nodeIdGenerator.next(), "value");
        Assertions.assertThat(value.accept(visitor()))
                .isEqualTo(value)
                .isNotSameAs(value);
    }

    @Test
    void visitNumberValue() {
        Value value = new NumberValue(nodeIdGenerator.next(), "1");
        Assertions.assertThat(value.accept(visitor()))
                .isEqualTo(value)
                .isNotSameAs(value);
    }

    @Test
    void visitBooleanValue() {
        Assertions.assertThat(BooleanValue.TRUE.accept(visitor()))
                .isEqualTo(BooleanValue.TRUE);
        Assertions.assertThat(BooleanValue.FALSE.accept(visitor()))
                .isEqualTo(BooleanValue.FALSE);
    }

    @Test
    void visitNoneValue() {
        Value value = NoneValue.INSTANCE;
        Assertions.assertThat(value.accept(visitor()))
                .isEqualTo(value)
                .isSameAs(value);
    }


    @Test
    void visitIdentifierValue() {
        Value value = new IdentifierValue(nodeIdGenerator.next(), "id");
        Assertions.assertThat(value.accept(visitor()))
                .isEqualTo(value)
                .isNotSameAs(value);
    }

    @Test
    void visitVarAssignmentValue() {
        Value value = new VarAssignmentValue(
                nodeIdGenerator.next(),
                "identifier",
                new TypeSpecSimple(nodeIdGenerator.next(), "T", Collections.emptyList()),
                NoneValue.INSTANCE
        );
        Assertions.assertThat(value.accept(visitor()))
                .isEqualTo(value)
                .isNotSameAs(value);
    }

    @Test
    void visitValAssignmentValue() {
        Value value = new ValAssignmentValue(
                nodeIdGenerator.next(),
                "identifier",
                new TypeSpecSimple(nodeIdGenerator.next(), "T", Collections.emptyList()),
                NoneValue.INSTANCE
        );
        Assertions.assertThat(value.accept(visitor()))
                .isEqualTo(value)
                .isNotSameAs(value);
    }

    @Test
    void visitIndirectAssignmentValue() {
        Value value = new IndirectAssignmentValue(
                nodeIdGenerator.next(),
                NoneValue.INSTANCE,
                "identifier",
                NoneValue.INSTANCE
        );
        Assertions.assertThat(value.accept(visitor()))
                .isEqualTo(value)
                .isNotSameAs(value);
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
                .isEqualTo(value)
                .isNotSameAs(value);
    }

    @Test
    void visitNestedValue() {
        Value value = new NestedValue(nodeIdGenerator.next(), BooleanValue.TRUE, BooleanValue.FALSE);
        Assertions.assertThat(value.accept(visitor()))
                .isEqualTo(value)
                .isNotSameAs(value);
    }

    @Test
    void visitStatement() {
        Statement statement = new Statement(nodeIdGenerator.next(), BooleanValue.TRUE, false);
        Assertions.assertThat(statement.accept(visitor()))
                .isEqualTo(statement)
                .isNotSameAs(statement);
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
                Collections.singletonList(new Statement(nodeIdGenerator.next(), BooleanValue.TRUE, false))
        );
        Assertions.assertThat(method.accept(visitor()))
                .isEqualTo(method)
                .isNotSameAs(method);
    }

    @Test
    void visitVarAttribute() {
        VarAttribute varAttribute = new VarAttribute(
                nodeIdGenerator.next(),
                "identifier",
                new TypeSpecSimple(
                        nodeIdGenerator.next(),
                        "None",
                        Collections.emptyList()
                ),
                NoneValue.INSTANCE
        );
        Assertions.assertThat(varAttribute.accept(visitor()))
                .isEqualTo(varAttribute)
                .isNotSameAs(varAttribute);
    }

    @Test
    void visitValAttribute() {
        ValAttribute valAttribute = new ValAttribute(
                nodeIdGenerator.next(),
                "identifier",
                new TypeSpecSimple(
                        nodeIdGenerator.next(),
                        "None",
                        Collections.emptyList()
                ),
                NoneValue.INSTANCE
        );
        Assertions.assertThat(valAttribute.accept(visitor()))
                .isEqualTo(valAttribute)
                .isNotSameAs(valAttribute);
    }

    private CopyVisitor visitor() {
        return new CopyVisitor() {
        };
    }
}
