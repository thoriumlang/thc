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
package org.thoriumlang.compiler.symbols;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Name {
    private final String fqName;

    public Name(String fqName) {
        this.fqName = Objects.requireNonNull(fqName, "fqName cannot be null");
    }

    /**
     * Creates a new Name instance, prefixing name with packageName in case it's not fully qualified
     *
     * @param name        the name
     * @param packageName the package name
     */
    public Name(String name, String packageName) {
        this(
                Objects.requireNonNull(name, "name cannot be null").contains(".")
                        ? name
                        : Objects.requireNonNull(packageName, "packageName cannot be null") + "." + name
        );
    }

    public String getSimpleName() {
        List<String> parts = getParts();
        return parts.get(parts.size() - 1);
    }

    public String getFullName() {
        return fqName;
    }

    public List<String> getParts() {
        return Arrays.asList(fqName.split("\\."));
    }

    public boolean isQualified() {
        return getParts().size() > 1;
    }

    @Override
    public String toString() {
        return fqName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Name that = (Name) o;
        return fqName.equals(that.fqName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fqName);
    }
}
