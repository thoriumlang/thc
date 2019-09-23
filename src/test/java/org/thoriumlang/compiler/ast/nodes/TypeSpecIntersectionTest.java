package org.thoriumlang.compiler.ast.nodes;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.ast.visitor.BaseVisitor;

import java.util.Collections;
import java.util.stream.Collectors;

class TypeSpecIntersectionTest {
    private NodeIdGenerator nodeIdGenerator;

    @BeforeEach
    void setup() {
        this.nodeIdGenerator = new NodeIdGenerator();
    }

    @Test
    void constructor_nodeId() {
        try {
            new TypeSpecIntersection(
                    null,
                    Collections.singletonList(new TypeSpecSimple(
                            nodeIdGenerator.next(),
                            "type",
                            Collections.emptyList()
                    ))
            );
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("nodeId cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void constructor_types() {
        try {
            new TypeSpecIntersection(nodeIdGenerator.next(), null);
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("types cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void accept() {
        Assertions.assertThat(
                new TypeSpecIntersection(
                        nodeIdGenerator.next(),
                        Collections.singletonList(new TypeSpecSimple(
                                nodeIdGenerator.next(),
                                "type",
                                Collections.emptyList()
                        ))
                ).accept(new BaseVisitor<String>() {
                    @Override
                    public String visit(TypeSpecIntersection node) {
                        return String.format("%s:%s",
                                node.getNodeId(),
                                node.getTypes().stream()
                                        .map(TypeSpec::toString)
                                        .collect(Collectors.joining(","))
                        );
                    }
                })
        ).isEqualTo("#1:type[]");
    }

    @Test
    void _toString() {
        Assertions.assertThat(
                new TypeSpecIntersection(
                        nodeIdGenerator.next(),
                        Collections.singletonList(
                                new TypeSpecSimple(nodeIdGenerator.next(), "type", Collections.emptyList())
                        )
                ).toString()
        ).isEqualTo("i:[type[]]");
    }

    @Test
    void getContext() {
        Assertions.assertThat(
                new TypeSpecIntersection(
                        nodeIdGenerator.next(),
                        Collections.emptyList()
                ).getContext()
        ).isNotNull();
    }
}