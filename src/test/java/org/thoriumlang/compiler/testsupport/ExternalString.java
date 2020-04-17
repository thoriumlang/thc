package org.thoriumlang.compiler.testsupport;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public final class ExternalString {
    private ExternalString() {
        // nothing
    }

    public static String fromClasspath(String filePath) {
        return new BufferedReader(
                new InputStreamReader(
                        ExternalString.class.getResourceAsStream(filePath)
                )
        ).lines().collect(Collectors.joining("\n"));
    }
}
