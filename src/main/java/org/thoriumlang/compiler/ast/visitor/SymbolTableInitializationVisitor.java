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
package org.thoriumlang.compiler.ast.visitor;

import org.thoriumlang.compiler.ast.context.Relatives;
import org.thoriumlang.compiler.ast.context.SourcePosition;
import org.thoriumlang.compiler.ast.nodes.Attribute;
import org.thoriumlang.compiler.ast.nodes.BooleanValue;
import org.thoriumlang.compiler.ast.nodes.Class;
import org.thoriumlang.compiler.ast.nodes.DirectAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.FunctionValue;
import org.thoriumlang.compiler.ast.nodes.IdentifierValue;
import org.thoriumlang.compiler.ast.nodes.IndirectAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.Method;
import org.thoriumlang.compiler.ast.nodes.MethodCallValue;
import org.thoriumlang.compiler.ast.nodes.MethodSignature;
import org.thoriumlang.compiler.ast.nodes.NestedValue;
import org.thoriumlang.compiler.ast.nodes.NewAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.Node;
import org.thoriumlang.compiler.ast.nodes.NoneValue;
import org.thoriumlang.compiler.ast.nodes.NumberValue;
import org.thoriumlang.compiler.ast.nodes.Parameter;
import org.thoriumlang.compiler.ast.nodes.Reference;
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
import org.thoriumlang.compiler.ast.nodes.Use;
import org.thoriumlang.compiler.symbols.SymbolTable;

import java.util.stream.Collectors;

// TODO make sure what is done survive the tree rewrites... (see https://github.com/thoriumlang/thc/issues/69)
public class SymbolTableInitializationVisitor extends IdentityVisitor {
    public static final String TABLE_NAME_BODY = "[body]";
    private final SymbolTable rootSymbolTable;

    public SymbolTableInitializationVisitor(SymbolTable rootSymbolTable) {
        this.rootSymbolTable = rootSymbolTable;
    }

    @Override
    public Node visit(Root node) {
        node.getContext().put(
                SymbolTable.class,
                rootSymbolTable.createScope(node.getTopLevelNode().getName())
        );

        node.getUses().forEach(n -> n.accept(this));
        node.getTopLevelNode().accept(this);

        return node;
    }

    @Override
    public Node visit(Use node) {
        node.getContext().put(SymbolTable.class, getSymbolTable(getParent(node)));
        return node;
    }

    private SymbolTable getSymbolTable(Node node) {
        return node.getContext().require(SymbolTable.class);
    }

    private Node getParent(Node node) {
        return node.getContext()
                .require(Relatives.class)
                .parent()
                .orElseThrow(() -> new IllegalStateException("No parent found"))
                .node();
    }

    @Override
    public Node visit(Type node) {
        node.getContext().put(
                SymbolTable.class,
                getSymbolTable(getParent(node)).createScope(TABLE_NAME_BODY)
        );

        node.getTypeParameters().forEach(n -> n.accept(this));
        node.getSuperType().accept(this);
        node.getMethods().forEach(n -> n.accept(this));

        return node;
    }

    @Override
    public Node visit(Class node) {
        node.getContext().put(
                SymbolTable.class,
                getSymbolTable(getParent(node)).createScope(TABLE_NAME_BODY)
        );

        node.getTypeParameters().forEach(n -> n.accept(this));
        node.getSuperType().accept(this);
        node.getMethods().forEach(n -> n.accept(this));
        node.getAttributes().forEach(a -> a.accept(this));

        return node;
    }

    @Override
    public Node visit(TypeSpecIntersection node) {
        node.getContext().put(SymbolTable.class, getSymbolTable(getParent(node)));

        node.getTypes().forEach(n -> n.accept(this));

        return node;
    }

    @Override
    public Node visit(TypeSpecUnion node) {
        node.getContext().put(SymbolTable.class, getSymbolTable(getParent(node)));

        node.getTypes().forEach(n -> n.accept(this));

        return node;
    }

    @Override
    public Node visit(TypeSpecSimple node) {
        node.getContext().put(SymbolTable.class, getSymbolTable(getParent(node)));

        node.getArguments().forEach(n -> n.accept(this));

        return node;
    }

    @Override
    public Node visit(TypeSpecFunction node) {
        node.getContext().put(SymbolTable.class, getSymbolTable(getParent(node)));

        node.getArguments().forEach(n -> n.accept(this));
        node.getReturnType().accept(this);

        return node;
    }

    @Override
    public Node visit(TypeSpecInferred node) {
        node.getContext().put(SymbolTable.class, getSymbolTable(getParent(node)));

        return node;
    }

