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
        return node;
    }

    @Override
    public Node visit(NoneValue node) {
        return node;
    }

    @Override
    public Node visit(IdentifierValue node) {
        return new IdentifierValue(node.getNodeId(), node.getValue());
    }

    @Override
    public Node visit(VarAssignmentValue node) {
        return new VarAssignmentValue(
                node.getNodeId(),
                node.getIdentifier(),
                (TypeSpec) node.getType().accept(this),
                (Value) node.getValue().accept(this)
        );
    }

    @Override
    public Node visit(ValAssignmentValue node) {
        return new ValAssignmentValue(
                node.getNodeId(),
                node.getIdentifier(),
                (TypeSpec) node.getType().accept(this),
                (Value) node.getValue().accept(this)
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
    public Node visit(VarAttribute node) {
        return new VarAttribute(
                node.getNodeId(),
                node.getIdentifier(),
                (TypeSpec) node.getType().accept(this),
                (Value) node.getValue().accept(this)
        );
    }

    @Override
    public Node visit(ValAttribute node) {
        return new ValAttribute(
                node.getNodeId(),
                node.getIdentifier(),
                (TypeSpec) node.getType().accept(this),
                (Value) node.getValue().accept(this)
        );
    }
}
