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

public interface Visitor<T> {
    // will have a visitRoot(Class, List<Use>)
    T visitRoot(Type type, List<Use> uses);

    T visitUse(String from, String to);

    T visitType(String name, List<MethodSignature> methods);

    T visitTypeIntersection(List<TypeSpec> types);

    T visitTypeUnion(List<TypeSpec> types);

    T visitTypeSingle(String type);

    T visitMethodSignature(Visibility visibility, String name, List<Parameter> parameters, TypeSpec returnType);

    T visitParameter(String name, TypeSpec type);
}
