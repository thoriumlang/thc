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

import java.util.Collections;

class ParameterTest {
    private NodeIdGenerator nodeIdGenerator;

    @BeforeEach
    void setup() {
        this.nodeIdGenerator = new NodeIdGenerator();
    }

    @Test
    void constructor_nodeId() {
        try {
            new Parameter(
                    null,
                    "name",
                    new TypeSpecSimple(
                            nodeIdGenerator.next(),
                            "test",
                            Collections.emptyList()
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
    void constructor_name() {
        try {
            new Parameter(
                    nodeIdGenerator.next(),
                    null,
                    new TypeSpecSimple(
                            nodeIdGenerator.next(),
                            "test",
                            Collections.emptyList()
                    )
            );
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("name cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void constructor_type() {
        try {
            new Parameter(nodeIdGenerator.next(), "name", null);
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("type cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void accept() {
        Assertions.assertThat(
                new Parameter(
                        nodeIdGenerator.next(),
                        "name",
                        new TypeSpecSimple(nodeIdGenerator.next(), "type", Collections.emptyList())
                ).accept(new BaseVisitor<String>() {
                    @Override
                    public String visitParameter(NodeId nodeId, String name, TypeSpec type) {
                        return String.format("%s:%s:%s",
                                nodeId.toString(),
                                name,
                                type.toString()
                        );
                    }
                })
        ).isEqualTo("#1:name:type[]");
    }

    @Test
    void _toString() {
        Assertions.assertThat(
                new Parameter(
                        nodeIdGenerator.next(),
                        "name",
                        new TypeSpecSimple(
                                nodeIdGenerator.next(),
                                "type",
                                Collections.emptyList()
                        )
                ).toString()
        ).isEqualTo("name: type[]");
    }
}
