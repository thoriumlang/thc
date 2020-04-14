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

import org.thoriumlang.compiler.ast.nodes.Attribute;
import org.thoriumlang.compiler.ast.nodes.TypeSpec;
import org.thoriumlang.compiler.ast.visitor.BaseVisitor;

class AttributeVisitor extends BaseVisitor<String> {
    private final TypeSpecVisitor typeSpecVisitor;
    private final ValueVisitor valueVisitor;

    AttributeVisitor(TypeSpecVisitor typeSpecVisitor, ValueVisitor valueVisitor) {
        this.typeSpecVisitor = typeSpecVisitor;
        this.valueVisitor = valueVisitor;
    }

    @Override
    public String visit(Attribute node) {
        return String.format("%s %s%s = %s",
                node.getMode().toString().toLowerCase(),
                node.getName(),
                type(node.getType()),
                node.getValue().accept(valueVisitor)
        );
    }

    private String type(TypeSpec typeSpec) {
        String type = typeSpec.accept(typeSpecVisitor);
        return type.isEmpty() ?
                "" :
                String.format(": %s", type);
    }

}
