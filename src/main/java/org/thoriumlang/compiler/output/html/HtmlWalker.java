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
package org.thoriumlang.compiler.output.html;

import com.google.common.collect.ImmutableMap;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import org.thoriumlang.compiler.ast.algorithms.typechecking.TypeCheckingError;
import org.thoriumlang.compiler.ast.nodes.Assignment;
import org.thoriumlang.compiler.ast.nodes.Attribute;
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
import org.thoriumlang.compiler.ast.nodes.NoneValue;
import org.thoriumlang.compiler.ast.nodes.NumberValue;
import org.thoriumlang.compiler.ast.nodes.Parameter;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.ast.nodes.Statement;
import org.thoriumlang.compiler.ast.nodes.StringValue;
import org.thoriumlang.compiler.ast.nodes.SymbolTableAwareNode;
import org.thoriumlang.compiler.ast.nodes.Type;
import org.thoriumlang.compiler.ast.nodes.TypeParameter;
import org.thoriumlang.compiler.ast.nodes.TypeSpec;
import org.thoriumlang.compiler.ast.nodes.TypeSpecFunction;
import org.thoriumlang.compiler.ast.nodes.TypeSpecInferred;
import org.thoriumlang.compiler.ast.nodes.TypeSpecIntersection;
import org.thoriumlang.compiler.ast.nodes.TypeSpecSimple;
import org.thoriumlang.compiler.ast.nodes.TypeSpecUnion;
import org.thoriumlang.compiler.ast.nodes.Use;
import org.thoriumlang.compiler.ast.nodes.ValAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.ValAttribute;
import org.thoriumlang.compiler.ast.nodes.VarAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.VarAttribute;
import org.thoriumlang.compiler.ast.visitor.BaseVisitor;
import org.thoriumlang.compiler.output.Walker;
import org.thoriumlang.compiler.symbols.SymbolTable;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.jtwig.JtwigTemplate.classpathTemplate;

@SuppressWarnings("squid:S1192")
public class HtmlWalker extends BaseVisitor<String> implements Walker<String> {
    private static final String TEMPLATE_PATH = HtmlWalker.class.getPackage().getName()
            .replace(".", File.separator) + File.separator;

    private static final Map<java.lang.Class, JtwigTemplate> templates = ImmutableMap.<java.lang.Class, JtwigTemplate>builder()
            .put(Root.class, classpathTemplate(TEMPLATE_PATH + "root.twig"))
            .put(Use.class, classpathTemplate(TEMPLATE_PATH + "use.twig"))
            .put(Class.class, classpathTemplate(TEMPLATE_PATH + "class.twig"))
            .put(TypeParameter.class, classpathTemplate(TEMPLATE_PATH + "typeParameter.twig"))
            .put(TypeSpecSimple.class, classpathTemplate(TEMPLATE_PATH + "typeSpecSimple.twig"))
            .put(TypeSpecIntersection.class, classpathTemplate(TEMPLATE_PATH + "typeSpecComposition.twig"))
            .put(TypeSpecUnion.class, classpathTemplate(TEMPLATE_PATH + "typeSpecComposition.twig"))
            .put(TypeSpecFunction.class, classpathTemplate(TEMPLATE_PATH + "typeSpecFunction.twig"))
            .put(TypeSpecInferred.class, classpathTemplate(TEMPLATE_PATH + "typeSpecInferred.twig"))
            .put(Attribute.class, classpathTemplate(TEMPLATE_PATH + "attribute.twig"))
            .put(NoneValue.class, classpathTemplate(TEMPLATE_PATH + "noneValue.twig"))
            .put(StringValue.class, classpathTemplate(TEMPLATE_PATH + "stringValue.twig"))
            .put(NumberValue.class, classpathTemplate(TEMPLATE_PATH + "numberValue.twig"))
            .put(BooleanValue.class, classpathTemplate(TEMPLATE_PATH + "booleanValue.twig"))
            .put(MethodCallValue.class, classpathTemplate(TEMPLATE_PATH + "methodCallValue.twig"))
            .put(FunctionValue.class, classpathTemplate(TEMPLATE_PATH + "functionValue.twig"))
            .put(IdentifierValue.class, classpathTemplate(TEMPLATE_PATH + "identifierValue.twig"))
            .put(NestedValue.class, classpathTemplate(TEMPLATE_PATH + "nestedValue.twig"))
            .put(IndirectAssignmentValue.class, classpathTemplate(TEMPLATE_PATH + "indirectAssignmentValue.twig"))
            .put(Method.class, classpathTemplate(TEMPLATE_PATH + "method.twig"))
            .put(MethodSignature.class, classpathTemplate(TEMPLATE_PATH + "methodSignature.twig"))
            .put(Parameter.class, classpathTemplate(TEMPLATE_PATH + "parameter.twig"))
            .put(Statement.class, classpathTemplate(TEMPLATE_PATH + "statement.twig"))
            .put(TypeCheckingError.class, classpathTemplate(TEMPLATE_PATH + "error_typeCheckingError.twig"))
            .put(SymbolTable.class, classpathTemplate(TEMPLATE_PATH + "symbolTable.twig"))
            .build();
    private final Root root;
    private final Map<Node, List<TypeCheckingError>> typecheckingErrors;
    private final List<String> symbolTables;

