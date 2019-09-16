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
import java.util.List;
import java.util.stream.Collectors;

class FunctionValueTest {
    private NodeIdGenerator nodeIdGenerator;

    @BeforeEach
    void setup() {
        this.nodeIdGenerator = new NodeIdGenerator();
    }

    @Test
    void constructor_nodeId() {
        try {
            new FunctionValue(
                    null,
                    Collections.emptyList(),
                    Collections.emptyList(),
                    new TypeSpecSimple(nodeIdGenerator.next(), "Type", Collections.emptyList()),
                    Collections.emptyList()
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
    void constructor_typeParameters() {
        try {
            new FunctionValue(
                    nodeIdGenerator.next(),
                    null,
                    Collections.emptyList(),
                    new TypeSpecSimple(nodeIdGenerator.next(), "Type", Collections.emptyList()),
                    Collections.emptyList()
            );
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("typeParameters cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void constructor_parameters() {
        try {
            new FunctionValue(
                    nodeIdGenerator.next(),
                    Collections.emptyList(),
                    null,
                    new TypeSpecSimple(nodeIdGenerator.next(), "Type", Collections.emptyList()),
                    Collections.emptyList()
            );
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("parameters cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void constructor_returnType() {
        try {
            new FunctionValue(
                    nodeIdGenerator.next(),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    null,
                    Collections.emptyList()
            );
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("returnType cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void constructor_statements() {
        try {
            new FunctionValue(
                    nodeIdGenerator.next(),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    new TypeSpecSimple(nodeIdGenerator.next(), "Type", Collections.emptyList()),
                    null
            );
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("statements cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void accept() {
        Assertions.assertThat(
                new FunctionValue(
                        nodeIdGenerator.next(),
                        Collections.singletonList(
                                new TypeParameter(nodeIdGenerator.next(), "T")
                        ),
                        Collections.singletonList(
                                new Parameter(
                                        nodeIdGenerator.next(),
                                        "param",
                                        new TypeSpecSimple(nodeIdGenerator.next(), "P", Collections.emptyList())
                                )
                        ),
                        new TypeSpecSimple(nodeIdGenerator.next(), "Type", Collections.emptyList()),
                        Collections.singletonList(
                                new Statement(nodeIdGenerator.next(), NoneValue.INSTANCE, true)
                        )
                )
                        .accept(new BaseVisitor<String>() {
                            @Override
                            public String visit(FunctionValue node) {
                                return String.format("%s:%s:%s:%s:%s",
                                        node.getNodeId(),
                                        node.getTypeParameters().stream()
                                                .map(TypeParameter::toString)
                                                .collect(Collectors.joining(",")),
                                        node.getParameters().stream()
                                                .map(Parameter::toString)
                                                .collect(Collectors.joining(",")),
                                        node.getReturnType(),
                                        node.getStatements().stream()
                                                .map(Statement::toString)
                                                .collect(Collectors.joining(";"))
                                );
                            }
                        })
        ).isEqualTo("#1:T:param: P[]:Type[]:none:true");
    }

    @Test
    void _toString() {
        Assertions.assertThat(
                new FunctionValue(
                        nodeIdGenerator.next(),
                        Collections.singletonList(
                                new TypeParameter(nodeIdGenerator.next(), "T")
                        ),
                        Collections.singletonList(
                                new Parameter(
                                        nodeIdGenerator.next(),
                                        "param",
                                        new TypeSpecSimple(nodeIdGenerator.next(), "P", Collections.emptyList())
                                )
                        ),
                        new TypeSpecSimple(nodeIdGenerator.next(), "Type", Collections.emptyList()),
                        Collections.singletonList(
                                new Statement(nodeIdGenerator.next(), NoneValue.INSTANCE, true)
                        )
                ).toString()
        ).isEqualTo("[T](param: P[]):Type[] { none:true }");
    }
}
