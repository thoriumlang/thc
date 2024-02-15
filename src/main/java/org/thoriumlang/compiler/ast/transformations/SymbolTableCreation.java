package org.thoriumlang.compiler.ast.transformations;

import io.vavr.control.Either;
import org.thoriumlang.compiler.api.errors.SemanticError;
import org.thoriumlang.compiler.api.errors.SymbolAlreadyDefinedError;
import org.thoriumlang.compiler.api.errors.SymbolNotDefinedError;
import org.thoriumlang.compiler.api.errors.TypeNotDefinedError;
import org.thoriumlang.compiler.ast.nodes.Attribute;
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
import org.thoriumlang.compiler.ast.nodes.Parameter;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.ast.nodes.Statement;
import org.thoriumlang.compiler.ast.nodes.Type;
import org.thoriumlang.compiler.ast.nodes.TypeParameter;
import org.thoriumlang.compiler.ast.nodes.TypeSpecFunction;
import org.thoriumlang.compiler.ast.nodes.TypeSpecIntersection;
import org.thoriumlang.compiler.ast.nodes.TypeSpecSimple;
import org.thoriumlang.compiler.ast.nodes.TypeSpecUnion;
import org.thoriumlang.compiler.ast.symbols.NodeRef;
import org.thoriumlang.compiler.ast.symbols.SymbolTable;
import org.thoriumlang.compiler.ast.visitor.BaseVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Function;

public class SymbolTableCreation implements Function<Root, Either<List<SemanticError>, SymbolTable>> {
    @Override
    public Either<List<SemanticError>, SymbolTable> apply(Root root) {
        SymbolTable symbolTable = new SymbolTable();
        List<SemanticError> errors = new ArrayList<>();

        new Visitor(symbolTable, errors).visit(root);

        return errors.isEmpty()
                ? Either.right(symbolTable)
                : Either.left(errors);
    }

    private static class Visitor extends BaseVisitor<Void> {
        private final Stack<SymbolTable> symbolTable;
        private final List<SemanticError> errors;

        private Visitor(SymbolTable symbolTable, List<SemanticError> errors) {
            this.symbolTable = new Stack<>();
            this.symbolTable.push(symbolTable);
            this.errors = errors;
        }

        @Override
        public Void visit(Root node) {
            node.getUses().forEach(use -> addSymbolOrIssueError(use.getFrom(), use));

            addSymbolOrIssueError(
                    node.getNamespace() + "." + node.getTopLevelNode().getName(),
                    node.getTopLevelNode()
            );

            node.getTopLevelNode().accept(this);

            return null;
        }

        private void addSymbolOrIssueError(String symbolName, Node node) {
            addSymbolOrIssueError(symbolName, node, symbolTable.peek());
        }

        private void addSymbolOrIssueError(String symbolName, Node node, SymbolTable symbolTable) {
            symbolTable.findInCurrentScope(symbolName).ifPresentOrElse(
                    initialNodeRef -> errors.add(new SymbolAlreadyDefinedError(symbolName, new NodeRef(node), initialNodeRef)),
                    () -> symbolTable.put(symbolName, new NodeRef(node))
            );
        }

//        @Override
//        public Void visit(Use node) {
//            return super.visit(node);
//        }

        @Override
        public Void visit(Type node) {
            SymbolTable localTable = symbolTable.peek().addChild(new SymbolTable("type " + node.getName()));
            symbolTable.push(localTable);

            node.getTypeParameters().forEach(n -> n.accept(this));

            int c = 0;
            for (MethodSignature method : node.getMethods()) {
                addSymbolOrIssueError(method.getName() + "$" + (c++), method, localTable); // todo better naming
                method.accept(this);
            }

            symbolTable.pop();
            return null;
        }

        @Override
        public Void visit(Class node) {
            SymbolTable localTable = symbolTable.peek().addChild(new SymbolTable("class " + node.getName()));
            symbolTable.push(localTable);

            node.getTypeParameters().forEach(n -> n.accept(this));
            node.getAttributes().forEach(n -> n.accept(this));

            int c = 0;
            for (Method method : node.getMethods()) {
                addSymbolOrIssueError(method.getSignature().getName() + "$" + (c++), method, localTable); // todo better naming
                method.accept(this);
            }

            symbolTable.pop();
            return null;
        }

