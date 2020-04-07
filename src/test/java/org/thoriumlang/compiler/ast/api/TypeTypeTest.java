package org.thoriumlang.compiler.ast.api;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.ast.api.testsupport.Helper;
import org.thoriumlang.compiler.ast.api.testsupport.MethodNameCondition;

class TypeTypeTest {
    @Test
    void constructor() {
        Assertions.assertThatThrownBy(() -> new TypeType(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("node cannot be null");
    }

    @Test
    void getName() {
        Assertions.assertThat(type().getName())
                .isEqualTo("Person");
    }

    @Test
    void getMethods() {
        Assertions.assertThat(type().getMethods())
                .hasSize(3)
                .haveAtLeastOne(new MethodNameCondition("getName"))
                .haveAtLeastOne(new MethodNameCondition("getLegalAddress"))
                .haveAtLeastOne(new MethodNameCondition("getMailingAddress"));
    }

    private TypeType type() {
        return (TypeType) Helper.getType("example.Person");
    }
}