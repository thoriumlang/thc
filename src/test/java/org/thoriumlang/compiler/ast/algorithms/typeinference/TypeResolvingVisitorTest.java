package org.thoriumlang.compiler.ast.algorithms.typeinference;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.api.errors.SemanticError;
import org.thoriumlang.compiler.ast.nodes.BooleanValue;
import org.thoriumlang.compiler.ast.nodes.Node;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.nodes.NoneValue;
import org.thoriumlang.compiler.ast.nodes.NumberValue;
import org.thoriumlang.compiler.ast.nodes.StringValue;
import org.thoriumlang.compiler.ast.nodes.TypeSpec;
import org.thoriumlang.compiler.ast.nodes.TypeSpecIntersection;
import org.thoriumlang.compiler.ast.nodes.TypeSpecSimple;
import org.thoriumlang.compiler.ast.nodes.TypeSpecUnion;
import org.thoriumlang.compiler.ast.nodes.Value;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class TypeResolvingVisitorTest {
    private static final NodeIdGenerator nodeIdGenerator = new NodeIdGenerator();

    @Test
    void stringValue() {
        Value value = new StringValue(nodeIdGenerator.next(), "String");
        List<SemanticError> errors = value.accept(new TypeResolvingVisitor(nodeIdGenerator));

        Assertions.assertThat(errors).isEmpty();
        Assertions.assertThat(value.getContext().get(TypeSpec.class))
                .get()
                .extracting(Object::toString)
                .isEqualTo("org.thoriumlang.String[]");
    }

    @Test
    void numberValue() {
        Value value = new NumberValue(nodeIdGenerator.next(), "1");
        List<SemanticError> errors = value.accept(new TypeResolvingVisitor(nodeIdGenerator));

        Assertions.assertThat(errors).isEmpty();
        Assertions.assertThat(value.getContext().get(TypeSpec.class))
                .get()
                .extracting(Object::toString)
                .isEqualTo("org.thoriumlang.Number[]");
    }

    @Test
    void booleanValue_true() {
        Value value = new BooleanValue(nodeIdGenerator.next(), true);
        List<SemanticError> errors = value.accept(new TypeResolvingVisitor(nodeIdGenerator));

        Assertions.assertThat(errors).isEmpty();
        Assertions.assertThat(value.getContext().get(TypeSpec.class))
                .get()
                .extracting(Object::toString)
                .isEqualTo("org.thoriumlang.Boolean[]");
    }

    @Test
    void booleanValue_false() {
        Value value = new BooleanValue(nodeIdGenerator.next(), false);
        List<SemanticError> errors = value.accept(new TypeResolvingVisitor(nodeIdGenerator));

        Assertions.assertThat(errors).isEmpty();
        Assertions.assertThat(value.getContext().get(TypeSpec.class))
                .get()
                .extracting(Object::toString)
                .isEqualTo("org.thoriumlang.Boolean[]");
    }

    @Test
    void noneValue() {
        Value value = new NoneValue(nodeIdGenerator.next());

        List<SemanticError> errors = value.accept(new TypeResolvingVisitor(nodeIdGenerator));

        Assertions.assertThat(errors).isEmpty();
        Assertions.assertThat(value.getContext().get(TypeSpec.class))
                .get()
                .extracting(Object::toString)
                .isEqualTo("org.thoriumlang.None[]");
    }

    @Test
    void typeSpecSimple() {
        Node node = new TypeSpecSimple(nodeIdGenerator.next(), "Type", Collections.emptyList());

        List<SemanticError> errors = node.accept(new TypeResolvingVisitor(nodeIdGenerator));

        Assertions.assertThat(errors).isEmpty();
        Assertions.assertThat(node.getContext().get(TypeSpec.class))
                .get()
                .isEqualTo(node);
    }

    @Test
    void typeSpecIntersection() {
        Node node = new TypeSpecIntersection(
                nodeIdGenerator.next(),
                Arrays.asList(
                        new TypeSpecSimple(nodeIdGenerator.next(), "Type1", Collections.emptyList()),
                        new TypeSpecSimple(nodeIdGenerator.next(), "Type2", Collections.emptyList())
                )
        );

        List<SemanticError> errors = node.accept(new TypeResolvingVisitor(nodeIdGenerator));

        Assertions.assertThat(errors).isEmpty();
        Assertions.assertThat(node.getContext().get(TypeSpec.class))
                .get()
                .isEqualTo(node);
    }

    @Test
    void typeSpecUnion() {
        Node node = new TypeSpecUnion(
                nodeIdGenerator.next(),
                Arrays.asList(
                        new TypeSpecSimple(nodeIdGenerator.next(), "Type1", Collections.emptyList()),
                        new TypeSpecSimple(nodeIdGenerator.next(), "Type2", Collections.emptyList())
                )
        );

        List<SemanticError> errors = node.accept(new TypeResolvingVisitor(nodeIdGenerator));

        Assertions.assertThat(errors).isEmpty();
        Assertions.assertThat(node.getContext().get(TypeSpec.class))
                .get()
                .isEqualTo(node);
    }
}