    public HtmlWalker(Root root) {
        this.root = root;
        //noinspection unchecked
        this.typecheckingErrors = root.getContext()
                .get("errors.typechecking", Map.class)
                .orElse(Collections.emptyMap());
        this.symbolTables = new LinkedList<>();
    }

    @Override
    public String walk() {
        return root.accept(this);
    }

    @Override
    public String visit(Root node) {
        return templates.get(node.getClass()).render(
                newModel(node)
                        .with("namespace", node.getNamespace())
                        .with("toplevelName", node.getTopLevelNode().accept(new BaseVisitor<String>() {
                            @Override
                            public String visit(Type node) {
                                return node.getName();
                            }

                            @Override
                            public String visit(Class node) {
                                return node.getName();
                            }
                        }))
                        .with("uses", node.getUses().stream()
                                .map(n -> n.accept(this))
                                .collect(Collectors.toList()))
                        .with("toplevel", node.getTopLevelNode().accept(this))
                        .with("errors", typecheckingErrors.values().stream()
                                .map(
                                        es -> es.stream()
                                                .map(e -> templates.get(e.getClass()).render(
                                                        newModel(e.getNode())
                                                                .with("error", e.toString())
                                                ))
                                                .collect(Collectors.toList())
                                )
                                .flatMap(List::stream)
                                .collect(Collectors.toList())
                        )
                        .with("symbolTables", symbolTables)
        );
    }

    private JtwigModel newModel(Node node) {
        return JtwigModel.newModel()
                .with("nodeId", formatNodeId(node))
                .with("nodeKind", node.getClass().getSimpleName())
                .with("hasErrors", typecheckingErrors.containsKey(node));
    }

    private String formatNodeId(Node node) {
        return node.getNodeId().format("node_%d");
    }

    @Override
    public String visit(Use node) {
        return templates.get(node.getClass()).render(
                newModel(node)
                        .with("from", node.getFrom())
                        .with("to", node.getTo())
        );
    }

    @Override
    public String visit(Class node) {
        renderSymbolTable(node);
        return templates.get(node.getClass()).render(
                newModel(node)
                        .with("visibility", node.getVisibility().toString().toLowerCase())
                        .with("className", node.getName())
                        .with("typeParameters", node.getTypeParameters().stream()
                                .map(n -> n.accept(this))
                                .collect(Collectors.toList()))
                        .with("superType", node.getSuperType().accept(this))
                        .with("attributes", node.getAttributes().stream()
                                .map(n -> n.accept(this))
                                .collect(Collectors.toList()))
                        .with("methods", node.getMethods().stream()
                                .map(n -> n.accept(this))
                                .collect(Collectors.toList()))
        );
    }

    @Override
    public String visit(TypeParameter node) {
        return templates.get(node.getClass()).render(
                newModel(node)
                        .with("name", node.getName())
        );
    }

    @Override
    public String visit(TypeSpecSimple node) {
        return templates.get(node.getClass()).render(
                newModel(node)
                        .with("typeName", node.getType())
                        .with("arguments", node.getArguments().stream()
                                .map(n -> n.accept(this))
                                .collect(Collectors.toList()))
        );
    }

    @Override
    public String visit(TypeSpecIntersection node) {
        return visitTypeSpecComposition(node, node.getTypes(), "|");
    }

    private String visitTypeSpecComposition(Node node, List<TypeSpec> typeSpecs, String mode) {
        return templates.get(node.getClass()).render(
                newModel(node)
                        .with("mode", mode)
                        .with("types", typeSpecs.stream()
                                .map(t -> t.accept(this))
                                .collect(Collectors.toList()))
        );
    }

    @Override
    public String visit(TypeSpecUnion node) {
        return visitTypeSpecComposition(node, node.getTypes(), "&");
    }

    @Override
    public String visit(TypeSpecFunction node) {
        return templates.get(node.getClass()).render(
                newModel(node)
                        .with("returnType", node.getReturnType().accept(this))
                        .with("arguments", node.getArguments().stream()
                                .map(n -> n.accept(this))
                                .collect(Collectors.toList()))
        );
    }

    @Override
    public String visit(TypeSpecInferred node) {
        return templates.get(node.getClass()).render(
                newModel(node)
        );
    }

    @Override
    public String visit(VarAttribute node) {
        return templates.get(Attribute.class).render(
                visitAssignment(node)
                        .with("kind", "var")
        );
    }

    private JtwigModel visitAssignment(Assignment node) {
        return newModel(node)
                .with("name", node.getIdentifier())
                .with("type", node.getType().accept(this))
                .with("value", node.getValue().accept(this));
    }

