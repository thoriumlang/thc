package org.thoriumlang.compiler.ast.visitor;

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

public class MappingVisitor<T> implements Visitor<T> {
    private final T defaultValue;

    public MappingVisitor(T defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public T visit(Root node) {
        return defaultValue;
    }

    @Override
    public T visit(Use node) {
        return defaultValue;
    }

    @Override
    public T visit(Type node) {
        return defaultValue;
    }

    @Override
    public T visit(Class node) {
        return defaultValue;
    }

    @Override
    public T visit(TypeSpecIntersection node) {
        return defaultValue;
    }

    @Override
    public T visit(TypeSpecUnion node) {
        return defaultValue;
    }

    @Override
    public T visit(TypeSpecSimple node) {
        return defaultValue;
    }

    @Override
    public T visit(TypeSpecFunction node) {
        return defaultValue;
    }

    @Override
    public T visit(TypeSpecInferred node) {
        return defaultValue;
    }

    @Override
    public T visit(MethodSignature node) {
        return defaultValue;
    }

    @Override
    public T visit(Parameter node) {
        return defaultValue;
    }

    @Override
    public T visit(TypeParameter node) {
        return defaultValue;
    }

    @Override
    public T visit(StringValue node) {
        return defaultValue;
    }

    @Override
    public T visit(NumberValue node) {
        return defaultValue;
    }

    @Override
    public T visit(BooleanValue node) {
        return defaultValue;
    }

    @Override
    public T visit(NoneValue node) {
        return defaultValue;
    }

    @Override
    public T visit(IdentifierValue node) {
        return defaultValue;
    }

    @Override
    public T visit(NewAssignmentValue node) {
        return defaultValue;
    }

    @Override
    public T visit(IndirectAssignmentValue node) {
        return defaultValue;
    }

    @Override
    public T visit(DirectAssignmentValue node) {
        return defaultValue;
    }

    @Override
    public T visit(MethodCallValue node) {
        return defaultValue;
    }

    @Override
    public T visit(NestedValue node) {
        return defaultValue;
    }

    @Override
    public T visit(FunctionValue node) {
        return defaultValue;
    }

    @Override
    public T visit(Statement node) {
        return defaultValue;
    }

    @Override
    public T visit(Method node) {
        return defaultValue;
    }

    @Override
    public T visit(Attribute node) {
        return defaultValue;
    }

    @Override
    public T visit(Reference node) {
        return defaultValue;
    }
}
