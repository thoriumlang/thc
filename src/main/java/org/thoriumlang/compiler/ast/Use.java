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

public class Use implements Node {
    private final String from;
    private final String to;

    public Use(String from, String to) {
        if (from == null) {
            throw new NullPointerException("from cannot be null");
        }
        if (to == null) {
            throw new NullPointerException("to cannot be null");
        }
        this.from = from;
        this.to = to;
    }

    public Use(String from) {
        if (from == null) {
            throw new NullPointerException("from cannot be null");
        }
        this.from = from;
        this.to = from.substring(from.lastIndexOf('.') + 1);
    }

    @Override
    public <T> T accept(Visitor<? extends T> visitor) {
        return visitor.visitUse(from, to);
    }

    @Override
    public String toString() {
        return String.format("USE %s : %s", from, to);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Use use = (Use) o;
        return from.equals(use.from) &&
                to.equals(use.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }
}
