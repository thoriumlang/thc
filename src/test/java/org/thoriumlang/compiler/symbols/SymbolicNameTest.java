package org.thoriumlang.compiler.symbols;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.testsupport.NodeStub;

class SymbolicNameTest {
    @Test
    void constructor_definingNode() {
        Assertions.assertThatThrownBy(() -> new SymbolicName(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("definingNode cannot be null");
    }

    @Test
    void _toString() {
        Assertions.assertThat(new SymbolicName(new NodeStub()).toString())
                .isEqualTo("#1");
    }
}
