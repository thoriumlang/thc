/*
 * Copyright 2020 Christophe Pollet
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
import org.thoriumlang.compiler.ast.nodes.DirectAssignmentValue;
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
import org.thoriumlang.compiler.ast.nodes.Reference;
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

public class PredicateVisitor implements Visitor<Boolean> {
    private final boolean defaultValue;

    public PredicateVisitor(boolean defaultValue) {
        this.defaultValue = defaultValue;
    }

    public PredicateVisitor() {
        this(false);
    }

    @Override
    public Boolean visit(Root node) {
        return defaultValue;
    }

    @Override
    public Boolean visit(Use node) {
        return defaultValue;
    }

    @Override
    public Boolean visit(Type node) {
        return defaultValue;
    }

    @Override
    public Boolean visit(Class node) {
        return defaultValue;
    }

    @Override
    public Boolean visit(TypeSpecIntersection node) {
        return defaultValue;
    }

    @Override
    public Boolean visit(TypeSpecUnion node) {
        return defaultValue;
    }

    @Override
    public Boolean visit(TypeSpecSimple node) {
        return defaultValue;
    }

    @Override
    public Boolean visit(TypeSpecFunction node) {
        return defaultValue;
    }

    @Override
    public Boolean visit(TypeSpecInferred node) {
        return defaultValue;
    }

    @Override
    public Boolean visit(MethodSignature node) {
        return defaultValue;
    }

    @Override
    public Boolean visit(Parameter node) {
        return defaultValue;
    }

    @Override
    public Boolean visit(TypeParameter node) {
        return defaultValue;
    }

    @Override
    public Boolean visit(StringValue node) {
        return defaultValue;
    }

    @Override
    public Boolean visit(NumberValue node) {
        return defaultValue;
    }

    @Override
    public Boolean visit(BooleanValue node) {
        return defaultValue;
    }

    @Override
    public Boolean visit(NoneValue node) {
        return defaultValue;
    }

    @Override
    public Boolean visit(IdentifierValue node) {
        return defaultValue;
    }

    @Override
    public Boolean visit(NewAssignmentValue node) {
        return defaultValue;
    }

    @Override
    public Boolean visit(IndirectAssignmentValue node) {
        return defaultValue;
    }

    @Override
    public Boolean visit(DirectAssignmentValue node) {
        return defaultValue;
    }

    @Override
    public Boolean visit(MethodCallValue node) {
        return defaultValue;
    }

    @Override
    public Boolean visit(NestedValue node) {
        return defaultValue;
    }

    @Override
    public Boolean visit(FunctionValue node) {
        return defaultValue;
    }

    @Override
    public Boolean visit(Statement node) {
        return defaultValue;
    }

    @Override
    public Boolean visit(Method node) {
        return defaultValue;
    }

    @Override
    public Boolean visit(Attribute node) {
        return defaultValue;
    }

    @Override
    public Boolean visit(Reference node) {
        return defaultValue;
    }
}
