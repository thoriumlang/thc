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

import org.thoriumlang.compiler.ast.algorithms.CompilationError;
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
import org.thoriumlang.compiler.symbols.JavaClass;
import org.thoriumlang.compiler.symbols.JavaInterface;
import org.thoriumlang.compiler.symbols.Name;
import org.thoriumlang.compiler.symbols.Symbol;
import org.thoriumlang.compiler.symbols.SymbolTable;
import org.thoriumlang.compiler.symbols.ThoriumType;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This visitor is in charge of discovering all the types / classes available in the current compilation unit. It fills
 * the root symbol tables with all types it finds.
 */
public class TypeDiscoveryVisitor extends BaseVisitor<List<CompilationError>> {
    private final JavaRuntimeClassLoader javaRuntimeClassLoader;

    public TypeDiscoveryVisitor(JavaRuntimeClassLoader javaRuntimeClassLoader) {
        this.javaRuntimeClassLoader = javaRuntimeClassLoader;
    }

    @Override
    public List<CompilationError> visit(Root node) {
        return Lists.merge(
                node.getUses().stream()
                        .map(u -> u.accept(this))
                        .flatMap(List::stream)
                        .collect(Collectors.toList()),
                node.getTopLevelNode().accept(this)
        );
    }

    @Override
    public List<CompilationError> visit(Use node) {
        // FIXME duplicate use name
        return javaRuntimeClassLoader.find(node.getFrom())
                .map(c -> {
                    getSymbolTable(node).put(new Name(node.getTo()), fromJavaClass(node.getTo(), node, c));
                    return Collections.<CompilationError>emptyList();
                })
                .orElse(Collections.singletonList(
                        new CompilationError(String.format("symbol not found: %s", node.getFrom()), node)
                ));
    }

    private SymbolTable getSymbolTable(Node node) {
        return node.getContext()
                .get(SymbolTable.class)
                .orElseThrow(() -> new IllegalStateException("no symbol table found"));
    }

    private Symbol fromJavaClass(String name, Node node, java.lang.Class clazz) {
        return clazz.isInterface() ?
                new JavaInterface(node, clazz) :
                new JavaClass(node, clazz);
    }

    @Override
    public List<CompilationError> visit(TypeParameter node) {
        getSymbolTable(node).put(
                new Name(node.getName()),
                new ThoriumType(node)
        );

        return Collections.emptyList();
    }

    @Override
    public List<CompilationError> visit(Type node) {
        List<CompilationError> errors = visitTopLevel(node, node.getName(), node.getTypeParameters());

        if (!errors.isEmpty()) {
            return errors;
        }

        return node.getMethods().stream()
                .map(m -> m.accept(this))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private List<CompilationError> visitTopLevel(TopLevelNode node, String name, List<TypeParameter> typeParameters) {
        SymbolTable symbolTable = getSymbolTable(node);

        if (symbolTable.find(new Name(name)).isPresent()) {
            return Collections.singletonList(
                    new CompilationError(String.format("symbol already defined: %s", name), node)
            );
        }

        symbolTable               // [body]
                .enclosingScope() // type or class
                .enclosingScope() // root
                .put(new Name(name), new ThoriumType(node));
        // symbolTable.put(new Name());

        return typeParameters.stream()
                .map(p -> p.accept(this))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @Override
    public List<CompilationError> visit(Class node) {
        List<CompilationError> errors = visitTopLevel(node, node.getName(), node.getTypeParameters());

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
    public List<CompilationError> visit(Method node) {
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
    public List<CompilationError> visit(MethodSignature node) {
        return node.getTypeParameters().stream()
                .map(p -> p.accept(this))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @Override
    public List<CompilationError> visit(FunctionValue node) {
        return node.getTypeParameters().stream()
                .map(p -> p.accept(this))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
}
