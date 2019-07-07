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

public class Parameter implements Visitable {
    private final String name;
    private final TypeSpec type;

    public Parameter(String name, TypeSpec type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public <T> T accept(Visitor<? extends T> visitor) {
        return visitor.visitParameter(name, type);
    }

    @Override
    public String toString() {
        return name + ": " + type;
    }
}
