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
import org.thoriumlang.compiler.ast.nodes.Type;
import org.thoriumlang.compiler.ast.nodes.Use;
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
public class TypeDiscoveryVisitor extends BaseTypeCheckingVisitor {
    private final JavaRuntimeClassLoader javaRuntimeClassLoader;

    public TypeDiscoveryVisitor(JavaRuntimeClassLoader javaRuntimeClassLoader) {
        this.javaRuntimeClassLoader = javaRuntimeClassLoader;
    }

    @Override
    public List<TypeCheckingError> visit(Use node) {
        return javaRuntimeClassLoader.find(node.getFrom())
                .map(c -> {
                    getSymbolTable(node).put(node.getTo(), fromJavaClass(c));
                    return Collections.<TypeCheckingError>emptyList();
                })
                .orElse(Collections.singletonList(
                        new TypeCheckingError(String.format("symbol not found: %s", node.getFrom()))
                ));
    }

    private Symbol fromJavaClass(java.lang.Class clazz) {
        return clazz.isInterface() ?
                new JavaInterface(clazz) :
                new JavaClass(clazz);
    }

    @Override
    @SuppressWarnings("squid:S3864") // ok to use peek()
    public List<TypeCheckingError> visit(Type node) {
        SymbolTable parentSymbolTable = getSymbolTable(node);
        SymbolTable symbolTable = setSymbolTable(node, parentSymbolTable.createNestedTable(node.getName()));

        if (parentSymbolTable.find(node.getName()).isPresent()) {
            return Collections.singletonList(
                    new TypeCheckingError(String.format("symbol already defined: %s", node.getName()))
            );
        }

        parentSymbolTable.put(node.getName(), new ThoriumType(node));

        node.getTypeParameters().forEach(t -> symbolTable.put(t.getName(), new ThoriumType(t)));

        return node.getMethods().stream()
                .peek(m -> setSymbolTable(m, symbolTable.createNestedTable(m.getName())))
                .map(m -> m.accept(this))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @Override
    public List<TypeCheckingError> visit(Class node) {
        SymbolTable parentSymbolTable = getSymbolTable(node);
        SymbolTable symbolTable = setSymbolTable(node, parentSymbolTable.createNestedTable(node.getName()));

        if (parentSymbolTable.find(node.getName()).isPresent()) {
            return Collections.singletonList(
                    new TypeCheckingError(String.format("symbol already defined: %s", node.getName()))
            );
        }

        parentSymbolTable.put(node.getName(), new ThoriumType(node));

        node.getTypeParameters().forEach(t -> symbolTable.put(t.getName(), new ThoriumType(t)));

        return node.getMethods().stream()
                .map(m -> m.accept(this))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @Override
    public List<TypeCheckingError> visit(Method node) {
        setSymbolTable(node, getSymbolTable(node).createNestedTable(node.getSignature().getName()));
        return super.visit(node);
    }

    @Override
    public List<TypeCheckingError> visit(MethodSignature node) {
        SymbolTable symbolTable = getSymbolTable(node);

        node.getTypeParameters().forEach(t -> symbolTable.put(t.getName(), new ThoriumType(t)));

        return super.visit(node);
    }
}
