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

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.ast.context.Relatives;
import org.thoriumlang.compiler.ast.context.SymbolTableAwareNode;
import org.thoriumlang.compiler.ast.nodes.Mode;
import org.thoriumlang.compiler.ast.nodes.NewAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.Node;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.nodes.NoneValue;
import org.thoriumlang.compiler.ast.nodes.TypeSpecSimple;
import org.thoriumlang.compiler.ast.visitor.BaseVisitor;
import org.thoriumlang.compiler.symbols.DefaultSymbolTable;
import org.thoriumlang.compiler.symbols.SymbolTable;

import java.util.Collections;

class SymbolTableAwareNodeTest {
    private final static NodeIdGenerator nodeIdGenerator = new NodeIdGenerator();

    @Test
    void wrap_nopIfAlreadyWrapped() {
        Node node = SymbolTableAwareNode.wrap(new NoneValue(nodeIdGenerator.next()));
        Node wrapped = SymbolTableAwareNode.wrap(node);

        Assertions.assertThat(node).isSameAs(wrapped);
    }

    @Test
    void wrap() {
        Node node = new NoneValue(nodeIdGenerator.next());

        Assertions.assertThat(node).isNotSameAs(SymbolTableAwareNode.wrap(node));
    }

    @Test
    void accept() {
        Node node = SymbolTableAwareNode.wrap(new NoneValue(nodeIdGenerator.next()));

        Boolean visited = node.accept(new BaseVisitor<Boolean>() {
            @Override
            public Boolean visit(NoneValue node) {
                return true;
            }
        });

        Assertions.assertThat(visited).isTrue();
    }

    @Test
    void getContext() {
        NoneValue noneValue = new NoneValue(nodeIdGenerator.next());

        Assertions.assertThat(SymbolTableAwareNode.wrap(noneValue).getContext()).isSameAs(noneValue.getContext());
    }

    @Test
    void setSymbolTable() {
        NoneValue noneValue = new NoneValue(nodeIdGenerator.next());
        SymbolTable symbolTable = new DefaultSymbolTable();
        SymbolTableAwareNode.wrap(noneValue).setSymbolTable(symbolTable);

        Assertions.assertThat(noneValue.getContext().get(SymbolTable.class))
                .get()
                .isSameAs(symbolTable);
    }

    @Test
    void getSymbolTable_direct() {
        SymbolTableAwareNode node = SymbolTableAwareNode.wrap(new NoneValue(nodeIdGenerator.next()));
        SymbolTable symbolTable = new DefaultSymbolTable();
        node.setSymbolTable(symbolTable);

        Assertions.assertThat(node.getSymbolTable()).isSameAs(symbolTable);
    }

    @Test
    void getSymbolTable_parent() {
        NoneValue value = new NoneValue(nodeIdGenerator.next());

        SymbolTableAwareNode childNode = SymbolTableAwareNode.wrap(value);
        SymbolTableAwareNode parentNode = SymbolTableAwareNode.wrap(new NewAssignmentValue(
                nodeIdGenerator.next(),
                "id",
                new TypeSpecSimple(
                        nodeIdGenerator.next(),
                        "T",
                        Collections.emptyList()
                ),
                value,
                Mode.VAL
        ));

        value.getContext().put(Relatives.class, new Relatives(value, new Relatives(parentNode)));

        SymbolTable symbolTable = new DefaultSymbolTable();
        parentNode.setSymbolTable(symbolTable);

        Assertions.assertThat(childNode.getSymbolTable()).isSameAs(symbolTable);
    }

    @Test
    void getSymbolTable_notFound() {
        SymbolTableAwareNode node = SymbolTableAwareNode.wrap(new NoneValue(nodeIdGenerator.next()));


        try {
            node.getSymbolTable();
        }
        catch (IllegalStateException e) {
            Assertions.assertThat(e).hasMessage("No relatives found for node");
            return;
        }
        Assertions.fail("Exception expected but not thrown");
    }
}
