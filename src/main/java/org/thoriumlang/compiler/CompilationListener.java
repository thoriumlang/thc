package org.thoriumlang.compiler;

import org.thoriumlang.compiler.ast.AST;
import org.thoriumlang.compiler.ast.algorithms.CompilationError;
import org.thoriumlang.compiler.input.Source;

public interface CompilationListener {
    void compilationStarted(int sourcesCount);

    void compilationFinished();

    void compilationProgress(float progress);

    void sourceStarted(Source source);

    void sourceFinished(Source source, AST ast); // TODO probably not AST here...

    void emitError(Source source, CompilationError error);
}
