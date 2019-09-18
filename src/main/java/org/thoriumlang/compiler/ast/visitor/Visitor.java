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
package org.thoriumlang.compiler.ast.visitor;

import org.thoriumlang.compiler.ast.BooleanValue;
import org.thoriumlang.compiler.ast.Class;
import org.thoriumlang.compiler.ast.FunctionValue;
import org.thoriumlang.compiler.ast.IdentifierValue;
import org.thoriumlang.compiler.ast.IndirectAssignmentValue;
import org.thoriumlang.compiler.ast.Method;
import org.thoriumlang.compiler.ast.MethodCallValue;
import org.thoriumlang.compiler.ast.MethodSignature;
import org.thoriumlang.compiler.ast.NestedValue;
import org.thoriumlang.compiler.ast.NoneValue;
import org.thoriumlang.compiler.ast.NumberValue;
import org.thoriumlang.compiler.ast.Parameter;
import org.thoriumlang.compiler.ast.Root;
import org.thoriumlang.compiler.ast.Statement;
import org.thoriumlang.compiler.ast.StringValue;
import org.thoriumlang.compiler.ast.Type;
import org.thoriumlang.compiler.ast.TypeParameter;
import org.thoriumlang.compiler.ast.TypeSpecFunction;
import org.thoriumlang.compiler.ast.TypeSpecInferred;
import org.thoriumlang.compiler.ast.TypeSpecIntersection;
import org.thoriumlang.compiler.ast.TypeSpecSimple;
import org.thoriumlang.compiler.ast.TypeSpecUnion;
import org.thoriumlang.compiler.ast.Use;
import org.thoriumlang.compiler.ast.ValAssignmentValue;
import org.thoriumlang.compiler.ast.ValAttribute;
import org.thoriumlang.compiler.ast.VarAssignmentValue;
import org.thoriumlang.compiler.ast.VarAttribute;

public interface Visitor<T> {
    T visit(Root node);

    T visit(Use node);

    T visit(Type node);

    T visit(Class node);

    T visit(TypeSpecIntersection node);

    T visit(TypeSpecUnion node);

    T visit(TypeSpecSimple node);

    T visit(TypeSpecFunction node);

    T visit(TypeSpecInferred node);

    T visit(MethodSignature node);

    T visit(Parameter node);

    T visit(TypeParameter node);

    T visit(StringValue node);

    T visit(NumberValue node);

    T visit(BooleanValue node);

    T visit(NoneValue node);

    T visit(IdentifierValue node);

    T visit(VarAssignmentValue node);

    T visit(ValAssignmentValue node);

    T visit(IndirectAssignmentValue node);

    T visit(MethodCallValue node);

    T visit(NestedValue node);

    T visit(FunctionValue node);

    T visit(Statement node);

    T visit(Method node);

    T visit(VarAttribute node);

    T visit(ValAttribute node);
}
