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

import org.thoriumlang.compiler.ast.nodes.Attribute;
import org.thoriumlang.compiler.ast.nodes.BooleanValue;
import org.thoriumlang.compiler.ast.nodes.Class;
import org.thoriumlang.compiler.ast.nodes.FunctionValue;
import org.thoriumlang.compiler.ast.nodes.IdentifierValue;
import org.thoriumlang.compiler.ast.nodes.IndirectAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.Method;
import org.thoriumlang.compiler.ast.nodes.MethodCallValue;
import org.thoriumlang.compiler.ast.nodes.MethodSignature;
import org.thoriumlang.compiler.ast.nodes.NestedValue;
import org.thoriumlang.compiler.ast.nodes.NewAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.NoneValue;
import org.thoriumlang.compiler.ast.nodes.NumberValue;
import org.thoriumlang.compiler.ast.nodes.Parameter;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.ast.nodes.Statement;
import org.thoriumlang.compiler.ast.nodes.StringValue;
import org.thoriumlang.compiler.ast.nodes.Type;
import org.thoriumlang.compiler.ast.nodes.TypeParameter;
import org.thoriumlang.compiler.ast.nodes.TypeSpecFunction;
import org.thoriumlang.compiler.ast.nodes.TypeSpecInferred;
import org.thoriumlang.compiler.ast.nodes.TypeSpecIntersection;
import org.thoriumlang.compiler.ast.nodes.TypeSpecSimple;
import org.thoriumlang.compiler.ast.nodes.TypeSpecUnion;
import org.thoriumlang.compiler.ast.nodes.Use;

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
    public T visit(NewAssignmentValue node) {
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
    public T visit(Attribute node) {
        return null;
    }
}
