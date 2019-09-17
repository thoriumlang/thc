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

public abstract class BaseVisitor<T> implements Visitor<T> {
    @Override
    public T visitRoot(Root node) {
        return null;
    }

    @Override
    public T visitUse(Use node) {
        return null;
    }

    @Override
    public T visitType(Type node) {
        return null;
    }

    @Override
    public T visitClass(Class node) {
        return null;
    }

    @Override
    public T visitTypeIntersection(TypeSpecIntersection node) {
        return null;
    }

    @Override
    public T visitTypeUnion(TypeSpecUnion node) {
        return null;
    }

    @Override
    public T visitTypeSingle(TypeSpecSimple node) {
        return null;
    }

    @Override
    public T visitTypeFunction(TypeSpecFunction node) {
        return null;
    }

    @Override
    public T visitTypeInferred(TypeSpecInferred node) {
        return null;
    }

    @Override
    public T visitMethodSignature(MethodSignature node) {
        return null;
    }

    @Override
    public T visitParameter(Parameter node) {
        return null;
    }

    @Override
    public T visitTypeParameter(TypeParameter node) {
        return null;
    }

    @Override
    public T visitStringValue(StringValue node) {
        return null;
    }

    @Override
    public T visitNumberValue(NumberValue node) {
        return null;
    }

    @Override
    public T visitBooleanValue(BooleanValue node) {
        return null;
    }

    @Override
    public T visitNoneValue(NoneValue node) {
        return null;
    }

    @Override
    public T visitIdentifierValue(IdentifierValue node) {
        return null;
    }

    @Override
    public T visitVarAssignmentValue(VarAssignmentValue node) {
        return null;
    }

    @Override
    public T visitValAssignmentValue(ValAssignmentValue node) {
        return null;
    }

    @Override
    public T visitIndirectAssignmentValue(IndirectAssignmentValue node) {
        return null;
    }

    @Override
    public T visitMethodCallValue(MethodCallValue node) {
        return null;
    }

    @Override
    public T visitNestedValue(NestedValue node) {
        return null;
    }

    @Override
    public T visitFunctionValue(FunctionValue node) {
        return null;
    }

    @Override
    public T visitStatement(Statement node) {
        return null;
    }

    @Override
    public T visitMethod(Method node) {
        return null;
    }

    @Override
    public T visitVarAttribute(VarAttribute node) {
        return null;
    }

    @Override
    public T visitValAttribute(ValAttribute node) {
        return null;
    }
}
