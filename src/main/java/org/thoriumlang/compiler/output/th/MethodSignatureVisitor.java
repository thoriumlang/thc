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

import org.thoriumlang.compiler.ast.visitor.BaseVisitor;
import org.thoriumlang.compiler.ast.MethodSignature;
import org.thoriumlang.compiler.ast.TypeSpec;

import java.util.stream.Collectors;

class MethodSignatureVisitor extends BaseVisitor<String> {
    private final TypeSpecVisitor typeSpecVisitor;
    private final TypeParameterVisitor typeParameterVisitor;
    private final ParameterVisitor parameterVisitor;

    MethodSignatureVisitor(TypeSpecVisitor typeSpecVisitor, TypeParameterVisitor typeParameterVisitor,
            ParameterVisitor parameterVisitor) {
        this.typeSpecVisitor = typeSpecVisitor;
        this.typeParameterVisitor = typeParameterVisitor;
        this.parameterVisitor = parameterVisitor;
    }

    @Override
    public String visit(MethodSignature node) {
        return String.format("%s %s%s(%s)%s",
                node.getVisibility().name().toLowerCase(),
                node.getName(),
                node.getTypeParameters().isEmpty() ?
                        "" :
                        node.getTypeParameters().stream()
                                .map(p -> p.accept(typeParameterVisitor))
                                .collect(Collectors.joining(", ", "[", "]")),
                node.getParameters().stream()
                        .map(p -> p.accept(parameterVisitor))
                        .collect(Collectors.joining(", ")),
                returnType(node.getReturnType())
        );
    }

    private String returnType(TypeSpec returnType) {
        String returnTypeStr = returnType.accept(typeSpecVisitor);
        return returnTypeStr.isEmpty() ?
                "" :
                String.format(": %s", returnTypeStr);
    }
}
