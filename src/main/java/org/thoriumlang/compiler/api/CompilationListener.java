package org.thoriumlang.compiler.api;

import org.thoriumlang.compiler.ast.algorithms.CompilationError;
import org.thoriumlang.compiler.input.Source;

public interface CompilationListener {
    void onCompilationStarted(int sourcesCount);

    void onCompilationFinished();

    void onCompilationProgress(float progress);

    void onSourceStarted(Source source);

    void onSourceFinished(Source source, CompilationContext context);

    void onError(Source source, CompilationError error);

    void onEvent(Event event);
}