package org.thoriumlang.compiler.input.loaders;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.ast.nodes.Node;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.visitor.Visitor;
import org.thoriumlang.compiler.symbols.Name;
import org.thoriumlang.compiler.symbols.Symbol;

import java.util.Optional;

class ThoriumRTClassLoaderTest {
    private final Node node = new Node(new NodeIdGenerator().next()) {
        @Override
        public <T> T accept(Visitor<? extends T> visitor) {
            return null;
        }
    };

    @Test
    void load_success() {
        Optional<Symbol> symbol = new ThoriumRTClassLoader()
                .load(new Name("org.thoriumlang.Object"), node);

        Assertions.assertThat(symbol)
                .get()
                .extracting(Object::toString)
                .isEqualTo("(th-rt: org.thoriumlang.Object)");
    }

    @Test
    void load_failure() {
        Optional<Symbol> symbol = new ThoriumRTClassLoader()
                .load(new Name("org.thoriumlang.Unknown"), node);

        Assertions.assertThat(symbol)
                .isEmpty();
    }
}
