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
import org.thoriumlang.compiler.api.errors.SemanticError;
import org.thoriumlang.compiler.ast.context.ReferencedNode;
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
import org.thoriumlang.compiler.ast.nodes.TypeSpec;
import org.thoriumlang.compiler.ast.nodes.TypeSpecFunction;
import org.thoriumlang.compiler.ast.nodes.TypeSpecInferred;
import org.thoriumlang.compiler.ast.nodes.TypeSpecIntersection;
import org.thoriumlang.compiler.ast.nodes.TypeSpecSimple;
import org.thoriumlang.compiler.ast.nodes.TypeSpecUnion;
import org.thoriumlang.compiler.ast.nodes.Use;
import org.thoriumlang.compiler.ast.visitor.BaseVisitor;
import org.thoriumlang.compiler.ast.visitor.Visitor;
import org.thoriumlang.compiler.output.Walker;
import org.thoriumlang.compiler.symbols.SymbolTable;

import java.io.File;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.jtwig.JtwigTemplate.classpathTemplate;

@SuppressWarnings("squid:S1192")
public class HtmlWalker implements Visitor<String>, Walker<String> {
    private static final String TEMPLATE_PATH = HtmlWalker.class.getPackage().getName()
            .replace(".", File.separator) + File.separator;

    private static final Map<java.lang.Class<?>, JtwigTemplate> templates = ImmutableMap.<java.lang.Class<?>, JtwigTemplate>builder()
            .put(Root.class, classpathTemplate(TEMPLATE_PATH + "root.twig"))
            .put(Use.class, classpathTemplate(TEMPLATE_PATH + "use.twig"))
            .put(Class.class, classpathTemplate(TEMPLATE_PATH + "class.twig"))
            .put(Type.class, classpathTemplate(TEMPLATE_PATH + "type.twig"))
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
            .put(NewAssignmentValue.class, classpathTemplate(TEMPLATE_PATH + "newAssignment.twig"))
            .put(IndirectAssignmentValue.class, classpathTemplate(TEMPLATE_PATH + "indirectAssignmentValue.twig"))
            .put(DirectAssignmentValue.class, classpathTemplate(TEMPLATE_PATH + "directAssignmentValue.twig"))
            .put(Method.class, classpathTemplate(TEMPLATE_PATH + "method.twig"))
            .put(MethodSignature.class, classpathTemplate(TEMPLATE_PATH + "methodSignature.twig"))
            .put(Parameter.class, classpathTemplate(TEMPLATE_PATH + "parameter.twig"))
            .put(Statement.class, classpathTemplate(TEMPLATE_PATH + "statement.twig"))
            .put(SemanticError.class, classpathTemplate(TEMPLATE_PATH + "compilationError.twig"))
            .put(SymbolTable.class, classpathTemplate(TEMPLATE_PATH + "symbolTable.twig"))
            .build();
    private final Root root;
    private final Map<Node, List<SemanticError>> compilationErrors;
    private final List<String> symbolTables;

    public HtmlWalker(Root root, Map<Node, List<SemanticError>> compilationErrors) {
        this.root = root;
        this.compilationErrors = compilationErrors;
        this.symbolTables = new LinkedList<>();
    }

    @Override
    public String walk() {
        return root.accept(this);
    }

