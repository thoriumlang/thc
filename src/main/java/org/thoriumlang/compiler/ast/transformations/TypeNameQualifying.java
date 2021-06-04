package org.thoriumlang.compiler.ast.transformations;

import org.thoriumlang.compiler.ast.nodes.Attribute;
import org.thoriumlang.compiler.ast.nodes.Class;
import org.thoriumlang.compiler.ast.nodes.FunctionValue;
import org.thoriumlang.compiler.ast.nodes.Method;
import org.thoriumlang.compiler.ast.nodes.MethodCallValue;
import org.thoriumlang.compiler.ast.nodes.MethodSignature;
import org.thoriumlang.compiler.ast.nodes.NewAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.Node;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.nodes.Parameter;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.ast.nodes.Statement;
import org.thoriumlang.compiler.ast.nodes.TopLevelNode;
import org.thoriumlang.compiler.ast.nodes.Type;
import org.thoriumlang.compiler.ast.nodes.TypeSpec;
import org.thoriumlang.compiler.ast.nodes.TypeSpecFunction;
import org.thoriumlang.compiler.ast.nodes.TypeSpecIntersection;
import org.thoriumlang.compiler.ast.nodes.TypeSpecSimple;
import org.thoriumlang.compiler.ast.nodes.TypeSpecUnion;
import org.thoriumlang.compiler.ast.nodes.Use;
import org.thoriumlang.compiler.ast.nodes.Value;
import org.thoriumlang.compiler.ast.visitor.IdentityVisitor;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TypeNameQualifying implements Function<Root, Root> {
    private final NodeIdGenerator nodeIdGenerator;

    public TypeNameQualifying(NodeIdGenerator nodeIdGenerator) {
        this.nodeIdGenerator = nodeIdGenerator;
    }

    @Override
    public Root apply(Root root) {
        Map<String, String> aliases = root.getUses().stream()
                .collect(Collectors.toMap(
                        Use::getTo,
                        Use::getFrom
                ));
        aliases.put(root.getTopLevelNode().getName(), root.getNamespace() + "." + root.getTopLevelNode().getName());
        return (Root) new Visitor(nodeIdGenerator, root.getNamespace(), aliases).visit(root);
    }

    private static class Visitor extends IdentityVisitor {
        private final NodeIdGenerator nodeIdGenerator;
        private final String namespace;
        private final Map<String, String> aliases;

        public Visitor(NodeIdGenerator nodeIdGenerator, String namespace, Map<String, String> aliases) {
            this.nodeIdGenerator = nodeIdGenerator;
            this.namespace = namespace;
            this.aliases = aliases;
        }

        @Override
        public Node visit(Root node) {
            TopLevelNode topLevelNode = (TopLevelNode) node.getTopLevelNode().accept(this);

            if (topLevelNode.equals(node.getTopLevelNode())) {
                return node;
            }

            return new Root(
                    nodeIdGenerator.next(),
                    node.getNamespace(),
                    node.getUses(),
                    topLevelNode
            );
        }

        @Override
        public Node visit(Type node) {
            TypeSpec superType = (TypeSpec) node.getSuperType().accept(this);

            List<MethodSignature> methods = node.getMethods().stream()
                    .map(n -> (MethodSignature) n.accept(this))
                    .collect(Collectors.toList());

            if (superType.equals(node.getSuperType()) && methods.equals(node.getMethods())) {
                return node;
            }

            return new Type(
                    nodeIdGenerator.next(),
                    node.getVisibility(),
                    node.getName(),
                    node.getTypeParameters(),
                    superType,
                    methods
            );
        }

        @Override
        public Node visit(Class node) {
            TypeSpec superType = (TypeSpec) node.getSuperType().accept(this);

            List<Method> methods = node.getMethods().stream()
                    .map(n -> (Method) n.accept(this))
                    .collect(Collectors.toList());

            List<Attribute> attributes = node.getAttributes().stream()
                    .map(n -> (Attribute) n.accept(this))
                    .collect(Collectors.toList());

            if (superType.equals(node.getSuperType())
                    && methods.equals(node.getMethods())
                    && attributes.equals(node.getAttributes())) {
                return node;
            }

            return new Class(
                    nodeIdGenerator.next(),
                    node.getVisibility(),
                    node.getName(),
                    node.getTypeParameters(),
                    superType,
                    methods,
                    attributes
            );
        }

        @Override
        public Node visit(TypeSpecIntersection node) {
            List<TypeSpec> types = node.getTypes().stream()
                    .map(n -> (TypeSpec) n.accept(this))
                    .collect(Collectors.toList());

            if (types.equals(node.getTypes())) {
                return node;
            }

            return new TypeSpecIntersection(
                    nodeIdGenerator.next(),
                    types
            );
        }

        @Override
        public Node visit(TypeSpecUnion node) {
            List<TypeSpec> types = node.getTypes().stream()
                    .map(n -> (TypeSpec) n.accept(this))
                    .collect(Collectors.toList());

            if (types.equals(node.getTypes())) {
                return node;
            }

            return new TypeSpecUnion(
                    nodeIdGenerator.next(),
                    types
            );
        }

        @Override
        public Node visit(TypeSpecSimple node) {
            String type = qualifyType(node.getType());

            List<TypeSpec> arguments = node.getArguments().stream()
                    .map(n -> (TypeSpec) n.accept(this))
                    .collect(Collectors.toList());

            if (type.equals(node.getType()) && arguments.equals(node.getArguments())) {
                return node;
            }

            return new TypeSpecSimple(
                    nodeIdGenerator.next(),
                    type,
                    arguments
            );
        }

        private String qualifyType(String type) {
            if (type.contains(".")) {
                return type;
            }
            return aliases.getOrDefault(type, namespace + "." + type);
        }

        @Override
        public Node visit(TypeSpecFunction node) {
            TypeSpec returnType = (TypeSpec) node.getReturnType().accept(this);

            List<TypeSpec> arguments = node.getArguments().stream()
                    .map(n -> (TypeSpec) n.accept(this))
                    .collect(Collectors.toList());

            if (returnType.equals(node.getReturnType()) && arguments.equals(node.getArguments())) {
                return node;
            }

            return new TypeSpecFunction(
                    nodeIdGenerator.next(),
                    arguments,
                    returnType
            );
        }

        @Override
        public Node visit(MethodSignature node) {
            List<Parameter> parameters = node.getParameters().stream()
                    .map(n -> (Parameter) n.accept(this))
                    .collect(Collectors.toList());

            TypeSpec returnType = (TypeSpec) node.getReturnType().accept(this);

            if (parameters.equals(node.getParameters()) && returnType.equals(node.getReturnType())) {
                return node;
            }

            return new MethodSignature(
                    nodeIdGenerator.next(),
                    node.getVisibility(),
                    node.getName(),
                    node.getTypeParameters(),
                    parameters,
                    returnType
            );
        }

        @Override
        public Node visit(Parameter node) {
            TypeSpec type = (TypeSpec) node.getType().accept(this);

            if (type.equals(node.getType())) {
                return node;
            }

            return new Parameter(
                    nodeIdGenerator.next(),
                    node.getName(),
                    type
            );
        }

        @Override
        public Node visit(NewAssignmentValue node) {
            TypeSpec type = (TypeSpec) node.getType().accept(this);
            Value value = (Value) node.getValue().accept(this);

            if (type.equals(node.getType()) && value.equals(node.getValue())) {
                return node;
            }

            return new NewAssignmentValue(
                    nodeIdGenerator.next(),
                    node.getName(),
                    type,
                    value,
                    node.getMode()
            );
        }

        @Override
        public Node visit(MethodCallValue node) {
            List<TypeSpec> typeArguments = node.getTypeArguments().stream()
                    .map(n -> (TypeSpec) n.accept(this))
                    .collect(Collectors.toList());

            List<Value> methodArguments = node.getMethodArguments().stream()
                    .map(n -> (Value) n.accept(this))
                    .collect(Collectors.toList());

            if (typeArguments.equals(node.getTypeArguments()) && methodArguments.equals(node.getMethodArguments())) {
                return node;
            }

            return new MethodCallValue(
                    nodeIdGenerator.next(),
                    node.getMethodReference(),
                    typeArguments,
                    methodArguments
            );
        }

        @Override
        public Node visit(FunctionValue node) {
            List<Parameter> parameters = node.getParameters().stream()
                    .map(n -> (Parameter) n.accept(this))
                    .collect(Collectors.toList());

            TypeSpec returnType = (TypeSpec) node.getReturnType().accept(this);

            List<Statement> statements = node.getStatements().stream()
                    .map(n -> (Statement) n.accept(this))
                    .collect(Collectors.toList());

            if (parameters.equals(node.getParameters())
                    && returnType.equals(node.getReturnType())
                    && statements.equals(node.getStatements())) {
                return node;
            }

            return new FunctionValue(
                    nodeIdGenerator.next(),
                    node.getTypeParameters(),
                    parameters,
                    returnType,
                    statements
            );
        }

        @Override
        public Node visit(Statement node) {
            Value value = (Value) node.getValue().accept(this);

            if (value.equals(node.getValue())) {
                return node;
            }

            return new Statement(
                    nodeIdGenerator.next(),
                    value,
                    node.isLast()
            );
        }

        @Override
        public Node visit(Method node) {
            MethodSignature methodSignature = (MethodSignature) node.getSignature().accept(this);

            List<Statement> statements = node.getStatements().stream()
                    .map(n -> (Statement) n.accept(this))
                    .collect(Collectors.toList());

            if (methodSignature.equals(node.getSignature()) && statements.equals(node.getStatements())) {
                return node;
            }

            return new Method(
                    nodeIdGenerator.next(),
                    methodSignature,
                    statements
            );
        }

        @Override
        public Node visit(Attribute node) {
            TypeSpec type = (TypeSpec) node.getType().accept(this);
            Value value = (Value) node.getValue().accept(this);

            if (type.equals(node.getType()) && value.equals(node.getValue())) {
                return node;
            }

            return new Attribute(
                    nodeIdGenerator.next(),
                    node.getName(),
                    type,
                    value,
                    node.getMode()
            );
        }
    }
}
