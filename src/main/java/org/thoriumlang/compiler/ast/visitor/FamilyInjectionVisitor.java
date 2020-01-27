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
import org.thoriumlang.compiler.ast.nodes.Class;
import org.thoriumlang.compiler.ast.nodes.DirectAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.Family;
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

public class FamilyInjectionVisitor extends IdentityVisitor {
    @Override
    public Node visit(Root node) {
        Family family = family(node);
        node.getUses().forEach(n -> setFamilyRecursively(n, family));
        setFamilyRecursively(node.getTopLevelNode(), family);
        return node;
    }

    private Family family(Node node) {
        return node.getContext().get(Family.class)
                .orElseGet(() -> new Family(node));
    }

    private void setFamilyRecursively(Node target, Family parent) {
        Family family = new Family(target, parent);
        target.getContext().put(Family.class, family);
        target.accept(this);
    }

    @Override
    public Node visit(Type node) {
        Family family = family(node);
        node.getMethods().forEach(n -> setFamilyRecursively(n, family));
        node.getTypeParameters().forEach(n -> setFamilyRecursively(n, family));
        setFamilyRecursively(node.getSuperType(), family);
        return node;
    }

    @Override
    public Node visit(Class node) {
        Family family = family(node);
        node.getAttributes().forEach(n -> setFamilyRecursively(n, family));
        node.getTypeParameters().forEach(n -> setFamilyRecursively(n, family));
        node.getMethods().forEach(n -> setFamilyRecursively(n, family));
        setFamilyRecursively(node.getSuperType(), family);
        return node;
    }

    @Override
    public Node visit(TypeSpecIntersection node) {
        Family family = family(node);
        node.getTypes().forEach(n -> setFamilyRecursively(n, family));
        return node;
    }

    @Override
    public Node visit(TypeSpecUnion node) {
        Family family = family(node);
        node.getTypes().forEach(n -> setFamilyRecursively(n, family));
        return node;
    }

    @Override
    public Node visit(TypeSpecSimple node) {
        Family family = family(node);
        node.getArguments().forEach(n -> setFamilyRecursively(n, family));
        return node;
    }

    @Override
    public Node visit(TypeSpecFunction node) {
        Family family = family(node);
        node.getArguments().forEach(n -> setFamilyRecursively(n, family));
        setFamilyRecursively(node.getReturnType(), family);
        return node;
    }

    @Override
    public Node visit(MethodSignature node) {
        Family family = family(node);
        node.getTypeParameters().forEach(n -> setFamilyRecursively(n, family));
        node.getParameters().forEach(n -> setFamilyRecursively(n, family));
        setFamilyRecursively(node.getReturnType(), family);
        return node;
    }

    @Override
    public Node visit(Parameter node) {
        Family family = family(node);
        setFamilyRecursively(node.getType(), family);
        return node;
    }

    @Override
    public Node visit(NewAssignmentValue node) {
        Family family = family(node);
        setFamilyRecursively(node.getType(), family);
        setFamilyRecursively(node.getValue(), family);
        return node;
    }

    @Override
    public Node visit(IndirectAssignmentValue node) {
        Family family = family(node);
        setFamilyRecursively(node.getIndirectValue(), family);
        setFamilyRecursively(node.getValue(), family);
        return node;
    }

    @Override
    public Node visit(DirectAssignmentValue node) {
        Family family = family(node);
        setFamilyRecursively(node.getValue(), family);
        return node;
    }

    @Override
    public Node visit(MethodCallValue node) {
        Family family = family(node);
        node.getMethodArguments().forEach(n -> setFamilyRecursively(n, family));
        node.getTypeArguments().forEach(n -> setFamilyRecursively(n, family));
        return node;
    }

    @Override
    public Node visit(NestedValue node) {
        Family family = family(node);
        setFamilyRecursively(node.getInner(), family);
        setFamilyRecursively(node.getOuter(), family);
        return node;
    }

    @Override
    public Node visit(FunctionValue node) {
        Family family = family(node);
        node.getTypeParameters().forEach(n -> setFamilyRecursively(n, family));
        node.getParameters().forEach(n -> setFamilyRecursively(n, family));
        node.getStatements().forEach(n -> setFamilyRecursively(n, family));
        setFamilyRecursively(node.getReturnType(), family);
        return node;
    }

    @Override
    public Node visit(Statement node) {
        Family family = family(node);
        setFamilyRecursively(node.getValue(), family);
        return node;
    }

    @Override
    public Node visit(Method node) {
        Family family = family(node);
        setFamilyRecursively(node.getSignature(), family);
        node.getStatements().forEach(n -> setFamilyRecursively(n, family));
        return node;
    }

    @Override
    public Node visit(Attribute node) {
        Family family = family(node);
        setFamilyRecursively(node.getType(), family);
        setFamilyRecursively(node.getValue(), family);
        return node;
    }
}
