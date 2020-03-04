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

class NewAssignmentValueTest {
    private NodeIdGenerator nodeIdGenerator;

    @BeforeEach
    void setup() {
        this.nodeIdGenerator = new NodeIdGenerator();
    }

    @Test
    void constructor_nodeId() {
        try {
            new NewAssignmentValue(
                    null,
                    new Reference(nodeIdGenerator.next(), "identifier"),
                    new TypeSpecSimple(nodeIdGenerator.next(), "T", Collections.emptyList()),
                    new NoneValue(nodeIdGenerator.next()),
                    Mode.VAR
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
            new NewAssignmentValue(
                    nodeIdGenerator.next(),
                    null,
                    new TypeSpecSimple(nodeIdGenerator.next(), "T", Collections.emptyList()),
                    new NoneValue(nodeIdGenerator.next()),
                    Mode.VAR
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
    void constructor_type() {
        try {
            new NewAssignmentValue(
                    nodeIdGenerator.next(),
                    new Reference(nodeIdGenerator.next(), "identifier"),
                    null,
                    new NoneValue(nodeIdGenerator.next()),
                    Mode.VAR
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
            new NewAssignmentValue(
                    nodeIdGenerator.next(),
                    new Reference(nodeIdGenerator.next(), "identifier"),
                    new TypeSpecSimple(nodeIdGenerator.next(), "T", Collections.emptyList()),
                    null,
                    Mode.VAR
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
            new NewAssignmentValue(
                    nodeIdGenerator.next(),
                    new Reference(nodeIdGenerator.next(), "identifier"),
                    new TypeSpecSimple(nodeIdGenerator.next(), "T", Collections.emptyList()),
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
                new NewAssignmentValue(
                        nodeIdGenerator.next(),
                        new Reference(nodeIdGenerator.next(), "identifier"),
                        new TypeSpecSimple(nodeIdGenerator.next(), "T", Collections.emptyList()),
                        new NoneValue(nodeIdGenerator.next()),
                        Mode.VAR
                )
                        .accept(new BaseVisitor<String>() {
                            @Override
                            public String visit(NewAssignmentValue node) {
                                return String.format("%s:%s:%s:%s:%s",
                                        node.getNodeId(),
                                        node.getMode().toString(),
                                        node.getType().toString(),
                                        node.getReference().toString(),
                                        node.getValue().toString()
                                );
                            }
                        })
        ).isEqualTo("#1:VAR:T[]:identifier:none");
    }

    @Test
    void _toString() {
        Assertions.assertThat(
                new NewAssignmentValue(
                        nodeIdGenerator.next(),
                        new Reference(nodeIdGenerator.next(), "identifier"),
                        new TypeSpecSimple(nodeIdGenerator.next(), "T", Collections.emptyList()),
                        new NoneValue(nodeIdGenerator.next()),
                        Mode.VAL
                ).toString()
        ).isEqualTo("VAL T[]:identifier = none");
    }

    @Test
    void getContext() {
        Assertions.assertThat(
                new NewAssignmentValue(
                        nodeIdGenerator.next(),
                        new Reference(nodeIdGenerator.next(), "identifier"),
                        new TypeSpecSimple(nodeIdGenerator.next(), "T", Collections.emptyList()),
                        new NoneValue(nodeIdGenerator.next()),
                        Mode.VAR
                ).getContext()
        ).isNotNull();
    }
}
