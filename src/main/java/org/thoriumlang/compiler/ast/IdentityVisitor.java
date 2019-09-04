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

public abstract class IdentityVisitor implements Visitor<Visitable> {
    @Override
    public Visitable visitRoot(String namespace, Type type, List<Use> uses) {
        return new Root(
                namespace,
                uses.stream()
                        .map(u -> (Use) u.accept(this))
                        .collect(Collectors.toList()),
                (Type) type.accept(this)
        );
    }

    @Override
    public Visitable visitRoot(String namespace, Class clazz, List<Use> uses) {
        return new Root(
                namespace,
                uses.stream()
                        .map(u -> (Use) u.accept(this))
                        .collect(Collectors.toList()),
                (Class) clazz.accept(this)
        );
    }

    @Override
    public Visitable visitUse(String from, String to) {
        return new Use(
                from, to
        );
    }

    @Override
    public Visitable visitType(Visibility visibility, String name, List<TypeParameter> typeParameters,
            TypeSpec superType, List<MethodSignature> methods) {
        return new Type(
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
    public Visitable visitClass(Visibility visibility, String name, List<TypeParameter> typeParameters,
            TypeSpec superType, List<Method> methods, List<Attribute> attributes) {
        return new Class(
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
    public Visitable visitTypeIntersection(List<TypeSpec> types) {
        return new TypeSpecIntersection(
                types.stream()
                        .map(t -> (TypeSpec) t.accept(this))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Visitable visitTypeUnion(List<TypeSpec> types) {
        return new TypeSpecUnion(
                types.stream()
                        .map(t -> (TypeSpec) t.accept(this))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Visitable visitTypeSingle(String type, List<TypeSpec> arguments) {
        return new TypeSpecSimple(
                type,
                arguments.stream()
                        .map(a -> (TypeSpec) a.accept(this))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Visitable visitMethodSignature(Visibility visibility, String name, List<TypeParameter> typeParameters,
            List<Parameter> parameters, TypeSpec returnType) {
        return new MethodSignature(
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
    public Visitable visitParameter(String name, TypeSpec type) {
        return new Parameter(
                name,
                (TypeSpec) type.accept(this)
        );
    }

    @Override
    public Visitable visitTypeParameter(String name) {
        return new TypeParameter(name);
    }

    @Override
    public Visitable visitStringValue(String value) {
        return new StringValue(value);
    }

    @Override
    public Visitable visitNumberValue(Integer value) {
        return new NumberValue(value);
    }

    @Override
    public Visitable visitBooleanValue(boolean value) {
        return value ? BooleanValue.TRUE : BooleanValue.FALSE;
    }

    @Override
    public Visitable visitNoneValue() {
        return NoneValue.INSTANCE;
    }

    @Override
    public Visitable visitIdentifierValue(String value) {
        return new IdentifierValue(value);
    }

    @Override
    public Visitable visitVarAssignmentValue(String identifier, TypeSpec type, Value value) {
        return new VarAssignmentValue(
                identifier,
                (TypeSpec) type.accept(this),
                (Value) value.accept(this)
        );
    }

    @Override
    public Visitable visitValAssignmentValue(String identifier, TypeSpec type, Value value) {
        return new ValAssignmentValue(
                identifier,
                (TypeSpec) type.accept(this),
                (Value) value.accept(this)
        );
    }

    @Override
    public Visitable visitIndirectAssignmentValue(Value indirectValue, String identifier, Value value) {
        return new IndirectAssignmentValue(
                (Value) indirectValue.accept(this),
                identifier,
                (Value) value.accept(this)
        );
    }

    @Override
    public Visitable visitMethodCallValue(String methodName, List<TypeSpec> typeArguments,
            List<Value> methodArguments) {
        return new MethodCallValue(
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
    public Visitable visitNestedValue(Value outer, Value inner) {
        return new NestedValue(
                (Value) outer.accept(this),
                (Value) inner.accept(this)
        );
    }

    @Override
    public Visitable visitStatement(Value value, boolean isLast) {
        return new Statement(
                (Value) value.accept(this),
                isLast
        );
    }

    @Override
    public Visitable visitMethod(MethodSignature signature, List<Statement> statements) {
        return new Method(
                (MethodSignature) signature.accept(this),
                statements.stream()
                        .map(s -> (Statement) s.accept(this))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Visitable visitVarAttribute(String identifier, TypeSpec type, Value value) {
        return new VarAttribute(
                identifier,
                (TypeSpec) type.accept(this),
                (Value) value.accept(this)
        );
    }

    @Override
    public Visitable visitValAttribute(String identifier, TypeSpec type, Value value) {
        return new ValAttribute(
                identifier,
                (TypeSpec) type.accept(this),
                (Value) value.accept(this)
        );
    }
}
