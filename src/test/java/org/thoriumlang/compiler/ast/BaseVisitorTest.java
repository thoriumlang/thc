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

class BaseVisitorTest {
    @Test
    void visitRoot() {
        Assertions.assertThat(
                visitor().visitRoot(null, null, null)
        )
                .isEqualTo(null);
    }

    @Test
    void visitUse() {
        Assertions.assertThat(
                visitor().visitUse(null, null)
        )
                .isEqualTo(null);
    }

    @Test
    void visitType() {
        Assertions.assertThat(
                visitor().visitType(null, null, null, null, null)
        )
                .isEqualTo(null);
    }

    @Test
    void visitClass() {
        Assertions.assertThat(
                visitor().visitClass(null, null, null, null, null)
        )
                .isEqualTo(null);
    }

    @Test
    void visitTypeIntersection() {
        Assertions.assertThat(
                visitor().visitTypeIntersection(null)
        )
                .isEqualTo(null);
    }

    @Test
    void visitTypeUnion() {
        Assertions.assertThat(
                visitor().visitTypeUnion(null)
        )
                .isEqualTo(null);
    }

    @Test
    void visitTypeSingle() {
        Assertions.assertThat(
                visitor().visitTypeSingle(null, null)
        )
                .isEqualTo(null);
    }

    @Test
    void visitMethodSignature() {
        Assertions.assertThat(
                visitor().visitMethodSignature(null, null, null, null, null)
        )
                .isEqualTo(null);
    }

    @Test
    void visitParameter() {
        Assertions.assertThat(
                visitor().visitParameter(null, null)
        )
                .isEqualTo(null);
    }

    @Test
    void visitTypeParameter() {
        Assertions.assertThat(
                visitor().visitTypeParameter(null)
        )
                .isEqualTo(null);
    }

    @Test
    void visitStringValue() {
        Assertions.assertThat(
                visitor().visitStringValue(null)
        )
                .isEqualTo(null);
    }

    @Test
    void visitNumberValue() {
        Assertions.assertThat(
                visitor().visitNumberValue(null)
        )
                .isEqualTo(null);
    }

    @Test
    void visitBooleanValue() {
        Assertions.assertThat(
                visitor().visitBooleanValue(false)
        )
                .isEqualTo(null);
    }

    @Test
    void visitNoneValue() {
        Assertions.assertThat(
                visitor().visitNoneValue()
        )
                .isEqualTo(null);
    }

    @Test
    void visitIdentifierValue() {
        Assertions.assertThat(
                visitor().visitIdentifierValue(null)
        )
                .isEqualTo(null);
    }

    @Test
    void visitVarAssignmentValue() {
        Assertions.assertThat(
                visitor().visitVarAssignmentValue(null, null, null)
        )
                .isEqualTo(null);
    }

    @Test
    void visitValAssignmentValue() {
        Assertions.assertThat(
                visitor().visitValAssignmentValue(null, null, null)
        )
                .isEqualTo(null);
    }

    @Test
    void visitIndirectAssignmentValue() {
        Assertions.assertThat(
                visitor().visitIndirectAssignmentValue(null, null, null)
        )
                .isEqualTo(null);
    }

    @Test
    void visitMethodCallValue() {
        Assertions.assertThat(
                visitor().visitMethodCallValue(null, null, null)
        )
                .isEqualTo(null);
    }

    @Test
    void visitNestedValue() {
        Assertions.assertThat(
                visitor().visitNestedValue(null, null)
        )
                .isEqualTo(null);
    }

    @Test
    void visitStatement() {
        Assertions.assertThat(
                visitor().visitStatement(null, false)
        )
                .isEqualTo(null);
    }

    @Test
    void visitMethod() {
        Assertions.assertThat(
                visitor().visitMethod(null, null)
        )
                .isEqualTo(null);
    }

    @Test
    void visitVarAttribute() {
        Assertions.assertThat(
                visitor().visitVarAttribute(null, null, null)
        )
                .isEqualTo(null);
    }

    @Test
    void visitValAttribute() {
        Assertions.assertThat(
                visitor().visitValAttribute(null, null, null)
        )
                .isEqualTo(null);
    }

    private BaseVisitor<Object> visitor() {
        return new BaseVisitor<Object>() {
        };
    }
}
