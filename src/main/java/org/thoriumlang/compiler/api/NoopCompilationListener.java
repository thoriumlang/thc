package org.thoriumlang.compiler.api;

import org.thoriumlang.compiler.api.errors.CompilationError;
import org.thoriumlang.compiler.input.Source;

public class NoopCompilationListener implements CompilationListener {
    @Override
    public void onCompilationStarted() {
        // noop
    }

    @Override
    public void onCompilationFinished() {
        // noop
    }

    @Override
    public void onSourceStarted(Source source) {
        // noop
    }

    @Override
    public void onSourceFinished(Source source, CompilationContext context) {
        // noop
    }

    @Override
    public void onError(Source source, CompilationError error) {
        // noop
    }

    @Override
    public void onEvent(Event event) {
        // noop
    }
}
