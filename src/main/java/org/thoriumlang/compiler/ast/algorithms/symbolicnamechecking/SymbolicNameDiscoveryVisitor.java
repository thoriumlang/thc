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

import org.thoriumlang.compiler.api.errors.SemanticError;
import org.thoriumlang.compiler.ast.context.ReferencedNode;
import org.thoriumlang.compiler.ast.nodes.Attribute;
import org.thoriumlang.compiler.ast.nodes.BooleanValue;
import org.thoriumlang.compiler.ast.nodes.Class;
import org.thoriumlang.compiler.ast.nodes.DirectAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.FunctionValue;
import org.thoriumlang.compiler.ast.nodes.IdentifierValue;
import org.thoriumlang.compiler.ast.nodes.IndirectAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.Method;
import org.thoriumlang.compiler.ast.nodes.MethodCallValue;
import org.thoriumlang.compiler.ast.nodes.NestedValue;
import org.thoriumlang.compiler.ast.nodes.NewAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.Node;
import org.thoriumlang.compiler.ast.nodes.NoneValue;
import org.thoriumlang.compiler.ast.nodes.NumberValue;
import org.thoriumlang.compiler.ast.nodes.Parameter;
import org.thoriumlang.compiler.ast.nodes.Reference;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.ast.nodes.Statement;
import org.thoriumlang.compiler.ast.nodes.StringValue;
import org.thoriumlang.compiler.ast.nodes.Type;
import org.thoriumlang.compiler.ast.visitor.BaseVisitor;
import org.thoriumlang.compiler.collections.Lists;
import org.thoriumlang.compiler.symbols.Name;
import org.thoriumlang.compiler.symbols.Symbol;
import org.thoriumlang.compiler.symbols.SymbolTable;
import org.thoriumlang.compiler.symbols.SymbolicName;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

class SymbolicNameDiscoveryVisitor extends BaseVisitor<List<SemanticError>> {
    @Override
    public List<SemanticError> visit(Root node) {
        return node.getTopLevelNode().accept(this);
    }

    @Override
    public List<SemanticError> visit(Type node) {
        return Collections.emptyList();
    }

    @Override
    public List<SemanticError> visit(Class node) {
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
    public List<SemanticError> visit(Attribute node) {
        SymbolTable symbolTable = getSymbolTable(node).enclosingScope();

        List<SemanticError> errors = Lists.merge(
                alreadyDefined(symbolTable, node.getName(), node),
                node.getValue().accept(this)
        );

        symbolTable.put(new Name(node.getName()), new SymbolicName(node));

        return errors;
    }

    @Override
    public List<SemanticError> visit(StringValue node) {
        return Collections.emptyList();
    }

    @Override
    public List<SemanticError> visit(NumberValue node) {
        return Collections.emptyList();
    }

    @Override
    public List<SemanticError> visit(BooleanValue node) {
        return Collections.emptyList();
    }

    @Override
    public List<SemanticError> visit(NoneValue node) {
        return Collections.emptyList();
    }

    @Override
    public List<SemanticError> visit(IdentifierValue node) {
        return node.getReference().accept(this);
    }

    @Override
    public List<SemanticError> visit(Reference node) {
        Optional<Node> referencedNode = getSymbolTable(node)
                .find(new Name(node.getName()))
                .map(Symbol::getDefiningNode);

        if (!referencedNode.isPresent()) {
            return Collections.singletonList(undefinedError(node.getName(), node));
        }

        node.getContext().put(ReferencedNode.class, new ReferencedNode(referencedNode.get()));

        return Collections.emptyList();
    }

    @Override
    public List<SemanticError> visit(DirectAssignmentValue node) {
        return Lists.merge(
                node.getReference().accept(this),
                node.getValue().accept(this)
        );
    }

    @Override
    public List<SemanticError> visit(IndirectAssignmentValue node) {
        // TODO cover with tests
        return Lists.merge(
                node.getReference().accept(this),
                node.getIndirectValue().accept(this),
                node.getValue().accept(this)
        );
    }

    @Override
    public List<SemanticError> visit(MethodCallValue node) {
        List<SemanticError> errors = Lists.merge(
                node.getMethodArguments().stream()
                        .map(n -> n.accept(this))
                        .flatMap(List::stream)
                        .collect(Collectors.toList()),
                node.getTypeArguments().stream()
                        .map(n -> n.accept(this))
                        .flatMap(List::stream)
                        .collect(Collectors.toList())
        );

        if (!getSymbolTable(node).find(new Name(node.getMethodName())).isPresent()) {
            errors = Lists.append(errors, undefinedError(node.getMethodName(), node));
        }

        return errors;
    }

    @Override
    public List<SemanticError> visit(NestedValue node) {
        return Lists.merge(
                node.getInner().accept(this),
                node.getOuter().accept(this)
        );
    }

    @Override
    public List<SemanticError> visit(Method node) {
        // the first parent is the signature's symbol table; the second parent is the enclosing class
        SymbolTable symbolTable = getSymbolTable(node).enclosingScope().enclosingScope();

        List<SemanticError> errors = alreadyDefined(symbolTable, node.getSignature().getName(), node);

        symbolTable.put(new Name(node.getSignature().getName()), new SymbolicName(node));

        return Lists.merge(
                errors,
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
    public List<SemanticError> visit(Parameter node) {
        SymbolTable symbolTable = getSymbolTable(node);

        List<SemanticError> errors = alreadyDefined(symbolTable, node.getName(), node);

        symbolTable.put(new Name(node.getName()), new SymbolicName(node));

        return errors;
    }

    @Override
    public List<SemanticError> visit(Statement node) {
        return node.getValue().accept(this);
    }

    @Override
    public List<SemanticError> visit(NewAssignmentValue node) {
        SymbolTable symbolTable = getSymbolTable(node);

        List<SemanticError> errors = Lists.merge(
                alreadyDefined(symbolTable, node.getName(), node),
                node.getValue().accept(this)
        );

        symbolTable.put(new Name(node.getName()), new SymbolicName(node));

        return errors;
    }

    @Override
    public List<SemanticError> visit(FunctionValue node) {
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

    private List<SemanticError> alreadyDefined(SymbolTable symbolTable, String identifier, Node node) {
        if (symbolTable.inScope(new Name(identifier))) {
            return Collections.singletonList(
                    new SemanticError(String.format("symbol already defined: %s", identifier), node)
            );
        }

        return Collections.emptyList();
    }

    private SemanticError undefinedError(String name, Node node) {
        return new SemanticError(String.format("symbol not found: %s", name), node);
    }
}
