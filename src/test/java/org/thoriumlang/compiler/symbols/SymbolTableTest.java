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
package org.thoriumlang.compiler.symbols;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.ast.nodes.Node;

class SymbolTableTest {
    @Test
    void simpleName() {
        Symbol symbol = symbol();
        SymbolTable symbolTable = new SymbolTable();

        symbolTable.put(new Name("SimpleName"), symbol);

        Assertions.assertThat(symbolTable.find(new Name("SimpleName")))
                .hasSize(1)
                .allMatch(s -> s == symbol);
    }

    @Test
    void simpleMethodName() {
        Symbol symbol = symbol();
        SymbolTable symbolTable = new SymbolTable();

        symbolTable.put(new Name("SimpleName()"), symbol);

        Assertions.assertThat(symbolTable.find(new Name("SimpleName()")))
                .hasSize(1)
                .allMatch(s -> s == symbol);
    }

    @Test
    void simpleMethodName_putDifferentParameterArity() {
        Symbol symbol1 = symbol();
        SymbolTable symbolTable = new SymbolTable();

        symbolTable.put(new Name("SimpleName()"), symbol1);

        Assertions.assertThat(symbolTable.find(new Name("SimpleName(Whatever)")))
                .isEmpty();
    }

    @Test
    void simpleMethodName_findDifferentParameterArity() {
        Symbol symbol1 = symbol();
        SymbolTable symbolTable = new SymbolTable();

        symbolTable.put(new Name("SimpleName(Object)"), symbol1);

        Assertions.assertThat(symbolTable.find(new Name("SimpleName()")))
                .isEmpty();
    }

    @Test
    void simpleMethodName_withParametersAndTypeParameters() {
        Symbol symbol = symbol();
        SymbolTable symbolTable = new SymbolTable();

        symbolTable.put(new Name("SimpleName[T[]](Object[T[])"), symbol);

        Assertions.assertThat(symbolTable.find(new Name("SimpleName(Whatever)")))
                .hasSize(1)
                .allMatch(s -> s == symbol);
    }

    @Test
    void simpleMethodNameOverloaded() {
        Symbol symbol1 = symbol();
        Symbol symbol2 = symbol();
        SymbolTable symbolTable = new SymbolTable();

        symbolTable.put(new Name("SimpleName(Object)"), symbol1);
        symbolTable.put(new Name("SimpleName(String)"), symbol2);

        Assertions.assertThat(symbolTable.find(new Name("SimpleName(Whatever)")))
                .hasSize(2)
                .containsExactly(symbol1, symbol2);
    }

    @Test
    void simpleMethodNameOverloaded_withFullParameters() {
        Symbol symbol1 = symbol();
        Symbol symbol2 = symbol();
        SymbolTable symbolTable = new SymbolTable();

        symbolTable.put(new Name("SimpleName(Object)"), symbol1);
        symbolTable.put(new Name("SimpleName(String)"), symbol2);

        Assertions.assertThat(symbolTable.find(new Name("SimpleName[](Object[])")))
                .hasSize(2)
                .containsExactly(symbol1, symbol2);
    }

    @Test
    void name() {
        Symbol symbol = symbol();
        SymbolTable symbolTable = new SymbolTable();

        symbolTable.put(new Name("namespace.SimpleName"), symbol);

        Assertions.assertThat(symbolTable.find(new Name("namespace.SimpleName")))
                .hasSize(1)
                .allMatch(s -> s == symbol);
    }

    @Test
    void methodName() {
        Symbol symbol = symbol();
        SymbolTable symbolTable = new SymbolTable();

        symbolTable.put(new Name("namespace.SimpleName()"), symbol);

        Assertions.assertThat(symbolTable.find(new Name("namespace.SimpleName()")))
                .hasSize(1)
                .allMatch(s -> s == symbol);
    }

    @Test
    void name_fromChild() {
        Symbol symbol = symbol();
        SymbolTable parentSymbolTable = new SymbolTable();
        SymbolTable childSymbolTable = parentSymbolTable.createScope("namespace");

        parentSymbolTable.put(new Name("namespace.SimpleName"), symbol);

        Assertions.assertThat(childSymbolTable.find(new Name("SimpleName")))
                .hasSize(1)
                .allMatch(s -> s == symbol);
    }

    @Test
    void methodName_fromChild() {
        Symbol symbol = symbol();
        SymbolTable parentSymbolTable = new SymbolTable();
        SymbolTable childSymbolTable = parentSymbolTable.createScope("namespace");

        parentSymbolTable.put(new Name("namespace.SimpleName()"), symbol);

        Assertions.assertThat(childSymbolTable.find(new Name("SimpleName()")))
                .hasSize(1)
                .allMatch(s -> s == symbol);
    }

    @Test
    void name_fromParent() {
        Symbol symbol = symbol();
        SymbolTable parentSymbolTable = new SymbolTable();
        SymbolTable childSymbolTable = parentSymbolTable.createScope("namespace");

        childSymbolTable.put(new Name("SimpleName"), symbol);

        Assertions.assertThat(parentSymbolTable.find(new Name("namespace.SimpleName")))
                .hasSize(1)
                .allMatch(s -> s == symbol);
    }

