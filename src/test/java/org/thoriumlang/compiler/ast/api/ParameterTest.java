package org.thoriumlang.compiler.ast.api;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.ast.api.testsupport.Helper;

class ParameterTest {
    @Test
    void constructor() {
        Assertions.assertThatThrownBy(() -> new Parameter(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("node cannot be null");
    }

    @Test
    void getName() {
        Assertions.assertThat(parameter().getName())
                .isEqualTo("personId");
    }

    @Test
    void getType() {
        Assertions.assertThat(parameter().getType().getName())
                .isEqualTo("Number");
    }

    private Parameter parameter() {
        return Helper.getClassMethod("example.Main#getPerson").getParameters().get(0);
    }
}
