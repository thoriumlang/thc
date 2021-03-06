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

import java.util.Collections;

class AttributeTest {
    private NodeIdGenerator nodeIdGenerator;

    @BeforeEach
    void setup() {
        this.nodeIdGenerator = new NodeIdGenerator();
    }

    @Test
    void constructor_nodeId() {
        try {
            new Attribute(
                    null,
                    "identifier",
                    new TypeSpecSimple(nodeIdGenerator.next(), "None", Collections.emptyList()),
                    new NoneValue(nodeIdGenerator.next()),
                    Mode.VAL
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
            new Attribute(
                    nodeIdGenerator.next(),
                    null,
                    new TypeSpecSimple(nodeIdGenerator.next(), "None", Collections.emptyList()),
                    new NoneValue(nodeIdGenerator.next()),
                    Mode.VAL
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
            new Attribute(
                    nodeIdGenerator.next(),
                    "identifier",
                    null,
                    new NoneValue(nodeIdGenerator.next()),
                    Mode.VAL
            );
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("type cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void constructor_value() {
        try {
            new Attribute(
                    nodeIdGenerator.next(),
                    "identifier",
                    new TypeSpecSimple(nodeIdGenerator.next(), "None", Collections.emptyList()),
                    null,
                    Mode.VAL
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
    void constructor_mode() {
        try {
            new Attribute(
                    nodeIdGenerator.next(),
                    "identifier",
                    new TypeSpecSimple(nodeIdGenerator.next(), "None", Collections.emptyList()),
                    new NoneValue(nodeIdGenerator.next()),
                    null
            );
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("mode cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void accept() {
        Assertions.assertThat(
                new Attribute(
                        nodeIdGenerator.next(),
                        "identifier",
                        new TypeSpecSimple(nodeIdGenerator.next(), "None", Collections.emptyList()),
                        new NoneValue(nodeIdGenerator.next()),
                        Mode.VAL
                )
                        .accept(new BaseVisitor<String>() {
                            @Override
                            public String visit(Attribute node) {
                                return String.format("%s:%s:%s:%s:%s",
                                        node.getNodeId(),
                                        node.getMode().toString(),
                                        node.getName(),
                                        node.getType(),
                                        node.getValue()
                                );
                            }
                        })
        ).isEqualTo("#1:VAL:identifier:None[]:none");
    }

    @Test
    void _toString() {
        Assertions.assertThat(
                new Attribute(
                        nodeIdGenerator.next(),
                        "identifier",
                        new TypeSpecSimple(nodeIdGenerator.next(), "None", Collections.emptyList()),
                        new NoneValue(nodeIdGenerator.next()),
                        Mode.VAR
                ).toString()
        ).isEqualTo("VAR identifier: None[] = none");
    }

    @Test
    void getContext() {
        Assertions.assertThat(
                new Attribute(
                        nodeIdGenerator.next(),
                        "identifier",
                        new TypeSpecSimple(nodeIdGenerator.next(), "None", Collections.emptyList()),
                        new NoneValue(nodeIdGenerator.next()),
                        Mode.VAL
                ).getContext()
        ).isNotNull();
    }
}
