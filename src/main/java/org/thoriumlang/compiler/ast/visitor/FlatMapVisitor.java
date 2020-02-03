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

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FlatMapVisitor<T> implements Visitor<List<T>> {
    private final Function<Node, List<T>> mapper;

    public FlatMapVisitor(Function<Node, List<T>> mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<T> visit(Root node) {
        return Lists.merge(
                mapper.apply(node),
                node.getUses().stream()
                        .map(n -> n.accept(this))
                        .flatMap(List::stream)
                        .collect(Collectors.toList()),
                node.getTopLevelNode().accept(this)
        );
    }

    @Override
    public List<T> visit(Use node) {
        return mapper.apply(node);
    }

    @Override
    public List<T> visit(Type node) {
        return Lists.merge(
                mapper.apply(node),
                node.getTypeParameters().stream()
                        .map(n -> n.accept(this))
                        .flatMap(List::stream)
                        .collect(Collectors.toList()),
                node.getSuperType().accept(this),
                node.getMethods().stream()
                        .map(n -> n.accept(this))
                        .flatMap(List::stream)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public List<T> visit(Class node) {
        return Lists.merge(
                mapper.apply(node),
                node.getTypeParameters().stream()
                        .map(n -> n.accept(this))
                        .flatMap(List::stream)
                        .collect(Collectors.toList()),
                node.getSuperType().accept(this),
                node.getMethods().stream()
                        .map(n -> n.accept(this))
                        .flatMap(List::stream)
                        .collect(Collectors.toList()),
                node.getAttributes().stream()
                        .map(n -> n.accept(this))
                        .flatMap(List::stream)
                        .collect(Collectors.toList())
        );

    }

    @Override
    public List<T> visit(TypeSpecIntersection node) {
        return Lists.merge(
                mapper.apply(node),
                node.getTypes().stream()
                        .map(n -> n.accept(this))
                        .flatMap(List::stream)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public List<T> visit(TypeSpecUnion node) {
        return Lists.merge(
                mapper.apply(node),
                node.getTypes().stream()
                        .map(n -> n.accept(this))
                        .flatMap(List::stream)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public List<T> visit(TypeSpecSimple node) {
        return Lists.merge(
                mapper.apply(node),
                node.getArguments().stream()
                        .map(n -> n.accept(this))
                        .flatMap(List::stream)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public List<T> visit(TypeSpecFunction node) {
        return Lists.merge(
                mapper.apply(node),
                node.getArguments().stream()
                        .map(n -> n.accept(this))
                        .flatMap(List::stream)
                        .collect(Collectors.toList()),
                node.getReturnType().accept(this)
        );
    }

    @Override
    public List<T> visit(TypeSpecInferred node) {
        return mapper.apply(node);
    }

    @Override
    public List<T> visit(MethodSignature node) {
        return Lists.merge(
                mapper.apply(node),
                node.getTypeParameters().stream()
                        .map(n -> n.accept(this))
                        .flatMap(List::stream)
                        .collect(Collectors.toList()),
                node.getParameters().stream()
                        .map(n -> n.accept(this))
                        .flatMap(List::stream)
                        .collect(Collectors.toList()),
                node.getReturnType().accept(this)
        );
    }

    @Override
    public List<T> visit(Parameter node) {
        return Lists.merge(
                mapper.apply(node),
                node.getType().accept(this)
        );
    }

    @Override
    public List<T> visit(TypeParameter node) {
        return mapper.apply(node);
    }

    @Override
    public List<T> visit(StringValue node) {
        return mapper.apply(node);
    }

    @Override
    public List<T> visit(NumberValue node) {
        return mapper.apply(node);
    }

    @Override
    public List<T> visit(BooleanValue node) {
        return mapper.apply(node);
    }

    @Override
    public List<T> visit(NoneValue node) {
        return mapper.apply(node);
    }

    @Override
    public List<T> visit(IdentifierValue node) {
        return mapper.apply(node);
    }

    @Override
    public List<T> visit(NewAssignmentValue node) {
        return Lists.merge(
                mapper.apply(node),
                node.getType().accept(this),
                node.getValue().accept(this)
        );
    }

    @Override
    public List<T> visit(IndirectAssignmentValue node) {
        return Lists.merge(
                mapper.apply(node),
                node.getIndirectValue().accept(this),
                node.getValue().accept(this)
        );
    }

    @Override
    public List<T> visit(DirectAssignmentValue node) {
        return Lists.merge(
                mapper.apply(node),
                node.getValue().accept(this)
        );
    }

    @Override
    public List<T> visit(MethodCallValue node) {
        return Lists.merge(
                mapper.apply(node),
                node.getTypeArguments().stream()
                        .map(n -> n.accept(this))
                        .flatMap(List::stream)
                        .collect(Collectors.toList()),
                node.getMethodArguments().stream()
                        .map(n -> n.accept(this))
                        .flatMap(List::stream)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public List<T> visit(NestedValue node) {
        return Lists.merge(
                mapper.apply(node),
                node.getOuter().accept(this),
                node.getInner().accept(this)
        );
    }

    @Override
    public List<T> visit(FunctionValue node) {
        return Lists.merge(
                mapper.apply(node),
                node.getTypeParameters().stream()
                        .map(n -> n.accept(this))
                        .flatMap(List::stream)
                        .collect(Collectors.toList()),
                node.getParameters().stream()
                        .map(n -> n.accept(this))
                        .flatMap(List::stream)
                        .collect(Collectors.toList()),
                node.getReturnType().accept(this),
                node.getStatements().stream()
                        .map(n -> n.accept(this))
                        .flatMap(List::stream)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public List<T> visit(Statement node) {
        return Lists.merge(
                mapper.apply(node),
                node.getValue().accept(this)
        );
    }

    @Override
    public List<T> visit(Method node) {
        return Lists.merge(
                mapper.apply(node),
                node.getSignature().accept(this),
                node.getStatements().stream()
                        .map(n -> n.accept(this))
                        .flatMap(List::stream)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public List<T> visit(Attribute node) {
        return Lists.merge(
                mapper.apply(node),
                node.getType().accept(this),
                node.getValue().accept(this)
        );
    }
}
