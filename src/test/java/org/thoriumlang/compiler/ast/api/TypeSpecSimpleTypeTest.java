package org.thoriumlang.compiler.ast.api;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.ast.api.testsupport.Helper;
import org.thoriumlang.compiler.ast.api.testsupport.MethodNameCondition;

class TypeSpecSimpleTypeTest {
    @Test
    void constructor() {
        Assertions.assertThatThrownBy(() -> new TypeSpecSimpleType(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("node cannot be null");
    }

    @Test
    void getName() {
        Assertions.assertThat(type().getName())
                .isEqualTo("String");
    }

    @Test
    void getMethod() {
        Assertions.assertThat(type().getMethods())
                .hasSize(1)
                .haveAtLeastOne(new MethodNameCondition("length"));
    }

    private TypeSpecSimpleType type() {
        return (TypeSpecSimpleType) Helper.getTypeMethod("example.Named#getName").getReturnType();
    }
}