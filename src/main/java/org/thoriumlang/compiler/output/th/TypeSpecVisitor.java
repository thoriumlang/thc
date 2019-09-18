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
import org.thoriumlang.compiler.ast.TypeSpecFunction;
import org.thoriumlang.compiler.ast.TypeSpecInferred;
import org.thoriumlang.compiler.ast.TypeSpecIntersection;
import org.thoriumlang.compiler.ast.TypeSpecSimple;
import org.thoriumlang.compiler.ast.TypeSpecUnion;

import java.util.Comparator;
import java.util.stream.Collectors;

class TypeSpecVisitor extends BaseVisitor<String> {
    @Override
    public String visit(TypeSpecIntersection node) {
        return String.format("(%s)",
                node.getTypes().stream()
                        .sorted(Comparator.comparing(Object::toString))
                        .map(t -> t.accept(this))
                        .collect(Collectors.joining(" | "))
        );
    }

    @Override
    public String visit(TypeSpecUnion node) {
        return String.format("(%s)",
                node.getTypes().stream()
                        .sorted(Comparator.comparing(Object::toString))
                        .map(t -> t.accept(this))
                        .collect(Collectors.joining(" & "))
        );
    }

    @Override
    public String visit(TypeSpecSimple node) {
        return String.format(
                "%s%s",
                node.getType(),
                node.getArguments().isEmpty() ? "" : node.getArguments().stream()
                        .map(e -> e.accept(this))
                        .collect(Collectors.joining(", ", "[", "]"))
        );
    }

    @Override
    public String visit(TypeSpecFunction node) {
        return String.format("(%s): %s",
                node.getArguments().stream()
                        .map(t -> t.accept(this))
                        .collect(Collectors.joining(", ")),
                node.getReturnType().accept(this)
        );
    }

    @Override
    public String visit(TypeSpecInferred node) {
        return "";
    }
}
