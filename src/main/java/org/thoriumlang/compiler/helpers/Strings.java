package org.thoriumlang.compiler.helpers;

import java.util.stream.Stream;

public final class Strings {
    private Strings() {
        // nothing
    }

    public static int indexOfFirst(String haystack, String... needles) {
        return Stream.of(needles)
                .map(haystack::indexOf)
                .filter(pos -> pos > -1)
                .sorted()
                .findFirst()
                .orElse(-1);
    }
}
