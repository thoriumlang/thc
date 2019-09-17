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
import org.thoriumlang.compiler.ast.BooleanValue;
import org.thoriumlang.compiler.ast.FunctionValue;
import org.thoriumlang.compiler.ast.IdentifierValue;
import org.thoriumlang.compiler.ast.IndirectAssignmentValue;
import org.thoriumlang.compiler.ast.MethodCallValue;
import org.thoriumlang.compiler.ast.NestedValue;
import org.thoriumlang.compiler.ast.NoneValue;
import org.thoriumlang.compiler.ast.NumberValue;
import org.thoriumlang.compiler.ast.Statement;
import org.thoriumlang.compiler.ast.StringValue;
import org.thoriumlang.compiler.ast.TypeSpec;
import org.thoriumlang.compiler.ast.ValAssignmentValue;
import org.thoriumlang.compiler.ast.VarAssignmentValue;

import java.util.stream.Collectors;

class ValueVisitor extends BaseVisitor<String> {
    private final TypeSpecVisitor typeSpecVisitor;
    private final TypeParameterVisitor typeParameterVisitor;
    private final ParameterVisitor parameterVisitor;

    ValueVisitor(TypeSpecVisitor typeSpecVisitor, TypeParameterVisitor typeParameterVisitor,
            ParameterVisitor parameterVisitor) {
        this.typeSpecVisitor = typeSpecVisitor;
        this.typeParameterVisitor = typeParameterVisitor;
        this.parameterVisitor = parameterVisitor;
    }

    @Override
    public String visitStatement(Statement node) {
        return node.isLast() ?
                "return " + node.getValue().accept(this) :
                node.getValue().accept(this);
    }

    @Override
    public String visitStringValue(StringValue node) {
        return "\"" + node.getValue() + "\"";
    }

    @Override
    public String visitNumberValue(NumberValue node) {
        return node.getValue();
    }

    @Override
    public String visitBooleanValue(BooleanValue node) {
        return String.valueOf(node.getValue());
    }

    @Override
    public String visitNoneValue(NoneValue node) {
        return "none";
    }

    @Override
    public String visitIdentifierValue(IdentifierValue node) {
        return node.getValue();
    }

    @Override
    public String visitVarAssignmentValue(VarAssignmentValue node) {
        return String.format("var %s%s = %s",
                node.getIdentifier(),
                type(node.getType()),
                node.getValue().accept(this)
        );
    }

    private String type(TypeSpec typeSpec) {
        String type = typeSpec.accept(typeSpecVisitor);
        return type.isEmpty() ?
                "" :
                String.format(": %s", type);
    }

    @Override
    public String visitValAssignmentValue(ValAssignmentValue node) {
        return String.format("val %s%s = %s",
                node.getIdentifier(),
                type(node.getType()),
                node.getValue().accept(this)
        );
    }

    @Override
    public String visitIndirectAssignmentValue(IndirectAssignmentValue node) {
        return String.format("%s.%s = %s",
                node.getIndirectValue().accept(this),
                node.getIdentifier(),
                node.getValue().accept(this)
        );
    }

    @Override
    public String visitMethodCallValue(MethodCallValue node) {
        return String.format("%s%s(%s)",
                node.getMethodName(),
                node.getTypeArguments().isEmpty() ?
                        "" :
                        node.getTypeArguments().stream()
                                .map(a -> a.accept(typeSpecVisitor))
                                .collect(Collectors.joining(", ", "[", "]")),
                node.getMethodArguments().isEmpty() ?
                        "" :
                        node.getMethodArguments().stream()
                                .map(a -> a.accept(this))
                                .collect(Collectors.joining(", "))
        );
    }

    @Override
    public String visitNestedValue(NestedValue node) {
        return node.getOuter().accept(this) + "." + node.getInner().accept(this);
    }

    @Override
    public String visitFunctionValue(FunctionValue node) {
        return String.format("%s(%s)%s => {%n%s%n}",
                node.getTypeParameters().isEmpty() ?
                        "" :
                        node.getTypeParameters().stream()
                                .map(t -> t.accept(typeParameterVisitor))
                                .collect(Collectors.joining(", ", "[", "]")),
                node.getParameters().stream()
                        .map(p -> p.accept(parameterVisitor))
                        .collect(Collectors.joining(", ")),
                type(node.getReturnType()),
                node.getStatements().stream()
                        .map(s -> s.accept(this))
                        .map(Indent.INSTANCE)
                        .map(s -> s + ";")
                        .collect(Collectors.joining("\n"))
        );
    }
}
