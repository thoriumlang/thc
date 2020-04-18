package org.thoriumlang.compiler.ast.algorithms.typeinference;

import org.thoriumlang.compiler.api.errors.SemanticError;
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
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
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
import org.thoriumlang.compiler.ast.visitor.Visitor;
import org.thoriumlang.compiler.collections.Lists;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TypeResolvingVisitor implements Visitor<List<SemanticError>> {
    private final NodeIdGenerator nodeIdGenerator;

    public TypeResolvingVisitor(NodeIdGenerator nodeIdGenerator) {
        this.nodeIdGenerator = nodeIdGenerator;
    }

    @Override
    public List<SemanticError> visit(Root node) {
        return node.getTopLevelNode().accept(this);
    }

    @Override
    public List<SemanticError> visit(Use node) {
        return Collections.emptyList();
    }

    @Override
    public List<SemanticError> visit(Type node) {
        return visitRecursive(node.getMethods());
    }

    private List<SemanticError> visitRecursive(List<? extends Node> nodes) {
        return nodes.stream()
                .map(n -> n.accept(this))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @Override
    public List<SemanticError> visit(Class node) {
        return Lists.merge(
                visitRecursive(node.getAttributes()),
                visitRecursive(node.getMethods())
        );
    }

    @Override
    public List<SemanticError> visit(TypeSpecIntersection node) {
        return visitRecursive(node.getTypes());
    }

    @Override
    public List<SemanticError> visit(TypeSpecUnion node) {
        return visitRecursive(node.getTypes());
    }

    @Override
    public List<SemanticError> visit(TypeSpecSimple node) {
        return Collections.emptyList();
    }

    @Override
    public List<SemanticError> visit(TypeSpecFunction node) {
        return Lists.merge(
                visitRecursive(node.getArguments()),
                node.getReturnType().accept(this)
        );
    }

    @Override
    public List<SemanticError> visit(TypeSpecInferred node) {
        return Collections.singletonList(new SemanticError("cannot infer type", node));
    }

    @Override
    public List<SemanticError> visit(MethodSignature node) {
        return node.getReturnType().accept(this);
    }

    @Override
    public List<SemanticError> visit(Parameter node) {
        return Collections.emptyList();
    }

    @Override
    public List<SemanticError> visit(TypeParameter node) {
        return Collections.emptyList();
    }

    @Override
    public List<SemanticError> visit(StringValue node) {
        node.getContext().put(
                TypeSpec.class,
                new TypeSpecSimple(nodeIdGenerator.next(), "org.thoriumlang.String", Collections.emptyList())
        );
        return Collections.emptyList();
    }

    @Override
    public List<SemanticError> visit(NumberValue node) {
        node.getContext().put(
                TypeSpec.class,
                new TypeSpecSimple(nodeIdGenerator.next(), "org.thoriumlang.Number", Collections.emptyList())
        );
        return Collections.emptyList();
    }

    @Override
    public List<SemanticError> visit(BooleanValue node) {
        node.getContext().put(
                TypeSpec.class,
                new TypeSpecSimple(nodeIdGenerator.next(), "org.thoriumlang.Boolean", Collections.emptyList())
        );
        return Collections.emptyList();
    }

    @Override
    public List<SemanticError> visit(NoneValue node) {
        node.getContext().put(
                TypeSpec.class,
                new TypeSpecSimple(nodeIdGenerator.next(), "org.thoriumlang.None", Collections.emptyList())
        );
        return Collections.emptyList();
    }

    @Override
    public List<SemanticError> visit(IdentifierValue node) {
        return Collections.emptyList();
    }

    @Override
    public List<SemanticError> visit(NewAssignmentValue node) {
        return node.getType().accept(this);
    }

    @Override
    public List<SemanticError> visit(DirectAssignmentValue node) {
        return Collections.emptyList();
    }

    @Override
    public List<SemanticError> visit(IndirectAssignmentValue node) {
        return Collections.emptyList();
    }

    @Override
    public List<SemanticError> visit(MethodCallValue node) {
        return Collections.emptyList();
    }

    @Override
    public List<SemanticError> visit(NestedValue node) {
        return Collections.emptyList();
    }

    @Override
    public List<SemanticError> visit(FunctionValue node) {
        return Lists.merge(
                visitRecursive(node.getStatements()),
                node.getReturnType().accept(this)
        );
    }

    @Override
    public List<SemanticError> visit(Statement node) {
        return node.getValue().accept(this);
    }

    @Override
    public List<SemanticError> visit(Method node) {
        return Lists.merge(
                node.getSignature().accept(this),
                visitRecursive(node.getStatements())
        );
    }

    @Override
    public List<SemanticError> visit(Attribute node) {
        return Collections.emptyList();
    }

    @Override
    public List<SemanticError> visit(Reference node) {
        return Collections.emptyList();
    }
}
