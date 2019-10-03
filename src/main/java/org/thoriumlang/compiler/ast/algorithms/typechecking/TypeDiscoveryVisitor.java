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

import org.thoriumlang.compiler.ast.nodes.Class;
import org.thoriumlang.compiler.ast.nodes.Method;
import org.thoriumlang.compiler.ast.nodes.MethodSignature;
import org.thoriumlang.compiler.ast.nodes.Node;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.ast.nodes.SymbolTableAwareNode;
import org.thoriumlang.compiler.ast.nodes.TopLevelNode;
import org.thoriumlang.compiler.ast.nodes.Type;
import org.thoriumlang.compiler.ast.nodes.TypeParameter;
import org.thoriumlang.compiler.ast.nodes.Use;
import org.thoriumlang.compiler.ast.visitor.BaseVisitor;
import org.thoriumlang.compiler.collections.Lists;
import org.thoriumlang.compiler.symbols.JavaClass;
import org.thoriumlang.compiler.symbols.JavaInterface;
import org.thoriumlang.compiler.symbols.Symbol;
import org.thoriumlang.compiler.symbols.SymbolTable;
import org.thoriumlang.compiler.symbols.ThoriumType;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This visitor is in charge of discovering all the types / classes available in the current compilation unit. It fills
 * the root symbol tables with all types it finds.
 */
public class TypeDiscoveryVisitor extends BaseVisitor<List<TypeCheckingError>> {
    private final JavaRuntimeClassLoader javaRuntimeClassLoader;

    public TypeDiscoveryVisitor(JavaRuntimeClassLoader javaRuntimeClassLoader) {
        this.javaRuntimeClassLoader = javaRuntimeClassLoader;
    }

    @Override
    public List<TypeCheckingError> visit(Root node) {
        return Lists.merge(
                node.getUses().stream()
                        .map(u -> u.accept(this))
                        .flatMap(List::stream)
                        .collect(Collectors.toList()),
                node.getTopLevelNode().accept(this)
        );
    }

    @Override
    public List<TypeCheckingError> visit(Use node) {
        return javaRuntimeClassLoader.find(node.getFrom())
                .map(c -> {
                    getSymbolTable(node).put(fromJavaClass(node.getTo(), node, c));
                    return Collections.<TypeCheckingError>emptyList();
                })
                .orElse(Collections.singletonList(
                        new TypeCheckingError(String.format("symbol not found: %s", node.getFrom()))
                ));
    }

    private SymbolTable getSymbolTable(Node node) {
        return SymbolTableAwareNode.wrap(node).getSymbolTable();
    }

    private Symbol fromJavaClass(String name, Node node, java.lang.Class clazz) {
        return clazz.isInterface() ?
                new JavaInterface(name, node, clazz) :
                new JavaClass(name, node, clazz);
    }

    @Override
    @SuppressWarnings("squid:S3864") // ok to use peek()
    public List<TypeCheckingError> visit(Type node) {
        List<TypeCheckingError> errors = visitTopLevel(node, node.getName(), node.getTypeParameters());

        if (!errors.isEmpty()) {
            return errors;
        }

        SymbolTable symbolTable = SymbolTableAwareNode.wrap(node).getSymbolTable();

        return node.getMethods().stream()
                .peek(m -> SymbolTableAwareNode.wrap(m).setSymbolTable(symbolTable.createNestedTable(m.getName())))
                .map(m -> m.accept(this))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private List<TypeCheckingError> visitTopLevel(TopLevelNode node, String name, List<TypeParameter> typeParameters) {
        SymbolTable parentSymbolTable = getSymbolTable(node);
        SymbolTable symbolTable = SymbolTableAwareNode.wrap(node)
                .setSymbolTable(parentSymbolTable.createNestedTable(name));

        if (parentSymbolTable.find(name).isPresent()) {
            return Collections.singletonList(
                    new TypeCheckingError(String.format("symbol already defined: %s", name))
            );
        }

        parentSymbolTable.put(new ThoriumType(name, node));

        typeParameters.forEach(t -> symbolTable.put(new ThoriumType(t.getName(), t)));

        return Collections.emptyList();
    }

    @Override
    public List<TypeCheckingError> visit(Class node) {
        List<TypeCheckingError> errors = visitTopLevel(node, node.getName(), node.getTypeParameters());

        if (!errors.isEmpty()) {
            return errors;
        }

        return node.getMethods().stream()
                .map(m -> m.accept(this))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @Override
    public List<TypeCheckingError> visit(Method node) {
        SymbolTableAwareNode symbolTableAwareNode = SymbolTableAwareNode.wrap(node);
        symbolTableAwareNode.setSymbolTable(
                symbolTableAwareNode.getSymbolTable().createNestedTable(node.getSignature().getName())
        );

        return node.getSignature().accept(this);
    }

    @Override
    public List<TypeCheckingError> visit(MethodSignature node) {
        SymbolTable symbolTable = getSymbolTable(node);

        node.getTypeParameters().forEach(t -> symbolTable.put(new ThoriumType(t.getName(), t)));

        return Collections.emptyList();
    }
}
