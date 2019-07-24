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

    private IdentityVisitor visitor() {
        return new IdentityVisitor() {
        };
    }
}
