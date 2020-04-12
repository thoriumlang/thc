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
package org.thoriumlang.compiler.ast.algorithms.symbolicnamechecking;

import org.thoriumlang.compiler.api.errors.SemanticError;
import org.thoriumlang.compiler.ast.algorithms.Algorithm;
import org.thoriumlang.compiler.ast.nodes.Root;

import java.util.List;

public class SymbolicNameChecker implements Algorithm {
    private final SymbolicNameDiscoveryVisitor symbolicNameDiscoveryVisitor;

    public SymbolicNameChecker() {
        symbolicNameDiscoveryVisitor = new SymbolicNameDiscoveryVisitor();
    }

    @Override
    public List<SemanticError> walk(Root root) {
        return root.accept(symbolicNameDiscoveryVisitor);
    }
}
