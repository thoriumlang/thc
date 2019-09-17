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
    public Node visitRoot(Root node) {
        return node;
    }

    @Override
    public Node visitUse(Use node) {
        return node;
    }

    @Override
    public Node visitType(Type node) {
        return node;
    }

    @Override
    public Node visitClass(Class node) {
        return node;
    }

    @Override
    public Node visitTypeIntersection(TypeSpecIntersection node) {
        return node;
    }

    @Override
    public Node visitTypeUnion(TypeSpecUnion node) {
        return node;
    }

    @Override
    public Node visitTypeSingle(TypeSpecSimple node) {
        return node;
    }

    @Override
    public Node visitTypeFunction(TypeSpecFunction node) {
        return node;
    }

    @Override
    public Node visitTypeInferred(TypeSpecInferred node) {
        return node;
    }

    @Override
    public Node visitMethodSignature(MethodSignature node) {
        return node;
    }

    @Override
    public Node visitParameter(Parameter node) {
        return node;
    }

    @Override
    public Node visitTypeParameter(TypeParameter node) {
        return node;
    }

    @Override
    public Node visitStringValue(StringValue node) {
        return node;
    }

    @Override
    public Node visitNumberValue(NumberValue node) {
        return node;
    }

    @Override
    public Node visitBooleanValue(BooleanValue node) {
        return node;
    }

    @Override
    public Node visitNoneValue(NoneValue node) {
        return node;
    }

    @Override
    public Node visitIdentifierValue(IdentifierValue node) {
        return node;
    }

    @Override
    public Node visitVarAssignmentValue(VarAssignmentValue node) {
        return node;
    }

    @Override
    public Node visitValAssignmentValue(ValAssignmentValue node) {
        return node;
    }

    @Override
    public Node visitIndirectAssignmentValue(IndirectAssignmentValue node) {
        return node;
    }

    @Override
    public Node visitMethodCallValue(MethodCallValue node) {
        return node;
    }

    @Override
    public Node visitNestedValue(NestedValue node) {
        return node;
    }

    @Override
    public Node visitFunctionValue(FunctionValue node) {
        return node;
    }

    @Override
    public Node visitStatement(Statement node) {
        return node;
    }

    @Override
    public Node visitMethod(Method node) {
        return node;
    }

    @Override
    public Node visitVarAttribute(VarAttribute node) {
        return node;
    }

    @Override
    public Node visitValAttribute(ValAttribute node) {
        return node;
    }
}
