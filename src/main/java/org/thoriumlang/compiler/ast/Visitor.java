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

public interface Visitor<T> {
    T visitRoot(NodeId nodeId, String namespace, List<Use> uses, Type type);

    T visitRoot(NodeId nodeId, String namespace, List<Use> uses, Class clazz);

    T visitUse(NodeId nodeId, String from, String to);

    T visitType(NodeId nodeId, Visibility visibility, String name, List<TypeParameter> typeParameters,
            TypeSpec superType, List<MethodSignature> methods);

    T visitClass(NodeId nodeId, Visibility visibility, String name, List<TypeParameter> typeParameters,
            TypeSpec superType, List<Method> methods, List<Attribute> attributes);

    T visitTypeIntersection(NodeId nodeId, List<TypeSpec> types);

    T visitTypeUnion(NodeId nodeId, List<TypeSpec> types);

    T visitTypeSingle(NodeId nodeId, String type, List<TypeSpec> arguments);

    T visitTypeFunction(NodeId nodeId, List<TypeSpec> arguments, TypeSpec returnType);

    T visitTypeInferred(NodeId nodeId);

    T visitMethodSignature(NodeId nodeId, Visibility visibility, String name, List<TypeParameter> typeParameters,
            List<Parameter> parameters, TypeSpec returnType);

    T visitParameter(NodeId nodeId, String name, TypeSpec type);

    T visitTypeParameter(NodeId nodeId, String name);

    T visitStringValue(NodeId nodeId, String value);

    T visitNumberValue(NodeId nodeId, String value);

    T visitBooleanValue(boolean value);

    T visitNoneValue();

    T visitIdentifierValue(NodeId nodeId, String value);

    T visitVarAssignmentValue(NodeId nodeId, String identifier, TypeSpec type, Value value);

    T visitValAssignmentValue(NodeId nodeId, String identifier, TypeSpec type, Value value);

    T visitIndirectAssignmentValue(NodeId nodeId, Value indirectValue, String identifier, Value value);

    T visitMethodCallValue(NodeId nodeId, String methodName, List<TypeSpec> typeArguments,
            List<Value> methodArguments);

    T visitNestedValue(NodeId nodeId, Value outer, Value inner);

    T visitFunctionValue(NodeId nodeId, List<TypeParameter> typeParameters, List<Parameter> parameters,
            TypeSpec returnType, List<Statement> statements);

    T visitStatement(NodeId nodeId, Value value, boolean isLast);

    T visitMethod(NodeId nodeId, MethodSignature signature, List<Statement> statements);

    T visitVarAttribute(NodeId nodeId, String identifier, TypeSpec type, Value value);

    T visitValAttribute(NodeId nodeId, String identifier, TypeSpec type, Value value);
}
