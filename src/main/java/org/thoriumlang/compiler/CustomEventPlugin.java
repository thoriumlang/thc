package org.thoriumlang.compiler;

import org.thoriumlang.compiler.api.CompilationContext;
import org.thoriumlang.compiler.api.Event;
import org.thoriumlang.compiler.api.Plugin;
import org.thoriumlang.compiler.api.errors.CompilationError;
import org.thoriumlang.compiler.api.errors.SemanticError;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CustomEventPlugin implements Plugin {
    @Override
    public List<CompilationError> execute(CompilationContext context) {
        context.listener().onEvent(new Event(
                Payload.class,
                new Payload(UUID.randomUUID().toString())
        ));

        return Collections.emptyList();
    }

    public static class Payload {
        private final String value;

        public Payload(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }
}
