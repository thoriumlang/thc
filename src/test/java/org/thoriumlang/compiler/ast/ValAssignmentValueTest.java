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

class ValAssignmentValueTest {
    private NodeIdGenerator nodeIdGenerator;

    @BeforeEach
    void setup() {
        this.nodeIdGenerator = new NodeIdGenerator();
    }

    @Test
    void constructor_nodeId() {
        try {
            new ValAssignmentValue(
                    null,
                    "identifier",
                    new TypeSpecSimple(nodeIdGenerator.next(), "T", Collections.emptyList()),
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
            new ValAssignmentValue(
                    nodeIdGenerator.next(),
                    null,
                    new TypeSpecSimple(nodeIdGenerator.next(), "T", Collections.emptyList()),
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
            new ValAssignmentValue(
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
            new ValAssignmentValue(nodeIdGenerator.next(), "identifier",
                    new TypeSpecSimple(nodeIdGenerator.next(), "T", Collections.emptyList()), null);
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
                new ValAssignmentValue(
                        nodeIdGenerator.next(),
                        "identifier",
                        new TypeSpecSimple(nodeIdGenerator.next(), "T", Collections.emptyList()),
                        new NoneValue(nodeIdGenerator.next())
                )
                        .accept(new BaseVisitor<String>() {
                            @Override
                            public String visit(ValAssignmentValue node) {
                                return String.format("%s:%s:%s:%s",
                                        node.getNodeId(),
                                        node.getType().toString(),
                                        node.getIdentifier(),
                                        node.getValue().toString()
                                );
                            }
                        })
        ).isEqualTo("#1:T[]:identifier:none");
    }

    @Test
    void _toString() {
        Assertions.assertThat(
                new ValAssignmentValue(
                        nodeIdGenerator.next(),
                        "identifier",
                        new TypeSpecSimple(nodeIdGenerator.next(), "T", Collections.emptyList()),
                        new NoneValue(nodeIdGenerator.next())
                ).toString()
        ).isEqualTo("VAL T[]:identifier = none");
    }

    @Test
    void getContext() {
        Assertions.assertThat(
                new ValAssignmentValue(
                        nodeIdGenerator.next(),
                        "identifier",
                        new TypeSpecSimple(nodeIdGenerator.next(), "T", Collections.emptyList()),
                        new NoneValue(nodeIdGenerator.next())
                ).getContext()
        ).isNotNull();
    }
}
