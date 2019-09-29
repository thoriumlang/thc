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
package org.thoriumlang.compiler.symbols;

import org.thoriumlang.compiler.ast.nodes.Class;
import org.thoriumlang.compiler.ast.nodes.Node;
import org.thoriumlang.compiler.ast.nodes.Type;
import org.thoriumlang.compiler.ast.nodes.TypeParameter;
import org.thoriumlang.compiler.ast.visitor.BaseVisitor;

public class ThoriumType implements Symbol {
    private final Node node;

    public ThoriumType(Node node) {
        this.node = node;
    }

    @Override
    public String toString() {
        return String.format("(th: %s)", node.accept(new BaseVisitor<String>() {
            @Override
            public String visit(Type node) {
                return String.format("type %s", node.getName());
            }

            @Override
            public String visit(Class node) {
                return String.format("class %s", node.getName());
            }

            @Override
            public String visit(TypeParameter node) {
                return String.format("param %s", node.getName());
            }
        }));
    }
}