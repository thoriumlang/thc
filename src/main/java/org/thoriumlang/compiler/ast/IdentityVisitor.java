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
    public Visitable visitRoot(Type type) {
        return new Root((Type) type.accept(this));
    }

    @Override
    public Visitable visitType(String name, List<MethodSignature> methods) {
        return new Type(
                name,
                methods.stream()
                        .map(m -> (MethodSignature) m.accept(this))
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
    public Visitable visitTypeSingle(String type) {
        return new TypeSpecSingle(type);
    }

    @Override
    public Visitable visitMethodSignature(Visibility visibility, String name, List<Parameter> parameters,
            TypeSpec returnType) {
        return new MethodSignature(
                visibility,
                name,
                parameters.stream()
                        .map(p -> (Parameter) p.accept(this))
                        .collect(Collectors.toList()),
                (TypeSpec) returnType.accept(this)
        );
    }

    @Override
    public Visitable visitParameter(String name, TypeSpec type) {
        return new Parameter(name, (TypeSpec) type.accept(this));
    }
}
