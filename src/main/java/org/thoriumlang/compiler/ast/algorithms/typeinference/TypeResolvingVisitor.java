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
import org.thoriumlang.compiler.ast.visitor.BaseVisitor;

import java.util.Collections;
import java.util.List;

public class TypeResolvingVisitor extends BaseVisitor<List<SemanticError>> {
    @Override
    public List<SemanticError> visit(Root node) {
        return Collections.emptyList();
    }

    @Override
    public List<SemanticError> visit(Use node) {
        return super.visit(node);
    }

    @Override
    public List<SemanticError> visit(Type node) {
        return super.visit(node);
    }

    @Override
    public List<SemanticError> visit(Class node) {
        return super.visit(node);
    }

    @Override
    public List<SemanticError> visit(TypeSpecIntersection node) {
        return super.visit(node);
    }

    @Override
    public List<SemanticError> visit(TypeSpecUnion node) {
        return super.visit(node);
    }

    @Override
    public List<SemanticError> visit(TypeSpecSimple node) {
        return super.visit(node);
    }

    @Override
    public List<SemanticError> visit(TypeSpecFunction node) {
        return super.visit(node);
    }

    @Override
    public List<SemanticError> visit(TypeSpecInferred node) {
        return super.visit(node);
    }

    @Override
    public List<SemanticError> visit(MethodSignature node) {
        return super.visit(node);
    }

    @Override
    public List<SemanticError> visit(Parameter node) {
        return super.visit(node);
    }

    @Override
    public List<SemanticError> visit(TypeParameter node) {
        return super.visit(node);
    }

    @Override
    public List<SemanticError> visit(StringValue node) {
        return super.visit(node);
    }

    @Override
    public List<SemanticError> visit(NumberValue node) {
        return super.visit(node);
    }

    @Override
    public List<SemanticError> visit(BooleanValue node) {
        return super.visit(node);
    }

    @Override
    public List<SemanticError> visit(NoneValue node) {
        return super.visit(node);
    }

    @Override
    public List<SemanticError> visit(IdentifierValue node) {
        return super.visit(node);
    }

    @Override
    public List<SemanticError> visit(NewAssignmentValue node) {
        return super.visit(node);
    }

    @Override
    public List<SemanticError> visit(DirectAssignmentValue node) {
        return super.visit(node);
    }

    @Override
    public List<SemanticError> visit(IndirectAssignmentValue node) {
        return super.visit(node);
    }

    @Override
    public List<SemanticError> visit(MethodCallValue node) {
        return super.visit(node);
    }

    @Override
    public List<SemanticError> visit(NestedValue node) {
        return super.visit(node);
    }

    @Override
    public List<SemanticError> visit(FunctionValue node) {
        return super.visit(node);
    }

    @Override
    public List<SemanticError> visit(Statement node) {
        return super.visit(node);
    }

    @Override
    public List<SemanticError> visit(Method node) {
        return super.visit(node);
    }

    @Override
    public List<SemanticError> visit(Attribute node) {
        return super.visit(node);
    }

    @Override
    public List<SemanticError> visit(Reference node) {
        return super.visit(node);
    }
}
