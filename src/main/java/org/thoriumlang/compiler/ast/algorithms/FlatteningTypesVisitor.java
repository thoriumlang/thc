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
package org.thoriumlang.compiler.ast.algorithms;

import org.thoriumlang.compiler.ast.BaseVisitor;
import org.thoriumlang.compiler.ast.CopyVisitor;
import org.thoriumlang.compiler.ast.Node;
import org.thoriumlang.compiler.ast.NodeIdGenerator;
import org.thoriumlang.compiler.ast.TypeSpec;
import org.thoriumlang.compiler.ast.TypeSpecIntersection;
import org.thoriumlang.compiler.ast.TypeSpecUnion;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FlatteningTypesVisitor extends CopyVisitor {
    private final NodeIdGenerator nodeIdGenerator;

    public FlatteningTypesVisitor(NodeIdGenerator nodeIdGenerator) {
        this.nodeIdGenerator = nodeIdGenerator;
    }

    @Override
    public Node visit(TypeSpecIntersection node) {
        return new TypeSpecIntersection(
                nodeIdGenerator.next(),
                flattenTypeIntersection(node.getTypes())
        );
    }

    private List<TypeSpec> flattenTypeIntersection(List<TypeSpec> types) {
        return Stream.concat(
                types.stream()
                        .filter(obj -> !(obj instanceof TypeSpecIntersection)),
                types.stream()
                        .filter(TypeSpecIntersection.class::isInstance)
                        .map(t -> t.accept(new BaseVisitor<List<TypeSpec>>() {
                            @Override
                            public List<TypeSpec> visit(TypeSpecIntersection node) {
                                return flattenTypeIntersection(node.getTypes());
                            }
                        }))
                        .flatMap(Collection::stream)
        ).collect(Collectors.toList());
    }

    @Override
    public Node visit(TypeSpecUnion node) {
        return new TypeSpecUnion(
                nodeIdGenerator.next(),
                flattenTypeUnion(node.getTypes())
        );
    }

    private List<TypeSpec> flattenTypeUnion(List<TypeSpec> types) {
        return Stream.concat(
                types.stream()
                        .filter(obj -> !(obj instanceof TypeSpecUnion)),
                types.stream()
                        .filter(TypeSpecUnion.class::isInstance)
                        .map(t -> t.accept(new BaseVisitor<List<TypeSpec>>() {
                            @Override
                            public List<TypeSpec> visit(TypeSpecUnion node) {
                                return flattenTypeUnion(node.getTypes());
                            }
                        }))
                        .flatMap(Collection::stream)
        ).collect(Collectors.toList());
    }
}
