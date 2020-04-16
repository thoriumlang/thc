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

import org.thoriumlang.compiler.ast.context.Relatives;
import org.thoriumlang.compiler.ast.nodes.Attribute;
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
import org.thoriumlang.compiler.ast.nodes.Parameter;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.ast.nodes.Statement;
import org.thoriumlang.compiler.ast.nodes.Type;
import org.thoriumlang.compiler.ast.nodes.TypeSpecFunction;
import org.thoriumlang.compiler.ast.nodes.TypeSpecIntersection;
import org.thoriumlang.compiler.ast.nodes.TypeSpecSimple;
import org.thoriumlang.compiler.ast.nodes.TypeSpecUnion;
import org.thoriumlang.compiler.ast.nodes.Use;

public class RelativesInjectionVisitor extends IdentityVisitor {
    @Override
    public Node visit(Root node) {
        Relatives relatives = family(node);
        node.getUses().forEach(n -> setFamilyRecursively(n, relatives));
        setFamilyRecursively(node.getTopLevelNode(), relatives);
        return node;
    }

    @Override
    public Node visit(Use node) {
        family(node);
        return node;
    }

    private Relatives family(Node node) {
        return node.getContext().putIfAbsentAndGet(Relatives.class, new Relatives(node));
    }

    private void setFamilyRecursively(Node target, Relatives parent) {
        Relatives relatives = new Relatives(target, parent);
        target.getContext().put(Relatives.class, relatives);
        target.accept(this);
    }

    @Override
    public Node visit(Type node) {
        Relatives relatives = family(node);
        node.getMethods().forEach(n -> setFamilyRecursively(n, relatives));
        node.getTypeParameters().forEach(n -> setFamilyRecursively(n, relatives));
        setFamilyRecursively(node.getSuperType(), relatives);
        return node;
    }

    @Override
    public Node visit(Class node) {
        Relatives relatives = family(node);
        node.getAttributes().forEach(n -> setFamilyRecursively(n, relatives));
        node.getTypeParameters().forEach(n -> setFamilyRecursively(n, relatives));
        node.getMethods().forEach(n -> setFamilyRecursively(n, relatives));
        setFamilyRecursively(node.getSuperType(), relatives);
        return node;
    }

    @Override
    public Node visit(TypeSpecIntersection node) {
        Relatives relatives = family(node);
        node.getTypes().forEach(n -> setFamilyRecursively(n, relatives));
        return node;
    }

    @Override
    public Node visit(TypeSpecUnion node) {
        Relatives relatives = family(node);
        node.getTypes().forEach(n -> setFamilyRecursively(n, relatives));
        return node;
    }

    @Override
    public Node visit(TypeSpecSimple node) {
        Relatives relatives = family(node);
        node.getArguments().forEach(n -> setFamilyRecursively(n, relatives));
        return node;
    }

    @Override
    public Node visit(TypeSpecFunction node) {
        Relatives relatives = family(node);
        node.getArguments().forEach(n -> setFamilyRecursively(n, relatives));
        setFamilyRecursively(node.getReturnType(), relatives);
        return node;
    }

    @Override
    public Node visit(MethodSignature node) {
        Relatives relatives = family(node);
        node.getTypeParameters().forEach(n -> setFamilyRecursively(n, relatives));
        node.getParameters().forEach(n -> setFamilyRecursively(n, relatives));
        setFamilyRecursively(node.getReturnType(), relatives);
        return node;
    }

    @Override
    public Node visit(Parameter node) {
        Relatives relatives = family(node);
        setFamilyRecursively(node.getType(), relatives);
        return node;
    }

    @Override
    public Node visit(NewAssignmentValue node) {
        Relatives relatives = family(node);
        setFamilyRecursively(node.getType(), relatives);
        setFamilyRecursively(node.getValue(), relatives);
        return node;
    }

    @Override
    public Node visit(IndirectAssignmentValue node) {
        Relatives relatives = family(node);
        setFamilyRecursively(node.getReference(), relatives);
        setFamilyRecursively(node.getIndirectValue(), relatives);
        setFamilyRecursively(node.getValue(), relatives);
        return node;
    }

    @Override
    public Node visit(DirectAssignmentValue node) {
        Relatives relatives = family(node);
        setFamilyRecursively(node.getReference(), relatives);
        setFamilyRecursively(node.getValue(), relatives);
        return node;
    }

    @Override
    public Node visit(MethodCallValue node) {
        Relatives relatives = family(node);
        node.getMethodArguments().forEach(n -> setFamilyRecursively(n, relatives));
        node.getTypeArguments().forEach(n -> setFamilyRecursively(n, relatives));
        return node;
    }

    @Override
    public Node visit(NestedValue node) {
        Relatives relatives = family(node);
        setFamilyRecursively(node.getInner(), relatives);
        setFamilyRecursively(node.getOuter(), relatives);
        return node;
    }

    @Override
    public Node visit(IdentifierValue node) {
        Relatives relatives = family(node);
        setFamilyRecursively(node.getReference(), relatives);
        return node;
    }

    @Override
    public Node visit(FunctionValue node) {
        Relatives relatives = family(node);
        node.getTypeParameters().forEach(n -> setFamilyRecursively(n, relatives));
        node.getParameters().forEach(n -> setFamilyRecursively(n, relatives));
        node.getStatements().forEach(n -> setFamilyRecursively(n, relatives));
        setFamilyRecursively(node.getReturnType(), relatives);
        return node;
    }

    @Override
    public Node visit(Statement node) {
        Relatives relatives = family(node);
        setFamilyRecursively(node.getValue(), relatives);
        return node;
    }

    @Override
    public Node visit(Method node) {
        Relatives relatives = family(node);
        setFamilyRecursively(node.getSignature(), relatives);
        node.getStatements().forEach(n -> setFamilyRecursively(n, relatives));
        return node;
    }

    @Override
    public Node visit(Attribute node) {
        Relatives relatives = family(node);
        setFamilyRecursively(node.getType(), relatives);
        setFamilyRecursively(node.getValue(), relatives);
        return node;
    }
}
