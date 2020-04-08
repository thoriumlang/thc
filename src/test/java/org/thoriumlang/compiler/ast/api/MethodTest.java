package org.thoriumlang.compiler.ast.api;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.ast.api.testsupport.Helper;
import org.thoriumlang.compiler.ast.api.testsupport.ParameterCondition;

class MethodTest {
    @Test
    void constructor() {
        Assertions.assertThatThrownBy(() -> new Method(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("node cannot be null");
    }


    @Test
    void _equals() {
        Assertions.assertThat(method())
                .isEqualTo(method());
    }

    @Test
    void _hashCode() {
        Assertions.assertThat(method().hashCode())
                .isEqualTo(method().hashCode());
    }

    @Test
    void getName() {
        Assertions.assertThat(method().getName())
                .isEqualTo("getPerson");
    }

    @Test
    void getReturnType() {
        Assertions.assertThat(method().getReturnType().getName())
                .isEqualTo("(Legal | Natural)");
    }

    @Test
    void getParameters() {
        Assertions.assertThat(method().getParameters())
                .hasSize(2)
                .haveAtLeastOne(new ParameterCondition("personId", "Number"))
                .haveAtLeastOne(new ParameterCondition("office", "String"));
    }

    private Method method() {
        return Helper.getClassMethod("example.Main#getPerson");
    }
}