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

class MethodCallValueTest {
    private NodeIdGenerator nodeIdGenerator;

    @BeforeEach
    void setup() {
        this.nodeIdGenerator = new NodeIdGenerator();
    }

    @Test
    void constructor_nodeId() {
        try {
            new MethodCallValue(null, "methodName", Collections.emptyList(), Collections.emptyList());
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("nodeId cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void constructor_methodName() {
        try {
            new MethodCallValue(nodeIdGenerator.next(), null, Collections.emptyList(), Collections.emptyList());
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("methodName cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void constructor_typeArguments() {
        try {
            new MethodCallValue(nodeIdGenerator.next(), "methodName", null, Collections.emptyList());
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("typeArguments cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void constructor_methodArguments() {
        try {
            new MethodCallValue(nodeIdGenerator.next(), "methodName", Collections.emptyList(), null);
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("methodArguments cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void accept() {
        Assertions.assertThat(
                new MethodCallValue(
                        nodeIdGenerator.next(),
                        "methodName",
                        Collections.singletonList(
                                new TypeSpecSimple(nodeIdGenerator.next(), "T", Collections.emptyList())
                        ),
                        Collections.singletonList(new NoneValue(nodeIdGenerator.next()))
                )
                        .accept(new BaseVisitor<String>() {
                            @Override
                            public String visit(MethodCallValue node) {
                                return String.format(
                                        "%s:%s:%s:%s",
                                        node.getNodeId().toString(),
                                        node.getMethodName(),
                                        node.getTypeArguments(),
                                        node.getMethodArguments()
                                );
                            }
                        })
        ).isEqualTo("#1:methodName:[T[]]:[none]");
    }

    @Test
    void _toString() {
        Assertions.assertThat(
                new MethodCallValue(
                        nodeIdGenerator.next(),
                        "methodName",
                        Collections.singletonList(
                                new TypeSpecSimple(nodeIdGenerator.next(), "T", Collections.emptyList())
                        ),
                        Collections.singletonList(new NoneValue(nodeIdGenerator.next()))
                ).toString()
        ).isEqualTo("methodName[T[]](none)");
    }

    @Test
    void getContext() {
        Assertions.assertThat(
                new MethodCallValue(
                        nodeIdGenerator.next(),
                        "methodName",
                        Collections.emptyList(),
                        Collections.emptyList()
                ).getContext()
        ).isNotNull();
    }
}
