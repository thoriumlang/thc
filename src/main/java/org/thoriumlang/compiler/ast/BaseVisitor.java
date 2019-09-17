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
    public T visit(Root node) {
        return null;
    }

    @Override
    public T visit(Use node) {
        return null;
    }

    @Override
    public T visit(Type node) {
        return null;
    }

    @Override
    public T visit(Class node) {
        return null;
    }

    @Override
    public T visit(TypeSpecIntersection node) {
        return null;
    }

    @Override
    public T visit(TypeSpecUnion node) {
        return null;
    }

    @Override
    public T visit(TypeSpecSimple node) {
        return null;
    }

    @Override
    public T visit(TypeSpecFunction node) {
        return null;
    }

    @Override
    public T visit(TypeSpecInferred node) {
        return null;
    }

    @Override
    public T visit(MethodSignature node) {
        return null;
    }

    @Override
    public T visit(Parameter node) {
        return null;
    }

    @Override
    public T visit(TypeParameter node) {
        return null;
    }

    @Override
    public T visit(StringValue node) {
        return null;
    }

    @Override
    public T visit(NumberValue node) {
        return null;
    }

    @Override
    public T visit(BooleanValue node) {
        return null;
    }

    @Override
    public T visit(NoneValue node) {
        return null;
    }

    @Override
    public T visit(IdentifierValue node) {
        return null;
    }

    @Override
    public T visit(VarAssignmentValue node) {
        return null;
    }

    @Override
    public T visit(ValAssignmentValue node) {
        return null;
    }

    @Override
    public T visit(IndirectAssignmentValue node) {
        return null;
    }

    @Override
    public T visit(MethodCallValue node) {
        return null;
    }

    @Override
    public T visit(NestedValue node) {
        return null;
    }

    @Override
    public T visit(FunctionValue node) {
        return null;
    }

    @Override
    public T visit(Statement node) {
        return null;
    }

    @Override
    public T visit(Method node) {
        return null;
    }

    @Override
    public T visit(VarAttribute node) {
        return null;
    }

    @Override
    public T visit(ValAttribute node) {
        return null;
    }
}
