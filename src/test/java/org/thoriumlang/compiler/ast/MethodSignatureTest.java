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

import java.util.Collections;
import java.util.stream.Collectors;

class MethodSignatureTest {
    private NodeIdGenerator nodeIdGenerator;

    @BeforeEach
    void setup() {
        this.nodeIdGenerator = new NodeIdGenerator();
    }

    @Test
    void constructor_nodeId() {
        try {
            new MethodSignature(null, Visibility.PRIVATE, "name", Collections.emptyList(), Collections.emptyList(),
                    new TypeSpecSimple(nodeIdGenerator.next(), "test", Collections.emptyList()));
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
            new MethodSignature(nodeIdGenerator.next(), null, "name", Collections.emptyList(), Collections.emptyList(),
                    new TypeSpecSimple(nodeIdGenerator.next(), "test", Collections.emptyList()));
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
            new MethodSignature(nodeIdGenerator.next(), Visibility.PRIVATE, null, Collections.emptyList(),
                    Collections.emptyList(),
                    new TypeSpecSimple(nodeIdGenerator.next(), "test", Collections.emptyList()));
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
            new MethodSignature(
                    nodeIdGenerator.next(),
                    Visibility.PRIVATE,
                    "name",
                    null,
                    Collections.emptyList(),
                    new TypeSpecSimple(nodeIdGenerator.next(), "test", Collections.emptyList())
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
    void constructor_parameters() {
        try {
            new MethodSignature(
                    nodeIdGenerator.next(),
                    Visibility.PRIVATE,
                    "name",
                    Collections.emptyList(),
                    null,
                    new TypeSpecSimple(nodeIdGenerator.next(), "test", Collections.emptyList())
            );
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("parameters cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void constructor_returnType() {
        try {
            new MethodSignature(
                    nodeIdGenerator.next(),
                    Visibility.PRIVATE,
                    "name",
                    Collections.emptyList(),
                    Collections.emptyList(),
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
                new MethodSignature(
                        nodeIdGenerator.next(),
                        Visibility.PRIVATE,
                        "name",
                        Collections.singletonList(new TypeParameter(nodeIdGenerator.next(), "T")),
                        Collections.singletonList(new Parameter(
                                nodeIdGenerator.next(),
                                "name",
                                new TypeSpecSimple(
                                        nodeIdGenerator.next(),
                                        "type",
                                        Collections.emptyList()
                                )
                        )),
                        new TypeSpecSimple(nodeIdGenerator.next(), "test", Collections.emptyList())
                ).accept(new BaseVisitor<String>() {
                    @Override
                    public String visit(MethodSignature node) {
                        return String.format(
                                "%s:%s:%s:[%s]:(%s):%s",
                                node.getNodeId().toString(),
                                node.getVisibility(),
                                node.getName(),
                                node.getTypeParameters().stream()
                                        .map(TypeParameter::toString)
                                        .collect(Collectors.joining(",")),
                                node.getParameters().stream()
                                        .map(Parameter::toString)
                                        .collect(Collectors.joining(",")),
                                node.getReturnType()
                        );
                    }
                })
        ).isEqualTo("#1:PRIVATE:name:[T]:(name: type[]):test[]");
    }

    @Test
    void _toString() {
        Assertions.assertThat(
                new MethodSignature(
                        nodeIdGenerator.next(),
                        Visibility.PRIVATE,
                        "name",
                        Collections.singletonList(new TypeParameter(nodeIdGenerator.next(), "T")),
                        Collections.singletonList(new Parameter(
                                nodeIdGenerator.next(),
                                "name",
                                new TypeSpecSimple(nodeIdGenerator.next(), "type", Collections.emptyList())
                        )),
                        new TypeSpecSimple(nodeIdGenerator.next(), "returnType", Collections.emptyList())
                ).toString()
        ).isEqualTo("PRIVATE name [T] (name: type[]) : returnType[]");
    }

    @Test
    void getContext() {
        Assertions.assertThat(
                new MethodSignature(
                        nodeIdGenerator.next(),
                        Visibility.PRIVATE,
                        "name",
                        Collections.emptyList(),
                        Collections.emptyList(),
                        new TypeSpecSimple(nodeIdGenerator.next(), "returnType", Collections.emptyList())
                ).getContext()
        ).isNotNull();
    }
}