    @Override
    public Node visit(MethodSignature node) {
        node.getContext().put(
                SymbolTable.class,
                /*
                 * Some nasty trick here... MethodSignature can be the child of either a Method or a Type.
                 *  - If the parent is a Type, we have to define a new symbol table specific for that signature
                 *    (otherwise, we pollute the upper table)
                 *  - If the parent is a Method, we have to use the same symbol table as signature is part of the
                 *    method.
                 */
                node.getContext()
                        .require(Relatives.class)
                        .parent()
                        .orElseThrow(() -> new IllegalStateException("No parent found"))
                        .node()
                        .accept(new BaseVisitor<SymbolTable>() {
                            @Override
                            public SymbolTable visit(Method parentNode) {
                                return getSymbolTable(parentNode);
                            }

                            @Override
                            public SymbolTable visit(Type parentNode) {
                                return getSymbolTable(parentNode).createScope(scopeName(node));
                            }
                        })
        );

        node.getParameters().forEach(n -> n.accept(this));
        node.getReturnType().accept(this);
        node.getTypeParameters().forEach(n -> n.accept(this));

        return node;
    }

    private String scopeName(MethodSignature node) {
        return String.format("%s(%s)",
                node.getName(),
                node.getParameters().stream()
                        .map(Parameter::getType)
                        .map(Object::toString)
                        .collect(Collectors.joining(","))
        );
    }

    @Override
    public Node visit(Parameter node) {
        node.getContext().put(SymbolTable.class, getSymbolTable(getParent(node)));

        node.getType().accept(this);

        return node;
    }

    @Override
    public Node visit(TypeParameter node) {
        node.getContext().put(SymbolTable.class, getSymbolTable(getParent(node)));

        return node;
    }

    @Override
    public Node visit(StringValue node) {
        node.getContext().put(SymbolTable.class, getSymbolTable(getParent(node)));
        return node;
    }

    @Override
    public Node visit(NumberValue node) {
        node.getContext().put(SymbolTable.class, getSymbolTable(getParent(node)));
        return node;
    }

    @Override
    public Node visit(BooleanValue node) {
        node.getContext().put(SymbolTable.class, getSymbolTable(getParent(node)));
        return node;
    }

    @Override
    public Node visit(NoneValue node) {
        node.getContext().put(SymbolTable.class, getSymbolTable(getParent(node)));
        return node;
    }

    @Override
    public Node visit(IdentifierValue node) {
        node.getContext().put(SymbolTable.class, getSymbolTable(getParent(node)));
        node.getReference().accept(this);
        return node;
    }

    @Override
    public Node visit(Reference node) {
        node.getContext().put(SymbolTable.class, getSymbolTable(getParent(node)));
        return node;
    }

    @Override
    public Node visit(NewAssignmentValue node) {
        node.getContext().put(SymbolTable.class, getSymbolTable(getParent(node)));

        node.getType().accept(this);
        node.getValue().accept(this);

        return node;
    }

    @Override
    public Node visit(DirectAssignmentValue node) {
        node.getContext().put(SymbolTable.class, getSymbolTable(getParent(node)));

        node.getReference().accept(this);
        node.getValue().accept(this);

        return node;
    }

    @Override
    public Node visit(IndirectAssignmentValue node) {
        node.getContext().put(SymbolTable.class, getSymbolTable(getParent(node)));

        node.getReference().accept(this);
        node.getValue().accept(this);
        node.getIndirectValue().accept(this);

        return node;
    }

    @Override
    public Node visit(MethodCallValue node) {
        node.getContext().put(SymbolTable.class, getSymbolTable(getParent(node)));
        node.getMethodReference().accept(this);
        node.getTypeArguments().forEach(n -> n.accept(this));
        node.getMethodArguments().forEach(n -> n.accept(this));

        return node;
    }

    @Override
    public Node visit(NestedValue node) {
        node.getContext().put(SymbolTable.class, getSymbolTable(getParent(node)));

        node.getInner().accept(this);
        node.getOuter().accept(this);

        return node;
    }

    @Override
    public Node visit(FunctionValue node) {
        node.getContext().put(
                SymbolTable.class,
                getSymbolTable(getParent(node))
                        .createScope(
                                String.format("[anon:%s]", node.getContext().get(SourcePosition.class)
                                        .map(SourcePosition::getStartLine)
                                        .map(Object::toString)
                                        .orElse("?")
                                )
                        )
        );

        node.getReturnType().accept(this);
        node.getParameters().forEach(n -> n.accept(this));
        node.getTypeParameters().forEach(n -> n.accept(this));

        node.getContext().put(
                SymbolTable.class,
                getSymbolTable(node).createScope(TABLE_NAME_BODY)
        );

        node.getStatements().forEach(n -> n.accept(this));

        return node;
    }

    @Override
    public Node visit(Statement node) {
        node.getContext().put(SymbolTable.class, getSymbolTable(getParent(node)));

        node.getValue().accept(this);

        return node;
    }

    @Override
    public Node visit(Method node) {
        node.getContext().put(
                SymbolTable.class,
                getSymbolTable(getParent(node)).createScope(scopeName(node.getSignature()))
        );

        node.getSignature().accept(this);

        node.getContext().put(
                SymbolTable.class,
                getSymbolTable(node).createScope(TABLE_NAME_BODY)
        );

        node.getStatements().forEach(s -> s.accept(this));

        return node;
    }

    @Override
    public Node visit(Attribute node) {
        node.getContext().put(
                SymbolTable.class,
                getSymbolTable(getParent(node)).createScope(node.getName())
        );

        node.getType().accept(this);
        node.getValue().accept(this);

        return node;
    }
}
