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

import org.thoriumlang.compiler.ast.BaseVisitor;
import org.thoriumlang.compiler.ast.MethodSignature;
import org.thoriumlang.compiler.ast.TypeParameter;
import org.thoriumlang.compiler.ast.TypeSpec;
import org.thoriumlang.compiler.ast.Visibility;

import java.util.List;
import java.util.stream.Collectors;

class TypeVisitor extends BaseVisitor<String> {
    private final TypeSpecVisitor typeSpecVisitor;
    private final MethodSignatureVisitor methodSignatureVisitor;

    TypeVisitor(TypeSpecVisitor typeSpecVisitor, MethodSignatureVisitor methodSignatureVisitor) {
        this.typeSpecVisitor = typeSpecVisitor;
        this.methodSignatureVisitor = methodSignatureVisitor;
    }

    @Override
    public String visitType(Visibility visibility, String name, List<TypeParameter> typeParameters, TypeSpec superType,
            List<MethodSignature> methods) {
        return String.format(
                "%s type %s%s : %s {%s}",
                visibility.name().toLowerCase(),
                name,
                typeParameters.isEmpty() ?
                        "" :
                        typeParameters.stream()
                                .map(TypeParameter::toString)
                                .collect(Collectors.joining(", ", "[", "]")),
                superType.accept(typeSpecVisitor),
                methods.isEmpty() ?
                        "\n" :
                        String.format("%n%s%n",
                                methods.stream()
                                        .map(m -> m.accept(methodSignatureVisitor))
                                        .map(Indent.INSTANCE)
                                        .collect(Collectors.joining("\n"))
                        )
        );
    }
}
