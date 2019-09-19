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

public class ValAttributeTest {
    private NodeIdGenerator nodeIdGenerator;

    @BeforeEach
    void setup() {
        this.nodeIdGenerator = new NodeIdGenerator();
    }

    @Test
    void constructor_nodeId() {
        try {
            new ValAttribute(
                    null,
                    null,
                    new TypeSpecSimple(nodeIdGenerator.next(), "None", Collections.emptyList()),
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
    void constructor_identifier() {
        try {
            new ValAttribute(
                    nodeIdGenerator.next(),
                    null,
                    new TypeSpecSimple(nodeIdGenerator.next(), "None", Collections.emptyList()),
                    new NoneValue(nodeIdGenerator.next())
            );
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("identifier cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void constructor_type() {
        try {
            new ValAttribute(
                    nodeIdGenerator.next(),
                    "identifier",
                    null,
                    new NoneValue(nodeIdGenerator.next())
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
            new ValAttribute(
                    nodeIdGenerator.next(),
                    "identifier",
                    new TypeSpecSimple(nodeIdGenerator.next(), "None", Collections.emptyList()),
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
                new ValAttribute(
                        nodeIdGenerator.next(),
                        "identifier",
                        new TypeSpecSimple(nodeIdGenerator.next(), "None", Collections.emptyList()),
                        new NoneValue(nodeIdGenerator.next())
                )
                        .accept(new BaseVisitor<String>() {
                            @Override
                            public String visit(ValAttribute node) {
                                return String.format("%s:%s:%s:%s",
                                        node.getNodeId(),
                                        node.getIdentifier(),
                                        node.getType(),
                                        node.getValue()
                                );
                            }
                        })
        ).isEqualTo("#1:identifier:None[]:none");
    }

    @Test
    void _toString() {
        Assertions.assertThat(
                new ValAttribute(
                        nodeIdGenerator.next(),
                        "identifier",
                        new TypeSpecSimple(nodeIdGenerator.next(), "None", Collections.emptyList()),
                        new NoneValue(nodeIdGenerator.next())
                ).toString()
        ).isEqualTo("VAL identifier: None[] = none");
    }

    @Test
    void getContext() {
        Assertions.assertThat(
                new ValAttribute(
                        nodeIdGenerator.next(),
                        "identifier",
                        new TypeSpecSimple(nodeIdGenerator.next(), "None", Collections.emptyList()),
                        new NoneValue(nodeIdGenerator.next())
                ).getContext()
        ).isNotNull();
    }
}
