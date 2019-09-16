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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class TypeSpecSimpleTest {
    private NodeIdGenerator nodeIdGenerator;

    @BeforeEach
    void setup() {
        this.nodeIdGenerator = new NodeIdGenerator();
    }

    @Test
    void constructor_nodeId() {
        try {
            new TypeSpecSimple(null, "type", Collections.emptyList());
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("nodeId cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void constructor_type() {
        try {
            new TypeSpecSimple(nodeIdGenerator.next(), null, Collections.emptyList());
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("type cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void constructor_arguments() {
        try {
            new TypeSpecSimple(nodeIdGenerator.next(), "type", null);
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("arguments cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void accept() {
        Assertions.assertThat(
                new TypeSpecSimple(
                        nodeIdGenerator.next(),
                        "type",
                        Arrays.asList(
                                new TypeSpecSimple(nodeIdGenerator.next(), "A", Collections.singletonList(
                                        new TypeSpecSimple(nodeIdGenerator.next(), "C", Collections.emptyList())
                                )),
                                new TypeSpecSimple(nodeIdGenerator.next(), "B", Collections.emptyList())
                        )
                ).accept(new BaseVisitor<String>() {
                    @Override
                    public String visitTypeSingle(NodeId nodeId, String type, List<TypeSpec> arguments) {
                        return String.format(
                                "%s:%s:[%s]",
                                nodeId,
                                type,
                                arguments.stream()
                                        .map(TypeSpec::toString)
                                        .collect(Collectors.joining(","))
                        );
                    }
                })
        ).isEqualTo("#1:type:[A[C[]],B[]]");
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
