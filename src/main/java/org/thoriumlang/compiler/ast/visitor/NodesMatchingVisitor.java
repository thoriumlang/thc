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
package org.thoriumlang.compiler.ast.visitor;

import org.thoriumlang.compiler.ast.nodes.Attribute;
import org.thoriumlang.compiler.ast.nodes.BooleanValue;
import org.thoriumlang.compiler.ast.nodes.Class;
import org.thoriumlang.compiler.ast.nodes.DirectAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.FunctionValue;
import org.thoriumlang.compiler.ast.nodes.IdentifierValue;
import org.thoriumlang.compiler.ast.nodes.IndirectAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.Method;
import org.thoriumlang.compiler.ast.nodes.MethodCallValue;
import org.thoriumlang.compiler.ast.nodes.MethodSignature;
import org.thoriumlang.compiler.ast.nodes.NestedValue;
import org.thoriumlang.compiler.ast.nodes.NewAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.Node;
import org.thoriumlang.compiler.ast.nodes.NoneValue;
import org.thoriumlang.compiler.ast.nodes.NumberValue;
import org.thoriumlang.compiler.ast.nodes.Parameter;
import org.thoriumlang.compiler.ast.nodes.Reference;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.ast.nodes.Statement;
import org.thoriumlang.compiler.ast.nodes.StringValue;
import org.thoriumlang.compiler.ast.nodes.Type;
import org.thoriumlang.compiler.ast.nodes.TypeParameter;
import org.thoriumlang.compiler.ast.nodes.TypeSpecFunction;
import org.thoriumlang.compiler.ast.nodes.TypeSpecInferred;
import org.thoriumlang.compiler.ast.nodes.TypeSpecIntersection;
import org.thoriumlang.compiler.ast.nodes.TypeSpecSimple;
import org.thoriumlang.compiler.ast.nodes.TypeSpecUnion;
import org.thoriumlang.compiler.ast.nodes.Use;
import org.thoriumlang.compiler.collections.Lists;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class NodesMatchingVisitor implements Visitor<List<Node>> {
    private final Predicate<Node> predicate;

    public NodesMatchingVisitor(Predicate<Node> predicate) {
        this.predicate = predicate;
    }

    private List<Node> matches(Node node) {
        return predicate.test(node) ?
                Collections.singletonList(node) :
                Collections.emptyList();
    }

    private List<Node> recursiveMatch(Node node) {
        return node.accept(this);
    }

    private List<Node> recursiveMatch(List<? extends Node> nodes) {
        return nodes.stream()
                .map(n -> n.accept(this))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @Override
    public List<Node> visit(Root node) {
        return Lists.merge(
                matches(node),
                recursiveMatch(node.getUses()),
                recursiveMatch(node.getTopLevelNode())
        );
    }

    @Override
    public List<Node> visit(Use node) {
        return matches(node);
    }

    @Override
    public List<Node> visit(Type node) {
        return Lists.merge(
                matches(node),
                recursiveMatch(node.getTypeParameters()),
                recursiveMatch(node.getSuperType()),
                recursiveMatch(node.getMethods())
        );
    }

    @Override
    public List<Node> visit(Class node) {
        return Lists.merge(
                matches(node),
                recursiveMatch(node.getTypeParameters()),
                recursiveMatch(node.getSuperType()),
                recursiveMatch(node.getAttributes()),
                recursiveMatch(node.getMethods())
        );
    }

    @Override
    public List<Node> visit(TypeSpecIntersection node) {
        return Lists.merge(
                matches(node),
                recursiveMatch(node.getTypes())
        );
    }

    @Override
    public List<Node> visit(TypeSpecUnion node) {
        return Lists.merge(
                matches(node),
                recursiveMatch(node.getTypes())
        );
    }

    @Override
    public List<Node> visit(TypeSpecSimple node) {
        return Lists.merge(
                matches(node),
                recursiveMatch(node.getArguments())
        );
    }

    @Override
    public List<Node> visit(TypeSpecFunction node) {
        return Lists.merge(
                matches(node),
                recursiveMatch(node.getArguments()),
                recursiveMatch(node.getReturnType())
        );
    }

    @Override
    public List<Node> visit(TypeSpecInferred node) {
        return Lists.merge(
                matches(node)
        );
    }

    @Override
    public List<Node> visit(MethodSignature node) {
        return Lists.merge(
                matches(node),
                recursiveMatch(node.getTypeParameters()),
                recursiveMatch(node.getParameters()),
                recursiveMatch(node.getReturnType())
        );
    }

    @Override
    public List<Node> visit(Parameter node) {
        return Lists.merge(
                matches(node),
                recursiveMatch(node.getType())
        );
    }

    @Override
    public List<Node> visit(TypeParameter node) {
        return Lists.merge(
                matches(node)
        );
    }

    @Override
    public List<Node> visit(StringValue node) {
        return Lists.merge(
                matches(node)
        );
    }

    @Override
    public List<Node> visit(NumberValue node) {
        return Lists.merge(
                matches(node)
        );
    }

    @Override
    public List<Node> visit(BooleanValue node) {
        return Lists.merge(
                matches(node)
        );
    }

    @Override
    public List<Node> visit(NoneValue node) {
        return Lists.merge(
                matches(node)
        );
    }

    @Override
    public List<Node> visit(IdentifierValue node) {
        return Lists.merge(
                matches(node),
                recursiveMatch(node.getReference())
        );
    }

    @Override
    public List<Node> visit(NewAssignmentValue node) {
        return Lists.merge(
                matches(node),
                recursiveMatch(node.getType()),
                recursiveMatch(node.getValue())
        );
    }

    @Override
    public List<Node> visit(IndirectAssignmentValue node) {
        return Lists.merge(
                matches(node),
                recursiveMatch(node.getReference()),
                recursiveMatch(node.getIndirectValue()),
                recursiveMatch(node.getValue())
        );
    }

    @Override
    public List<Node> visit(DirectAssignmentValue node) {
        return Lists.merge(
                matches(node),
                recursiveMatch(node.getReference()),
                recursiveMatch(node.getValue())
        );
    }

    @Override
    public List<Node> visit(MethodCallValue node) {
        return Lists.merge(
                matches(node),
                recursiveMatch(node.getTypeArguments()),
                recursiveMatch(node.getMethodArguments())
        );
    }

    @Override
    public List<Node> visit(NestedValue node) {
        return Lists.merge(
                matches(node),
                recursiveMatch(node.getInner()),
                recursiveMatch(node.getOuter())
        );
    }

    @Override
    public List<Node> visit(FunctionValue node) {
        return Lists.merge(
                matches(node),
                recursiveMatch(node.getTypeParameters()),
                recursiveMatch(node.getParameters()),
                recursiveMatch(node.getReturnType()),
                recursiveMatch(node.getStatements())
        );
    }

    @Override
    public List<Node> visit(Statement node) {
        return Lists.merge(
                matches(node),
                recursiveMatch(node.getValue())
        );
    }

    @Override
    public List<Node> visit(Method node) {
        return Lists.merge(
                matches(node),
                recursiveMatch(node.getSignature()),
                recursiveMatch(node.getStatements())
        );
    }

    @Override
    public List<Node> visit(Attribute node) {
        return Lists.merge(
                matches(node),
                recursiveMatch(node.getType()),
                recursiveMatch(node.getValue())
        );
    }

    @Override
    public List<Node> visit(Reference node) {
        return Lists.merge(
                matches(node)
        );
    }
}