        @Override
        public Void visit(TypeSpecIntersection node) {
            return super.visit(node);
        }

        @Override
        public Void visit(TypeSpecUnion node) {
            return super.visit(node);
        }

        @Override
        public Void visit(TypeSpecSimple node) {
            if (!symbolTable.peek().inScope(node.getType())) {
                errors.add(new TypeNotDefinedError(node.getType(), new NodeRef(node)));
            }
            node.getArguments().forEach(n -> n.accept(this));

            return null;
        }

        @Override
        public Void visit(TypeSpecFunction node) {
            return super.visit(node);
        }

//        @Override
//        public Void visit(TypeSpecInferred node) {
//            return super.visit(node);
//        }

        @Override
        public Void visit(MethodSignature node) {
            SymbolTable localTable = symbolTable.peek().addChild(new SymbolTable(node.getName() + "()")); // todo find better name
            symbolTable.push(localTable);

            node.getTypeParameters().forEach(n -> n.accept(this));
            node.getParameters().forEach(n -> n.accept(this));
            node.getReturnType().accept(this);

            symbolTable.pop();
            return null;
        }

        @Override
        public Void visit(Parameter node) {
            node.getType().accept(this);
            addSymbolOrIssueError(node.getName(), node);

            return super.visit(node);
        }

        @Override
        public Void visit(TypeParameter node) {
            addSymbolOrIssueError(node.getName(), node);
            return null;
        }
//
//        @Override
//        public Void visit(StringValue node) {
//            return super.visit(node);
//        }
//
//        @Override
//        public Void visit(NumberValue node) {
//            return super.visit(node);
//        }
//
//        @Override
//        public Void visit(BooleanValue node) {
//            return super.visit(node);
//        }
//
//        @Override
//        public Void visit(NoneValue node) {
//            return super.visit(node);
//        }

        @Override
        public Void visit(IdentifierValue node) {
            if (!node.getReference().allowForwardReference()) {
                if (!symbolTable.peek().inScope(node.getReference().getName())) {
                    errors.add(new SymbolNotDefinedError(node.getReference().getName(), new NodeRef(node)));
                }
            }
            return null;
        }

        @Override
        public Void visit(NewAssignmentValue node) {
            addSymbolOrIssueError(node.getName(), node);
            return null;
        }

        @Override
        public Void visit(DirectAssignmentValue node) {
            if (!node.getReference().allowForwardReference()) {
                if (!symbolTable.peek().inScope(node.getReference().getName())) {
                    errors.add(new SymbolNotDefinedError(node.getReference().getName(), new NodeRef(node)));
                }
            }

            return null;
        }

        @Override
        public Void visit(IndirectAssignmentValue node) {
            return super.visit(node);
        }

        @Override
        public Void visit(MethodCallValue node) {
            return super.visit(node);
        }

        @Override
        public Void visit(NestedValue node) {
            node.getOuter().accept(this);
            return null;
        }

        @Override
        public Void visit(FunctionValue node) {
            return super.visit(node);
        }

        @Override
        public Void visit(Statement node) {
            node.getValue().accept(this);
            return null;
        }

        @Override
        public Void visit(Method node) {
            SymbolTable localTable = symbolTable.peek().addChild(new SymbolTable(node.getSignature().getName() + "()")); // todo find better name
            symbolTable.push(localTable);

            node.getSignature().getTypeParameters().forEach(n -> addSymbolOrIssueError(n.getName(), n));
            node.getSignature().getParameters().forEach(n -> n.accept(this));
            node.getSignature().getReturnType().accept(this);

            SymbolTable bodyTable = localTable.addChild(new SymbolTable("body"));
            symbolTable.push(bodyTable);

            node.getStatements().forEach(n -> n.accept(this));

            symbolTable.pop();
            symbolTable.pop();
            return null;
        }

        @Override
        public Void visit(Attribute node) {
            node.getType().accept(this);
            addSymbolOrIssueError(node.getName(), node);
            return null;
        }
//
//        @Override
//        public Void visit(Reference node) {
//            return super.visit(node);
//        }
    }
}
