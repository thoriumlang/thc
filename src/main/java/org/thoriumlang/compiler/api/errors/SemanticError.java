/*
 * Copyright 2020 Christophe Pollet
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
package org.thoriumlang.compiler.api.errors;

import org.thoriumlang.compiler.ast.context.SourcePosition;
import org.thoriumlang.compiler.ast.nodes.Node;

public class SemanticError implements CompilationError {
    private static final SemanticErrorFormatter DEFAULT_FORMATTER = new DefaultErrorFormatter();

    private final Node node;
    private final String message;

    SemanticError(String message, Node node) {
        this.node = node;
        this.message = message;
    }

    public Node getNode() {
        return node;
    }

    public String format(SemanticErrorFormatter formatter) {
        return formatter.format(node.getContext().require(SourcePosition.class), message);
    }

    @Override
    public String toString() {
        return format(DEFAULT_FORMATTER);
    }
}