    @Override
    public String visit(Root node) {
        renderSymbolTable(node);
        return templates.get(node.getClass()).render(
                newModel(node)
                        .with("date", LocalDateTime.now())
                        .with("css", classpathTemplate(TEMPLATE_PATH + "style.css").render(new JtwigModel()))
                        .with("js", classpathTemplate(TEMPLATE_PATH + "script.js").render(new JtwigModel()))
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
                        .with("errors", compilationErrors.values().stream()
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
        Optional<SourcePosition> sourcePosition = node.getContext().get(SourcePosition.class);

        return JtwigModel.newModel()
                .with("nodeId", formatNodeId(node))
                .with("nodeKind", node.getClass().getSimpleName())
                .with("hasErrors", compilationErrors.containsKey(node))
                .with("line", sourcePosition.map(p -> String.valueOf(p.getLine())).orElse("?"))
                .with("char", sourcePosition.map(p -> String.valueOf(p.getChar())).orElse("?"));
    }

    private String formatNodeId(Node node) {
        return node.getNodeId().format("node_%d");
    }

    @Override
    public String visit(Use node) {
        renderSymbolTable(node);
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
    public String visit(Type node) {
        renderSymbolTable(node);
        return templates.get(node.getClass()).render(
                newModel(node)
                        .with("visibility", node.getVisibility().toString().toLowerCase())
                        .with("typeName", node.getName())
                        .with("typeParameters", node.getTypeParameters().stream()
                                .map(n -> n.accept(this))
                                .collect(Collectors.toList()))
                        .with("superType", node.getSuperType().accept(this))
                        .with("methods", node.getMethods().stream()
                                .map(n -> n.accept(this))
                                .collect(Collectors.toList()))
        );
    }

    @Override
    public String visit(TypeParameter node) {
        renderSymbolTable(node);
        return templates.get(node.getClass()).render(
                newModel(node)
                        .with("name", node.getName())
        );
    }

    @Override
    public String visit(TypeSpecSimple node) {
        renderSymbolTable(node);
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
        renderSymbolTable(node);
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
        renderSymbolTable(node);
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
        renderSymbolTable(node);
        return templates.get(node.getClass()).render(
                newModel(node)
        );
    }

    @Override
    public String visit(Attribute node) {
        renderSymbolTable(node);
        return templates.get(node.getClass()).render(
                newModel(node)
                        .with("name", node.getName())
                        .with("type", node.getType().accept(this))
                        .with("value", node.getValue().accept(this))
                        .with("kind", node.getMode().toString().toLowerCase())
        );
    }

    @Override
    public String visit(StringValue node) {
        renderSymbolTable(node);
        return templates.get(node.getClass()).render(
                newModel(node)
                        .with("value", node.getValue())
        );
    }

    @Override
    public String visit(NumberValue node) {
        renderSymbolTable(node);
        return templates.get(node.getClass()).render(
                newModel(node)
                        .with("value", node.getValue())
        );
    }

    @Override
    public String visit(BooleanValue node) {
        renderSymbolTable(node);
        return templates.get(node.getClass()).render(
                newModel(node)
                        .with("value", node.getValue())
        );
    }

    @Override
    public String visit(NoneValue node) {
        renderSymbolTable(node);
        return templates.get(node.getClass()).render(
                newModel(node)
        );
    }

    private void renderSymbolTable(Node node) {
        SymbolTable symbolTable = node.getContext()
                .get(SymbolTable.class)
                .orElseThrow(() -> new IllegalStateException("no symbol table found"));
        symbolTables.add(templates.get(SymbolTable.class).render(
                newModel(node)
                        .with("nodeId", formatNodeId(node))
                        .with("sourceNodeId", "")
                        .with("name", symbolTable)
                        .with("hash", Integer.toHexString(symbolTable.hashCode()))
// FIXME rewrite
//                        .with("symbols", symbolTable.symbolsStream()
//                                .map(s -> ImmutableMap.of(
//                                        "name", s.getName(),
//                                        "kind", s.getClass().getSimpleName(),
//                                        "refNodeId", formatNodeId(s.getNode())
//                                ))
//                                .collect(Collectors.toList()))
        ));
    }

    @Override
    public String visit(IdentifierValue node) {
        renderSymbolTable(node);
        return templates.get(node.getClass()).render(
                newModel(node)
                        .with("name", node.getReference().accept(this))
                        .with("referencedNodeId", referencedNodeId(node.getReference()))
        );
    }

    private String referencedNodeId(Reference node) {
        return node.getContext()
                .get(ReferencedNode.class)
                .map(ReferencedNode::node)
                .map(this::formatNodeId)
                .orElse("node_0");
    }

    @Override
    public String visit(NewAssignmentValue node) {
        renderSymbolTable(node);
        return templates.get(node.getClass()).render(
                newModel(node)
                        .with("name", node.getName())
                        .with("type", node.getType().accept(this))
                        .with("value", node.getValue().accept(this))
                        .with("kind", node.getMode().toString().toLowerCase())
        );
    }

    @Override
    public String visit(IndirectAssignmentValue node) {
        renderSymbolTable(node);
        return templates.get(node.getClass()).render(
                newModel(node)
                        .with("indirectValue", node.getIndirectValue().accept(this))
                        .with("identifier", node.getReference().accept(this))
                        .with("value", node.getValue().accept(this))
        );
    }

    @Override
    public String visit(DirectAssignmentValue node) {
        renderSymbolTable(node);
        return templates.get(node.getClass()).render(
                newModel(node)
                        .with("identifier", node.getReference().accept(this))
                        .with("value", node.getValue().accept(this))
                        .with("referencedNodeId", referencedNodeId(node.getReference()))
        );
    }

    @Override
    public String visit(MethodCallValue node) {
        renderSymbolTable(node);
        return templates.get(node.getClass()).render(
                newModel(node)
                        .with("methodName", node.getMethodReference().getName())
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
        renderSymbolTable(node);
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
        renderSymbolTable(node);
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
        renderSymbolTable(node);
        return templates.get(node.getClass()).render(
                newModel(node)
                        .with("name", node.getName())
                        .with("type", node.getType().accept(this))
        );
    }

    @Override
    public String visit(Statement node) {
        renderSymbolTable(node);
        return templates.get(Statement.class).render(
                newModel(node)
                        .with("isLast", node.isLast())
                        .with("value", node.getValue().accept(this))
        );
    }

    @Override
    public String visit(Reference node) {
        return node.getName();
    }
}