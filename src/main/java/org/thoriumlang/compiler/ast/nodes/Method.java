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
package org.thoriumlang.compiler.ast.nodes;

import org.thoriumlang.compiler.ast.visitor.Visitor;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Method extends Node {
    private final MethodSignature signature;
    private final List<Statement> statements;

    public Method(NodeId nodeId, MethodSignature signature, List<Statement> statements) {
        super(nodeId);
        this.signature = Objects.requireNonNull(signature, "signature cannot be null");
        this.statements = Objects.requireNonNull(statements, "statements cannot be null");
    }

    @Override
    public <T> T accept(Visitor<? extends T> visitor) {
        return visitor.visit(this);
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

    public MethodSignature getSignature() {
        return signature;
    }

    public List<Statement> getStatements() {
        return statements;
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
        return getNodeId().equals(method.getNodeId()) &&
                signature.equals(method.signature) &&
                statements.equals(method.statements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNodeId(), signature, statements);
    }
}
