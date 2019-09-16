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
    public T visitRoot(NodeId nodeId, String namespace, List<Use> uses, Type type) {
        return null;
    }

    @Override
    public T visitRoot(NodeId nodeId, String namespace, List<Use> uses, Class clazz) {
        return null;
    }

    @Override
    public T visitUse(NodeId nodeId, String from, String to) {
        return null;
    }

    @Override
    public T visitType(NodeId nodeId, Visibility visibility, String name, List<TypeParameter> typeParameters,
            TypeSpec superType, List<MethodSignature> methods) {
        return null;
    }

    @Override
    public T visitClass(NodeId nodeId, Visibility visibility, String name, List<TypeParameter> typeParameters,
            TypeSpec superType, List<Method> methods, List<Attribute> attributes) {
        return null;
    }

    @Override
    public T visitTypeIntersection(NodeId nodeId, List<TypeSpec> types) {
        return null;
    }

    @Override
    public T visitTypeUnion(NodeId nodeId, List<TypeSpec> types) {
        return null;
    }

    @Override
    public T visitTypeSingle(NodeId nodeId, String type, List<TypeSpec> arguments) {
        return null;
    }

    @Override
    public T visitTypeFunction(NodeId nodeId, List<TypeSpec> arguments, TypeSpec returnType) {
        return null;
    }

    @Override
    public T visitTypeInferred(NodeId nodeId) {
        return null;
    }

    @Override
    public T visitMethodSignature(NodeId nodeId, Visibility visibility, String name, List<TypeParameter> typeParameters,
            List<Parameter> parameters, TypeSpec returnType) {
        return null;
    }

    @Override
    public T visitParameter(NodeId nodeId, String name, TypeSpec type) {
        return null;
    }

    @Override
    public T visitTypeParameter(NodeId nodeId, String name) {
        return null;
    }

    @Override
    public T visitStringValue(NodeId nodeId, String value) {
        return null;
    }

    @Override
    public T visitNumberValue(NodeId nodeId, String value) {
        return null;
    }

    @Override
    public T visitBooleanValue(boolean value) {
        return null;
    }

    @Override
    public T visitNoneValue() {
        return null;
    }

    @Override
    public T visitIdentifierValue(NodeId nodeId, String value) {
        return null;
    }

    @Override
    public T visitVarAssignmentValue(NodeId nodeId, String identifier, TypeSpec type, Value value) {
        return null;
    }

    @Override
    public T visitValAssignmentValue(NodeId nodeId, String identifier, TypeSpec type, Value value) {
        return null;
    }

    @Override
    public T visitIndirectAssignmentValue(NodeId nodeId, Value indirectValue, String identifier, Value value) {
        return null;
    }

    @Override
    public T visitMethodCallValue(NodeId nodeId, String methodName, List<TypeSpec> typeArguments,
            List<Value> methodArguments) {
        return null;
    }

    @Override
    public T visitNestedValue(NodeId nodeId, Value outer, Value inner) {
        return null;
    }

    @Override
    public T visitFunctionValue(NodeId nodeId, List<TypeParameter> typeParameters, List<Parameter> parameters,
            TypeSpec returnType, List<Statement> statements) {
        return null;
    }

    @Override
    public T visitStatement(NodeId nodeId, Value value, boolean isLast) {
        return null;
    }

    @Override
    public T visitMethod(NodeId nodeId, MethodSignature signature, List<Statement> statements) {
        return null;
    }

    @Override
    public T visitVarAttribute(NodeId nodeId, String identifier, TypeSpec type, Value value) {
        return null;
    }

    @Override
    public T visitValAttribute(NodeId nodeId, String identifier, TypeSpec type, Value value) {
        return null;
    }
}
