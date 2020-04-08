package org.thoriumlang.compiler;

import org.thoriumlang.compiler.ast.algorithms.CompilationError;
import org.thoriumlang.compiler.ast.nodes.Root;

import java.util.List;

public interface Plugin {
    List<CompilationError> execute(Root root);
}
