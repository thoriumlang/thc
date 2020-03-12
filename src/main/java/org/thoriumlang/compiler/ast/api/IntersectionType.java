/*
 * Copyright 2020 Christophe Pollet
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
package org.thoriumlang.compiler.ast.api;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class IntersectionType implements Type {
    private final List<Type> types;

    public IntersectionType(List<Type> types) {
        this.types = Objects.requireNonNull(types, "types cannot be null");
        this.types.sort(Comparator.comparing(Object::toString));
    }

    @Override
    public String getName() {
        return types.stream()
                .map(Type::getName)
                .sorted()
                .collect(Collectors.joining(" | ", "(", ")"));
    }

    @Override
    public Collection<Method> getMethods() {
        Iterator<Collection<Method>> listsIterator = types.stream()
                .map(Type::getMethods)
                .collect(Collectors.toList())
                .iterator();

        if (!listsIterator.hasNext()) {
            return Collections.emptyList();
        }

        Set<Method> commonMethods = new HashSet<>(listsIterator.next());

        while (listsIterator.hasNext() && !commonMethods.isEmpty()) {
            commonMethods.retainAll(listsIterator.next());
        }

        return commonMethods;
    }

    @Override
    public Optional<Method> findMethod(String name) {
        return Optional.empty();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IntersectionType that = (IntersectionType) o;
        return types.equals(that.types);
    }

    @Override
    public int hashCode() {
        return Objects.hash(types);
    }
}
