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
package org.thoriumlang.compiler.ast.algorithms.typechecking;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.ast.nodes.BooleanValue;
import org.thoriumlang.compiler.ast.nodes.Class;
import org.thoriumlang.compiler.ast.nodes.FunctionValue;
import org.thoriumlang.compiler.ast.nodes.IdentifierValue;
import org.thoriumlang.compiler.ast.nodes.IndirectAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.Method;
import org.thoriumlang.compiler.ast.nodes.MethodCallValue;
import org.thoriumlang.compiler.ast.nodes.MethodSignature;
import org.thoriumlang.compiler.ast.nodes.NestedValue;
import org.thoriumlang.compiler.ast.nodes.Node;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.nodes.NoneValue;
import org.thoriumlang.compiler.ast.nodes.NumberValue;
import org.thoriumlang.compiler.ast.nodes.Parameter;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.ast.nodes.Statement;
import org.thoriumlang.compiler.ast.nodes.StringValue;
import org.thoriumlang.compiler.ast.nodes.Type;
import org.thoriumlang.compiler.ast.nodes.TypeParameter;
import org.thoriumlang.compiler.ast.nodes.TypeSpecFunction;
import org.thoriumlang.compiler.ast.nodes.TypeSpecInferred;
import org.thoriumlang.compiler.ast.nodes.TypeSpecIntersection;
import org.thoriumlang.compiler.ast.nodes.TypeSpecSimple;
import org.thoriumlang.compiler.ast.nodes.TypeSpecUnion;
import org.thoriumlang.compiler.ast.nodes.ValAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.ValAttribute;
import org.thoriumlang.compiler.ast.nodes.VarAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.VarAttribute;
import org.thoriumlang.compiler.ast.nodes.Visibility;
import org.thoriumlang.compiler.symbols.DefaultSymbolTable;
import org.thoriumlang.compiler.symbols.SymbolTable;

import java.util.Collections;

class BaseTypeCheckingVisitorTest {
    private NodeIdGenerator nodeIdGenerator;
    private BaseTypeCheckingVisitor visitor;

    @BeforeEach
    void setup() {
        nodeIdGenerator = new NodeIdGenerator();
        visitor = new BaseTypeCheckingVisitor() {
        };
    }

    @Test
    void setSymbolTable_table() {
        Node node = new NoneValue(nodeIdGenerator.next());
        SymbolTable symbolTable = new DefaultSymbolTable();
        visitor.setSymbolTable(node, symbolTable);
        Assertions.assertThat(node.getContext().get(SymbolTable.class))
                .get()
                .isEqualTo(symbolTable);
    }

    @Test
    void setSymbolTable_node() {
        Node nodeSrc = new NoneValue(nodeIdGenerator.next());
        Node nodeDst = new NoneValue(nodeIdGenerator.next());
        SymbolTable symbolTable = new DefaultSymbolTable();

        nodeSrc.getContext().put(SymbolTable.class, symbolTable);

        visitor.setSymbolTable(nodeDst, nodeSrc);
        Assertions.assertThat(nodeDst.getContext().get(SymbolTable.class))
                .get()
                .isEqualTo(symbolTable);
    }

    @Test
    void getSymbolTable() {
        Node node = new NoneValue(nodeIdGenerator.next());
        DefaultSymbolTable symbolTable = new DefaultSymbolTable();
        node.getContext().put(SymbolTable.class, symbolTable);
        Assertions.assertThat(visitor.getSymbolTable(node))
                .isEqualTo(symbolTable);
    }

    @Test
    void visitRoot() {
        Assertions.assertThat(visitor.visit(new Root(
                nodeIdGenerator.next(),
                "namespace",
                Collections.emptyList(),
                new Type(
                        nodeIdGenerator.next(),
                        Visibility.NAMESPACE,
                        "Type",
                        Collections.emptyList(),
                        new TypeSpecSimple(
                                nodeIdGenerator.next(),
                                "Type",
                                Collections.emptyList()
                        ),
                        Collections.emptyList()
                )
        )))
                .isEqualTo(Collections.emptyList());
    }

    @Test
    void visitType() {
        Assertions.assertThat(visitor.visit((Type) null))
                .isEqualTo(Collections.emptyList());
    }

    @Test
    void visitClass() {
        Assertions.assertThat(visitor.visit((Class) null))
                .isEqualTo(Collections.emptyList());
    }

