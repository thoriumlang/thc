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
package org.thoriumlang.compiler.ast.nodes;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.ast.visitor.BaseVisitor;

class DirectAssignmentValueTest {
    private NodeIdGenerator nodeIdGenerator;

    @BeforeEach
    void setup() {
        this.nodeIdGenerator = new NodeIdGenerator();
    }

    @Test
    void constructor_nodeId() {
        try {
            new DirectAssignmentValue(
                    null,
                    new Reference(nodeIdGenerator.next(), "identifier"),
                    new NoneValue(nodeIdGenerator.next())
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
    void constructor_reference() {
        try {
            new IndirectAssignmentValue(
                    nodeIdGenerator.next(),
                    new NoneValue(nodeIdGenerator.next()),
                    null,
                    new NoneValue(nodeIdGenerator.next())
            );
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("reference cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void constructor_value() {
        try {
            new DirectAssignmentValue(
                    nodeIdGenerator.next(),
                    new Reference(nodeIdGenerator.next(), "identifier"),
                    null
            );
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("value cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void accept() {
        Assertions.assertThat(
                new DirectAssignmentValue(
                        nodeIdGenerator.next(),
                        new Reference(nodeIdGenerator.next(), "identifier"),
                        new NoneValue(nodeIdGenerator.next())
                )
                        .accept(new BaseVisitor<String>() {
                            @Override
                            public String visit(DirectAssignmentValue node) {
                                return String.format(
                                        "%s:%s:%s",
                                        node.getNodeId().toString(),
                                        node.getReference().toString(),
                                        node.getValue().toString()
                                );
                            }
                        })
        ).isEqualTo("#1:identifier:none");
    }

    @Test
    void _toString() {
        Assertions.assertThat(
                new DirectAssignmentValue(
                        nodeIdGenerator.next(),
                        new Reference(nodeIdGenerator.next(), "identifier"),
                        new NoneValue(nodeIdGenerator.next())
                ).toString()
        ).isEqualTo("DIRECT identifier = none");
    }

    @Test
    void getContext() {
        Assertions.assertThat(
                new DirectAssignmentValue(
                        nodeIdGenerator.next(),
                        new Reference(nodeIdGenerator.next(), "identifier"),
                        new NoneValue(nodeIdGenerator.next())
                ).getContext()
        ).isNotNull();
    }
}
