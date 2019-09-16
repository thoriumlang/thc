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

class ClassTest {
    private NodeIdGenerator nodeIdGenerator;

    @BeforeEach
    void setup() {
        this.nodeIdGenerator = new NodeIdGenerator();
    }

    @Test
    void constructor_nodeId() {
        try {
            new Class(
                     null,
                    Visibility.PUBLIC,
                    "name",
                    Collections.emptyList(),
                    new TypeSpecSimple(nodeIdGenerator.next(), "Type", Collections.emptyList()),
                    Collections.emptyList(),
                    Collections.emptyList());
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
            new Class(
                    nodeIdGenerator.next(),
                    null,
                    "name",
                    Collections.emptyList(),
                    new TypeSpecSimple(nodeIdGenerator.next(), "Object", Collections.emptyList()),
                    Collections.emptyList(),
                    Collections.emptyList());
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
            new Class(
                    nodeIdGenerator.next(),
                    Visibility.PUBLIC,
                    null,
                    Collections.emptyList(),
                    new TypeSpecSimple(nodeIdGenerator.next(), "Object", Collections.emptyList()),
                    Collections.emptyList(),
                    Collections.emptyList());
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
            new Class(
                    nodeIdGenerator.next(),
                    Visibility.PUBLIC,
                    "name",
                    null,
                    new TypeSpecSimple(nodeIdGenerator.next(), "Object", Collections.emptyList()),
                    Collections.emptyList(),
                    Collections.emptyList());
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
            new Class(
                    nodeIdGenerator.next(),
                    Visibility.PUBLIC,
                    "name",
                    Collections.emptyList(),
                    null,
                    Collections.emptyList(),
                    Collections.emptyList());
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
            new Class(
                    nodeIdGenerator.next(),
                    Visibility.PUBLIC,
                    "name",
                    Collections.emptyList(),
                    new TypeSpecSimple(nodeIdGenerator.next(), "Object", Collections.emptyList()),
                    null,
                    Collections.emptyList());
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("methods cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void constructor_attributes() {
        try {
            new Class(
                    nodeIdGenerator.next(),
                    Visibility.PUBLIC,
                    "name",
                    Collections.emptyList(),
                    new TypeSpecSimple(nodeIdGenerator.next(), "Object", Collections.emptyList()),
                    Collections.emptyList(),
                    null);
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("attributes cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void accept() {
        Assertions.assertThat(
                new Class(
                        nodeIdGenerator.next(),
                        Visibility.PUBLIC,
                        "name",
                        Collections.singletonList(new TypeParameter(nodeIdGenerator.next(), "A")),
                        new TypeSpecSimple(nodeIdGenerator.next(), "Object", Collections.emptyList()),
                        Collections.singletonList(
                                new Method(
                                        nodeIdGenerator.next(),
                                        new MethodSignature(
                                                nodeIdGenerator.next(),
                                                Visibility.PRIVATE,
                                                "name",
                                                Collections.emptyList(),
                                                Collections.emptyList(),
                                                new TypeSpecSimple(
                                                        nodeIdGenerator.next(),
                                                        "type",
                                                        Collections.emptyList())
                                        ),
                                        Collections.singletonList(
                                                new Statement(
                                                        nodeIdGenerator.next(),
                                                        NoneValue.INSTANCE,
                                                        true
                                                )
                                        )
                                )
                        ),
                        Collections.singletonList(
                                new VarAttribute(
                                        nodeIdGenerator.next(),
                                        "attribute",
                                        new TypeSpecSimple(nodeIdGenerator.next(), "None", Collections.emptyList()),
                                        NoneValue.INSTANCE
                                )
                        )
                ).accept(new BaseVisitor<String>() {
                    @Override
                    public String visitClass(NodeId nodeId, Visibility visibility, String name,
                            List<TypeParameter> typeParameters,
                            TypeSpec superType, List<Method> methods, List<Attribute> attributes) {
                        return String.format("%s:%s:%s:[%s]:%s:{ %s ; %s }",
                                nodeId.toString(),
                                visibility,
                                name,
                                typeParameters.stream()
                                        .map(TypeParameter::toString)
                                        .collect(Collectors.joining(", ")),
                                superType,
                                attributes.stream()
                                        .map(Attribute::toString)
                                        .collect(Collectors.joining(", ")),
                                methods.stream()
                                        .map(Method::toString)
                                        .collect(Collectors.joining(", "))
                        );
                    }
                })
        ).isEqualTo("#1:PUBLIC:name:[A]:Object[]:{ " +
                "VAR attribute: None[] = none ; " +
                "PRIVATE name [] () : type[] { none:true } " +
                "}"
        );
    }

    @Test
    void _toString() {
        Assertions.assertThat(
                new Class(
                        nodeIdGenerator.next(),
                        Visibility.PUBLIC,
                        "name",
                        Arrays.asList(
                                new TypeParameter(nodeIdGenerator.next(), "A"),
                                new TypeParameter(nodeIdGenerator.next(), "B")
                        ),
                        new TypeSpecSimple(nodeIdGenerator.next(), "Object", Collections.emptyList()),
                        Collections.singletonList(
                                new Method(
                                        nodeIdGenerator.next(),
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
                                        ),
                                        Collections.singletonList(
                                                new Statement(
                                                        nodeIdGenerator.next(),
                                                        BooleanValue.TRUE,
                                                        false
                                                )
                                        )
                                )
                        ),
                        Collections.singletonList(
                                new VarAttribute(
                                        nodeIdGenerator.next(),
                                        "attribute",
                                        new TypeSpecSimple(
                                                nodeIdGenerator.next(),
                                                "None",
                                                Collections.emptyList()
                                        ),
                                        NoneValue.INSTANCE
                                )
                        )
                ).toString()
        ).isEqualTo("PUBLIC CLASS name[A, B] : Object[]:\n" +
                "VAR attribute: None[] = none\n" +
                "PRIVATE name [] () : returnType[] { true:false }"
        );
    }

    @Test
    void _toStringWithoutTypeParameter() {
        Assertions.assertThat(
                new Class(
                        nodeIdGenerator.next(),
                        Visibility.NAMESPACE,
                        "name",
                        Collections.emptyList(),
                        new TypeSpecSimple(nodeIdGenerator.next(), "Object", Collections.emptyList()),
                        Collections.singletonList(
                                new Method(
                                        nodeIdGenerator.next(),
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
                                        ),
                                        Collections.singletonList(
                                                new Statement(
                                                        nodeIdGenerator.next(),
                                                        BooleanValue.TRUE,
                                                        false
                                                )
                                        )
                                )
                        ),
                        Collections.singletonList(
                                new VarAttribute(
                                        nodeIdGenerator.next(),
                                        "attribute",
                                        new TypeSpecSimple(
                                                nodeIdGenerator.next(),
                                                "None",
                                                Collections.emptyList()
                                        ),
                                        NoneValue.INSTANCE
                                )
                        )
                ).toString()
        ).isEqualTo("NAMESPACE CLASS name[] : Object[]:\n" +
                "VAR attribute: None[] = none\n" +
                "PRIVATE name [] () : returnType[] { true:false }"
        );
    }
}
