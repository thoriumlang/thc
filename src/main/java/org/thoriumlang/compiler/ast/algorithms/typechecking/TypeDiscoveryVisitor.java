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

import org.thoriumlang.compiler.api.errors.SemanticError;
import org.thoriumlang.compiler.api.errors.SymbolAlreadyDefinedError;
import org.thoriumlang.compiler.api.errors.SymbolNotFoundError;
import org.thoriumlang.compiler.ast.nodes.Class;
import org.thoriumlang.compiler.ast.nodes.FunctionValue;
import org.thoriumlang.compiler.ast.nodes.Method;
import org.thoriumlang.compiler.ast.nodes.MethodSignature;
import org.thoriumlang.compiler.ast.nodes.Node;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.ast.nodes.TopLevelNode;
import org.thoriumlang.compiler.ast.nodes.Type;
import org.thoriumlang.compiler.ast.nodes.TypeParameter;
import org.thoriumlang.compiler.ast.nodes.Use;
import org.thoriumlang.compiler.ast.visitor.BaseVisitor;
import org.thoriumlang.compiler.collections.Lists;
import org.thoriumlang.compiler.input.loaders.TypeLoader;
import org.thoriumlang.compiler.symbols.AliasSymbol;
import org.thoriumlang.compiler.symbols.Name;
import org.thoriumlang.compiler.symbols.Symbol;
import org.thoriumlang.compiler.symbols.SymbolTable;
import org.thoriumlang.compiler.symbols.ThoriumType;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This visitor is in charge of discovering all the types / classes available in the current compilation unit. It fills
 * the root symbol tables with all types it finds.
 */
public class TypeDiscoveryVisitor extends BaseVisitor<List<SemanticError>> {
    private final String namespace;
    private final TypeLoader typeLoader;

    public TypeDiscoveryVisitor(String namespace, TypeLoader typeLoader) {
        this.namespace = namespace;
        this.typeLoader = typeLoader;
    }

    @Override
    public List<SemanticError> visit(Root node) {
        return Lists.merge(
                node.getUses().stream()
                        .map(u -> u.accept(this))
                        .flatMap(List::stream)
                        .collect(Collectors.toList()),
                node.getTopLevelNode().accept(this)
        );
    }

    @Override
    public List<SemanticError> visit(Use node) {
        SymbolTable symbolTable = node.getContext().require(SymbolTable.class);

        Name aliasName = new Name(node.getTo());
        Name targetName = new Name(node.getFrom(), namespace);

        if (!symbolTable.find(targetName).isEmpty()) {
            symbolTable.put(aliasName, new AliasSymbol(node, targetName.getFullName()));
            return Collections.emptyList();
        }

        List<SemanticError> symbolAlreadyDefinedErrors = Stream.of(aliasName)
                .map(name -> symbolTable
                        .find(name)
                        .stream()
                        .map(s -> new SymbolAlreadyDefinedError(node, name.toString()))
                )
                .map(s -> s.collect(Collectors.toList()))
                .flatMap(List::stream)
                .collect(Collectors.toList());

        if (!symbolAlreadyDefinedErrors.isEmpty()) {
            return symbolAlreadyDefinedErrors;
        }

        Optional<Symbol> symbol = typeLoader.load(targetName, node);

        if (symbol.isPresent()) {
            symbolTable.put(aliasName, new AliasSymbol(node, targetName.getFullName()));
            symbolTable.put(targetName, symbol.get());

            return Collections.emptyList();
        }

        return Collections.singletonList(
                new SymbolNotFoundError(node, node.getFrom())
        );
    }

    private SymbolTable getSymbolTable(Node node) {
        return node.getContext().require(SymbolTable.class);
    }

    @Override
    public List<SemanticError> visit(TypeParameter node) {
        getSymbolTable(node).put(
                new Name(node.getName()),
                new ThoriumType(node, node)
        );

        return Collections.emptyList();
    }

    @Override
    public List<SemanticError> visit(Type node) {
        List<SemanticError> errors = visitTopLevel(node, node.getName(), node.getTypeParameters());

        if (!errors.isEmpty()) {
            return errors;
        }

        return node.getMethods().stream()
                .map(m -> m.accept(this))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private List<SemanticError> visitTopLevel(TopLevelNode node, String name, List<TypeParameter> typeParameters) {
        SymbolTable symbolTable = getSymbolTable(node);

        if (!symbolTable.find(new Name(name)).isEmpty()) {
            return Collections.singletonList(
                    new SymbolAlreadyDefinedError(node, name)
            );
        }

        String fqName = namespace + "." + name;

        symbolTable               // [body]
                .enclosingScope() // type or class
                .put(new Name(name), new AliasSymbol(node, fqName));

        symbolTable.put(new Name(fqName), new ThoriumType(node, node));

        return typeParameters.stream()
                .map(p -> p.accept(this))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @Override
    public List<SemanticError> visit(Class node) {
        List<SemanticError> errors = visitTopLevel(node, node.getName(), node.getTypeParameters());

        if (!errors.isEmpty()) {
            return errors;
        }

        return Lists.merge(
                node.getAttributes().stream()
                        .map(a -> a.getValue().accept(this))
                        .filter(Objects::nonNull)
                        .flatMap(List::stream)
                        .collect(Collectors.toList()),
                node.getMethods().stream()
                        .map(m -> m.accept(this))
                        .flatMap(List::stream)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public List<SemanticError> visit(Method node) {
        return Lists.merge(
                node.getSignature().accept(this),
                node.getStatements().stream()
                        .map(s -> s.getValue().accept(this))
                        .filter(Objects::nonNull)
                        .flatMap(List::stream)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public List<SemanticError> visit(MethodSignature node) {
        return node.getTypeParameters().stream()
                .map(p -> p.accept(this))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @Override
    public List<SemanticError> visit(FunctionValue node) {
        return node.getTypeParameters().stream()
                .map(p -> p.accept(this))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
}
