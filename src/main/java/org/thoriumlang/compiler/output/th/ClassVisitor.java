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

import org.thoriumlang.compiler.ast.Attribute;
import org.thoriumlang.compiler.ast.BaseVisitor;
import org.thoriumlang.compiler.ast.Method;
import org.thoriumlang.compiler.ast.NodeId;
import org.thoriumlang.compiler.ast.TypeParameter;
import org.thoriumlang.compiler.ast.TypeSpec;
import org.thoriumlang.compiler.ast.Visibility;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ClassVisitor extends BaseVisitor<String> {
    private final TypeSpecVisitor typeSpecVisitor;
    private final AttributeVisitor attributeVisitor;
    private final MethodVisitor methodVisitor;

    ClassVisitor(TypeSpecVisitor typeSpecVisitor, AttributeVisitor attributeVisitor, MethodVisitor methodVisitor) {
        this.typeSpecVisitor = typeSpecVisitor;
        this.attributeVisitor = attributeVisitor;
        this.methodVisitor = methodVisitor;
    }

    @Override
    public String visitClass(NodeId nodeId, Visibility visibility, String name, List<TypeParameter> typeParameters,
            TypeSpec superType,
            List<Method> methods, List<Attribute> attributes) {
        return String.format(
                "%s class %s%s : %s {%s}",
                visibility.name().toLowerCase(),
                name,
                typeParameters.isEmpty() ?
                        "" :
                        typeParameters.stream()
                                .map(TypeParameter::toString)
                                .collect(Collectors.joining(", ", "[", "]")),
                superType.accept(typeSpecVisitor),
                Stream
                        .of(
                                attributes.stream()
                                        .map(a -> a.accept(attributeVisitor))
                                        .map(Indent.INSTANCE)
                                        .map(s -> s + ";")
                                        .collect(Collectors.joining("\n")),
                                methods.stream()
                                        .map(m -> m.accept(methodVisitor))
                                        .map(Indent.INSTANCE)
                                        .collect(Collectors.joining("\n\n"))
                        )
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.joining("\n\n", "\n", "\n"))
        );
    }
}
