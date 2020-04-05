package org.thoriumlang.compiler.symbols;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.testsupport.NodeStub;

class JavaClassTest {
    @Test
    void constructor_definingNode() {
        Assertions.assertThatThrownBy(() -> new JavaClass(null, Object.class))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("definingNode cannot be null");
    }

    @Test
    void constructor_clazz() {
        Assertions.assertThatThrownBy(() -> new JavaClass(new NodeStub(), null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("clazz cannot be null");
    }

    @Test
    void _toString() {
        Assertions.assertThat(new JavaClass(new NodeStub(), Object.class).toString())
                .isEqualTo("(rt.jar: class java.lang.Object)");
    }
}
