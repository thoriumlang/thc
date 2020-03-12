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

class PredicateVisitorTest {
    @Test
    void visitRoot_true() {
        Assertions.assertThat(
                trueVisitor().visit((Root) null)
        )
                .isEqualTo(true);
    }

    @Test
    void visitUse_true() {
        Assertions.assertThat(
                trueVisitor().visit((Use) null)
        )
                .isEqualTo(true);
    }

    @Test
    void visitType_true() {
        Assertions.assertThat(
                trueVisitor().visit((Type) null)
        )
                .isEqualTo(true);
    }

    @Test
    void visitClass_true() {
        Assertions.assertThat(
                trueVisitor().visit((Class) null)
        )
                .isEqualTo(true);
    }

    @Test
    void visitTypeIntersection_true() {
        Assertions.assertThat(
                trueVisitor().visit((TypeSpecIntersection) null)
        )
                .isEqualTo(true);
    }

    @Test
    void visitTypeUnion_true() {
        Assertions.assertThat(
                trueVisitor().visit((TypeSpecUnion) null)
        )
                .isEqualTo(true);
    }

    @Test
    void visitTypeSimple_true() {
        Assertions.assertThat(
                trueVisitor().visit((TypeSpecSimple) null)
        )
                .isEqualTo(true);
    }

    @Test
    void visitTypeFunction_true() {
        Assertions.assertThat(
                trueVisitor().visit((TypeSpecFunction) null)
        )
                .isEqualTo(true);
    }

    @Test
    void visitTypeInferred_true() {
        Assertions.assertThat(
                trueVisitor().visit((TypeSpecInferred) null)
        )
                .isEqualTo(true);
    }

    @Test
    void visitMethodSignature_true() {
        Assertions.assertThat(
                trueVisitor().visit((MethodSignature) null)
        )
                .isEqualTo(true);
    }

    @Test
    void visitParameter_true() {
        Assertions.assertThat(
                trueVisitor().visit((Parameter) null)
        )
                .isEqualTo(true);
    }

    @Test
    void visitTypeParameter_true() {
        Assertions.assertThat(
                trueVisitor().visit((TypeParameter) null)
        )
                .isEqualTo(true);
    }

    @Test
    void visitStringValue_true() {
        Assertions.assertThat(
                trueVisitor().visit((StringValue) null)
        )
                .isEqualTo(true);
    }

    @Test
    void visitNumberValue_true() {
        Assertions.assertThat(
                trueVisitor().visit((NumberValue) null)
        )
                .isEqualTo(true);
    }

    @Test
    void visitBooleanValue_true() {
        Assertions.assertThat(
                trueVisitor().visit((BooleanValue) null)
        )
                .isEqualTo(true);
    }

    @Test
    void visitNoneValue_true() {
        Assertions.assertThat(
                trueVisitor().visit((NoneValue) null)
        )
                .isEqualTo(true);
    }

    @Test
    void visitIdentifierValue_true() {
        Assertions.assertThat(
                trueVisitor().visit((IdentifierValue) null)
        )
                .isEqualTo(true);
    }

    @Test
    void visitNewAssignmentValue_true() {
        Assertions.assertThat(
                trueVisitor().visit((NewAssignmentValue) null)
        )
                .isEqualTo(true);
    }

    @Test
    void visitIndirectAssignmentValue_true() {
        Assertions.assertThat(
                trueVisitor().visit((IndirectAssignmentValue) null)
        )
                .isEqualTo(true);
    }

    @Test
    void visitDirectAssignmentValue_true() {
        Assertions.assertThat(
                trueVisitor().visit((DirectAssignmentValue) null)
        )
                .isEqualTo(true);
    }

    @Test
    void visitMethodCallValue_true() {
        Assertions.assertThat(
                trueVisitor().visit((MethodCallValue) null)
        )
                .isEqualTo(true);
    }

    @Test
    void visitNestedValue_true() {
        Assertions.assertThat(
                trueVisitor().visit((NestedValue) null)
        )
                .isEqualTo(true);
    }

    @Test
    void visitFunctionValue_true() {
        Assertions.assertThat(
                trueVisitor().visit((FunctionValue) null)
        )
                .isEqualTo(true);
    }

    @Test
    void visitStatement_true() {
        Assertions.assertThat(
                trueVisitor().visit((Statement) null)
        )
                .isEqualTo(true);
    }

    @Test
    void visitMethod_true() {
        Assertions.assertThat(
                trueVisitor().visit((Method) null)
        )
                .isEqualTo(true);
    }

    @Test
    void visitAttribute_true() {
        Assertions.assertThat(
                trueVisitor().visit((Attribute) null)
        )
                .isEqualTo(true);
    }

    @Test
    void visitReference_true() {
        Assertions.assertThat(
                trueVisitor().visit((Reference) null)
        )
                .isEqualTo(true);
    }

