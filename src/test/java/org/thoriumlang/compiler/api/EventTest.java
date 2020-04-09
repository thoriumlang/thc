package org.thoriumlang.compiler.api;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class EventTest {
    @Test
    void constructor_type() {
        Assertions.assertThatThrownBy(() -> new Event(null, "Object"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("type cannot be null");
    }

    @Test
    void constructor_object() {
        Assertions.assertThatThrownBy(() -> new Event(String.class, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("payload cannot be null");
    }

    @Test
    void payload_sameType() {
        Assertions.assertThat(new Event(String.class, "String").payload(String.class))
                .get()
                .isEqualTo("String");
    }

    @Test
    void payload_differentType() {
        Assertions.assertThat(new Event(String.class, "String").payload(Long.class))
                .isEmpty();
    }
}