    @Test
    void methodName_fromParent() {
        Symbol symbol = symbol();
        SymbolTable parentSymbolTable = new SymbolTable();
        SymbolTable childSymbolTable = parentSymbolTable.createScope("namespace");

        childSymbolTable.put(new Name("SimpleName()"), symbol);

        Assertions.assertThat(parentSymbolTable.find(new Name("namespace.SimpleName()")))
                .hasSize(1)
                .allMatch(s -> s == symbol);
    }

    @Test
    void name_fromMiddle() {
        Symbol symbol = symbol();
        SymbolTable parentSymbolTable = new SymbolTable();
        SymbolTable middleSymbolTable = parentSymbolTable.createScope("a");
        SymbolTable childSymbolTable = middleSymbolTable.createScope("b");

        childSymbolTable.put(new Name("SimpleName"), symbol);

        Assertions.assertThat(middleSymbolTable.find(new Name("a.b.SimpleName")))
                .hasSize(1)
                .allMatch(s -> s == symbol);
    }

    @Test
    void methodName_fromMiddle() {
        Symbol symbol = symbol();
        SymbolTable parentSymbolTable = new SymbolTable();
        SymbolTable middleSymbolTable = parentSymbolTable.createScope("a");
        SymbolTable childSymbolTable = middleSymbolTable.createScope("b");

        childSymbolTable.put(new Name("SimpleName()"), symbol);

        Assertions.assertThat(middleSymbolTable.find(new Name("a.b.SimpleName()")))
                .hasSize(1)
                .allMatch(s -> s == symbol);
    }

    @Test
    void createScope_new() {
        SymbolTable parentSymbolTable = new SymbolTable();
        SymbolTable childSymbolTable = parentSymbolTable.createScope("namespace");

        Assertions.assertThat(parentSymbolTable)
                .isNotSameAs(childSymbolTable);
    }

    @Test
    void createScope_exist() {
        SymbolTable parentSymbolTable = new SymbolTable();
        SymbolTable childSymbolTable1 = parentSymbolTable.createScope("namespace");
        SymbolTable childSymbolTable2 = parentSymbolTable.createScope("namespace");

        Assertions.assertThat(childSymbolTable2)
                .isSameAs(childSymbolTable1);
    }

    @Test
    void inScope_name() {
        SymbolTable symbolTable = new SymbolTable();
        Assertions.assertThat(symbolTable.inScope(new Name("a.Name")))
                .isFalse();
    }

    @Test
    void inScope_simpleName_doestExist() {
        SymbolTable symbolTable = new SymbolTable();
        Assertions.assertThat(symbolTable.inScope(new Name("Name")))
                .isFalse();
    }

    @Test
    void inScope_simpleName_exists() {
        SymbolTable symbolTable = new SymbolTable();
        symbolTable.put(new Name("Name"), symbol());
        Assertions.assertThat(symbolTable.inScope(new Name("Name")))
                .isTrue();
    }

    @Test
    void enclosingScope_root() {
        Assertions.assertThatIllegalStateException()
                .isThrownBy(() -> new SymbolTable().enclosingScope())
                .withMessage("root has no enclosing scope");
    }

    @Test
    void enclosingScope_nonRoot() {
        SymbolTable root = new SymbolTable();
        SymbolTable child = root.createScope("child");
        Assertions.assertThat(child.enclosingScope())
                .isSameAs(root);
    }

    @Test
    void root_onRoot() {
        SymbolTable root = new SymbolTable();
        Assertions.assertThat(root.root())
                .isSameAs(root);
    }

    @Test
    void root_onNonRoot() {
        SymbolTable root = new SymbolTable();
        SymbolTable child = root.createScope("child");
        Assertions.assertThat(child.root())
                .isSameAs(root);
    }

    @Test
    void accept() {
        SymbolTable root = new SymbolTable();
        SymbolTable child = root.createScope("child");
        child.put(new Name("name"), null);

        SymbolTableVisitor<String> visitor = (name, symbolTable, symbols, scopes) ->
                String.format("%s:%s:%s:%s", name, symbolTable.hashCode(), symbols.keySet(), scopes.keySet());

        Assertions.assertThat(root.accept(visitor))
                .isEqualTo(String.format(":%s:[]:[child]", root.hashCode()));
        Assertions.assertThat(child.accept(visitor))
                .isEqualTo(String.format("child:%s:[name]:[]", child.hashCode()));
    }

    @Test
    void toString_root() {
        Assertions.assertThat(new SymbolTable().toString())
                .isEqualTo("");
    }

    @Test
    void toString_rootChild() {
        Assertions.assertThat(new SymbolTable().createScope("child").toString())
                .isEqualTo("child");
    }

    @Test
    void toString_general() {
        Assertions.assertThat(
                new SymbolTable()
                        .createScope("parent")
                        .createScope("child")
                        .toString()
        )
                .isEqualTo("parent.child");
    }

    private Symbol symbol() {
        return new Symbol() {
            @Override
            public Node getDefiningNode() {
                return null;
            }
        };
    }
}
