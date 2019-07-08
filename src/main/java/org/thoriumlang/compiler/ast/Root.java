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
import java.util.Objects;
import java.util.stream.Collectors;

public class Root implements Visitable {
    private final List<Use> uses;
    private final Type type;

    public Root(List<Use> uses, Type type) {
        this.uses = uses;
        this.type = type;
    }

    @Override
    public <T> T accept(Visitor<? extends T> visitor) {
        return visitor.visitRoot(type, uses);
    }

    @Override
    public String toString() {
        String use = uses.stream()
                .map(Use::toString)
                .collect(Collectors.joining("\n"));

        return String.format("%s%s",
                use.isEmpty() ? "" : String.format("%s%n", use),
                type.toString()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Root root = (Root) o;
        return Objects.equals(type, root.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }
}
