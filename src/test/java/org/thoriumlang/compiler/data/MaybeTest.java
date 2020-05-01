package org.thoriumlang.compiler.data;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

class MaybeTest {
    @Test
    void factory_success() {
        Assertions.assertThatThrownBy(() -> Maybe.success(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value cannot be null");
    }

    @Test
    void factory_failure() {
        Assertions.assertThatThrownBy(() -> Maybe.failure(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("error cannot be null");
    }

    @Test
    void success() {
        Maybe<String, String> maybe = Maybe.success("Success");
        Assertions.assertThat(maybe.success()).isTrue();
        Assertions.assertThat(maybe.get()).isEqualTo("Success");
        Assertions.assertThatThrownBy(() -> maybe.error())
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("No error present");
    }
    @Test
    void failure() {
        Maybe<String, String> maybe = Maybe.failure("Failure");
        Assertions.assertThat(maybe.success()).isFalse();
        Assertions.assertThat(maybe.error()).isEqualTo("Failure");
        Assertions.assertThatThrownBy(() -> maybe.get())
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("No value present");
    }
}