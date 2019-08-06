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

class SymbolTableTest {
    @Test
    void find_unknown() {
        SymbolTable symbolTable = new SymbolTable();
        Assertions.assertThat(symbolTable.find("unknown")).isEmpty();
    }

    @Test
    void find_localSymbol() {
        SymbolTable symbolTable = new SymbolTable();
        symbolTable.put("someVar", new Symbol());
        Assertions.assertThat(symbolTable.find("someVar")).isNotEmpty();
    }

    @Test
    void find_parentSymbol() {
        SymbolTable symbolTable = new SymbolTable();
        symbolTable.put("someVar", new Symbol());

        SymbolTable nestedSymbolTable = new SymbolTable(symbolTable);

        Assertions.assertThat(nestedSymbolTable.find("someVar")).isNotEmpty();
    }

    @Test
    void find_precedence() {
        Symbol parentSymbol = new Symbol();
        SymbolTable symbolTable = new SymbolTable();
        symbolTable.put("someVar", parentSymbol);

        Symbol symbol = new Symbol();
        SymbolTable nestedSymbolTable = new SymbolTable(symbolTable);
        symbolTable.put("someVar", symbol);

        Assertions.assertThat(nestedSymbolTable.find("someVar"))
                .isNotEmpty()
                .get()
                .isSameAs(symbol);
    }

    @Test
    void child() {
        Symbol symbol = new Symbol();
        SymbolTable symbolTable = new SymbolTable();
        symbolTable.put("someVar", symbol);

        SymbolTable nestedTable = symbolTable.createNestedTable();

        Assertions.assertThat(nestedTable.find("someVar"))
                .isNotEmpty()
                .get()
                .isSameAs(symbol);
    }
}
