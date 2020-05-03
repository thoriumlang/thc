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
package org.thoriumlang.compiler.ast.visitor;

import org.thoriumlang.compiler.ast.context.SourcePosition;
import org.thoriumlang.compiler.ast.nodes.Node;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.nodes.TypeSpec;
import org.thoriumlang.compiler.ast.nodes.TypeSpecIntersection;
import org.thoriumlang.compiler.ast.nodes.TypeSpecUnion;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO verify what it does with context (see https://github.com/thoriumlang/thc/issues/69)
public class TypeFlatteningVisitor extends CopyVisitor {
    private final NodeIdGenerator nodeIdGenerator;

    public TypeFlatteningVisitor(NodeIdGenerator nodeIdGenerator) {
        this.nodeIdGenerator = nodeIdGenerator;
    }

    public static Predicate<TypeSpec> distinctByKey(Function<? super TypeSpec, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    @Override
    public Node visit(TypeSpecIntersection node) {
        List<TypeSpec> flattenedIntersection = flattenTypeIntersection(node.getTypes());

        TypeSpec typeSpec = (flattenedIntersection.size() > 1)
                ? new TypeSpecIntersection(nodeIdGenerator.next(), flattenedIntersection)
                : flattenedIntersection.get(0);

        return typeSpec.getContext()
                .copyFrom(SourcePosition.class, node)
                .getNode();
    }

    private List<TypeSpec> flattenTypeIntersection(List<TypeSpec> types) {
        return Stream
                .concat(
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
                )
                .filter(distinctByKey(Object::toString))
                .collect(Collectors.toList());
    }

    @Override
    public Node visit(TypeSpecUnion node) {
        List<TypeSpec> flattenedUnion = flattenTypeUnion(node.getTypes());

        TypeSpec typeSpec = (flattenedUnion.size() > 1)
                ? new TypeSpecUnion(nodeIdGenerator.next(), flattenedUnion)
                : flattenedUnion.get(0);

        return typeSpec.getContext()
                .copyFrom(SourcePosition.class, node)
                .getNode();
    }

    private List<TypeSpec> flattenTypeUnion(List<TypeSpec> types) {
        return Stream
                .concat(
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
                )
                .filter(distinctByKey(Object::toString))
                .collect(Collectors.toList());
    }
}
