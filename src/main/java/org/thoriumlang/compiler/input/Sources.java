package org.thoriumlang.compiler.input;

import java.util.List;
import java.util.Optional;

public interface Sources {
    List<Source> sources();

    Optional<Source> load(String name);
}
