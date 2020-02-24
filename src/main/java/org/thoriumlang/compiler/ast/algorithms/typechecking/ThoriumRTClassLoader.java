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
package org.thoriumlang.compiler.ast.algorithms.typechecking;

import org.thoriumlang.compiler.ast.nodes.Node;
import org.thoriumlang.compiler.symbols.Symbol;
import org.thoriumlang.compiler.symbols.ThoriumLibType;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

// TODO this is a mock, we need an actual implementation
public class ThoriumRTClassLoader implements TypeLoader {
    private final List<String> knownTypes = Arrays.asList(
            "org.thoriumlang.Object",
            "org.thoriumlang.None"
    );

    @Override
    public Optional<Symbol> load(String name, Node node) {
        if (knownTypes.contains(name)) {
            return Optional.of(new ThoriumLibType(node, name));
        }

        return Optional.empty();
    }
}
