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
import org.thoriumlang.compiler.ast.nodes.NestedValue;
import org.thoriumlang.compiler.ast.nodes.NewAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.NoneValue;
import org.thoriumlang.compiler.ast.nodes.NumberValue;
import org.thoriumlang.compiler.ast.nodes.Parameter;
import org.thoriumlang.compiler.ast.nodes.Reference;
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

class BaseVisitorTest {
    @Test
    void visitRoot() {
        Assertions.assertThat(
                visitor().visit((Root) null)
        )
                .isEqualTo(null);
    }

    @Test
    void visitUse() {
        Assertions.assertThat(
                visitor().visit((Use) null)
        )
                .isEqualTo(null);
    }

    @Test
    void visitType() {
        Assertions.assertThat(
                visitor().visit((Type) null)
        )
                .isEqualTo(null);
    }

    @Test
    void visitClass() {
        Assertions.assertThat(
                visitor().visit((Class) null)
        )
                .isEqualTo(null);
    }

    @Test
    void visitTypeIntersection() {
        Assertions.assertThat(
                visitor().visit((TypeSpecIntersection) null)
        )
                .isEqualTo(null);
    }

    @Test
    void visitTypeUnion() {
        Assertions.assertThat(
                visitor().visit((TypeSpecUnion) null)
        )
                .isEqualTo(null);
    }

    @Test
    void visitTypeSimple() {
        Assertions.assertThat(
                visitor().visit((TypeSpecSimple) null)
        )
                .isEqualTo(null);
    }

    @Test
    void visitTypeFunction() {
        Assertions.assertThat(
                visitor().visit((TypeSpecFunction) null)
        )
                .isEqualTo(null);
    }

    @Test
    void visitTypeInferred() {
        Assertions.assertThat(
                visitor().visit((TypeSpecInferred) null)
        )
                .isEqualTo(null);
    }

    @Test
    void visitMethodSignature() {
        Assertions.assertThat(
                visitor().visit((MethodSignature) null)
        )
                .isEqualTo(null);
    }

    @Test
    void visitParameter() {
        Assertions.assertThat(
                visitor().visit((Parameter) null)
        )
                .isEqualTo(null);
    }

    @Test
    void visitTypeParameter() {
        Assertions.assertThat(
                visitor().visit((TypeParameter) null)
        )
                .isEqualTo(null);
    }

    @Test
    void visitStringValue() {
        Assertions.assertThat(
                visitor().visit((StringValue) null)
        )
                .isEqualTo(null);
    }

    @Test
    void visitNumberValue() {
        Assertions.assertThat(
                visitor().visit((NumberValue) null)
        )
                .isEqualTo(null);
    }

    @Test
    void visitBooleanValue() {
        Assertions.assertThat(
                visitor().visit((BooleanValue) null)
        )
                .isEqualTo(null);
    }

    @Test
    void visitNoneValue() {
        Assertions.assertThat(
                visitor().visit((NoneValue) null)
        )
                .isEqualTo(null);
    }

    @Test
    void visitIdentifierValue() {
        Assertions.assertThat(
                visitor().visit((IdentifierValue) null)
        )
                .isEqualTo(null);
    }

    @Test
    void visitNewAssignmentValue() {
        Assertions.assertThat(
                visitor().visit((NewAssignmentValue) null)
        )
                .isEqualTo(null);
    }

    @Test
    void visitIndirectAssignmentValue() {
        Assertions.assertThat(
                visitor().visit((IndirectAssignmentValue) null)
        )
                .isEqualTo(null);
    }

    @Test
    void visitDrectAssignmentValue() {
        Assertions.assertThat(
                visitor().visit((DirectAssignmentValue) null)
        )
                .isEqualTo(null);
    }

    @Test
    void visitMethodCallValue() {
        Assertions.assertThat(
                visitor().visit((MethodCallValue) null)
        )
                .isEqualTo(null);
    }

    @Test
    void visitNestedValue() {
        Assertions.assertThat(
                visitor().visit((NestedValue) null)
        )
                .isEqualTo(null);
    }

    @Test
    void visitFunctionValue() {
        Assertions.assertThat(
                visitor().visit((FunctionValue) null)
        )
                .isEqualTo(null);
    }

    @Test
    void visitStatement() {
        Assertions.assertThat(
                visitor().visit((Statement) null)
        )
                .isEqualTo(null);
    }

    @Test
    void visitMethod() {
        Assertions.assertThat(
                visitor().visit((Method) null)
        )
                .isEqualTo(null);
    }

    @Test
    void visitAttribute() {
        Assertions.assertThat(
                visitor().visit((Attribute) null)
        )
                .isEqualTo(null);
    }

    @Test
    void visitIdentifier() {
        Assertions.assertThat(
                visitor().visit((Reference) null)
        )
                .isEqualTo(null);
    }

    private BaseVisitor<Object> visitor() {
        return new BaseVisitor<Object>() {
        };
    }
}