    @Override
    public String visit(ValAttribute node) {
        return templates.get(Attribute.class).render(
                visitAssignment(node)
                        .with("kind", "val")
        );
    }

    @Override
    public String visit(StringValue node) {
        return templates.get(node.getClass()).render(
                newModel(node)
                        .with("value", node.getValue())
        );
    }

    @Override
    public String visit(NumberValue node) {
        return templates.get(node.getClass()).render(
                newModel(node)
                        .with("value", node.getValue())
        );
    }

    @Override
    public String visit(BooleanValue node) {
        return templates.get(node.getClass()).render(
                newModel(node)
                        .with("value", node.getValue())
        );
    }

    @Override
    public String visit(NoneValue node) {
        return templates.get(node.getClass()).render(
                newModel(node)
        );
    }

    private void renderSymbolTable(Node node) {
        SymbolTable symbolTable = SymbolTableAwareNode.wrap(node).getSymbolTable();
        symbolTables.add(templates.get(SymbolTable.class).render(
                newModel(node)
                        .with("nodeId", formatNodeId(node))
                        .with("name", symbolTable.fqName().replaceFirst("^root\\.", ""))
                        .with("symbols", symbolTable.symbolsStream()
                                .map(s -> ImmutableMap.of(
                                        "name", s.getName(),
                                        "kind", s.getClass().getSimpleName(),
                                        "refNodeId", formatNodeId(s.getNode())
                                ))
                                .collect(Collectors.toList()))
        ));
    }

    @Override
    public String visit(IdentifierValue node) {
        return templates.get(node.getClass()).render(
                newModel(node)
                        .with("name", node.getValue())
        );
    }

    @Override
    public String visit(VarAssignmentValue node) {
        return templates.get(Attribute.class).render(
                visitAssignment(node)
                        .with("kind", "var")
        );
    }

    @Override
    public String visit(ValAssignmentValue node) {
        return templates.get(Attribute.class).render(
                visitAssignment(node)
                        .with("kind", "val")
        );
    }

    @Override
    public String visit(IndirectAssignmentValue node) {
        return templates.get(node.getClass()).render(
                newModel(node)
                        .with("indirectValue", node.getIndirectValue().accept(this))
                        .with("identifier", node.getIdentifier())
                        .with("value", node.getValue().accept(this))
        );
    }

    @Override
    public String visit(MethodCallValue node) {
        return templates.get(node.getClass()).render(
                newModel(node)
                        .with("methodName", node.getMethodName())
                        .with("typeArguments", node.getTypeArguments().stream()
                                .map(n -> n.accept(this))
                                .collect(Collectors.toList()))
                        .with("methodArguments", node.getMethodArguments().stream()
                                .map(n -> n.accept(this))
                                .collect(Collectors.toList()))
        );
    }

    @Override
    public String visit(NestedValue node) {
        return templates.get(node.getClass()).render(
                newModel(node)
                        .with("outer", node.getOuter().accept(this))
                        .with("inner", node.getInner().accept(this))
        );
    }

    @Override
    public String visit(FunctionValue node) {
        renderSymbolTable(node);
        return templates.get(node.getClass()).render(
                newModel(node)
                        .with("returnType", node.getReturnType().accept(this))
                        .with("typeParameters", node.getTypeParameters().stream()
                                .map(n -> n.accept(this))
                                .collect(Collectors.toList()))
                        .with("parameters", node.getParameters().stream()
                                .map(n -> n.accept(this))
                                .collect(Collectors.toList()))
                        .with("statements", node.getStatements().stream()
                                .map(n -> n.accept(this))
                                .collect(Collectors.toList()))
        );
    }

    @Override
    public String visit(Method node) {
        return templates.get(node.getClass()).render(
                newModel(node)
                        .with("signature", node.getSignature().accept(this))
                        .with("statements", node.getStatements().stream()
                                .map(n -> n.accept(this))
                                .collect(Collectors.toList()))
        );
    }

    @Override
    public String visit(MethodSignature node) {
        renderSymbolTable(node);
        return templates.get(MethodSignature.class).render(
                newModel(node)
                        .with("visibility", node.getVisibility().toString().toLowerCase())
                        .with("name", node.getName())
                        .with("typeParameters", node.getTypeParameters().stream()
                                .map(n -> n.accept(this))
                                .collect(Collectors.toList()))
                        .with("methodParameters", node.getParameters().stream()
                                .map(n -> n.accept(this))
                                .collect(Collectors.toList()))
                        .with("returnType", node.getReturnType().accept(this))
        );
    }

    @Override
    public String visit(Parameter node) {
        return templates.get(node.getClass()).render(
                newModel(node)
                        .with("name", node.getName())
                        .with("type", node.getType().accept(this))
        );
    }

    @Override
    public String visit(Statement node) {
        return templates.get(Statement.class).render(
                newModel(node)
                        .with("isLast", node.isLast())
                        .with("value", node.getValue().accept(this))
        );
    }
}
