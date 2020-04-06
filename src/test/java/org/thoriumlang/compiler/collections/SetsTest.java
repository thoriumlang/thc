package org.thoriumlang.compiler.collections;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;

class SetsTest {
    @Test
    void merge() {
        Assertions.assertThat(
                Lists.merge(Collections.singletonList("A"), Collections.singletonList("B"))
        ).containsExactly("A", "B");
    }
}