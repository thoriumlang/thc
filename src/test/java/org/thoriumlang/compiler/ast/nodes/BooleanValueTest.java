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

class BooleanValueTest {
    private NodeIdGenerator nodeIdGenerator;

    @BeforeEach
    void setup() {
        this.nodeIdGenerator = new NodeIdGenerator();
    }

    @Test
    void constructor_nodeId() {
        try {
            new BooleanValue(
                    null,
                    true);
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("nodeId cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void accept_true() {
        Assertions.assertThat(
                new BooleanValue(nodeIdGenerator.next(), true)
                        .accept(new BaseVisitor<Boolean>() {
                            @Override
                            public Boolean visit(BooleanValue node) {
                                return node.getValue();
                            }
                        })
        ).isEqualTo(true);
    }

    @Test
    void accept_false() {
        Assertions.assertThat(
                new BooleanValue(nodeIdGenerator.next(), false)
                        .accept(new BaseVisitor<Boolean>() {
                            @Override
                            public Boolean visit(BooleanValue node) {
                                return node.getValue();
                            }
                        })
        ).isEqualTo(false);
    }

    @Test
    void _toString() {
        Assertions.assertThat(
                new BooleanValue(nodeIdGenerator.next(), true).toString()
        ).isEqualTo("true");
        Assertions.assertThat(
                new BooleanValue(nodeIdGenerator.next(), false).toString()
        ).isEqualTo("false");
    }

    @Test
    void getContext() {
        Assertions.assertThat(
                new BooleanValue(nodeIdGenerator.next(), true).getContext()
        ).isNotNull();
    }
}
