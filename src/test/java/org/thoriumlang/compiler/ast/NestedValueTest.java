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

class NestedValueTest {
    private NodeIdGenerator nodeIdGenerator;

    @BeforeEach
    void setup() {
        this.nodeIdGenerator = new NodeIdGenerator();
    }

    @Test
    void constructor_nodeId() {
        try {
            new NestedValue(null, NoneValue.INSTANCE, NoneValue.INSTANCE);
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("nodeId cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void constructor_outer() {
        try {
            new NestedValue(nodeIdGenerator.next(), null, NoneValue.INSTANCE);
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("outer cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void constructor_inner() {
        try {
            new NestedValue(nodeIdGenerator.next(), NoneValue.INSTANCE, null);
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("inner cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void accept() {
        Assertions.assertThat(
                new NestedValue(
                        nodeIdGenerator.next(),
                        BooleanValue.TRUE,
                        NoneValue.INSTANCE
                )
                        .accept(new BaseVisitor<String>() {
                            @Override
                            public String visitNestedValue(NestedValue node) {
                                return String.format(
                                        "%s:%s:%s",
                                        node.getNodeId().toString(),
                                        node.getOuter(),
                                        node.getInner()
                                );
                            }
                        })
        ).isEqualTo("#1:true:none");
    }

    @Test
    void _toString() {
        Assertions.assertThat(
                new NestedValue(
                        nodeIdGenerator.next(),
                        BooleanValue.TRUE,
                        NoneValue.INSTANCE
                ).toString()
        ).isEqualTo("true.none");
    }
}
