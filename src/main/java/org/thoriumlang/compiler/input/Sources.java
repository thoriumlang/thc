package org.thoriumlang.compiler.input;

import org.thoriumlang.compiler.symbols.Name;

import java.util.List;
import java.util.Optional;

public interface Sources {
    List<Source> sources();

    Optional<Source> load(Name name);
}
