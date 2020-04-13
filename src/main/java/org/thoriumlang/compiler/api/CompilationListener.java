package org.thoriumlang.compiler.api;

import org.thoriumlang.compiler.api.errors.CompilationError;
import org.thoriumlang.compiler.input.Source;

public interface CompilationListener {
    void onCompilationStarted();

    void onCompilationFinished();

    void onSourceStarted(Source source);

    void onSourceFinished(Source source, CompilationContext context);

    void onError(Source source, CompilationError error);

    void onEvent(Event event);
}
