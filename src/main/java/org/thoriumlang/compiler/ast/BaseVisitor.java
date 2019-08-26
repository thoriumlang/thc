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

import java.util.List;

public abstract class BaseVisitor<T> implements Visitor<T> {
    @Override
    public T visitRoot(String namespace, Type type, List<Use> uses) {
        return null;
    }

    @Override
    public T visitUse(String from, String to) {
        return null;
    }

    @Override
    public T visitType(Visibility visibility, String name, List<TypeParameter> typeParameters, TypeSpec superType,
            List<MethodSignature> methods) {
        return null;
    }

    @Override
    public T visitTypeIntersection(List<TypeSpec> types) {
        return null;
    }

    @Override
    public T visitTypeUnion(List<TypeSpec> types) {
        return null;
    }

    @Override
    public T visitTypeSingle(String type, List<TypeSpec> arguments) {
        return null;
    }

    @Override
    public T visitMethodSignature(Visibility visibility, String name, List<TypeParameter> typeParameters,
            List<Parameter> parameters, TypeSpec returnType) {
        return null;
    }

    @Override
    public T visitParameter(String name, TypeSpec type) {
        return null;
    }

    @Override
    public T visitTypeParameter(String name) {
        return null;
    }

    @Override
    public T visitStringValue(String value) {
        return null;
    }

    @Override
    public T visitNumberValue(Integer value) {
        return null;
    }

    @Override
    public T visitBooleanValue(Boolean value) {
        return null;
    }

    @Override
    public T visitNoneValue() {
        return null;
    }

    @Override
    public T visitIdentifierValue(String value) {
        return null;
    }

    @Override
    public T visitVarAssignmentValue(String identifier, TypeSpec type, Value value) {
        return null;
    }

    @Override
    public T visitValAssignmentValue(String identifier, TypeSpec type, Value value) {
        return null;
    }

    @Override
    public T visitIndirectAssignmentValue(Value indirectValue, String identifier, Value value) {
        return null;
    }

    @Override
    public T visitMethodCallValue(String methodName, List<TypeSpec> typeArguments,
            List<Value> methodArguments) {
        return null;
    }

    @Override
    public T visitNestedValue(Value outer, Value inner) {
        return null;
    }

    @Override
    public T visitStatement(Value value, boolean isLast) {
        return null;
    }

    @Override
    public T visitMethod(MethodSignature signature, List<Statement> statements) {
        return null;
    }

    @Override
    public T visitVarAttribute(String identifier, TypeSpec type, Value value) {
        return null;
    }

    @Override
    public T visitValAttribute(String identifier, TypeSpec type, Value value) {
        return null;
    }
}
