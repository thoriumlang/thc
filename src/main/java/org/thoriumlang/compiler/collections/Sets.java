package org.thoriumlang.compiler.collections;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Sets {
    private Sets() {
        // nothing
    }

    @SafeVarargs
    public static <T> Set<T> merge(Collection<T>... collections) {
        Set<T> set = new HashSet<>(
                Arrays.stream(collections)
                        .map(Collection::size)
                        .reduce(0, (i, j) -> j + j)
        );
        for (Collection<T> l : collections) {
            set.addAll(l);
        }
        return set;
    }
}
