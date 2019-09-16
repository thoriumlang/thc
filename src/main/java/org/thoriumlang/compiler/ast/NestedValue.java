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

import java.util.Objects;

public class NestedValue implements Value {
    private final NodeId nodeId;
    private final Value outer;
    private final Value inner;

    public NestedValue(NodeId nodeId, Value outer, Value inner) {
        if (nodeId == null) {
            throw new NullPointerException("nodeId cannot be null");
        }
        if (outer == null) {
            throw new NullPointerException("outer cannot be null");
        }
        if (inner == null) {
            throw new NullPointerException("inner cannot be null");
        }
        this.nodeId = nodeId;
        this.outer = outer;
        this.inner = inner;
    }

    @Override
    public <T> T accept(Visitor<? extends T> visitor) {
        return visitor.visitNestedValue(nodeId, outer, inner);
    }

    @Override
    public String toString() {
        return String.format(
                "%s.%s",
                outer.toString(),
                inner.toString()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NestedValue that = (NestedValue) o;
        return nodeId.equals(that.nodeId) &&
                outer.equals(that.outer) &&
                inner.equals(that.inner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeId, outer, inner);
    }
}
