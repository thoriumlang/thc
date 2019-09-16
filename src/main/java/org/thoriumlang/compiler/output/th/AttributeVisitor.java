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
import org.thoriumlang.compiler.ast.NodeId;
import org.thoriumlang.compiler.ast.TypeSpec;
import org.thoriumlang.compiler.ast.Value;

class AttributeVisitor extends BaseVisitor<String> {
    private final TypeSpecVisitor typeSpecVisitor;
    private final ValueVisitor valueVisitor;

    AttributeVisitor(TypeSpecVisitor typeSpecVisitor, ValueVisitor valueVisitor) {
        this.typeSpecVisitor = typeSpecVisitor;
        this.valueVisitor = valueVisitor;
    }

    @Override
    public String visitVarAttribute(NodeId nodeId, String identifier, TypeSpec type, Value value) {
        return String.format("var %s%s = %s",
                identifier,
                type(type),
                value.accept(valueVisitor)
        );
    }

    private String type(TypeSpec typeSpec) {
        String type = typeSpec.accept(typeSpecVisitor);
        return type.isEmpty() ?
                "" :
                String.format(": %s", type);
    }

    @Override
    public String visitValAttribute(NodeId nodeId, String identifier, TypeSpec type, Value value) {
        return String.format("val %s%s = %s",
                identifier,
                type(type),
                value.accept(valueVisitor)
        );
    }
}
