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
import org.thoriumlang.compiler.ast.TypeSpec;
import org.thoriumlang.compiler.ast.Visibility;
import org.thoriumlang.compiler.tree.BasePrintableWrapper;
import org.thoriumlang.compiler.tree.Node;
import org.thoriumlang.compiler.tree.PrintableWrapper;

import java.util.Map;

public class MethodSignatureVisitor extends BaseVisitor<String> {
    private final Node<PrintableWrapper> parent;
    private final Configuration configuration;

    public MethodSignatureVisitor(Node<PrintableWrapper> parent, Configuration configuration) {
        this.parent = parent;
        this.configuration = configuration;
    }

    @Override
    public String visitMethodSignature(Visibility visibility, String name,
            Map<String, String> parameters,
            TypeSpec returnType) {

        return new Node<>(
                parent,
                new BasePrintableWrapper() {
                    @Override
                    public String toString() {
                        return String.format("%s %s(): %s;",
                                visibility.name().toLowerCase(),
                                name,
                                returnType.accept(new TypeSpecVisitor(configuration)));
                    }
                }
        ).toString();
    }
}
