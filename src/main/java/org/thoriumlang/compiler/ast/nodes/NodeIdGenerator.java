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

import java.util.concurrent.atomic.AtomicLong;

public class NodeIdGenerator { // TODO make sure we have only one...
    private final AtomicLong longGenerator;

    public NodeIdGenerator() {
        this.longGenerator = new AtomicLong();
    }

    public NodeId next() {
        return new NodeId(longGenerator.incrementAndGet());
    }
}
