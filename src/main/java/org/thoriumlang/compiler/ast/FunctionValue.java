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
package org.thoriumlang.compiler.ast;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FunctionValue implements Value {
    private final List<TypeParameter> typeParameters;
    private final List<Parameter> parameters;
    private final TypeSpec returnType;
    private final List<Statement> statements;

    public FunctionValue(List<TypeParameter> typeParameters, List<Parameter> parameters, TypeSpec returnType,
            List<Statement> statements) {
        if (typeParameters == null) {
            throw new NullPointerException("typeParameters cannot be null");
        }
        if (parameters == null) {
            throw new NullPointerException("parameters cannot be null");
        }
        if (returnType == null) {
            throw new NullPointerException("returnType cannot be null");
        }
        if (statements == null) {
            throw new NullPointerException("statements cannot be null");
        }
        this.typeParameters = typeParameters;
        this.parameters = parameters;
        this.returnType = returnType;
        this.statements = statements;
    }

    @Override
    public <T> T accept(Visitor<? extends T> visitor) {
        return visitor.visitFunctionValue(
                typeParameters,
                parameters,
                returnType,
                statements
        );
    }

    @Override
    public String toString() {
        return String.format(
                "[%s](%s):%s { %s }",
                typeParameters.stream()
                        .map(TypeParameter::toString)
                        .collect(Collectors.joining(";")),
                parameters.stream()
                        .map(Parameter::toString)
                        .collect(Collectors.joining(";")),
                returnType.toString(),
                statements.stream()
                        .map(Statement::toString)
                        .collect(Collectors.joining(";"))
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FunctionValue that = (FunctionValue) o;
        return typeParameters.equals(that.typeParameters) &&
                parameters.equals(that.parameters) &&
                returnType.equals(that.returnType) &&
                statements.equals(that.statements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeParameters, parameters, returnType, statements);
    }
}
