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

import org.thoriumlang.compiler.ast.nodes.TypeSpec;
import org.thoriumlang.compiler.ast.nodes.TypeSpecIntersection;
import org.thoriumlang.compiler.ast.nodes.TypeSpecSimple;
import org.thoriumlang.compiler.ast.nodes.TypeSpecUnion;
import org.thoriumlang.compiler.ast.visitor.BaseVisitor;
import org.thoriumlang.compiler.symbols.Name;
import org.thoriumlang.compiler.symbols.SymbolTable;
import org.thoriumlang.compiler.symbols.ThoriumType;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

class TypeSpecToTypeConverter implements Function<TypeSpec, Type> {
    @Override
    public Type apply(TypeSpec typeSpec) {
        Function<TypeSpecSimple, Type> typeSpecSimpleToType = n ->
                ((ThoriumType) n.getContext()
                        .get(SymbolTable.class)
                        .orElseThrow(() -> new IllegalStateException("no symbol table found"))
                        .find(new Name(n.getType()))
                        .orElseThrow(() -> new IllegalStateException("symbol not found: " + n.getType()))
                )
                        .getNode()
                        .accept(new BaseVisitor<Type>() {
                            @Override
                            public Type visit(org.thoriumlang.compiler.ast.nodes.Type node) {
                                return new TypeType(node);
                            }

                            @Override
                            public Type visit(org.thoriumlang.compiler.ast.nodes.Class node) {
                                return new Class(node);
                            }
                        });

        BaseVisitor<List<Type>> converter = new BaseVisitor<List<Type>>() {
            @Override
            public List<Type> visit(TypeSpecIntersection node) {
                BaseVisitor<List<Type>> self = this;
                return node.getTypes().stream()
                        .map(t -> t.accept(new BaseVisitor<Type>() {
                            @Override
                            public Type visit(TypeSpecSimple node) {
                                return typeSpecSimpleToType.apply(node);
                            }

                            @Override
                            public Type visit(TypeSpecUnion node) {
                                return new UnionType(node.accept(self));
                            }
                        }))
                        .collect(Collectors.toList());
            }

            @Override
            public List<Type> visit(TypeSpecUnion node) {
                BaseVisitor<List<Type>> self = this;
                return node.getTypes().stream()
                        .map(t -> t.accept(new BaseVisitor<Type>() {
                            @Override
                            public Type visit(TypeSpecSimple node) {
                                return typeSpecSimpleToType.apply(node);
                            }

                            @Override
                            public Type visit(TypeSpecIntersection node) {
                                return new IntersectionType(node.accept(self));
                            }
                        }))
                        .collect(Collectors.toList());
            }
        };

        return typeSpec.accept(new BaseVisitor<Type>() {
            @Override
            public Type visit(TypeSpecSimple node) {
                return typeSpecSimpleToType.apply(node);
            }

            @Override
            public Type visit(TypeSpecIntersection node) {
                return new IntersectionType(converter.visit(node));
            }

            @Override
            public Type visit(TypeSpecUnion node) {
                return new UnionType(converter.visit(node));
            }
        });
    }
}
