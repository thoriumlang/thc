package org.thoriumlang.compiler.ast.api;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.ast.api.testsupport.Helper;

class ClassTest {
    @Test
    void constructor() {
        Assertions.assertThatThrownBy(() -> new Class(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("node cannot be null");
    }

    @Test
    void getType() {
        Assertions.assertThat(Helper.getClass("example.LegalPerson").getType())
                .isNotNull();
    }
}