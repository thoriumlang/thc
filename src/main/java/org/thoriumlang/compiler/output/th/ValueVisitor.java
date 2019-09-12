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
import org.thoriumlang.compiler.ast.Value;

import java.util.List;
import java.util.stream.Collectors;

public class ValueVisitor extends BaseVisitor<String> {
    private final TypeSpecVisitor typeSpecVisitor;

    public ValueVisitor(TypeSpecVisitor typeSpecVisitor) {
        this.typeSpecVisitor = typeSpecVisitor;
    }

    @Override
    public String visitStringValue(String value) {
        return "\"" + value + "\"";
    }

    @Override
    public String visitNumberValue(String value) {
        return value;
    }

    @Override
    public String visitBooleanValue(boolean value) {
        return String.valueOf(value);
    }

    @Override
    public String visitNoneValue() {
        return "none";
    }

    @Override
    public String visitIdentifierValue(String value) {
        return value;
    }

    @Override
    public String visitVarAssignmentValue(String identifier, TypeSpec type, Value value) {
        return String.format("var %s: %s = %s",
                identifier,
                type.accept(typeSpecVisitor),
                value.accept(this)
        );
    }

    @Override
    public String visitValAssignmentValue(String identifier, TypeSpec type, Value value) {
        return String.format("val %s: %s = %s",
                identifier,
                type.accept(typeSpecVisitor),
                value.accept(this)
        );
    }

    @Override
    public String visitIndirectAssignmentValue(Value indirectValue, String identifier, Value value) {
        return String.format("%s.%s = %s",
                indirectValue.accept(this),
                identifier,
                value.accept(this)
        );
    }

    @Override
    public String visitMethodCallValue(String methodName, List<TypeSpec> typeArguments, List<Value> methodArguments) {
        return String.format("%s%s(%s)",
                methodName,
                typeArguments.isEmpty() ?
                        "" :
                        typeArguments.stream()
                                .map(a -> a.accept(typeSpecVisitor))
                                .collect(Collectors.joining(", ", "[", "]")),
                methodArguments.isEmpty() ?
                        "" :
                        methodArguments.stream()
                                .map(a -> a.accept(this))
                                .collect(Collectors.joining(", "))
        );
    }

    @Override
    public String visitNestedValue(Value outer, Value inner) {
        return outer.accept(this) + "." + inner.accept(this);
    }
}
