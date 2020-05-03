package org.thoriumlang.compiler.ast.algorithms.typeinference;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.ast.context.SourcePosition;
import org.thoriumlang.compiler.ast.nodes.Node;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.nodes.TypeSpecSimple;
import org.thoriumlang.compiler.testsupport.NodeStub;

import java.util.Collections;

class MethodParameterTypesTest {
    private static final NodeIdGenerator nodeIdGenerator = new NodeIdGenerator();

    @Test
    void constructor() {
        Assertions.assertThatThrownBy(() -> new MethodParameterTypes(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("parameterTypes cannot be null");
    }

    @Test
    void listsSizeDontMatch_1() {
        MethodParameterTypes mpt = new MethodParameterTypes(Collections.singletonList(
                new TypeSpecSimple(nodeIdGenerator.next(), "Type", Collections.emptyList())
        ));
        Assertions.assertThatThrownBy(() -> mpt
                .findBestMatch(new NodeStub(), Collections.singletonMap(new NodeStub(), Collections.emptyList()))
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("potentialMatches types count does not always match parameters types count");
    }

    @Test
    void listsSizeDontMatch_2() {
        Assertions.assertThatThrownBy(() -> new MethodParameterTypes(Collections.emptyList())
                .findBestMatch(
                        new NodeStub(),
                        Collections.singletonMap(
                                new NodeStub(),
                                Collections.singletonList(
                                        new TypeSpecSimple(nodeIdGenerator.next(), "Type", Collections.emptyList())
                                )
                        )
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("potentialMatches types count does not always match parameters types count");
    }

    @Test
    void noMatchFound() {
        MethodParameterTypes mpt = new MethodParameterTypes(Collections.singletonList(new TypeSpecSimple(
                nodeIdGenerator.next(),
                "A",
                Collections.emptyList()
        )));
        NodeStub node = new NodeStub();
        node.getContext().put(
                SourcePosition.class,
                new SourcePosition(
                        new SourcePosition.Position(1,1),
                        new SourcePosition.Position(1,1),
                        Collections.singletonList("")
                )
        );
        Assertions.assertThat(
                mpt.findBestMatch(
                        node,
                        Collections.singletonMap(
                                new NodeStub(),
                                Collections.singletonList(
                                        new TypeSpecSimple(nodeIdGenerator.next(), "B", Collections.emptyList())
                                )
                        )
                ).isSuccess()
        ).isFalse();
    }

    @Test
    void matchFound() {
        Node node = new NodeStub();

        MethodParameterTypes mpt = new MethodParameterTypes(Collections.singletonList(new TypeSpecSimple(
                nodeIdGenerator.next(),
                "A",
                Collections.emptyList()
        )));

        Assertions.assertThat(
                mpt.findBestMatch(
                        new NodeStub(),
                        Collections.singletonMap(
                                node,
                                Collections.singletonList(
                                        new TypeSpecSimple(nodeIdGenerator.next(), "A", Collections.emptyList())
                                )
                        )
                ).value()
        ).isEqualTo(node);
    }
}