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
import java.util.Objects;
import java.util.Optional;

public class Context {
    private static final String KEY_CANNOT_BE_NULL = "key cannot be null";
    private static final String TYPE_CANNOT_BE_NULL = "type cannot be null";
    private static final String VALUE_CANNOT_BE_NULL = "value cannot be null";
    private final Node node;
    private final Map<Key, Object> map;

    public Context(Node node) {
        this.node = node;
        this.map = new HashMap<>();
    }

    public Node getNode() {
        return node;
    }

    public <T> Context put(String key, java.lang.Class<T> type, T value) {
        if (key == null) {
            throw new IllegalArgumentException(KEY_CANNOT_BE_NULL);
        }
        if (type == null) {
            throw new IllegalArgumentException(TYPE_CANNOT_BE_NULL);
        }
        if (value == null) {
            throw new IllegalArgumentException(VALUE_CANNOT_BE_NULL);
        }
        map.put(new Key(key, type), value);
        return this;
    }

    @SuppressWarnings("unchecked") // we're sure the type will be the expected one thanks to put(Class<T>, T)
    public <T> Optional<T> get(String key, java.lang.Class<T> type) {
        if (key == null) {
            throw new IllegalArgumentException(KEY_CANNOT_BE_NULL);
        }
        if (type == null) {
            throw new IllegalArgumentException(TYPE_CANNOT_BE_NULL);
        }
        return Optional.ofNullable((T) map.get(new Key(key, type)));
    }

    @SuppressWarnings({"OptionalGetWithoutIsPresent", "squid:S3655"}) // we are sure the optional will not be empty
    public <T> T putIfAbsentAndGet(String key, java.lang.Class<T> type, T value) {
        if (key == null) {
            throw new IllegalArgumentException(KEY_CANNOT_BE_NULL);
        }
        if (type == null) {
            throw new IllegalArgumentException(TYPE_CANNOT_BE_NULL);
        }
        if (value == null) {
            throw new IllegalArgumentException(VALUE_CANNOT_BE_NULL);
        }
        map.putIfAbsent(new Key(key, type), value);
        return get(key, type).get();
    }

    public <T> boolean contains(String key, java.lang.Class<T> type) {
        if (key == null) {
            throw new IllegalArgumentException(KEY_CANNOT_BE_NULL);
        }
        if (type == null) {
            throw new IllegalArgumentException(TYPE_CANNOT_BE_NULL);
        }
        return map.containsKey(new Key(key, type));
    }

    private static class Key {
        private final String name;
        private final java.lang.Class type;

        private Key(String name, java.lang.Class type) {
            this.name = name;
            this.type = type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Key key1 = (Key) o;
            return name.equals(key1.name) &&
                    type.equals(key1.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, type);
        }
    }
}
