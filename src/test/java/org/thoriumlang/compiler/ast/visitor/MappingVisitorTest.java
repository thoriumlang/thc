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

class MappingVisitorTest {
    @Test
    void visitRoot_false() {
        Assertions.assertThat(
                mappingVisitor().visit((Root) null)
        )
                .isEqualTo("SomeString");
    }

    @Test
    void visitUse_false() {
        Assertions.assertThat(
                mappingVisitor().visit((Use) null)
        )
                .isEqualTo("SomeString");
    }

    @Test
    void visitType_false() {
        Assertions.assertThat(
                mappingVisitor().visit((Type) null)
        )
                .isEqualTo("SomeString");
    }

    @Test
    void visitClass_false() {
        Assertions.assertThat(
                mappingVisitor().visit((Class) null)
        )
                .isEqualTo("SomeString");
    }

    @Test
    void visitTypeIntersection_false() {
        Assertions.assertThat(
                mappingVisitor().visit((TypeSpecIntersection) null)
        )
                .isEqualTo("SomeString");
    }

    @Test
    void visitTypeUnion_false() {
        Assertions.assertThat(
                mappingVisitor().visit((TypeSpecUnion) null)
        )
                .isEqualTo("SomeString");
    }

    @Test
    void visitTypeSimple_false() {
        Assertions.assertThat(
                mappingVisitor().visit((TypeSpecSimple) null)
        )
                .isEqualTo("SomeString");
    }

    @Test
    void visitTypeFunction_false() {
        Assertions.assertThat(
                mappingVisitor().visit((TypeSpecFunction) null)
        )
                .isEqualTo("SomeString");
    }

    @Test
    void visitTypeInferred_false() {
        Assertions.assertThat(
                mappingVisitor().visit((TypeSpecInferred) null)
        )
                .isEqualTo("SomeString");
    }

    @Test
    void visitMethodSignature_false() {
        Assertions.assertThat(
                mappingVisitor().visit((MethodSignature) null)
        )
                .isEqualTo("SomeString");
    }

    @Test
    void visitParameter_false() {
        Assertions.assertThat(
                mappingVisitor().visit((Parameter) null)
        )
                .isEqualTo("SomeString");
    }

    @Test
    void visitTypeParameter_false() {
        Assertions.assertThat(
                mappingVisitor().visit((TypeParameter) null)
        )
                .isEqualTo("SomeString");
    }

    @Test
    void visitStringValue_false() {
        Assertions.assertThat(
                mappingVisitor().visit((StringValue) null)
        )
                .isEqualTo("SomeString");
    }

    @Test
    void visitNumberValue_false() {
        Assertions.assertThat(
                mappingVisitor().visit((NumberValue) null)
        )
                .isEqualTo("SomeString");
    }

    @Test
    void visitBooleanValue_false() {
        Assertions.assertThat(
                mappingVisitor().visit((BooleanValue) null)
        )
                .isEqualTo("SomeString");
    }

    @Test
    void visitNoneValue_false() {
        Assertions.assertThat(
                mappingVisitor().visit((NoneValue) null)
        )
                .isEqualTo("SomeString");
    }

    @Test
    void visitIdentifierValue_false() {
        Assertions.assertThat(
                mappingVisitor().visit((IdentifierValue) null)
        )
                .isEqualTo("SomeString");
    }

    @Test
    void visitNewAssignmentValue_false() {
        Assertions.assertThat(
                mappingVisitor().visit((NewAssignmentValue) null)
        )
                .isEqualTo("SomeString");
    }

    @Test
    void visitIndirectAssignmentValue_false() {
        Assertions.assertThat(
                mappingVisitor().visit((IndirectAssignmentValue) null)
        )
                .isEqualTo("SomeString");
    }

    @Test
    void visitDrectAssignmentValue_false() {
        Assertions.assertThat(
                mappingVisitor().visit((DirectAssignmentValue) null)
        )
                .isEqualTo("SomeString");
    }

    @Test
    void visitMethodCallValue_false() {
        Assertions.assertThat(
                mappingVisitor().visit((MethodCallValue) null)
        )
                .isEqualTo("SomeString");
    }

    @Test
    void visitNestedValue_false() {
        Assertions.assertThat(
                mappingVisitor().visit((NestedValue) null)
        )
                .isEqualTo("SomeString");
    }

    @Test
    void visitFunctionValue_false() {
        Assertions.assertThat(
                mappingVisitor().visit((FunctionValue) null)
        )
                .isEqualTo("SomeString");
    }

    @Test
    void visitStatement_false() {
        Assertions.assertThat(
                mappingVisitor().visit((Statement) null)
        )
                .isEqualTo("SomeString");
    }

    @Test
    void visitMethod_false() {
        Assertions.assertThat(
                mappingVisitor().visit((Method) null)
        )
                .isEqualTo("SomeString");
    }

    @Test
    void visitAttribute_false() {
        Assertions.assertThat(
                mappingVisitor().visit((Attribute) null)
        )
                .isEqualTo("SomeString");
    }

    @Test
    void visitReference_false() {
        Assertions.assertThat(
                mappingVisitor().visit((Reference) null)
        )
                .isEqualTo("SomeString");
    }

    private MappingVisitor<String> mappingVisitor() {
        return new MappingVisitor<>("SomeString");
    }
}