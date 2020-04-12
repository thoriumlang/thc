package org.thoriumlang.compiler.api;

import org.thoriumlang.compiler.api.errors.CompilationError;

import java.util.List;

public interface Plugin {
    List<CompilationError> execute(CompilationContext context);
}
