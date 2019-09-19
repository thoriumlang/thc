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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Context {
    private final Node node;
    private final Map<java.lang.Class, Object> map;

    public Context(Node node) {
        this.node = node;
        this.map = new HashMap<>();
    }

    public Node getNode() {
        return node;
    }

    public <T> Context put(java.lang.Class<T> key, T value) {
        map.put(key, value);
        return this;
    }

    @SuppressWarnings("unchecked") // we're sure the type will be the expected one thanks to put(Class<T>, T)
    public <T> Optional<T> get(java.lang.Class<T> key) {
        return Optional.ofNullable((T) map.get(key));
    }

    @SuppressWarnings({"OptionalGetWithoutIsPresent", "squid:S3655"}) // we are sure the optional will not be empty
    public <T> T putIfAbsentAndGet(java.lang.Class<T> key, String value) {
        map.putIfAbsent(key, value);
        return get(key).get();
    }

    public <T> boolean contains(java.lang.Class<T> key) {
        return map.containsKey(key);
    }
}
