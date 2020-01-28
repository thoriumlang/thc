/*
 * Copyright 2020 Christophe Pollet
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
package org.thoriumlang.compiler.ast.context;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.ast.context.Relatives;
import org.thoriumlang.compiler.ast.nodes.MethodSignature;
import org.thoriumlang.compiler.ast.nodes.Node;
import org.thoriumlang.compiler.ast.nodes.NodeId;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.nodes.Type;
import org.thoriumlang.compiler.ast.nodes.TypeSpecSimple;
import org.thoriumlang.compiler.ast.nodes.Visibility;
import org.thoriumlang.compiler.ast.visitor.BaseVisitor;
import org.thoriumlang.compiler.ast.visitor.Visitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class RelativesTest {
    private NodeIdGenerator nodeIdGenerator;

    @BeforeEach
    void setup() {
        this.nodeIdGenerator = new NodeIdGenerator();
    }

    @Test
    void node() {
        Node node = new FamilyTestNode();
        Relatives relatives = new Relatives(node);

        Assertions.assertThat(relatives.node())
                .isSameAs(node);
    }

    @Test
    void noParent() {
        Relatives relatives = new Relatives(new FamilyTestNode());
        Assertions.assertThat(relatives.parent())
                .isEmpty();
    }

    @Test
    void parent() {
        Relatives parent = new Relatives(new FamilyTestNode());
        Relatives relatives = new Relatives(new FamilyTestNode(), parent);

        Assertions.assertThat(relatives.parent())
                .get()
                .isSameAs(parent);
    }

    @Test
    void children() {
        MethodSignature methodSignature = new MethodSignature(
                nodeIdGenerator.next(),
                Visibility.NAMESPACE,
                "Method",
                Collections.emptyList(),
                Collections.emptyList(),
                new TypeSpecSimple(nodeIdGenerator.next(), "None", Collections.emptyList())
        );

        Type type = new Type(
                nodeIdGenerator.next(),
                Visibility.NAMESPACE,
                "Type",
                Collections.emptyList(),
                new TypeSpecSimple(nodeIdGenerator.next(), "Object", Collections.emptyList()),
                Collections.singletonList(methodSignature)
        );

        Relatives parentRelatives = new Relatives(type);
        Relatives childRelatives = new Relatives(methodSignature, parentRelatives);
        methodSignature.getContext().put(Relatives.class, childRelatives);

        Assertions.assertThat(
                parentRelatives.children(new BaseVisitor<List<Node>>() {
                    @Override
                    public List<Node> visit(Type node) {
                        return new ArrayList<>(node.getMethods());
                    }
                })
        ).containsExactly(childRelatives);
    }

    @Test
    void siblings() {
        MethodSignature methodSignature = new MethodSignature(
                nodeIdGenerator.next(),
                Visibility.NAMESPACE,
                "Method",
                Collections.emptyList(),
                Collections.emptyList(),
                new TypeSpecSimple(nodeIdGenerator.next(), "None", Collections.emptyList())
        );

        Type type = new Type(
                nodeIdGenerator.next(),
                Visibility.NAMESPACE,
                "Type",
                Collections.emptyList(),
                new TypeSpecSimple(nodeIdGenerator.next(), "Object", Collections.emptyList()),
                Collections.singletonList(methodSignature)
        );

        Relatives parentRelatives = new Relatives(type);
        Relatives childRelatives = new Relatives(methodSignature, parentRelatives);
        methodSignature.getContext().put(Relatives.class, childRelatives);

        Assertions.assertThat(
                childRelatives.siblings(new BaseVisitor<List<Node>>() {
                    @Override
                    public List<Node> visit(Type node) {
                        return new ArrayList<>(node.getMethods());
                    }
                })
        ).containsExactly(childRelatives);
    }

    @Test
    void nextSiblings() {
        MethodSignature methodSignature1 = new MethodSignature(
                nodeIdGenerator.next(),
                Visibility.NAMESPACE,
                "Method",
                Collections.emptyList(),
                Collections.emptyList(),
                new TypeSpecSimple(nodeIdGenerator.next(), "None", Collections.emptyList())
        );
        MethodSignature methodSignature2 = new MethodSignature(
                nodeIdGenerator.next(),
                Visibility.NAMESPACE,
                "Method",
                Collections.emptyList(),
                Collections.emptyList(),
                new TypeSpecSimple(nodeIdGenerator.next(), "None", Collections.emptyList())
        );

        Type type = new Type(
                nodeIdGenerator.next(),
                Visibility.NAMESPACE,
                "Type",
                Collections.emptyList(),
                new TypeSpecSimple(nodeIdGenerator.next(), "Object", Collections.emptyList()),
                Arrays.asList(methodSignature1, methodSignature2)
        );

        Relatives parentRelatives = new Relatives(type);
        Relatives methodSignature1Relatives = new Relatives(methodSignature1, parentRelatives);
        Relatives methodSignature2Relatives = new Relatives(methodSignature2, parentRelatives);
        methodSignature1.getContext().put(Relatives.class, methodSignature1Relatives);
        methodSignature2.getContext().put(Relatives.class, methodSignature2Relatives);

        Assertions.assertThat(
                methodSignature1Relatives.nextSibling(new BaseVisitor<List<Node>>() {
                    @Override
                    public List<Node> visit(Type node) {
                        return new ArrayList<>(node.getMethods());
                    }
                })
        ).get().isSameAs(methodSignature2Relatives);
    }

    @Test
    void previousSiblings() {
        MethodSignature methodSignature1 = new MethodSignature(
                nodeIdGenerator.next(),
                Visibility.NAMESPACE,
                "Method",
                Collections.emptyList(),
                Collections.emptyList(),
                new TypeSpecSimple(nodeIdGenerator.next(), "None", Collections.emptyList())
        );
        MethodSignature methodSignature2 = new MethodSignature(
                nodeIdGenerator.next(),
                Visibility.NAMESPACE,
                "Method",
                Collections.emptyList(),
                Collections.emptyList(),
                new TypeSpecSimple(nodeIdGenerator.next(), "None", Collections.emptyList())
        );

        Type type = new Type(
                nodeIdGenerator.next(),
                Visibility.NAMESPACE,
                "Type",
                Collections.emptyList(),
                new TypeSpecSimple(nodeIdGenerator.next(), "Object", Collections.emptyList()),
                Arrays.asList(methodSignature1, methodSignature2)
        );

        Relatives parentRelatives = new Relatives(type);
        Relatives methodSignature1Relatives = new Relatives(methodSignature1, parentRelatives);
        Relatives methodSignature2Relatives = new Relatives(methodSignature2, parentRelatives);
        methodSignature1.getContext().put(Relatives.class, methodSignature1Relatives);
        methodSignature2.getContext().put(Relatives.class, methodSignature2Relatives);

        Assertions.assertThat(
                methodSignature2Relatives.previousSibling(new BaseVisitor<List<Node>>() {
                    @Override
                    public List<Node> visit(Type node) {
                        return new ArrayList<>(node.getMethods());
                    }
                })
        ).get().isSameAs(methodSignature1Relatives);
    }

    @Test
    void sibling_0() {
        MethodSignature methodSignature = new MethodSignature(
                nodeIdGenerator.next(),
                Visibility.NAMESPACE,
                "Method",
                Collections.emptyList(),
                Collections.emptyList(),
                new TypeSpecSimple(nodeIdGenerator.next(), "None", Collections.emptyList())
        );

        Type type = new Type(
                nodeIdGenerator.next(),
                Visibility.NAMESPACE,
                "Type",
                Collections.emptyList(),
                new TypeSpecSimple(nodeIdGenerator.next(), "Object", Collections.emptyList()),
                Collections.singletonList(methodSignature)
        );

        Relatives parentRelatives = new Relatives(type);
        Relatives childRelatives = new Relatives(methodSignature, parentRelatives);
        methodSignature.getContext().put(Relatives.class, childRelatives);

        Assertions.assertThat(
                childRelatives.sibling(0, new BaseVisitor<List<Node>>() {
                    @Override
                    public List<Node> visit(Type node) {
                        return new ArrayList<>(node.getMethods());
                    }
                })
        ).get().isSameAs(childRelatives);
    }

    @Test
    void sibling_notFound() {
        MethodSignature methodSignature = new MethodSignature(
                nodeIdGenerator.next(),
                Visibility.NAMESPACE,
                "Method",
                Collections.emptyList(),
                Collections.emptyList(),
                new TypeSpecSimple(nodeIdGenerator.next(), "None", Collections.emptyList())
        );

        Type type = new Type(
                nodeIdGenerator.next(),
                Visibility.NAMESPACE,
                "Type",
                Collections.emptyList(),
                new TypeSpecSimple(nodeIdGenerator.next(), "Object", Collections.emptyList()),
                Collections.singletonList(methodSignature)
        );

        Relatives parentRelatives = new Relatives(type);
        Relatives childRelatives = new Relatives(methodSignature, parentRelatives);
        methodSignature.getContext().put(Relatives.class, childRelatives);

        Assertions.assertThat(
                childRelatives.sibling(10, new BaseVisitor<List<Node>>() {
                    @Override
                    public List<Node> visit(Type node) {
                        return new ArrayList<>(node.getMethods());
                    }
                })
        ).isEmpty();
    }

    private static class FamilyTestNode extends Node {
        private static final NodeId nodeId = new NodeId(1);
        private final List<Node> children;

        FamilyTestNode(List<Node> children) {
            super(nodeId);
            this.children = children;
        }

        FamilyTestNode() {
            this(Collections.emptyList());
        }

        @Override
        public <T> T accept(Visitor<? extends T> visitor) {
            return null;
        }
    }
}
