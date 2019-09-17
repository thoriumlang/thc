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

public abstract class IdentityVisitor implements Visitor<Node> {
    @Override
    public Node visit(Root node) {
        return node;
    }

    @Override
    public Node visit(Use node) {
        return node;
    }

    @Override
    public Node visit(Type node) {
        return node;
    }

    @Override
    public Node visit(Class node) {
        return node;
    }

    @Override
    public Node visit(TypeSpecIntersection node) {
        return node;
    }

    @Override
    public Node visit(TypeSpecUnion node) {
        return node;
    }

    @Override
    public Node visit(TypeSpecSimple node) {
        return node;
    }

    @Override
    public Node visit(TypeSpecFunction node) {
        return node;
    }

    @Override
    public Node visit(TypeSpecInferred node) {
        return node;
    }

    @Override
    public Node visit(MethodSignature node) {
        return node;
    }

    @Override
    public Node visit(Parameter node) {
        return node;
    }

    @Override
    public Node visit(TypeParameter node) {
        return node;
    }

    @Override
    public Node visit(StringValue node) {
        return node;
    }

    @Override
    public Node visit(NumberValue node) {
        return node;
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
        return node;
    }

    @Override
    public Node visit(VarAssignmentValue node) {
        return node;
    }

    @Override
    public Node visit(ValAssignmentValue node) {
        return node;
    }

    @Override
    public Node visit(IndirectAssignmentValue node) {
        return node;
    }

    @Override
    public Node visit(MethodCallValue node) {
        return node;
    }

    @Override
    public Node visit(NestedValue node) {
        return node;
    }

    @Override
    public Node visit(FunctionValue node) {
        return node;
    }

    @Override
    public Node visit(Statement node) {
        return node;
    }

    @Override
    public Node visit(Method node) {
        return node;
    }

    @Override
    public Node visit(VarAttribute node) {
        return node;
    }

    @Override
    public Node visit(ValAttribute node) {
        return node;
    }
}
