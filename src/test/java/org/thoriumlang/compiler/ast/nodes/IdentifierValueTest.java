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

class IdentifierValueTest {
    private NodeIdGenerator nodeIdGenerator;

    @BeforeEach
    void setup() {
        this.nodeIdGenerator = new NodeIdGenerator();
    }

    @Test
    void constructor_nodeId() {
        try {
            new IdentifierValue(null, new Reference(nodeIdGenerator.next(), "id", false));
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("nodeId cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void constructor_value() {
        try {
            new IdentifierValue(nodeIdGenerator.next(), null);
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("reference cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void accept() {
        Assertions.assertThat(
                new IdentifierValue(
                        nodeIdGenerator.next(),
                        new Reference(nodeIdGenerator.next(), "id", false)
                )
                        .accept(new BaseVisitor<String>() {
                            @Override
                            public String visit(IdentifierValue node) {
                                return String.format("%s:%s",
                                        node.getNodeId().toString(),
                                        node.getReference().toString()
                                );
                            }
                        })
        ).isEqualTo("#1:id");
    }

    @Test
    void _toString() {
        Assertions.assertThat(
                new IdentifierValue(
                        nodeIdGenerator.next(),
                        new Reference(nodeIdGenerator.next(), "id", false)
                ).toString()
        ).isEqualTo("id");
    }

    @Test
    void getContext() {
        Assertions.assertThat(
                new IdentifierValue(
                        nodeIdGenerator.next(),
                        new Reference(nodeIdGenerator.next(), "id", false)
                ).getContext()
        ).isNotNull();
    }
}
