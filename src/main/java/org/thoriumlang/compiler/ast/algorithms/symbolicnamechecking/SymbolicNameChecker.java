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
import org.thoriumlang.compiler.ast.nodes.Reference;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.ast.visitor.NodesMatchingVisitor;
import org.thoriumlang.compiler.ast.visitor.PredicateVisitor;
import org.thoriumlang.compiler.collections.Lists;

import java.util.List;
import java.util.stream.Collectors;

public class SymbolicNameChecker implements Algorithm {
    private final SymbolicNameDiscoveryVisitor visitorSkippingNodesAllowingForwardReference;
    private final SymbolicNameDiscoveryVisitor visitor;

    public SymbolicNameChecker() {
        visitorSkippingNodesAllowingForwardReference = new SymbolicNameDiscoveryVisitor(true);
        visitor = new SymbolicNameDiscoveryVisitor(false);
    }

    @Override
    public List<SemanticError> walk(Root root) {
        List<SemanticError> firstPassErrors = root.accept(visitorSkippingNodesAllowingForwardReference);

        // now we have to do a second pass on the Reference nodes that allow forward reference (we discovered the
        // potential missing targets from the first pass)
        List<SemanticError> secondPassErrors = root
                .accept(
                        new NodesMatchingVisitor(n -> n.accept(new PredicateVisitor(false) {
                            @Override
                            public Boolean visit(Reference node) {
                                return node.allowForwardReference();
                            }
                        }))
                )
                .stream()
                .map(n -> n.accept(visitor))
                .flatMap(List::stream)
                .collect(Collectors.toList());

        return Lists.merge(firstPassErrors, secondPassErrors);
    }
}
