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

class TypeTest {
    private NodeIdGenerator nodeIdGenerator;

    @BeforeEach
    void setup() {
        this.nodeIdGenerator = new NodeIdGenerator();
    }

    @Test
    void constructor_nodeId() {
        try {
            new Type(
                    null,
                    null,
                    "name",
                    Collections.emptyList(),
                    new TypeSpecSimple(nodeIdGenerator.next(), "Object", Collections.emptyList()),
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
    void constructor_visibility() {
        try {
            new Type(
                    nodeIdGenerator.next(),
                    null,
                    "name",
                    Collections.emptyList(),
                    new TypeSpecSimple(nodeIdGenerator.next(), "Object", Collections.emptyList()),
                    Collections.emptyList()
            );
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("visibility cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void constructor_name() {
        try {
            new Type(
                    nodeIdGenerator.next(),
                    Visibility.PUBLIC,
                    null,
                    Collections.emptyList(),
                    new TypeSpecSimple(nodeIdGenerator.next(), "Object", Collections.emptyList()),
                    Collections.emptyList()
            );
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("name cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void constructor_typeParameters() {
        try {
            new Type(
                    nodeIdGenerator.next(),
                    Visibility.PUBLIC,
                    "name",
                    null,
                    new TypeSpecSimple(nodeIdGenerator.next(), "Object", Collections.emptyList()),
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
    void constructor_implements() {
        try {
            new Type(
                    nodeIdGenerator.next(),
                    Visibility.PUBLIC,
                    "name",
                    Collections.emptyList(),
                    null,
                    Collections.emptyList()
            );
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("superType cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void constructor_methods() {
        try {
            new Type(
                    nodeIdGenerator.next(),
                    Visibility.PUBLIC,
                    "name",
                    Collections.emptyList(),
                    new TypeSpecSimple(nodeIdGenerator.next(), "Object", Collections.emptyList()),
                    null
            );
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("methods cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void accept() {
        Assertions.assertThat(
                new Type(
                        nodeIdGenerator.next(),
                        Visibility.PUBLIC,
                        "name",
                        Collections.singletonList(new TypeParameter(nodeIdGenerator.next(), "A")),
                        new TypeSpecSimple(nodeIdGenerator.next(), "Object", Collections.emptyList()),
                        Collections.singletonList(
                                new MethodSignature(
                                        nodeIdGenerator.next(),
                                        Visibility.PRIVATE,
                                        "name",
                                        Collections.emptyList(),
                                        Collections.emptyList(),
                                        new TypeSpecSimple(
                                                nodeIdGenerator.next(),
                                                "type",
                                                Collections.emptyList()
                                        )
                                )
                        )
                ).accept(new BaseVisitor<String>() {
                    @Override
                    public String visit(Type node) {
                        return String.format("%s:%s:%s:[%s]:%s:%s",
                                node.getNodeId(),
                                node.getVisibility(),
                                node.getName(),
                                node.getTypeParameters().stream()
                                        .map(TypeParameter::toString)
                                        .collect(Collectors.joining(", ")),
                                node.getSuperType(),
                                node.getMethods()
                        );
                    }
                })
        ).isEqualTo("#1:PUBLIC:name:[A]:Object[]:[PRIVATE name [] () : type[]]");
    }

    @Test
    void _toString() {
        Assertions.assertThat(
                new Type(
                        nodeIdGenerator.next(),
                        Visibility.PUBLIC,
                        "name",
                        Arrays.asList(
                                new TypeParameter(nodeIdGenerator.next(), "A"),
                                new TypeParameter(nodeIdGenerator.next(), "B")
                        ),
                        new TypeSpecSimple(nodeIdGenerator.next(), "Object", Collections.emptyList()),
                        Collections.singletonList(
                                new MethodSignature(
                                        nodeIdGenerator.next(),
                                        Visibility.PRIVATE,
                                        "name",
                                        Collections.emptyList(),
                                        Collections.emptyList(),
                                        new TypeSpecSimple(
                                                nodeIdGenerator.next(),
                                                "returnType",
                                                Collections.emptyList()
                                        )
                                )
                        )
                ).toString()
        ).isEqualTo("PUBLIC TYPE name[A, B] : Object[]:\nPRIVATE name [] () : returnType[]");
    }

    @Test
    void _toStringWithoutTypeParameter() {
        Assertions.assertThat(
                new Type(
                        nodeIdGenerator.next(),
                        Visibility.NAMESPACE,
                        "name",
                        Collections.emptyList(),
                        new TypeSpecSimple(nodeIdGenerator.next(), "Object", Collections.emptyList()),
                        Collections.singletonList(
                                new MethodSignature(
                                        nodeIdGenerator.next(),
                                        Visibility.PRIVATE,
                                        "name",
                                        Collections.emptyList(),
                                        Collections.singletonList(new Parameter(
                                                nodeIdGenerator.next(),
                                                "parameter",
                                                new TypeSpecSimple(
                                                        nodeIdGenerator.next(),
                                                        "type",
                                                        Collections.emptyList()
                                                )
                                        )),
                                        new TypeSpecSimple(
                                                nodeIdGenerator.next(),
                                                "returnType",
                                                Collections.emptyList()
                                        )
                                )
                        )
                ).toString()
        ).isEqualTo(
                "NAMESPACE TYPE name[] : Object[]:\nPRIVATE name [] (parameter: type[]) : returnType[]");
    }

    @Test
    void getContext() {
        Assertions.assertThat(
                new Type(
                        nodeIdGenerator.next(),
                        Visibility.NAMESPACE,
                        "name",
                        Collections.emptyList(),
                        new TypeSpecSimple(nodeIdGenerator.next(), "Object", Collections.emptyList()),
                        Collections.emptyList()
                ).getContext()
        ).isNotNull();
    }
}
