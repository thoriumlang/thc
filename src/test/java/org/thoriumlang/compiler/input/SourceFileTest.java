package org.thoriumlang.compiler.input;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.ast.AST;
import org.thoriumlang.compiler.ast.algorithms.symboltable.SymbolTableInitializer;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.symbols.SymbolTable;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Collections;

class SourceFileTest {
    @Test
    void ast_withAlgorithms() throws URISyntaxException {
        AST ast = new SourceFile(
                "namespace",
                Paths.get(new URI(SourceFileTest.class.getResource("/org/thoriumlang/compiler/tests/class.th").toString()))
        ).ast(
                new NodeIdGenerator(),
                Collections.singletonList(new SymbolTableInitializer(new SymbolTable()))
        );

        Assertions.assertThat(ast.root())
                .isNotNull();

        Assertions.assertThat(ast
                .root()
                .orElseThrow(() -> new IllegalStateException("no root found"))
                .getContext()
                .get(SymbolTable.class)
        ).isPresent();
    }

    @Test
    void ast_withoutAlgorithms() throws URISyntaxException {
        AST ast = new SourceFile(
                "namespace",
                Paths.get(new URI(SourceFileTest.class.getResource("/org/thoriumlang/compiler/tests/class.th").toString()))
        ).ast(new NodeIdGenerator(), Collections.emptyList());

        Assertions.assertThat(ast.root())
                .isNotNull();
    }
}
