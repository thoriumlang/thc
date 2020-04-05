package org.thoriumlang.compiler.symbols;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.testsupport.NodeStub;

import java.util.List;

class JavaInterfaceTest {
    @Test
    void constructor_definingNode() {
        Assertions.assertThatThrownBy(() -> new JavaInterface(null, List.class))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("definingNode cannot be null");
    }

    @Test
    void constructor_clazz() {
        Assertions.assertThatThrownBy(() -> new JavaInterface(new NodeStub(), null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("clazz cannot be null");
    }

    @Test
    void _toString() {
        Assertions.assertThat(new JavaInterface(new NodeStub(), List.class).toString())
                .isEqualTo("(rt.jar: interface java.util.List)");
    }
}
