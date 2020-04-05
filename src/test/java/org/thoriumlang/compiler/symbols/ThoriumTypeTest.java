package org.thoriumlang.compiler.symbols;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.testsupport.NodeStub;

class ThoriumTypeTest {
    @Test
    void constructor_definingNode() {
        Assertions.assertThatThrownBy(() -> new ThoriumType(null, new NodeStub()))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("definingNode cannot be null");
    }

    @Test
    void constructor_node() {
        Assertions.assertThatThrownBy(() -> new ThoriumType(new NodeStub(), null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("node cannot be null");
    }
}
