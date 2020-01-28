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
package org.thoriumlang.compiler.ast.algorithms.typeflattening;

import org.thoriumlang.compiler.ast.context.Context;
import org.thoriumlang.compiler.ast.nodes.Node;
import org.thoriumlang.compiler.ast.nodes.NodeId;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.ast.visitor.Visitor;

public class TypeFlattenedRoot extends Node {
    private final NodeIdGenerator nodeIdGenerator;
    private final Root root;
    private final Context context;
    private Root flattenedTypesRootCache;

    public TypeFlattenedRoot(NodeIdGenerator nodeIdGenerator, Root root) {
        super(root.getNodeId());
        this.nodeIdGenerator = nodeIdGenerator;
        this.root = root;
        this.context = new Context(this);
    }

    private Root flattenedTypesRoot() {
        if (flattenedTypesRootCache == null) {
            flattenedTypesRootCache = (Root) root.accept(new TypeFlatteningVisitor(nodeIdGenerator));
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

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public NodeId getNodeId() {
        return root.getNodeId();
    }
}
