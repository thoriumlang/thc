package org.thoriumlang.compiler.ast.context;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;


class ReferencedNodeTest {
    @Test
    void constructor_nodesIsNull() {
        Assertions.assertThatThrownBy(() -> new ReferencedNode(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("nodes cannot be null");
    }

    @Test
    void constructor_nodesIsEmpty() {
        Assertions.assertThatThrownBy(() -> new ReferencedNode(Collections.emptyList()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("nodes cannot be empty");
    }
}