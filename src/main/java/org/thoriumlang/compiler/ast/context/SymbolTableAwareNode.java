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
package org.thoriumlang.compiler.ast.context;

import org.thoriumlang.compiler.ast.nodes.Node;
import org.thoriumlang.compiler.ast.visitor.Visitor;
import org.thoriumlang.compiler.symbols.SymbolTable;

public class SymbolTableAwareNode extends Node {
    private final Node node;

    private SymbolTableAwareNode(Node node) {
        super(node.getNodeId());
        this.node = node;
    }

    public static SymbolTableAwareNode wrap(Node node) {
        if (node instanceof SymbolTableAwareNode) {
            return (SymbolTableAwareNode) node;
        }
        return new SymbolTableAwareNode(node);
    }

    @Override
    public <T> T accept(Visitor<? extends T> visitor) {
        return node.accept(visitor);
    }

    @Override
    public Context getContext() {
        return node.getContext();
    }

    public SymbolTable setSymbolTable(SymbolTable symbolTable) {
        node.getContext().put(SymbolTable.class, symbolTable);
        return symbolTable;
    }

    public SymbolTable getSymbolTable() {
        return node.getContext()
                .get(SymbolTable.class)
                .orElseGet(() ->
                        wrap(
                                node.getContext()
                                        .get(Relatives.class)
                                        .orElseThrow(() -> new IllegalStateException("No relatives found for node"))
                                        .parent()
                                        .orElseThrow(() -> new IllegalStateException("No parent found in family"))
                                        .node()
                        ).getSymbolTable()
                );
    }
}
