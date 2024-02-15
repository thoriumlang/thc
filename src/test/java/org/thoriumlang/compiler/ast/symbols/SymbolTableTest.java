package org.thoriumlang.compiler.ast.symbols;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.ast.nodes.NodeId;
import org.thoriumlang.compiler.ast.nodes.StringValue;

class SymbolTableTest {
    @Test
    void inCurrentScope() {
        SymbolTable symbolTable = new SymbolTable();
        Assertions.assertFalse(symbolTable.inCurrentScope("someSymbol"));

        symbolTable.put("someSymbol", new NodeRef(new StringValue(new NodeId(1L), "val")));
        Assertions.assertTrue(symbolTable.inCurrentScope("someSymbol"));
    }
}