    @Test
    void visitRoot_false() {
        Assertions.assertThat(
                falseVisitor().visit((Root) null)
        )
                .isEqualTo(false);
    }

    @Test
    void visitUse_false() {
        Assertions.assertThat(
                falseVisitor().visit((Use) null)
        )
                .isEqualTo(false);
    }

    @Test
    void visitType_false() {
        Assertions.assertThat(
                falseVisitor().visit((Type) null)
        )
                .isEqualTo(false);
    }

    @Test
    void visitClass_false() {
        Assertions.assertThat(
                falseVisitor().visit((Class) null)
        )
                .isEqualTo(false);
    }

    @Test
    void visitTypeIntersection_false() {
        Assertions.assertThat(
                falseVisitor().visit((TypeSpecIntersection) null)
        )
                .isEqualTo(false);
    }

    @Test
    void visitTypeUnion_false() {
        Assertions.assertThat(
                falseVisitor().visit((TypeSpecUnion) null)
        )
                .isEqualTo(false);
    }

    @Test
    void visitTypeSimple_false() {
        Assertions.assertThat(
                falseVisitor().visit((TypeSpecSimple) null)
        )
                .isEqualTo(false);
    }

    @Test
    void visitTypeFunction_false() {
        Assertions.assertThat(
                falseVisitor().visit((TypeSpecFunction) null)
        )
                .isEqualTo(false);
    }

    @Test
    void visitTypeInferred_false() {
        Assertions.assertThat(
                falseVisitor().visit((TypeSpecInferred) null)
        )
                .isEqualTo(false);
    }

    @Test
    void visitMethodSignature_false() {
        Assertions.assertThat(
                falseVisitor().visit((MethodSignature) null)
        )
                .isEqualTo(false);
    }

    @Test
    void visitParameter_false() {
        Assertions.assertThat(
                falseVisitor().visit((Parameter) null)
        )
                .isEqualTo(false);
    }

    @Test
    void visitTypeParameter_false() {
        Assertions.assertThat(
                falseVisitor().visit((TypeParameter) null)
        )
                .isEqualTo(false);
    }

    @Test
    void visitStringValue_false() {
        Assertions.assertThat(
                falseVisitor().visit((StringValue) null)
        )
                .isEqualTo(false);
    }

    @Test
    void visitNumberValue_false() {
        Assertions.assertThat(
                falseVisitor().visit((NumberValue) null)
        )
                .isEqualTo(false);
    }

    @Test
    void visitBooleanValue_false() {
        Assertions.assertThat(
                falseVisitor().visit((BooleanValue) null)
        )
                .isEqualTo(false);
    }

    @Test
    void visitNoneValue_false() {
        Assertions.assertThat(
                falseVisitor().visit((NoneValue) null)
        )
                .isEqualTo(false);
    }

    @Test
    void visitIdentifierValue_false() {
        Assertions.assertThat(
                falseVisitor().visit((IdentifierValue) null)
        )
                .isEqualTo(false);
    }

    @Test
    void visitNewAssignmentValue_false() {
        Assertions.assertThat(
                falseVisitor().visit((NewAssignmentValue) null)
        )
                .isEqualTo(false);
    }

    @Test
    void visitIndirectAssignmentValue_false() {
        Assertions.assertThat(
                falseVisitor().visit((IndirectAssignmentValue) null)
        )
                .isEqualTo(false);
    }

    @Test
    void visitDrectAssignmentValue_false() {
        Assertions.assertThat(
                falseVisitor().visit((DirectAssignmentValue) null)
        )
                .isEqualTo(false);
    }

    @Test
    void visitMethodCallValue_false() {
        Assertions.assertThat(
                falseVisitor().visit((MethodCallValue) null)
        )
                .isEqualTo(false);
    }

    @Test
    void visitNestedValue_false() {
        Assertions.assertThat(
                falseVisitor().visit((NestedValue) null)
        )
                .isEqualTo(false);
    }

    @Test
    void visitFunctionValue_false() {
        Assertions.assertThat(
                falseVisitor().visit((FunctionValue) null)
        )
                .isEqualTo(false);
    }

    @Test
    void visitStatement_false() {
        Assertions.assertThat(
                falseVisitor().visit((Statement) null)
        )
                .isEqualTo(false);
    }

    @Test
    void visitMethod_false() {
        Assertions.assertThat(
                falseVisitor().visit((Method) null)
        )
                .isEqualTo(false);
    }

    @Test
    void visitAttribute_false() {
        Assertions.assertThat(
                falseVisitor().visit((Attribute) null)
        )
                .isEqualTo(false);
    }

    @Test
    void visitReference_false() {
        Assertions.assertThat(
                falseVisitor().visit((Reference) null)
        )
                .isEqualTo(false);
    }

    private PredicateVisitor falseVisitor() {
        return new PredicateVisitor() {
        };
    }

    private PredicateVisitor trueVisitor() {
        return new PredicateVisitor(true) {
        };
    }
}
