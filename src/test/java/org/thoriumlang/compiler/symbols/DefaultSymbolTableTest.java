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
package org.thoriumlang.compiler.symbols;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.ast.nodes.Node;

class DefaultSymbolTableTest {
    @Test
    void fqName_parentRoot() {
        Assertions.assertThat(new DefaultSymbolTable().fqName())
                .isEqualTo("root");
    }

    @Test
    void fqName() {
        Assertions.assertThat(new DefaultSymbolTable().createNestedTable("child").fqName())
                .isEqualTo("root.child");
    }

    @Test
    void find_unknown() {
        DefaultSymbolTable symbolTable = new DefaultSymbolTable();
        Assertions.assertThat(symbolTable.find("unknown")).isEmpty();
    }

    @Test
    void find_localSymbol() {
        DefaultSymbolTable symbolTable = new DefaultSymbolTable();
        symbolTable.put(new SymbolStub("someVar"));
        Assertions.assertThat(symbolTable.find("someVar")).isNotEmpty();
    }

    @Test
    void find_parentSymbol() {
        DefaultSymbolTable symbolTable = new DefaultSymbolTable();
        symbolTable.put(new SymbolStub("someVar"));

        DefaultSymbolTable nestedSymbolTable = new DefaultSymbolTable("", symbolTable);

        Assertions.assertThat(nestedSymbolTable.find("someVar")).isNotEmpty();
    }

    @Test
    void find_precedence() {
        Symbol parentSymbol = new SymbolStub("someVar");
        DefaultSymbolTable symbolTable = new DefaultSymbolTable();
        symbolTable.put(parentSymbol);

        Symbol symbol = new SymbolStub("someVar");
        DefaultSymbolTable nestedSymbolTable = new DefaultSymbolTable("", symbolTable);
        symbolTable.put(symbol);

        Assertions.assertThat(nestedSymbolTable.find("someVar"))
                .isNotEmpty()
                .get()
                .isSameAs(symbol);
    }

    @Test
    void symbolStream() {
        Symbol parentSymbolA = new SymbolStub("A");
        Symbol parentSymbolB = new SymbolStub("B");
        DefaultSymbolTable symbolTable = new DefaultSymbolTable();
        symbolTable.put(parentSymbolA);
        symbolTable.put(parentSymbolB);

        Symbol symbolA = new SymbolStub("A");
        Symbol symbolC = new SymbolStub("C");
        DefaultSymbolTable nestedSymbolTable = new DefaultSymbolTable("", symbolTable);
        symbolTable.put(symbolA);
        symbolTable.put(symbolC);

        Assertions.assertThat(nestedSymbolTable.symbolsStream())
                .containsExactlyInAnyOrder(
                        symbolA,
                        symbolC,
                        parentSymbolB
                );
    }

    @Test
    void child() {
        Symbol symbol = new SymbolStub("someVar");
        DefaultSymbolTable symbolTable = new DefaultSymbolTable();
        symbolTable.put(symbol);

        DefaultSymbolTable nestedTable = symbolTable.createNestedTable("");

        Assertions.assertThat(nestedTable.find("someVar"))
                .isNotEmpty()
                .get()
                .isSameAs(symbol);
    }

    private static class SymbolStub implements Symbol {
        private final String name;

        private SymbolStub(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Node getNode() {
            return null;
        }
    }
}
