package org.thoriumlang.compiler.input;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.ast.AST;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;

class SourceFileTest {
    @Test
    void ast() throws URISyntaxException {
        AST ast = new SourceFile(
                "namespace",
                Paths.get(new URI(SourceFileTest.class.getResource("/org/thoriumlang/compiler/input/class.th").toString()))
        ).ast(new NodeIdGenerator());

        Assertions.assertThat(ast.root())
                .isNotNull();
    }
}
