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

class MethodTest {
    private NodeIdGenerator nodeIdGenerator;

    @BeforeEach
    void setup() {
        this.nodeIdGenerator = new NodeIdGenerator();
    }

    @Test
    void constructor_nodeId() {
        try {
            new Method(
                    null,
                    new MethodSignature(
                            nodeIdGenerator.next(),
                            Visibility.PUBLIC,
                            "method",
                            Collections.emptyList(),
                            Collections.emptyList(),
                            new TypeSpecSimple(nodeIdGenerator.next(), "None", Collections.emptyList())
                    ),
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
    void constructor_signature() {
        try {
            new Method(nodeIdGenerator.next(), null, Collections.emptyList());
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("signature cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void constructor_statements() {
        try {
            new Method(
                    nodeIdGenerator.next(),
                    new MethodSignature(
                            nodeIdGenerator.next(),
                            Visibility.PUBLIC,
                            "method",
                            Collections.emptyList(),
                            Collections.emptyList(),
                            new TypeSpecSimple(nodeIdGenerator.next(), "None", Collections.emptyList())
                    ),
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
                new Method(
                        nodeIdGenerator.next(),
                        new MethodSignature(
                                nodeIdGenerator.next(),
                                Visibility.PUBLIC,
                                "method",
                                Collections.emptyList(),
                                Collections.emptyList(),
                                new TypeSpecSimple(nodeIdGenerator.next(), "None", Collections.emptyList())
                        ),
                        Collections.singletonList(
                                new Statement(
                                        nodeIdGenerator.next(),
                                        BooleanValue.TRUE,
                                        true
                                )
                        )
                ).accept(new BaseVisitor<String>() {
                    @Override
                    public String visitMethod(NodeId nodeId, MethodSignature signature, List<Statement> statements) {
                        return String.format(
                                "%s:{%s}:{%s}",
                                nodeId.toString(),
                                signature.toString(),
                                statements.stream()
                                        .map(Statement::toString)
                                        .collect(Collectors.joining(","))
                        );
                    }
                })
        ).isEqualTo("#1:{PUBLIC method [] () : None[]}:{true:true}");
    }

    @Test
    void _toString() {
        Assertions.assertThat(
                new Method(
                        nodeIdGenerator.next(),
                        new MethodSignature(
                                nodeIdGenerator.next(),
                                Visibility.PUBLIC,
                                "method",
                                Collections.emptyList(),
                                Collections.emptyList(),
                                new TypeSpecSimple(nodeIdGenerator.next(), "None", Collections.emptyList())
                        ),
                        Collections.singletonList(
                                new Statement(
                                        nodeIdGenerator.next(),
                                        BooleanValue.TRUE,
                                        true
                                )
                        )
                ).toString()
        ).isEqualTo("PUBLIC method [] () : None[] { true:true }");
    }
}
