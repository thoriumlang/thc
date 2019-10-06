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
import org.thoriumlang.compiler.ast.nodes.Class;
import org.thoriumlang.compiler.ast.nodes.DirectAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.FunctionValue;
import org.thoriumlang.compiler.ast.nodes.IndirectAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.Method;
import org.thoriumlang.compiler.ast.nodes.MethodCallValue;
import org.thoriumlang.compiler.ast.nodes.MethodSignature;
import org.thoriumlang.compiler.ast.nodes.NestedValue;
import org.thoriumlang.compiler.ast.nodes.NewAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.Node;
import org.thoriumlang.compiler.ast.nodes.Parameter;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.ast.nodes.Statement;
import org.thoriumlang.compiler.ast.nodes.Type;
import org.thoriumlang.compiler.ast.nodes.TypeSpecFunction;
import org.thoriumlang.compiler.ast.nodes.TypeSpecIntersection;
import org.thoriumlang.compiler.ast.nodes.TypeSpecSimple;
import org.thoriumlang.compiler.ast.nodes.TypeSpecUnion;

public class ParentInjectionVisitor extends IdentityVisitor {
    @Override
    public Node visit(Root node) {
        node.getUses().forEach(n -> setParentRecursively(n, node));
        setParentRecursively(node.getTopLevelNode(), node);
        return node;
    }

    private void setParentRecursively(Node target, Node parent) {
        target.getContext().put("parent", Node.class, parent);
        target.accept(this);
    }

    @Override
    public Node visit(Type node) {
        node.getMethods().forEach(n -> setParentRecursively(n, node));
        node.getTypeParameters().forEach(n -> setParentRecursively(n, node));
        setParentRecursively(node.getSuperType(), node);
        return node;
    }

    @Override
    public Node visit(Class node) {
        node.getAttributes().forEach(n -> setParentRecursively(n, node));
        node.getTypeParameters().forEach(n -> setParentRecursively(n, node));
        node.getMethods().forEach(n -> setParentRecursively(n, node));
        setParentRecursively(node.getSuperType(), node);
        return node;
    }

    @Override
    public Node visit(TypeSpecIntersection node) {
        node.getTypes().forEach(n -> setParentRecursively(n, node));
        return node;
    }

    @Override
    public Node visit(TypeSpecUnion node) {
        node.getTypes().forEach(n -> setParentRecursively(n, node));
        return node;
    }

    @Override
    public Node visit(TypeSpecSimple node) {
        node.getArguments().forEach(n -> setParentRecursively(n, node));
        return node;
    }

    @Override
    public Node visit(TypeSpecFunction node) {
        node.getArguments().forEach(n -> setParentRecursively(n, node));
        setParentRecursively(node.getReturnType(), node);
        return node;
    }

    @Override
    public Node visit(MethodSignature node) {
        node.getTypeParameters().forEach(n -> setParentRecursively(n, node));
        node.getParameters().forEach(n -> setParentRecursively(n, node));
        setParentRecursively(node.getReturnType(), node);
        return node;
    }

    @Override
    public Node visit(Parameter node) {
        setParentRecursively(node.getType(), node);
        return node;
    }

    @Override
    public Node visit(NewAssignmentValue node) {
        setParentRecursively(node.getType(), node);
        setParentRecursively(node.getValue(), node);
        return node;
    }

    @Override
    public Node visit(IndirectAssignmentValue node) {
        setParentRecursively(node.getIndirectValue(), node);
        setParentRecursively(node.getValue(), node);
        return node;
    }

    @Override
    public Node visit(DirectAssignmentValue node) {
        setParentRecursively(node.getValue(), node);
        return node;
    }

    @Override
    public Node visit(MethodCallValue node) {
        node.getMethodArguments().forEach(n -> setParentRecursively(n, node));
        node.getTypeArguments().forEach(n -> setParentRecursively(n, node));
        return node;
    }

    @Override
    public Node visit(NestedValue node) {
        setParentRecursively(node.getInner(), node);
        setParentRecursively(node.getOuter(), node);
        return node;
    }

    @Override
    public Node visit(FunctionValue node) {
        node.getTypeParameters().forEach(n -> setParentRecursively(n, node));
        node.getParameters().forEach(n -> setParentRecursively(n, node));
        node.getStatements().forEach(n -> setParentRecursively(n, node));
        setParentRecursively(node.getReturnType(), node);
        return node;
    }

    @Override
    public Node visit(Statement node) {
        setParentRecursively(node.getValue(), node);
        return node;
    }

    @Override
    public Node visit(Method node) {
        setParentRecursively(node.getSignature(), node);
        node.getStatements().forEach(n -> setParentRecursively(n, node));
        return node;
    }

    @Override
    public Node visit(Attribute node) {
        setParentRecursively(node.getType(), node);
        setParentRecursively(node.getValue(), node);
        return node;
    }
}