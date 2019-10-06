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
import org.thoriumlang.compiler.ast.nodes.FunctionValue;
import org.thoriumlang.compiler.ast.nodes.IdentifierValue;
import org.thoriumlang.compiler.ast.nodes.IndirectAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.Method;
import org.thoriumlang.compiler.ast.nodes.MethodCallValue;
import org.thoriumlang.compiler.ast.nodes.MethodSignature;
import org.thoriumlang.compiler.ast.nodes.Mode;
import org.thoriumlang.compiler.ast.nodes.NestedValue;
import org.thoriumlang.compiler.ast.nodes.NewAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.Node;
import org.thoriumlang.compiler.ast.nodes.NoneValue;
import org.thoriumlang.compiler.ast.nodes.NumberValue;
import org.thoriumlang.compiler.ast.nodes.Parameter;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.ast.nodes.Statement;
import org.thoriumlang.compiler.ast.nodes.StringValue;
import org.thoriumlang.compiler.ast.nodes.TopLevelNode;
import org.thoriumlang.compiler.ast.nodes.Type;
import org.thoriumlang.compiler.ast.nodes.TypeParameter;
import org.thoriumlang.compiler.ast.nodes.TypeSpec;
import org.thoriumlang.compiler.ast.nodes.TypeSpecFunction;
import org.thoriumlang.compiler.ast.nodes.TypeSpecInferred;
import org.thoriumlang.compiler.ast.nodes.TypeSpecIntersection;
import org.thoriumlang.compiler.ast.nodes.TypeSpecSimple;
import org.thoriumlang.compiler.ast.nodes.TypeSpecUnion;
import org.thoriumlang.compiler.ast.nodes.Use;
import org.thoriumlang.compiler.ast.nodes.Value;

import java.util.stream.Collectors;

public abstract class CopyVisitor implements Visitor<Node> {
    @Override
    public Node visit(Root node) {
        return new Root(
                node.getNodeId(),
                node.getNamespace(),
                node.getUses().stream()
                        .map(u -> (Use) u.accept(this))
                        .collect(Collectors.toList()),
                (TopLevelNode) node.getTopLevelNode().accept(this)
        );
    }

    @Override
    public Node visit(Use node) {
        return new Use(
                node.getNodeId(), node.getFrom(), node.getTo()
        );
    }

