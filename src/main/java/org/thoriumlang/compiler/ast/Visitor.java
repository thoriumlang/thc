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
    T visitRoot(String namespace, List<Use> uses, Type type);

    T visitRoot(String namespace, List<Use> uses, Class clazz);

    T visitUse(String from, String to);

    T visitType(Visibility visibility, String name, List<TypeParameter> typeParameters, TypeSpec superType,
            List<MethodSignature> methods);

    T visitClass(Visibility visibility, String name, List<TypeParameter> typeParameters,
            TypeSpec superType, List<Method> methods, List<Attribute> attributes);

    T visitTypeIntersection(List<TypeSpec> types);

    T visitTypeUnion(List<TypeSpec> types);

    T visitTypeSingle(String type, List<TypeSpec> arguments);

    T visitTypeFunction(List<TypeSpec> arguments, TypeSpec returnType);

    T visitTypeInferred();

    T visitMethodSignature(Visibility visibility, String name, List<TypeParameter> typeParameters,
            List<Parameter> parameters, TypeSpec returnType);

    T visitParameter(String name, TypeSpec type);

    T visitTypeParameter(String name);

    T visitStringValue(String value);

    T visitNumberValue(String value);

    T visitBooleanValue(boolean value);

    T visitNoneValue();

    T visitIdentifierValue(String value);

    T visitVarAssignmentValue(String identifier, TypeSpec type, Value value);

    T visitValAssignmentValue(String identifier, TypeSpec type, Value value);

    T visitIndirectAssignmentValue(Value indirectValue, String identifier, Value value);

    T visitMethodCallValue(String methodName, List<TypeSpec> typeArguments,
            List<Value> methodArguments);

    T visitNestedValue(Value outer, Value inner);

    T visitFunctionValue(List<TypeParameter> typeParameters, List<Parameter> parameters, TypeSpec returnType,
            List<Statement> statements);

    T visitStatement(Value value, boolean isLast);

    T visitMethod(MethodSignature signature, List<Statement> statements);

    T visitVarAttribute(String identifier, TypeSpec type, Value value);

    T visitValAttribute(String identifier, TypeSpec type, Value value);
}
