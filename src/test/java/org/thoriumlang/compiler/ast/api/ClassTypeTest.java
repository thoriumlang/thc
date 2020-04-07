package org.thoriumlang.compiler.ast.api;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.ast.api.testsupport.Helper;
import org.thoriumlang.compiler.ast.api.testsupport.MethodNameCondition;

class ClassTypeTest {
    @Test
    void constructor() {
        Assertions.assertThatThrownBy(() -> new ClassType(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("node cannot be null");
    }

    @Test
    void getName() {
        Assertions.assertThat(type().getName())
                .isEqualTo("LegalPerson");
    }

    @Test
    void getMethods() {
        Assertions.assertThat(type().getMethods())
                .hasSize(4)
                .haveAtLeastOne(new MethodNameCondition("getId"))
                .haveAtLeastOne(new MethodNameCondition("getName"))
                .haveAtLeastOne(new MethodNameCondition("getLegalAddress"))
                .haveAtLeastOne(new MethodNameCondition("getMailingAddress"));
    }

    private ClassType type() {
        return (ClassType) Helper.getClass("example.LegalPerson").getType();
    }
}