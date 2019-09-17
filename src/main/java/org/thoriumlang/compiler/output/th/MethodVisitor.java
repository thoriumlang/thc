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
import org.thoriumlang.compiler.ast.Method;

import java.util.stream.Collectors;

class MethodVisitor extends BaseVisitor<String> {
    private final MethodSignatureVisitor methodSignatureVisitor;
    private final ValueVisitor valueVisitor;

    MethodVisitor(MethodSignatureVisitor methodSignatureVisitor, ValueVisitor valueVisitor) {
        this.methodSignatureVisitor = methodSignatureVisitor;
        this.valueVisitor = valueVisitor;
    }

    @Override
    public String visitMethod(Method node) {
        return String.format("%s {%n%s%n}",
                node.getSignature().accept(methodSignatureVisitor),
                node.getStatements().stream()
                        .map(s -> s.accept(valueVisitor))
                        .map(Indent.INSTANCE)
                        .map(s -> s + ";")
                        .collect(Collectors.joining("\n"))
        );
    }
}
