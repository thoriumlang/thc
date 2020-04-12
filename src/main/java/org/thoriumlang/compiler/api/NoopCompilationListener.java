package org.thoriumlang.compiler.api;

import org.thoriumlang.compiler.api.errors.CompilationError;
import org.thoriumlang.compiler.input.Source;

public class NoopCompilationListener implements CompilationListener {
    @Override
    public void onCompilationStarted(int sourcesCount) {

    }

    @Override
    public void onCompilationFinished() {

    }

    @Override
    public void onCompilationProgress(float progress) {

    }

    @Override
    public void onSourceStarted(Source source) {

    }

    @Override
    public void onSourceFinished(Source source, CompilationContext context) {

    }

    @Override
    public void onError(Source source, CompilationError error) {

    }

    @Override
    public void onEvent(Event event) {

    }
}
