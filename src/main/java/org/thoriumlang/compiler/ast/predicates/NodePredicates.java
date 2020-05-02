package org.thoriumlang.compiler.ast.predicates;

import org.thoriumlang.compiler.ast.nodes.Attribute;
import org.thoriumlang.compiler.ast.nodes.Class;
import org.thoriumlang.compiler.ast.nodes.DirectAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.Node;
import org.thoriumlang.compiler.ast.nodes.TypeSpecInferred;
import org.thoriumlang.compiler.ast.visitor.PredicateVisitor;

public final class NodePredicates {
    private NodePredicates() {
        // nothing
    }

    public static boolean isTypeSpecInferred(Node node) {
        return node.accept(new PredicateVisitor() {
            @Override
            public Boolean visit(TypeSpecInferred node) {
                return true;
            }
        });
    }

    public static boolean isDirectAssignmentValue(Node node) {
        return node.accept(new PredicateVisitor() {
            @Override
            public Boolean visit(DirectAssignmentValue node) {
                return true;
            }
        });
    }

    public static boolean isAttribute(Node node) {
        return node.accept(new PredicateVisitor() {
            @Override
            public Boolean visit(Attribute node) {
                return true;
            }
        });
    }

    public static boolean isClass(Node node) {
        return node.accept(new PredicateVisitor() {
            @Override
            public Boolean visit(Class node) {
                return true;
            }
        });
    }
}
