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
package org.thoriumlang.compiler.output.th;

import org.thoriumlang.compiler.ast.nodes.Class;
import org.thoriumlang.compiler.ast.nodes.Type;
import org.thoriumlang.compiler.ast.nodes.TypeParameter;
import org.thoriumlang.compiler.ast.visitor.BaseVisitor;
import org.thoriumlang.compiler.helpers.Indent;

import java.util.stream.Collectors;
import java.util.stream.Stream;

class TopLevelVisitor extends BaseVisitor<String> {
    private final TypeSpecVisitor typeSpecVisitor;
    private final AttributeVisitor attributeVisitor;
    private final MethodVisitor methodVisitor;
    private final MethodSignatureVisitor methodSignatureVisitor;


    TopLevelVisitor(TypeSpecVisitor typeSpecVisitor, AttributeVisitor attributeVisitor, MethodVisitor methodVisitor,
            MethodSignatureVisitor methodSignatureVisitor) {
        this.typeSpecVisitor = typeSpecVisitor;
        this.attributeVisitor = attributeVisitor;
        this.methodVisitor = methodVisitor;
        this.methodSignatureVisitor = methodSignatureVisitor;
    }

    @Override
    public String visit(Class node) {
        return String.format(
                "%s class %s%s : %s {%s}",
                node.getVisibility().name().toLowerCase(),
                node.getName(),
                node.getTypeParameters().isEmpty() ?
                        "" :
                        node.getTypeParameters().stream()
                                .map(TypeParameter::toString)
                                .collect(Collectors.joining(", ", "[", "]")),
                node.getSuperType().accept(typeSpecVisitor),
                Stream
                        .of(
                                node.getAttributes().stream()
                                        .map(a -> a.accept(attributeVisitor))
                                        .map(Indent.INSTANCE)
                                        .map(s -> s + ";")
                                        .collect(Collectors.joining("\n")),
                                node.getMethods().stream()
                                        .map(m -> m.accept(methodVisitor))
                                        .map(Indent.INSTANCE)
                                        .collect(Collectors.joining("\n\n"))
                        )
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.joining("\n\n", "\n", "\n"))
        );
    }

    @Override
    public String visit(Type node) {
        return String.format(
                "%s type %s%s : %s {%s}",
                node.getVisibility().name().toLowerCase(),
                node.getName(),
                node.getTypeParameters().isEmpty() ?
                        "" :
                        node.getTypeParameters().stream()
                                .map(TypeParameter::toString)
                                .collect(Collectors.joining(", ", "[", "]")),
                node.getSuperType().accept(typeSpecVisitor),
                node.getMethods().isEmpty() ?
                        "\n" :
                        String.format("%n%s%n",
                                node.getMethods().stream()
                                        .map(m -> m.accept(methodSignatureVisitor))
                                        .map(s -> s + ";")
                                        .map(Indent.INSTANCE)
                                        .collect(Collectors.joining("\n"))
                        )
        );
    }
}
