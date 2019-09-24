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
package org.thoriumlang.compiler.ast.algorithms.typechecking;

import org.thoriumlang.compiler.ast.nodes.BooleanValue;
import org.thoriumlang.compiler.ast.nodes.Class;
import org.thoriumlang.compiler.ast.nodes.FunctionValue;
import org.thoriumlang.compiler.ast.nodes.IdentifierValue;
import org.thoriumlang.compiler.ast.nodes.IndirectAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.Method;
import org.thoriumlang.compiler.ast.nodes.MethodCallValue;
import org.thoriumlang.compiler.ast.nodes.MethodSignature;
import org.thoriumlang.compiler.ast.nodes.NestedValue;
import org.thoriumlang.compiler.ast.nodes.Node;
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
import org.thoriumlang.compiler.ast.nodes.ValAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.ValAttribute;
import org.thoriumlang.compiler.ast.nodes.VarAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.VarAttribute;
import org.thoriumlang.compiler.ast.visitor.BaseVisitor;
import org.thoriumlang.compiler.symbols.SymbolTable;

import java.util.Collections;
import java.util.List;

abstract class BaseTypeCheckingVisitor extends BaseVisitor<List<TypeCheckingError>> {
    Node setSymbolTable(Node node, SymbolTable symbolTable) {
        return node
                .getContext()
                .put(SymbolTable.class, symbolTable)
                .getNode();
    }

    Node setSymbolTable(Node destination, Node source) {
        return setSymbolTable(destination, getSymbolTable(source));
    }

    SymbolTable getSymbolTable(Node node) {
        return node.getContext()
                .get(SymbolTable.class)
                .orElseThrow(() -> new IllegalStateException("SymbolTable not found"));
    }

    @Override
    public List<TypeCheckingError> visit(Root node) {
        return Collections.emptyList();
    }

    @Override
    public List<TypeCheckingError> visit(Type node) {
        return Collections.emptyList();
    }

    @Override
    public List<TypeCheckingError> visit(Class node) {
        return Collections.emptyList();
    }

    @Override
    public List<TypeCheckingError> visit(TypeSpecIntersection node) {
        return Collections.emptyList();
    }

    @Override
    public List<TypeCheckingError> visit(TypeSpecUnion node) {
        return Collections.emptyList();
    }

    @Override
    public List<TypeCheckingError> visit(TypeSpecSimple node) {
        return Collections.emptyList();
    }

    @Override
    public List<TypeCheckingError> visit(TypeSpecFunction node) {
        return Collections.emptyList();
    }

    @Override
    public List<TypeCheckingError> visit(TypeSpecInferred node) {
        return Collections.emptyList();
    }

    @Override
    public List<TypeCheckingError> visit(MethodSignature node) {
        return Collections.emptyList();
    }

    @Override
    public List<TypeCheckingError> visit(Parameter node) {
        return Collections.emptyList();
    }

    @Override
    public List<TypeCheckingError> visit(TypeParameter node) {
        return Collections.emptyList();
    }

    @Override
    public List<TypeCheckingError> visit(StringValue node) {
        return Collections.emptyList();
    }

    @Override
    public List<TypeCheckingError> visit(NumberValue node) {
        return Collections.emptyList();
    }

    @Override
    public List<TypeCheckingError> visit(BooleanValue node) {
        return Collections.emptyList();
    }

    @Override
    public List<TypeCheckingError> visit(NoneValue node) {
        return Collections.emptyList();
    }

    @Override
    public List<TypeCheckingError> visit(IdentifierValue node) {
        return Collections.emptyList();
    }

    @Override
    public List<TypeCheckingError> visit(VarAssignmentValue node) {
        return Collections.emptyList();
    }

    @Override
    public List<TypeCheckingError> visit(ValAssignmentValue node) {
        return Collections.emptyList();
    }

    @Override
    public List<TypeCheckingError> visit(IndirectAssignmentValue node) {
        return Collections.emptyList();
    }

    @Override
    public List<TypeCheckingError> visit(MethodCallValue node) {
        return Collections.emptyList();
    }

    @Override
    public List<TypeCheckingError> visit(NestedValue node) {
        return Collections.emptyList();
    }

    @Override
    public List<TypeCheckingError> visit(FunctionValue node) {
        return Collections.emptyList();
    }

    @Override
    public List<TypeCheckingError> visit(Statement node) {
        return Collections.emptyList();
    }

    @Override
    public List<TypeCheckingError> visit(Method node) {
        return Collections.emptyList();
    }

    @Override
    public List<TypeCheckingError> visit(VarAttribute node) {
        return Collections.emptyList();
    }

    @Override
    public List<TypeCheckingError> visit(ValAttribute node) {
        return Collections.emptyList();
    }
}
