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
package org.thoriumlang.compiler.ast.api;

import com.google.common.collect.ImmutableMap;
import org.thoriumlang.compiler.ast.AST;
import org.thoriumlang.compiler.ast.nodes.Node;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.ast.visitor.BaseVisitor;
import org.thoriumlang.compiler.symbols.AliasSymbol;
import org.thoriumlang.compiler.symbols.Name;
import org.thoriumlang.compiler.symbols.Symbol;
import org.thoriumlang.compiler.symbols.SymbolTable;
import org.thoriumlang.compiler.symbols.ThoriumType;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class CompilationUnit {
    private final Root root;

    public CompilationUnit(AST ast) throws IOException {
        Objects.requireNonNull(ast, "ast cannot be null");
        this.root = ast.root();
    }

    public TopLevelKind getTopLevelKind() {
        return root.getTopLevelNode().accept(new BaseVisitor<TopLevelKind>() {
            @Override
            public TopLevelKind visit(org.thoriumlang.compiler.ast.nodes.Type node) {
                return TopLevelKind.TYPE;
            }

            @Override
            public TopLevelKind visit(org.thoriumlang.compiler.ast.nodes.Class node) {
                return TopLevelKind.CLASS;
            }
        });
    }

    public Optional<Class> findClass(String name) {
        SymbolTable symbolTable = root.getTopLevelNode().getContext()
                .get(SymbolTable.class).orElseThrow(() -> new IllegalStateException("no symbol table found"));

        return symbolTable
                .find(new Name(name))
                .map(s -> decode(s, symbolTable))
                .map(n -> n.accept(new BaseVisitor<org.thoriumlang.compiler.ast.nodes.Class>() {
                    @Override
                    public org.thoriumlang.compiler.ast.nodes.Class visit(
                            org.thoriumlang.compiler.ast.nodes.Class node) {
                        return node.getName().equals(name) ? node : null;
                    }
                }))
                .map(Class::new);
    }

    private Node decode(Symbol symbol, SymbolTable symbolTable) {
        Map<java.lang.Class<?>, Function<Symbol, Node>> map = ImmutableMap.of(
                ThoriumType.class, new Function<Symbol, Node>() {
                    @Override
                    public Node apply(Symbol symbol) {
                        return ((ThoriumType) symbol).getNode();
                    }
                },
                AliasSymbol.class, new Function<Symbol, Node>() {
                    @Override
                    public Node apply(Symbol symbol) {
                        Name name = new Name(((AliasSymbol) symbol).getTarget());
                        return symbolTable.find(name)
                                .map(new Function<Symbol, Node>() {
                                    @Override
                                    public Node apply(Symbol symbol) {
                                        return ((ThoriumType) symbol).getNode();
                                    }
                                })
                                .orElseThrow(() -> new IllegalStateException(name + " not found"));
                    }
                }
        );
        return map.get(symbol.getClass()).apply(symbol);
    }

    public Optional<Type> findType(String name) {
        return Optional.ofNullable(
                root.getTopLevelNode().accept(new BaseVisitor<org.thoriumlang.compiler.ast.nodes.Type>() {
                    @Override
                    public org.thoriumlang.compiler.ast.nodes.Type visit(
                            org.thoriumlang.compiler.ast.nodes.Type node) {
                        return node.getName().equals(name) ? node : null;
                    }
                })
        ).map(TypeType::new);
    }

    public enum TopLevelKind {
        CLASS, TYPE
    }
}
