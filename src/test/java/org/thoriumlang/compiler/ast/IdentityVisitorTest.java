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
import org.junit.jupiter.api.Test;

import java.util.Collections;

class IdentityVisitorTest {
    @Test
    void visitRoot() {
        Root root = new Root(
                "namespace",
                Collections.emptyList(), // FIXME
                new Type(
                        Visibility.PRIVATE,
                        "name",
                        Collections.emptyList(),
                        TypeSpecSimple.OBJECT,
                        Collections.emptyList()
                )
        );
        Assertions.assertThat(root.accept(visitor()))
                .isEqualTo(root);
    }

    @Test
    void visitType() {
        Type type = new Type(
                Visibility.NAMESPACE,
                "name",
                Collections.singletonList(new TypeParameter("T")),
                TypeSpecSimple.OBJECT,
                Collections.emptyList()
        );
        Assertions.assertThat(type.accept(visitor()))
                .isEqualTo(type);
    }

    @Test
    void visitClass() {
        Class clazz = new Class(
                Visibility.NAMESPACE,
                "name",
                Collections.singletonList(new TypeParameter("T")),
                TypeSpecSimple.OBJECT,
                Collections.singletonList(
                        new Method(
                                new MethodSignature(
                                        Visibility.NAMESPACE,
                                        "method",
                                        Collections.emptyList(),
                                        Collections.emptyList(),
                                        TypeSpecSimple.NONE
                                ),
                                Collections.singletonList(
                                        new Statement(
                                                BooleanValue.TRUE,
                                                false
                                        )
                                )
                        )
                )
        );
        Assertions.assertThat(clazz.accept(visitor()))
                .isEqualTo(clazz);
    }

    @Test
    void visitTypeIntersection() {
        TypeSpecIntersection typeSpec = new TypeSpecIntersection(Collections.singletonList(new TypeSpecSimple(
                "type",
                Collections.emptyList()
        )));
        Assertions.assertThat(typeSpec.accept(visitor()))
                .isEqualTo(typeSpec);
    }

    @Test
    void visitTypeUnion() {
        TypeSpecUnion typeSpec = new TypeSpecUnion(Collections.singletonList(new TypeSpecSimple(
                "type",
                Collections.emptyList()
        )));
        Assertions.assertThat(typeSpec.accept(visitor()))
                .isEqualTo(typeSpec);
    }

    @Test
    void visitTypeSingle() {
        TypeSpecSimple typeSpec = new TypeSpecSimple(
                "type",
                Collections.emptyList()
        );
        Assertions.assertThat(typeSpec.accept(visitor()))
                .isEqualTo(typeSpec);
    }

    @Test
    void visitMethodSignature() {
        MethodSignature methodSignature = new MethodSignature(
                Visibility.PRIVATE,
                "name",
                Collections.emptyList(),
                Collections.emptyList(),
                new TypeSpecSimple(
                        "type",
                        Collections.emptyList()
                )
        );
        Assertions.assertThat(methodSignature.accept(visitor()))
                .isEqualTo(methodSignature);
    }

    @Test
    void visitParameter() {
        Parameter parameter = new Parameter("name", new TypeSpecSimple(
                "type",
                Collections.emptyList()
        ));
        Assertions.assertThat(parameter.accept(visitor()))
                .isEqualTo(parameter);
    }

    @Test
    void visitTypeParameter() {
        TypeParameter parameter = new TypeParameter("name");
        Assertions.assertThat(parameter.accept(visitor()))
                .isEqualTo(parameter);
    }

    @Test
    void visitStringValue() {
        Value value = new StringValue("value");
        Assertions.assertThat(value.accept(visitor()))
                .isEqualTo(value);
    }

    @Test
    void visitNumberValue() {
        Value value = new NumberValue(1);
        Assertions.assertThat(value.accept(visitor()))
                .isEqualTo(value);
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
                .isEqualTo(value);
    }


    @Test
    void visitIdentifierValue() {
        Value value = new IdentifierValue("id");
        Assertions.assertThat(value.accept(visitor()))
                .isEqualTo(value);
    }

    @Test
    void visitVarAssignmentValue() {
        Value value = new VarAssignmentValue(
                "identifier",
                new TypeSpecSimple("T", Collections.emptyList()),
                NoneValue.INSTANCE
        );
        Assertions.assertThat(value.accept(visitor()))
                .isEqualTo(value);
    }

    @Test
    void visitValAssignmentValue() {
        Value value = new ValAssignmentValue(
                "identifier",
                new TypeSpecSimple("T", Collections.emptyList()),
                NoneValue.INSTANCE
        );
        Assertions.assertThat(value.accept(visitor()))
                .isEqualTo(value);
    }

    @Test
    void visitIndirectAssignmentValue() {
        Value value = new IndirectAssignmentValue(
                NoneValue.INSTANCE,
                "identifier",
                NoneValue.INSTANCE
        );
        Assertions.assertThat(value.accept(visitor()))
                .isEqualTo(value);
    }

    @Test
    void visitMethodCallValue() {
        Value value = new MethodCallValue(
                "identifier",
                Collections.emptyList(),
                Collections.emptyList()
        );
        Assertions.assertThat(value.accept(visitor()))
                .isEqualTo(value);
    }

    @Test
    void visitNestedValue() {
        Value value = new NestedValue(BooleanValue.TRUE, BooleanValue.FALSE);
        Assertions.assertThat(value.accept(visitor()))
                .isEqualTo(value);
    }

    @Test
    void visitStatement() {
        Statement statement = new Statement(BooleanValue.TRUE, false);
        Assertions.assertThat(statement.accept(visitor()))
                .isEqualTo(statement);
    }

    @Test
    void visitMethod() {
        Method method = new Method(
                new MethodSignature(
                        Visibility.NAMESPACE,
                        "method",
                        Collections.emptyList(),
                        Collections.emptyList(),
                        TypeSpecSimple.NONE
                ),
                Collections.singletonList(new Statement(BooleanValue.TRUE, false))
        );
        Assertions.assertThat(method.accept(visitor()))
                .isEqualTo(method);
    }

    @Test
    void visitVarAttribute() {
        VarAttribute varAttribute = new VarAttribute(
                "identifier",
                TypeSpecSimple.NONE,
                NoneValue.INSTANCE
        );
        Assertions.assertThat(varAttribute.accept(visitor()))
                .isEqualTo(varAttribute);
    }

    @Test
    void visitValAttribute() {
        ValAttribute valAttribute = new ValAttribute(
                "identifier",
                TypeSpecSimple.NONE,
                NoneValue.INSTANCE
        );
        Assertions.assertThat(valAttribute.accept(visitor()))
                .isEqualTo(valAttribute);
    }

    private IdentityVisitor visitor() {
        return new IdentityVisitor() {
        };
    }
}
