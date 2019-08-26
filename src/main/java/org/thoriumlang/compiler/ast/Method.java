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

public class Method implements Visitable {
    private final MethodSignature signature;
    private final List<Statement> statements;

    public Method(MethodSignature signature, List<Statement> statements) {
        if (signature == null) {
            throw new NullPointerException("signature cannot be null");
        }
        if (statements == null) {
            throw new NullPointerException("statements cannot be null");
        }
        this.signature = signature;
        this.statements = statements;
    }

    @Override
    public <T> T accept(Visitor<? extends T> visitor) {
        return visitor.visitMethod(signature, statements);
    }

    @Override
    public String toString() {
        return String.format(
                "%s { %s }",
                signature.toString(),
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
        Method method = (Method) o;
        return signature.equals(method.signature) &&
                statements.equals(method.statements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(signature, statements);
    }
}