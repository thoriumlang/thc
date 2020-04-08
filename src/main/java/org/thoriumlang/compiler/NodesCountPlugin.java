package org.thoriumlang.compiler;

import org.thoriumlang.compiler.ast.algorithms.CompilationError;
import org.thoriumlang.compiler.ast.algorithms.NodesMatching;
import org.thoriumlang.compiler.ast.nodes.Root;

import java.util.Collections;
import java.util.List;

public class NodesCountPlugin implements Plugin {
    @Override
    public List<CompilationError> execute(Root root) {
        // TODO find a better way (CompliationConext?)
        root.getContext().put(
                NodesCountPlugin.class.getName(),
                Integer.class,
                new NodesMatching(n -> true).visit(root).size()
        );
        return Collections.emptyList();
    }
}
