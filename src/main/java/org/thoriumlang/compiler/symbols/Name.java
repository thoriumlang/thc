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

import org.thoriumlang.compiler.collections.Lists;
import org.thoriumlang.compiler.helpers.Strings;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Name {
    private final String fqName;
    private final boolean isMethod;
    private final boolean isQualified;
    private final String simpleName;
    private final List<String> parts;
    private final String normalizedSimpleName;

    public Name(String fqName) {
        this.fqName = Objects.requireNonNull(fqName, "fqName cannot be null");
        this.isMethod = fqName.indexOf('(') > -1;

        this.parts = isMethod ? extractMethodParts(fqName) : extractParts(fqName);
        this.isQualified = parts.size() > 1;
        this.simpleName = Lists.last(parts).orElseThrow(() -> new IllegalStateException("no last part found"));

        List<String> normalizedParts = extractParts(isMethod ? normalizeMethodParameters(fqName) : fqName);
        this.normalizedSimpleName = Lists.last(normalizedParts).orElseThrow(() -> new IllegalStateException("no last part found"));
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

    private static List<String> extractMethodParts(String fqName) {
        String name = extractMethodName(fqName);
        String parameters = extractMethodParameters(fqName);

        List<String> parts = extractParts(name);
        String methodName = Lists.last(parts).orElseThrow(() -> new IllegalStateException("no last element"));

        return Lists.append(
                Lists.withoutLast(parts),
                // TODO shouldn't we have the type parameter as well? (see SymbolicNameDiscoveryVisitor as well)
                String.format("%s(%s)", methodName, parameters)
        );
    }

    private static String extractMethodName(String fqName) {
        return fqName.substring(
                0,
                Strings.indexOfFirst(fqName, "[", "(")
        );
    }

    private static String extractMethodParameters(String fqName) {
        return fqName.substring(fqName.indexOf('(') + 1, fqName.indexOf(')'));
    }

    private static List<String> extractParts(String fqNormalizedName) {
        return Arrays.asList(fqNormalizedName.split("\\."));
    }

    private static String normalizeMethodParameters(String fqName) {
        return String.format("%s(%s)",
                extractMethodName(fqName),
                Arrays.stream(extractMethodParameters(fqName).split(","))
                        .filter(s -> !s.isEmpty())
                        .map(p -> "_")
                        .collect(Collectors.joining(","))
        );
    }

    /**
     * For methods, return a normalized version of the simple name (see {@link #getSimpleName()}). For attributes,
     * this method returns the same value as {@link #getSimpleName()}.
     * <p>
     * The normalized method name is the method signature with all its types replaced by "_". For instance:
     * method(String[]) becomes method(_).
     * </p>
     *
     * @return the normalizes simple name of the symbol
     */
    public String getNormalizedSimpleName() {
        return normalizedSimpleName;
    }

    /**
     * @return the symbol's name without the package prefix.
     */
    public String getSimpleName() {
        return simpleName;
    }

    /**
     * @return the fully qualified symbol name.
     */
    public String getFullName() {
        return fqName;
    }

    /**
     * @return a list containing each part of a name, i.e. all the namespaces are split in separate element of the list.
     * The actual name (or full method signature) is the last element of that list.
     */
    public List<String> getParts() {
        return parts;
    }

    public boolean isQualified() {
        return isQualified;
    }

    public boolean isMethod() {
        return isMethod;
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
