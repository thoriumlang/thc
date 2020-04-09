package org.thoriumlang.compiler.api;

import java.util.Objects;
import java.util.Optional;

public class Event {
    private final Class<?> type;
    private final Object payload;

    public <T> Event(Class<T> type, T payload) {
        this.type = Objects.requireNonNull(type, "type cannot be null");
        this.payload = Objects.requireNonNull(payload, "payload cannot be null");
    }

    @SuppressWarnings("unchecked") // we know it's the correct type thanks to the ctor
    public <T> Optional<T> payload(Class<T> type) {
        if (this.type == type) {
            return Optional.of((T) payload);
        }
        return Optional.empty();
    }
}
