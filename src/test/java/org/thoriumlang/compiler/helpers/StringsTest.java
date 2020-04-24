package org.thoriumlang.compiler.helpers;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class StringsTest {
    @Test
    void indexOfFirst_none() {
        Assertions.assertThat(Strings.indexOfFirst("_", "[", "("))
                .isEqualTo(-1);
    }

    @Test
    void indexOfFirst_both1() {
        Assertions.assertThat(Strings.indexOfFirst("_[(", "[", "("))
                .isEqualTo(1);
    }

    @Test
    void indexOfFirst_both2() {
        Assertions.assertThat(Strings.indexOfFirst("_([", "[", "("))
                .isEqualTo(1);
    }

    @Test
    void indexOfFirst_first() {
        Assertions.assertThat(Strings.indexOfFirst("_[", "[", "("))
                .isEqualTo(1);
    }

    @Test
    void indexOfFirst_second() {
        Assertions.assertThat(Strings.indexOfFirst("_(", "[", "("))
                .isEqualTo(1);
    }
}