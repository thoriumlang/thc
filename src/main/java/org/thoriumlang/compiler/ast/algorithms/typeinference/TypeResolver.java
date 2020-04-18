package org.thoriumlang.compiler.ast.algorithms.typeinference;

import org.thoriumlang.compiler.api.errors.SemanticError;
import org.thoriumlang.compiler.ast.algorithms.Algorithm;
import org.thoriumlang.compiler.ast.nodes.Root;

import java.util.List;

public class TypeResolver implements Algorithm {
    @Override
    public List<SemanticError> walk(Root root) {
        return new TypeResolvingVisitor().visit(root);
    }
}
