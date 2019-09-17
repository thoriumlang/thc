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

public interface Visitor<T> {
    T visitRoot(Root node);

    T visitUse(Use node);

    T visitType(Type node);

    T visitClass(Class node);

    T visitTypeIntersection(TypeSpecIntersection node);

    T visitTypeUnion(TypeSpecUnion node);

    T visitTypeSingle(TypeSpecSimple node);

    T visitTypeFunction(TypeSpecFunction node);

    T visitTypeInferred(TypeSpecInferred node);

    T visitMethodSignature(MethodSignature node);

    T visitParameter(Parameter node);

    T visitTypeParameter(TypeParameter node);

    T visitStringValue(StringValue node);

    T visitNumberValue(NumberValue node);

    T visitBooleanValue(BooleanValue node);

    T visitNoneValue(NoneValue node);

    T visitIdentifierValue(IdentifierValue node);

    T visitVarAssignmentValue(VarAssignmentValue node);

    T visitValAssignmentValue(ValAssignmentValue node);

    T visitIndirectAssignmentValue(IndirectAssignmentValue node);

    T visitMethodCallValue(MethodCallValue node);

    T visitNestedValue(NestedValue node);

    T visitFunctionValue(FunctionValue node);

    T visitStatement(Statement node);

    T visitMethod(Method node);

    T visitVarAttribute(VarAttribute node);

    T visitValAttribute(ValAttribute node);
}
