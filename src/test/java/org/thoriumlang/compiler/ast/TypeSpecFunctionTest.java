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

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

class TypeSpecFunctionTest {
    private NodeIdGenerator nodeIdGenerator;

    @BeforeEach
    void setup() {
        this.nodeIdGenerator = new NodeIdGenerator();
    }

    @Test
    void constructor_nodeId() {
        try {
            new TypeSpecFunction(
                    null,
                    Collections.singletonList(
                            new TypeSpecSimple(nodeIdGenerator.next(), "None", Collections.emptyList())
                    ),
                    new TypeSpecSimple(nodeIdGenerator.next(), "None", Collections.emptyList())
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
    void constructor_arguments() {
        try {
            new TypeSpecFunction(
                    nodeIdGenerator.next(),
                    null,
                    new TypeSpecSimple(nodeIdGenerator.next(), "None", Collections.emptyList())
            );
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("arguments cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void constructor_returnType() {
        try {
            new TypeSpecFunction(
                    nodeIdGenerator.next(),
                    Collections.singletonList(
                            new TypeSpecSimple(nodeIdGenerator.next(), "None", Collections.emptyList())
                    ),
                    null
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
    void accept() {
        Assertions.assertThat(
                new TypeSpecFunction(
                        nodeIdGenerator.next(),
                        Collections.singletonList(
                                new TypeSpecSimple(nodeIdGenerator.next(), "A", Collections.emptyList())
                        ),
                        new TypeSpecSimple(nodeIdGenerator.next(), "B", Collections.emptyList())
                ).accept(new BaseVisitor<String>() {
                    @Override
                    public String visit(TypeSpecFunction node) {
                        return String.format(
                                "%s:(%s):%s",
                                node.getNodeId(),
                                node.getArguments().stream()
                                        .map(TypeSpec::toString)
                                        .collect(Collectors.joining(",")),
                                node.getReturnType().toString()
                        );
                    }
                })
        ).isEqualTo("#1:(A[]):B[]");
    }

    @Test
    void _toString() {
        Assertions.assertThat(
                new TypeSpecSimple(
                        nodeIdGenerator.next(),
                        "type",
                        Arrays.asList(
                                new TypeSpecSimple(nodeIdGenerator.next(), "A", Collections.emptyList()),
                                new TypeSpecSimple(nodeIdGenerator.next(), "B", Collections.emptyList())
                        )
                ).toString()
        ).isEqualTo("type[A[], B[]]");
    }
}
