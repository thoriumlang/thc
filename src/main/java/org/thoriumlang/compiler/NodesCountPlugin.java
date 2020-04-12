package org.thoriumlang.compiler;

import org.thoriumlang.compiler.api.CompilationContext;
import org.thoriumlang.compiler.api.errors.CompilationError;
import org.thoriumlang.compiler.api.Plugin;
import org.thoriumlang.compiler.ast.algorithms.NodesMatching;

import java.util.Collections;
import java.util.List;

public class NodesCountPlugin implements Plugin {
    @Override
    public List<CompilationError> execute(CompilationContext context) {
        context.put(
                Count.class,
                new Count(new NodesMatching(n -> true).visit(context.root()).size())
        );

        return Collections.emptyList();
    }

    public static class Count {
        private final int count;

        public Count(int count) {
            this.count = count;
        }

        public int getCount() {
            return count;
        }
    }
}
