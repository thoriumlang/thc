package org.thoriumlang.compiler.data;

import java.util.NoSuchElementException;
import java.util.Objects;

public class Maybe<T, E> {
    private final T value;
    private final E error;

    private Maybe(T value, E error) {
        this.value = value;
        this.error = error;
    }

    public static <T, E> Maybe<T, E> success(T value) {
        return new Maybe<>(Objects.requireNonNull(value, "value cannot be null"), null);
    }

    public static <T, E> Maybe<T, E> failure(E error) {
        return new Maybe<>(null, Objects.requireNonNull(error, "error cannot be null"));
    }

    public boolean isSuccess() {
        return value != null;
    }

    public boolean isFailure() {
        return !isSuccess();
    }

    public T value() {
        if (value == null) {
            throw new NoSuchElementException("No value present");
        }
        return value;
    }

    public E error() {
        if (error == null) {
            throw new NoSuchElementException("No error present");
        }
        return error;
    }
}
