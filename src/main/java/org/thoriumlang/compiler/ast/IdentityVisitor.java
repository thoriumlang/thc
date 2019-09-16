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
package org.thoriumlang.compiler.ast;

import java.util.List;
import java.util.stream.Collectors;

public abstract class IdentityVisitor implements Visitor<Node> {
    @Override
    public Node visitRoot(NodeId nodeId, String namespace, List<Use> uses, Type type) {
        return new Root(
                nodeId,
                namespace,
                uses.stream()
                        .map(u -> (Use) u.accept(this))
                        .collect(Collectors.toList()),
                (Type) type.accept(this)
        );
    }

    @Override
    public Node visitRoot(NodeId nodeId, String namespace, List<Use> uses, Class clazz) {
        return new Root(
                nodeId,
                namespace,
                uses.stream()
                        .map(u -> (Use) u.accept(this))
                        .collect(Collectors.toList()),
                (Class) clazz.accept(this)
        );
    }

    @Override
    public Node visitUse(NodeId nodeId, String from, String to) {
        return new Use(
                nodeId, from, to
        );
    }

    @Override
    public Node visitType(NodeId nodeId, Visibility visibility, String name, List<TypeParameter> typeParameters,
            TypeSpec superType, List<MethodSignature> methods) {
        return new Type(
                nodeId,
                visibility,
                name,
                typeParameters.stream()
                        .map(p -> (TypeParameter) p.accept(this))
                        .collect(Collectors.toList()),
                (TypeSpec) superType.accept(this),
                methods.stream()
                        .map(m -> (MethodSignature) m.accept(this))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Node visitClass(NodeId nodeId, Visibility visibility, String name, List<TypeParameter> typeParameters,
            TypeSpec superType, List<Method> methods, List<Attribute> attributes) {
        return new Class(
                nodeId,
                visibility,
                name,
                typeParameters.stream()
                        .map(p -> (TypeParameter) p.accept(this))
                        .collect(Collectors.toList()),
                (TypeSpec) superType.accept(this),
                methods.stream()
                        .map(m -> (Method) m.accept(this))
                        .collect(Collectors.toList()),
                attributes.stream()
                        .map(a -> (Attribute) a.accept(this))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Node visitTypeIntersection(NodeId nodeId, List<TypeSpec> types) {
        return new TypeSpecIntersection(
                nodeId,
                types.stream()
                        .map(t -> (TypeSpec) t.accept(this))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Node visitTypeUnion(NodeId nodeId, List<TypeSpec> types) {
        return new TypeSpecUnion(
                nodeId,
                types.stream()
                        .map(t -> (TypeSpec) t.accept(this))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Node visitTypeSingle(NodeId nodeId, String type, List<TypeSpec> arguments) {
        return new TypeSpecSimple(
                nodeId,
                type,
                arguments.stream()
                        .map(a -> (TypeSpec) a.accept(this))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Node visitTypeFunction(NodeId nodeId, List<TypeSpec> arguments, TypeSpec returnType) {
        return new TypeSpecFunction(
                nodeId,
                arguments.stream()
                        .map(t -> (TypeSpec) t.accept(this))
                        .collect(Collectors.toList()),
                (TypeSpec) returnType.accept(this)
        );
    }

    @Override
    public Node visitTypeInferred(NodeId nodeId) {
        return new TypeSpecInferred(nodeId);
    }

    @Override
    public Node visitMethodSignature(NodeId nodeId, Visibility visibility, String name,
            List<TypeParameter> typeParameters,
            List<Parameter> parameters, TypeSpec returnType) {
        return new MethodSignature(
                nodeId,
                visibility,
                name,
                typeParameters.stream()
                        .map(p -> (TypeParameter) p.accept(this))
                        .collect(Collectors.toList()),
                parameters.stream()
                        .map(p -> (Parameter) p.accept(this))
                        .collect(Collectors.toList()),
                (TypeSpec) returnType.accept(this)
        );
    }

    @Override
    public Node visitParameter(NodeId nodeId, String name, TypeSpec type) {
        return new Parameter(
                nodeId,
                name,
                (TypeSpec) type.accept(this)
        );
    }

    @Override
    public Node visitTypeParameter(NodeId nodeId, String name) {
        return new TypeParameter(nodeId, name);
    }

    @Override
    public Node visitStringValue(NodeId nodeId, String value) {
        return new StringValue(nodeId, value);
    }

    @Override
    public Node visitNumberValue(NodeId nodeId, String value) {
        return new NumberValue(nodeId, value);
    }

    @Override
    public Node visitBooleanValue(boolean value) {
        return value ? BooleanValue.TRUE : BooleanValue.FALSE;
    }

    @Override
    public Node visitNoneValue() {
        return NoneValue.INSTANCE;
    }

    @Override
    public Node visitIdentifierValue(NodeId nodeId, String value) {
        return new IdentifierValue(nodeId, value);
    }

    @Override
    public Node visitVarAssignmentValue(NodeId nodeId, String identifier, TypeSpec type, Value value) {
        return new VarAssignmentValue(
                nodeId,
                identifier,
                (TypeSpec) type.accept(this),
                (Value) value.accept(this)
        );
    }

    @Override
    public Node visitValAssignmentValue(NodeId nodeId, String identifier, TypeSpec type, Value value) {
        return new ValAssignmentValue(
                nodeId,
                identifier,
                (TypeSpec) type.accept(this),
                (Value) value.accept(this)
        );
    }

    @Override
    public Node visitIndirectAssignmentValue(NodeId nodeId, Value indirectValue, String identifier, Value value) {
        return new IndirectAssignmentValue(
                nodeId,
                (Value) indirectValue.accept(this),
                identifier,
                (Value) value.accept(this)
        );
    }

    @Override
    public Node visitMethodCallValue(NodeId nodeId, String methodName, List<TypeSpec> typeArguments,
            List<Value> methodArguments) {
        return new MethodCallValue(
                nodeId,
                methodName,
                typeArguments.stream()
                        .map(a -> (TypeSpec) a.accept(this))
                        .collect(Collectors.toList()),
                methodArguments.stream()
                        .map(v -> (Value) v.accept(this))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Node visitNestedValue(NodeId nodeId, Value outer, Value inner) {
        return new NestedValue(
                nodeId,
                (Value) outer.accept(this),
                (Value) inner.accept(this)
        );
    }

    @Override
    public Node visitFunctionValue(NodeId nodeId, List<TypeParameter> typeParameters, List<Parameter> parameters,
            TypeSpec returnType, List<Statement> statements) {
        return new FunctionValue(
                nodeId,
                typeParameters.stream()
                        .map(t -> (TypeParameter) t.accept(this))
                        .collect(Collectors.toList()),
                parameters.stream()
                        .map(p -> (Parameter) p.accept(this))
                        .collect(Collectors.toList()),
                (TypeSpec) returnType.accept(this),
                statements.stream()
                        .map(s -> (Statement) s.accept(this))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Node visitStatement(NodeId nodeId, Value value, boolean isLast) {
        return new Statement(
                nodeId,
                (Value) value.accept(this),
                isLast
        );
    }

    @Override
    public Node visitMethod(NodeId nodeId, MethodSignature signature, List<Statement> statements) {
        return new Method(
                nodeId,
                (MethodSignature) signature.accept(this),
                statements.stream()
                        .map(s -> (Statement) s.accept(this))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Node visitVarAttribute(NodeId nodeId, String identifier, TypeSpec type, Value value) {
        return new VarAttribute(
                nodeId,
                identifier,
                (TypeSpec) type.accept(this),
                (Value) value.accept(this)
        );
    }

    @Override
    public Node visitValAttribute(NodeId nodeId, String identifier, TypeSpec type, Value value) {
        return new ValAttribute(
                nodeId,
                identifier,
                (TypeSpec) type.accept(this),
                (Value) value.accept(this)
        );
    }
}
