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
package org.thoriumlang.compiler.ast.algorithms;

import org.thoriumlang.compiler.ast.NodeIdGenerator;
import org.thoriumlang.compiler.ast.Root;
import org.thoriumlang.compiler.ast.Node;
import org.thoriumlang.compiler.ast.Visitor;

public class FlattenedTypesRoot implements Node {
    private final NodeIdGenerator nodeIdGenerator;
    private final Root root;
    private Root flattenedTypesRootCache;

    public FlattenedTypesRoot(NodeIdGenerator nodeIdGenerator, Root root) {
        this.nodeIdGenerator=nodeIdGenerator;
        this.root = root;
    }

    private Root flattenedTypesRoot() {
        if (flattenedTypesRootCache == null) {
            flattenedTypesRootCache = (Root) root.accept(new FlatteningTypesVisitor(nodeIdGenerator));
        }
        return flattenedTypesRootCache;
    }

    public Root root() {
        return flattenedTypesRoot();
    }

    @Override
    public <T> T accept(Visitor<? extends T> visitor) {
        return flattenedTypesRoot().accept(visitor);
    }
}
