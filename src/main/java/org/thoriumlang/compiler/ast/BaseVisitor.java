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
import java.util.Map;

public abstract class BaseVisitor<T> implements Visitor<T> {
    @Override
    public T visitRoot(Type type) {
        return null;
    }

    @Override
    public T visitType(String name, List<MethodSignature> methods) {
        return null;
    }

    @Override
    public T visitMethodSignature(Visibility visibility, String name, Map<String, String> parameters, TypeSpec returnType) {
        return null;
    }

    @Override
    public T visitTypeIntersection(List<TypeSpec> types) {
        return null;
    }

    @Override
    public T visitTypeOptional(TypeSpec typeSpec) {
        return null;
    }

    @Override
    public T visitTypeUnion(List<TypeSpec> types) {
        return null;
    }

    @Override
    public T visitTypeSingle(String type) {
        return null;
    }
}