    @Override
    public Node visit(Type node) {
        return new Type(
                node.getNodeId(),
                node.getVisibility(),
                node.getName(),
                node.getTypeParameters().stream()
                        .map(p -> (TypeParameter) p.accept(this))
                        .collect(Collectors.toList()),
                (TypeSpec) node.getSuperType().accept(this),
                node.getMethods().stream()
                        .map(m -> (MethodSignature) m.accept(this))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Node visit(Class node) {
        return new Class(
                node.getNodeId(),
                node.getVisibility(),
                node.getName(),
                node.getTypeParameters().stream()
                        .map(p -> (TypeParameter) p.accept(this))
                        .collect(Collectors.toList()),
                (TypeSpec) node.getSuperType().accept(this),
                node.getMethods().stream()
                        .map(m -> (Method) m.accept(this))
                        .collect(Collectors.toList()),
                node.getAttributes().stream()
                        .map(a -> (Attribute) a.accept(this))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Node visit(TypeSpecIntersection node) {
        return new TypeSpecIntersection(
                node.getNodeId(),
                node.getTypes().stream()
                        .map(t -> (TypeSpec) t.accept(this))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Node visit(TypeSpecUnion node) {
        return new TypeSpecUnion(
                node.getNodeId(),
                node.getTypes().stream()
                        .map(t -> (TypeSpec) t.accept(this))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Node visit(TypeSpecSimple node) {
        return new TypeSpecSimple(
                node.getNodeId(),
                node.getType(),
                node.getArguments().stream()
                        .map(a -> (TypeSpec) a.accept(this))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Node visit(TypeSpecFunction node) {
        return new TypeSpecFunction(
                node.getNodeId(),
                node.getArguments().stream()
                        .map(t -> (TypeSpec) t.accept(this))
                        .collect(Collectors.toList()),
                (TypeSpec) node.getReturnType().accept(this)
        );
    }

    @Override
    public Node visit(TypeSpecInferred node) {
        return new TypeSpecInferred(node.getNodeId());
    }

    @Override
    public Node visit(MethodSignature node) {
        return new MethodSignature(
                node.getNodeId(),
                node.getVisibility(),
                node.getName(),
                node.getTypeParameters().stream()
                        .map(p -> (TypeParameter) p.accept(this))
                        .collect(Collectors.toList()),
                node.getParameters().stream()
                        .map(p -> (Parameter) p.accept(this))
                        .collect(Collectors.toList()),
                (TypeSpec) node.getReturnType().accept(this)
        );
    }

    @Override
    public Node visit(Parameter node) {
        return new Parameter(
                node.getNodeId(),
                node.getName(),
                (TypeSpec) node.getType().accept(this)
        );
    }

    @Override
    public Node visit(TypeParameter node) {
        return new TypeParameter(node.getNodeId(), node.getName());
    }

    @Override
    public Node visit(StringValue node) {
        return new StringValue(node.getNodeId(), node.getValue());
    }

    @Override
    public Node visit(NumberValue node) {
        return new NumberValue(node.getNodeId(), node.getValue());
    }

    @Override
    public Node visit(BooleanValue node) {
        return new BooleanValue(node.getNodeId(), node.getValue());
    }

    @Override
    public Node visit(NoneValue node) {
        return new NoneValue(node.getNodeId());
    }

    @Override
    public Node visit(IdentifierValue node) {
        return new IdentifierValue(node.getNodeId(), node.getValue());
    }

    @Override
    public Node visit(NewAssignmentValue node) {
        return new NewAssignmentValue(
                node.getNodeId(),
                node.getIdentifier(),
                (TypeSpec) node.getType().accept(this),
                (Value) node.getValue().accept(this),
                node.getMode()
        );
    }

    @Override
    public Node visit(IndirectAssignmentValue node) {
        return new IndirectAssignmentValue(
                node.getNodeId(),
                (Value) node.getIndirectValue().accept(this),
                node.getIdentifier(),
                (Value) node.getValue().accept(this)
        );
    }

    @Override
    public Node visit(MethodCallValue node) {
        return new MethodCallValue(
                node.getNodeId(),
                node.getMethodName(),
                node.getTypeArguments().stream()
                        .map(a -> (TypeSpec) a.accept(this))
                        .collect(Collectors.toList()),
                node.getMethodArguments().stream()
                        .map(v -> (Value) v.accept(this))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Node visit(NestedValue node) {
        return new NestedValue(
                node.getNodeId(),
                (Value) node.getOuter().accept(this),
                (Value) node.getInner().accept(this)
        );
    }

    @Override
    public Node visit(FunctionValue node) {
        return new FunctionValue(
                node.getNodeId(),
                node.getTypeParameters().stream()
                        .map(t -> (TypeParameter) t.accept(this))
                        .collect(Collectors.toList()),
                node.getParameters().stream()
                        .map(p -> (Parameter) p.accept(this))
                        .collect(Collectors.toList()),
                (TypeSpec) node.getReturnType().accept(this),
                node.getStatements().stream()
                        .map(s -> (Statement) s.accept(this))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Node visit(Statement node) {
        return new Statement(
                node.getNodeId(),
                (Value) node.getValue().accept(this),
                node.isLast()
        );
    }

    @Override
    public Node visit(Method node) {
        return new Method(
                node.getNodeId(),
                (MethodSignature) node.getSignature().accept(this),
                node.getStatements().stream()
                        .map(s -> (Statement) s.accept(this))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Node visit(Attribute node) {
        return new Attribute(
                node.getNodeId(),
                node.getIdentifier(),
                (TypeSpec) node.getType().accept(this),
                (Value) node.getValue().accept(this),
                node.getMode()
        );
    }
}
