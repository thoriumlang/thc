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
package org.thoriumlang.compiler.ast.algorithms.symbolicnamechecking;

import org.thoriumlang.compiler.ast.algorithms.CompilationError;
import org.thoriumlang.compiler.ast.context.SourcePosition;
import org.thoriumlang.compiler.ast.nodes.Attribute;
import org.thoriumlang.compiler.ast.nodes.Class;
import org.thoriumlang.compiler.ast.nodes.FunctionValue;
import org.thoriumlang.compiler.ast.nodes.Method;
import org.thoriumlang.compiler.ast.nodes.NewAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.Node;
import org.thoriumlang.compiler.ast.nodes.Parameter;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.ast.nodes.Statement;
import org.thoriumlang.compiler.ast.nodes.Type;
import org.thoriumlang.compiler.ast.visitor.BaseVisitor;
import org.thoriumlang.compiler.collections.Lists;
import org.thoriumlang.compiler.symbols.SymbolTable;
import org.thoriumlang.compiler.symbols.SymbolicName;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SymbolicNameDiscoveryVisitor extends BaseVisitor<List<CompilationError>> {
    @Override
    public List<CompilationError> visit(Root node) {
        return node.getTopLevelNode().accept(this);
    }

    @Override
    public List<CompilationError> visit(Type node) {
        return Collections.emptyList();
    }

    @Override
    public List<CompilationError> visit(Class node) {
        return Lists.merge(
                node.getAttributes().stream()
                        .map(m -> m.accept(this))
                        .flatMap(List::stream)
                        .collect(Collectors.toList()),
                node.getMethods().stream()
                        .map(m -> m.accept(this))
                        .flatMap(List::stream)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public List<CompilationError> visit(Attribute node) {
        SymbolTable symbolTable = getSymbolTable(node).parent();

        if (symbolTable.findInScope(node.getIdentifier()).isPresent()) {
            return Collections.singletonList(error(node.getIdentifier(), node));
        }

        symbolTable.put(
                new SymbolicName(node.getIdentifier(), node)
        );

        return Optional.ofNullable(node.getValue().accept(this))
                .orElse(Collections.emptyList());
    }

    private CompilationError error(String name, Node node) {
        return new CompilationError(String.format(
                "symbol already defined: %s (%d)",
                name,
                node.getContext().get(SourcePosition.class)
                        .orElseThrow(() -> new IllegalStateException("no source position found"))
                        .getLine()
        ), node);
    }

    @Override
    public List<CompilationError> visit(Method node) {
        // the first parent is the signature's symbol table; the second parent is the enclosing class
        SymbolTable symbolTable = getSymbolTable(node).parent().parent();

        if (symbolTable.findInScope(node.getSignature().getName()).isPresent()) {
            return Collections.singletonList(error(node.getSignature().getName(), node));
        }

        symbolTable.put(
                new SymbolicName(node.getSignature().getName(), node)
        );

        return Lists.merge(
                node.getSignature().getParameters().stream()
                        .map(p -> p.accept(this))
                        .flatMap(List::stream)
                        .collect(Collectors.toList()),
                node.getStatements().stream()
                        .map(s -> s.accept(this))
                        .flatMap(List::stream)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public List<CompilationError> visit(Parameter node) {
        SymbolTable symbolTable = getSymbolTable(node);

        if (symbolTable.findInScope(node.getName()).isPresent()) {
            return Collections.singletonList(error(node.getName(), node));
        }

        symbolTable.put(
                new SymbolicName(node.getName(), node)
        );

        return Collections.emptyList();
    }

    @Override
    public List<CompilationError> visit(Statement node) {
        return Optional.ofNullable(node.getValue().accept(this))
                .orElse(Collections.emptyList());
    }

    @Override
    public List<CompilationError> visit(NewAssignmentValue node) {
        SymbolTable symbolTable = getSymbolTable(node);

        if (symbolTable.findInScope(node.getIdentifier()).isPresent()) {
            return Collections.singletonList(error(node.getIdentifier(), node));
        }

        symbolTable.put(
                new SymbolicName(node.getIdentifier(), node)
        );

        return Collections.emptyList();
    }

    @Override
    public List<CompilationError> visit(FunctionValue node) {
        return Lists.merge(
                node.getParameters().stream()
                        .map(p -> p.accept(this))
                        .flatMap(List::stream)
                        .collect(Collectors.toList()),
                node.getStatements().stream()
                        .map(s -> s.accept(this))
                        .flatMap(List::stream)
                        .collect(Collectors.toList())
        );
    }

    private SymbolTable getSymbolTable(Node node) {
        return node.getContext()
                .get(SymbolTable.class)
                .orElseThrow(() -> new IllegalStateException("no symbol table found"));
    }
}
