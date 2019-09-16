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

class RootTest {
    private NodeIdGenerator nodeIdGenerator;

    @BeforeEach
    void setup() {
        this.nodeIdGenerator = new NodeIdGenerator();
    }

    @Test
    void constructor_nodeId() {
        try {
            new Root(
                    null,
                    "namespace",
                    Collections.emptyList(),
                    new Type(
                            nodeIdGenerator.next(),
                            Visibility.PUBLIC,
                            "name",
                            Collections.emptyList(),
                            new TypeSpecSimple(
                                    nodeIdGenerator.next(),
                                    "Object",
                                    Collections.emptyList()
                            ),
                            Collections.emptyList()
                    )
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
    void constructor_namespace() {
        try {
            new Root(
                    nodeIdGenerator.next(),
                    null,
                    Collections.emptyList(),
                    new Type(
                            nodeIdGenerator.next(),
                            Visibility.PUBLIC,
                            "name",
                            Collections.emptyList(),
                            new TypeSpecSimple(
                                    nodeIdGenerator.next(),
                                    "Object",
                                    Collections.emptyList()
                            ),
                            Collections.emptyList()
                    )
            );
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("namespace cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void constructor_uses() {
        try {
            new Root(
                    nodeIdGenerator.next(),
                    "namespace",
                    null,
                    new Type(
                            nodeIdGenerator.next(),
                            Visibility.PUBLIC,
                            "name",
                            Collections.emptyList(),
                            new TypeSpecSimple(
                                    nodeIdGenerator.next(),
                                    "Object",
                                    Collections.emptyList()
                            ),
                            Collections.emptyList()
                    )
            );
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("uses cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void constructor_clazz() {
        try {
            new Root(nodeIdGenerator.next(), "namespace", Collections.emptyList(), (Class) null);
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("topLevel cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void constructor_type() {
        try {
            new Root(nodeIdGenerator.next(), "namespace", Collections.emptyList(), (Type) null);
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("topLevel cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void accept_type() {
        Assertions.assertThat(
                new Root(
                        nodeIdGenerator.next(),
                        "namespace",
                        Collections.singletonList(new Use(nodeIdGenerator.next(), "from")),
                        new Type(
                                nodeIdGenerator.next(),
                                Visibility.NAMESPACE,
                                "name",
                                Collections.emptyList(),
                                new TypeSpecSimple(
                                        nodeIdGenerator.next(),
                                        "Object",
                                        Collections.emptyList()
                                ),
                                Collections.emptyList()
                        )
                ).accept(new BaseVisitor<String>() {
                    @Override
                    public String visitRoot(NodeId nodeId, String namespace, List<Use> uses, Type type) {
                        return String.format("%s:%s:%s:%s",
                                nodeId,
                                namespace,
                                uses,
                                type
                        );
                    }
                })
        ).isEqualTo("#1:namespace:[USE from : from]:NAMESPACE TYPE name[] : Object[]:");
    }

    @Test
    void accept_clazz() {
        Assertions.assertThat(
                new Root(
                        nodeIdGenerator.next(),
                        "namespace",
                        Collections.singletonList(new Use(nodeIdGenerator.next(), "from")),
                        new Class(
                                nodeIdGenerator.next(),
                                Visibility.NAMESPACE,
                                "name",
                                Collections.emptyList(),
                                new TypeSpecSimple(
                                        nodeIdGenerator.next(),
                                        "Object",
                                        Collections.emptyList()
                                ),
                                Collections.emptyList(),
                                Collections.emptyList()
                        )
                ).accept(new BaseVisitor<String>() {
                    @Override
                    public String visitRoot(NodeId nodeId, String namespace, List<Use> uses, Class clazz) {
                        return String.format("%s:%s:%s:%s",
                                nodeIdGenerator.next(),
                                namespace,
                                uses,
                                clazz
                        );
                    }
                })
        ).isEqualTo("#5:namespace:[USE from : from]:NAMESPACE CLASS name[] : Object[]:");
    }

    @Test
    void _toString_type() {
        Assertions.assertThat(
                new Root(
                        nodeIdGenerator.next(),
                        "namespace",
                        Collections.singletonList(new Use(nodeIdGenerator.next(), "from")),
                        new Type(
                                nodeIdGenerator.next(),
                                Visibility.PUBLIC,
                                "name",
                                Collections.emptyList(),
                                new TypeSpecSimple(
                                        nodeIdGenerator.next(),
                                        "Object",
                                        Collections.emptyList()
                                ),
                                Collections.emptyList()
                        )
                ).toString()
        ).isEqualTo("NAMESPACE namespace\nUSE from : from\nPUBLIC TYPE name[] : Object[]:");
    }

    @Test
    void _toString_clazz() {
        Assertions.assertThat(
                new Root(
                        nodeIdGenerator.next(),
                        "namespace",
                        Collections.singletonList(new Use(nodeIdGenerator.next(), "from")),
                        new Class(
                                nodeIdGenerator.next(),
                                Visibility.PUBLIC,
                                "name",
                                Collections.emptyList(),
                                new TypeSpecSimple(
                                        nodeIdGenerator.next(),
                                        "Object",
                                        Collections.emptyList()
                                ),
                                Collections.emptyList(),
                                Collections.emptyList()
                        )
                ).toString()
        ).isEqualTo("NAMESPACE namespace\nUSE from : from\nPUBLIC CLASS name[] : Object[]:");
    }
}
