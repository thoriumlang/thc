/*
 * Copyright 2019 Christophe Pollet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thoriumlang.compiler.ast;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.ast.visitor.BaseVisitor;

import java.util.Collections;
import java.util.stream.Collectors;

class TypeSpecUnionTest {
    private NodeIdGenerator nodeIdGenerator;

    @BeforeEach
    void setup() {
        this.nodeIdGenerator = new NodeIdGenerator();
    }

    @Test
    void constructor_nodeId() {
        try {
            new TypeSpecUnion(
                    null,
                    Collections.singletonList(
                            new TypeSpecSimple(nodeIdGenerator.next(), "type", Collections.emptyList())
                    )
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
            new TypeSpecUnion(
                    nodeIdGenerator.next(),
                    null
            );
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
                new TypeSpecUnion(
                        nodeIdGenerator.next(),
                        Collections.singletonList(
                                new TypeSpecSimple(nodeIdGenerator.next(), "type", Collections.emptyList())
                        )
                ).accept(new BaseVisitor<String>() {
                    @Override
                    public String visit(TypeSpecUnion node) {
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
                new TypeSpecUnion(
                        nodeIdGenerator.next(),
                        Collections.singletonList(
                                new TypeSpecSimple(nodeIdGenerator.next(), "type", Collections.emptyList())
                        )
                ).toString()
        ).isEqualTo("u:[type[]]");
    }
}
