package org.thoriumlang.compiler.symbols;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.testsupport.NodeStub;

class AliasSymbolTest {
    @Test
    void constructor_definingNode() {
        Assertions.assertThatThrownBy(() -> new AliasSymbol(null, "target"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("definingNode cannot be null");
    }

    @Test
    void constructor_target() {
        Assertions.assertThatThrownBy(() -> new AliasSymbol(new NodeStub(), null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("target cannot be null");
    }

    @Test
    void _toString() {
        Assertions.assertThat(new AliasSymbol(new NodeStub(), "target").toString())
                .isEqualTo("(alias: target)");
    }
}
