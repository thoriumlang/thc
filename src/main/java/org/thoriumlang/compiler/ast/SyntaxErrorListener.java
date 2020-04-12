package org.thoriumlang.compiler.ast;

import org.thoriumlang.compiler.api.errors.SyntaxError;

public interface SyntaxErrorListener {
    void onError(SyntaxError syntaxError);
}
