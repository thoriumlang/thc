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
package org.thoriumlang.compiler.ast.nodes;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.ast.visitor.BaseVisitor;
import org.thoriumlang.compiler.ast.visitor.Visitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class FamilyTest {
    private NodeIdGenerator nodeIdGenerator;

    @BeforeEach
    void setup() {
        this.nodeIdGenerator = new NodeIdGenerator();
    }

    @Test
    void node() {
        Node node = new FamilyTestNode();
        Family family = new Family(node);

        Assertions.assertThat(family.node())
                .isSameAs(node);
    }

    @Test
    void noParent() {
        Family family = new Family(new FamilyTestNode());
        Assertions.assertThat(family.parent())
                .isEmpty();
    }

    @Test
    void parent() {
        Family parent = new Family(new FamilyTestNode());
        Family family = new Family(new FamilyTestNode(), parent);

        Assertions.assertThat(family.parent())
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

        Family parentFamily = new Family(type);
        Family childFamily = new Family(methodSignature, parentFamily);
        methodSignature.getContext().put(Family.class, childFamily);

        Assertions.assertThat(
                parentFamily.children(new BaseVisitor<List<Node>>() {
                    @Override
                    public List<Node> visit(Type node) {
                        return new ArrayList<>(node.getMethods());
                    }
                })
        ).containsExactly(childFamily);
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

        Family parentFamily = new Family(type);
        Family childFamily = new Family(methodSignature, parentFamily);
        methodSignature.getContext().put(Family.class, childFamily);

        Assertions.assertThat(
                childFamily.siblings(new BaseVisitor<List<Node>>() {
                    @Override
                    public List<Node> visit(Type node) {
                        return new ArrayList<>(node.getMethods());
                    }
                })
        ).containsExactly(childFamily);
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

        Family parentFamily = new Family(type);
        Family methodSignature1Family = new Family(methodSignature1, parentFamily);
        Family methodSignature2Family = new Family(methodSignature2, parentFamily);
        methodSignature1.getContext().put(Family.class, methodSignature1Family);
        methodSignature2.getContext().put(Family.class, methodSignature2Family);

        Assertions.assertThat(
                methodSignature1Family.nextSibling(new BaseVisitor<List<Node>>() {
                    @Override
                    public List<Node> visit(Type node) {
                        return new ArrayList<>(node.getMethods());
                    }
                })
        ).get().isSameAs(methodSignature2Family);
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

        Family parentFamily = new Family(type);
        Family methodSignature1Family = new Family(methodSignature1, parentFamily);
        Family methodSignature2Family = new Family(methodSignature2, parentFamily);
        methodSignature1.getContext().put(Family.class, methodSignature1Family);
        methodSignature2.getContext().put(Family.class, methodSignature2Family);

        Assertions.assertThat(
                methodSignature2Family.previousSibling(new BaseVisitor<List<Node>>() {
                    @Override
                    public List<Node> visit(Type node) {
                        return new ArrayList<>(node.getMethods());
                    }
                })
        ).get().isSameAs(methodSignature1Family);
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

        Family parentFamily = new Family(type);
        Family childFamily = new Family(methodSignature, parentFamily);
        methodSignature.getContext().put(Family.class, childFamily);

        Assertions.assertThat(
                childFamily.sibling(0, new BaseVisitor<List<Node>>() {
                    @Override
                    public List<Node> visit(Type node) {
                        return new ArrayList<>(node.getMethods());
                    }
                })
        ).get().isSameAs(childFamily);
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

        Family parentFamily = new Family(type);
        Family childFamily = new Family(methodSignature, parentFamily);
        methodSignature.getContext().put(Family.class, childFamily);

        Assertions.assertThat(
                childFamily.sibling(10, new BaseVisitor<List<Node>>() {
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
