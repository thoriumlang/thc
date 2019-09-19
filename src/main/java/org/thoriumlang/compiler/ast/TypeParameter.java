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

import org.thoriumlang.compiler.ast.visitor.Visitor;

import java.util.Objects;

public class TypeParameter implements Node {
    private final NodeId nodeId;
    private final String name;
    private final Context context;

    public TypeParameter(NodeId nodeId, String name) {
        if (nodeId == null) {
            throw new NullPointerException("nodeId cannot be null");
        }
        if (name == null) {
            throw new NullPointerException("name cannot be null");
        }
        this.nodeId = nodeId;
        this.name = name;
        this.context = new Context(this);
    }

    @Override
    public <T> T accept(Visitor<? extends T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public String toString() {
        return name;
    }

    public NodeId getNodeId() {
        return nodeId;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TypeParameter that = (TypeParameter) o;
        return nodeId.equals(that.nodeId) &&
                name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeId, name);
    }
}
