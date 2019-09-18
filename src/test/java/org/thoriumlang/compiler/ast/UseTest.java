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

class UseTest {
    private NodeIdGenerator nodeIdGenerator;

    @BeforeEach
    void setup() {
        this.nodeIdGenerator = new NodeIdGenerator();
    }

    @Test
    void constructor_nodeId() {
        try {
            new Use(null, "from");
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("nodeId cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void constructor_from1() {
        try {
            new Use(nodeIdGenerator.next(), null);
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("from cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void constructor_from2() {
        try {
            new Use(nodeIdGenerator.next(), null, "to");
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("from cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void constructor_to() {
        try {
            new Use(nodeIdGenerator.next(), "from", null);
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("to cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void accept1() {
        Assertions.assertThat(
                new Use(nodeIdGenerator.next(), "f.r.o.m").accept(new BaseVisitor<String>() {
                    @Override
                    public String visit(Use node) {
                        return String.format("%s:%s:%s",
                                node.getNodeId(),
                                node.getFrom(),
                                node.getTo()
                        );
                    }
                })
        ).isEqualTo("#1:f.r.o.m:m");
    }

    @Test
    void accept2() {
        Assertions.assertThat(
                new Use(nodeIdGenerator.next(), "f.r.o.m", "to").accept(new BaseVisitor<String>() {
                    @Override
                    public String visit(Use node) {
                        return String.format("%s:%s:%s",
                                node.getNodeId(),
                                node.getFrom(),
                                node.getTo()
                        );
                    }
                })
        ).isEqualTo("#1:f.r.o.m:to");
    }

    @Test
    void _toString() {
        Assertions.assertThat(
                new Use(nodeIdGenerator.next(), "from", "to").toString()
        ).isEqualTo("USE from : to");
    }
}
