package org.thoriumlang.compiler.ast.api;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.ast.api.testsupport.Helper;
import org.thoriumlang.compiler.ast.api.testsupport.MethodNameCondition;

class TypeSpecUnionTypeTest {
    @Test
    void constructor() {
        Assertions.assertThatThrownBy(() -> new TypeSpecUnionType(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("node cannot be null");
    }

    @Test
    void getName() {
        Assertions.assertThat(type().getName())
                .isEqualTo("(Country & Identified)");
    }

    @Test
    void getMethods() {
        Assertions.assertThat(type().getMethods())
                .hasSize(2)
                .haveAtLeastOne(new MethodNameCondition("getId"))
                .haveAtLeastOne(new MethodNameCondition("getName"));
    }

    @Test
    void _toString() {
        Assertions.assertThat(type().toString())
                .isEqualTo("(Country & Identified)");
    }

    private Type type() {
        return Helper.getClassMethod("example.Main#getIdentifiedCountry").getReturnType();
    }
}