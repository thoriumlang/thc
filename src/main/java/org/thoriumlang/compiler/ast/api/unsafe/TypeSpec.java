package org.thoriumlang.compiler.ast.api.unsafe;

import org.thoriumlang.compiler.ast.api.Type;
import org.thoriumlang.compiler.ast.api.TypeSpecIntersectionType;
import org.thoriumlang.compiler.ast.api.TypeSpecSimpleType;
import org.thoriumlang.compiler.ast.api.TypeSpecUnionType;
import org.thoriumlang.compiler.ast.nodes.TypeSpecFunction;
import org.thoriumlang.compiler.ast.nodes.TypeSpecInferred;
import org.thoriumlang.compiler.ast.nodes.TypeSpecIntersection;
import org.thoriumlang.compiler.ast.nodes.TypeSpecSimple;
import org.thoriumlang.compiler.ast.nodes.TypeSpecUnion;
import org.thoriumlang.compiler.ast.visitor.BaseVisitor;

import java.util.Objects;

public class TypeSpec {
    private final org.thoriumlang.compiler.ast.nodes.TypeSpec node;

    public TypeSpec(org.thoriumlang.compiler.ast.nodes.TypeSpec node) {
        this.node = Objects.requireNonNull(node, "node cannot be null");
    }

    public Type getType() {
        return node.accept(new BaseVisitor<Type>() {
            @Override
            public Type visit(TypeSpecSimple node) {
                return new TypeSpecSimpleType(node);
            }

            @Override
            public Type visit(TypeSpecIntersection node) {
                return new TypeSpecIntersectionType(node);
            }

            @Override
            public Type visit(TypeSpecUnion node) {
                return new TypeSpecUnionType(node);
            }

            @Override
            public Type visit(TypeSpecFunction node) {
                throw new IllegalStateException("not implemented"); // TODO implement
            }

            @Override
            public Type visit(TypeSpecInferred node) {
                throw new IllegalStateException("not implemented"); // TODO implement
            }
        });
    }
}
