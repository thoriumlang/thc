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

import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.output.Walker;

public class ThWalker implements Walker<String> {
    private final Root root;
    private final RootVisitor visitor;

    public ThWalker(Root root) {
        this.root = root;

        TypeSpecVisitor typeSpecVisitor = new TypeSpecVisitor();
        TypeParameterVisitor typeParameterVisitor = new TypeParameterVisitor();
        ParameterVisitor parameterVisitor = new ParameterVisitor(typeSpecVisitor);
        MethodSignatureVisitor methodSignatureVisitor = new MethodSignatureVisitor(
                typeSpecVisitor,
                typeParameterVisitor,
                parameterVisitor
        );
        ValueVisitor valueVisitor = new ValueVisitor(
                typeSpecVisitor,
                typeParameterVisitor,
                parameterVisitor
        );
        this.visitor = new RootVisitor(
                new UseVisitor(),
                new TopLevelVisitor(
                        typeSpecVisitor,
                        new AttributeVisitor(
                                typeSpecVisitor,
                                valueVisitor
                        ),
                        new MethodVisitor(
                                methodSignatureVisitor,
                                valueVisitor
                        ),
                        methodSignatureVisitor
                )
        );
    }

    @Override
    public String walk() {
        return root.accept(visitor);
    }
}
