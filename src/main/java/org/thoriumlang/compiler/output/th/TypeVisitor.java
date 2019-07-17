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
import org.thoriumlang.compiler.tree.BasePrintableWrapper;
import org.thoriumlang.compiler.tree.Node;
import org.thoriumlang.compiler.tree.PrintableWrapper;

import java.util.List;
import java.util.stream.Collectors;

class TypeVisitor extends BaseVisitor<String> {
    @Override
    public String visitType(String name, List<TypeParameter> typeParameters, TypeSpec superType,
            List<MethodSignature> methods) {
        Node<PrintableWrapper> typeNode = new Node<>(
                new BasePrintableWrapper() {
                    @Override
                    public String startString() {
                        return String.format(
                                "type %s%s : %s {",
                                name,
                                typeParameters.isEmpty() ? "" : typeParameters.stream()
                                        .map(Object::toString)
                                        .collect(Collectors.joining(",", "[", "]")),
                                superType.accept(new TypeSpecVisitor())
                        );
                    }

                    @Override
                    public String toString() {
                        if (methods.isEmpty()) {
                            return startString() + endString();
                        }
                        return "";
                    }

                    @Override
                    public String endString() {
                        return "}";
                    }
                }
        );

        MethodSignatureVisitor methodSignatureVisitor = new MethodSignatureVisitor(typeNode);
        methods.forEach(m -> m.accept(methodSignatureVisitor));

        return typeNode.toString();
    }
}