    @Test
    void visitTypeSpecIntersection() {
        Assertions.assertThat(visitor.visit((TypeSpecIntersection) null))
                .isEqualTo(Collections.emptyList());
    }

    @Test
    void visitTypeSpecUnion() {
        Assertions.assertThat(visitor.visit((TypeSpecUnion) null))
                .isEqualTo(Collections.emptyList());
    }

    @Test
    void visitTypeSpecSimple() {
        Assertions.assertThat(visitor.visit((TypeSpecSimple) null))
                .isEqualTo(Collections.emptyList());
    }

    @Test
    void visitTypeSpecFunction() {
        Assertions.assertThat(visitor.visit((TypeSpecFunction) null))
                .isEqualTo(Collections.emptyList());
    }

    @Test
    void visitTypeSpecInferred() {
        Assertions.assertThat(visitor.visit((TypeSpecInferred) null))
                .isEqualTo(Collections.emptyList());
    }

    @Test
    void visitMethodSignature() {
        Assertions.assertThat(visitor.visit((MethodSignature) null))
                .isEqualTo(Collections.emptyList());
    }

    @Test
    void visitParameter() {
        Assertions.assertThat(visitor.visit((Parameter) null))
                .isEqualTo(Collections.emptyList());
    }

    @Test
    void visitTypeParameter() {
        Assertions.assertThat(visitor.visit((TypeParameter) null))
                .isEqualTo(Collections.emptyList());
    }

    @Test
    void visitStringValue() {
        Assertions.assertThat(visitor.visit((StringValue) null))
                .isEqualTo(Collections.emptyList());
    }

    @Test
    void visitNumberValue() {
        Assertions.assertThat(visitor.visit((NumberValue) null))
                .isEqualTo(Collections.emptyList());
    }

    @Test
    void visitBooleanValue() {
        Assertions.assertThat(visitor.visit((BooleanValue) null))
                .isEqualTo(Collections.emptyList());
    }

    @Test
    void visitNoneValue() {
        Assertions.assertThat(visitor.visit((NoneValue) null))
                .isEqualTo(Collections.emptyList());
    }

    @Test
    void visitIdentifierValue() {
        Assertions.assertThat(visitor.visit((IdentifierValue) null))
                .isEqualTo(Collections.emptyList());
    }

    @Test
    void visitVarAssignmentValue() {
        Assertions.assertThat(visitor.visit((VarAssignmentValue) null))
                .isEqualTo(Collections.emptyList());
    }

    @Test
    void visitValAssignmentValue() {
        Assertions.assertThat(visitor.visit((ValAssignmentValue) null))
                .isEqualTo(Collections.emptyList());
    }

    @Test
    void visitIndirectAssignmentValue() {
        Assertions.assertThat(visitor.visit((IndirectAssignmentValue) null))
                .isEqualTo(Collections.emptyList());
    }

    @Test
    void visitMethodCallValue() {
        Assertions.assertThat(visitor.visit((MethodCallValue) null))
                .isEqualTo(Collections.emptyList());
    }

    @Test
    void visitNestedValue() {
        Assertions.assertThat(visitor.visit((NestedValue) null))
                .isEqualTo(Collections.emptyList());
    }

    @Test
    void visitFunctionValue() {
        Assertions.assertThat(visitor.visit((FunctionValue) null))
                .isEqualTo(Collections.emptyList());
    }

    @Test
    void visitStatement() {
        Assertions.assertThat(visitor.visit((Statement) null))
                .isEqualTo(Collections.emptyList());
    }

    @Test
    void visitMethod() {
        Assertions.assertThat(visitor.visit(new Method(
                nodeIdGenerator.next(),
                new MethodSignature(
                        nodeIdGenerator.next(),
                        Visibility.NAMESPACE,
                        "method",
                        Collections.emptyList(),
                        Collections.emptyList(),
                        new TypeSpecSimple(nodeIdGenerator.next(), "Type", Collections.emptyList())
                ),
                Collections.emptyList()
        )))
                .isEqualTo(Collections.emptyList());
    }

    @Test
    void visitVarAttribute() {
        Assertions.assertThat(visitor.visit((VarAttribute) null))
                .isEqualTo(Collections.emptyList());
    }

    @Test
    void visitValAttribute() {
        Assertions.assertThat(visitor.visit((ValAttribute) null))
                .isEqualTo(Collections.emptyList());
    }
}
