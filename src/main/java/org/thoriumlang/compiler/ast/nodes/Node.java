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
package org.thoriumlang.compiler.ast.nodes;

import org.thoriumlang.compiler.ast.context.Context;
import org.thoriumlang.compiler.ast.visitor.Visitor;

import java.util.Objects;

public abstract class Node {
    private final Context context;
    private final NodeId nodeId;

    protected Node(NodeId nodeId) {
        this.nodeId = Objects.requireNonNull(nodeId, "nodeId cannot be null");
        this.context = new Context(this);
    }

    public abstract <T> T accept(Visitor<? extends T> visitor);

    public Context getContext() {
        return context;
    }

    public NodeId getNodeId() {
        return nodeId;
    }
}
