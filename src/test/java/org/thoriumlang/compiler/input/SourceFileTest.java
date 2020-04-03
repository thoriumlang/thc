package org.thoriumlang.compiler.input;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.ast.AST;
import org.thoriumlang.compiler.ast.algorithms.symboltable.SymbolTableInitializer;
import org.thoriumlang.compiler.symbols.SymbolTable;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Collections;

class SourceFileTest {
    @Test
    void ast_withAlgorithms() throws URISyntaxException, IOException {
        AST ast = new SourceFile(
                "namespace",
                Paths.get(new URI(SourceFileTest.class.getResource("/org/thoriumlang/compiler/tests/class.th").toString()))
        ).ast(Collections.singletonList(new SymbolTableInitializer(new SymbolTable())));

        Assertions.assertThat(ast.root())
                .isNotNull();

        Assertions.assertThat(ast.root().getContext().get(SymbolTable.class))
                .isPresent();
    }

    @Test
    void ast_withoutAlgorithms() throws URISyntaxException, IOException {
        AST ast = new SourceFile(
                "namespace",
                Paths.get(new URI(SourceFileTest.class.getResource("/org/thoriumlang/compiler/tests/class.th").toString()))
        ).ast();

        Assertions.assertThat(ast.root())
                .isNotNull();
    }
}
