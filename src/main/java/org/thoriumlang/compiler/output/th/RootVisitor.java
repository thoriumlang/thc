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
import org.thoriumlang.compiler.ast.Type;
import org.thoriumlang.compiler.ast.Use;

import java.util.List;
import java.util.stream.Collectors;

class RootVisitor extends BaseVisitor<String> {
    private final TypeVisitor typeVisitor;
    private final UseVisitor useVisitor;

    RootVisitor(UseVisitor useVisitor, TypeVisitor typeVisitor) {
        this.useVisitor = useVisitor;
        this.typeVisitor = typeVisitor;
    }

    @Override
    public String visitRoot(String namespace, Type type, List<Use> uses) {
        String use = uses.stream()
                .map(u -> u.accept(useVisitor))
                .collect(Collectors.joining("\n"));
        return String.format("// namespace %s%n%n%s%s",
                namespace,
                use.isEmpty() ? "" : String.format("%s%n%n", use),
                type.accept(typeVisitor)
        );
    }
}
