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
package org.thoriumlang.compiler.ast.context;

import org.thoriumlang.compiler.ast.nodes.Node;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
        return put(new Key(key, type), value);
    }

    public <T> Context put(java.lang.Class<T> type, T value) {
        if (type == null) {
            throw new IllegalArgumentException(TYPE_CANNOT_BE_NULL);
        }
        if (value == null) {
            throw new IllegalArgumentException(VALUE_CANNOT_BE_NULL);
        }
        return put(new Key(null, type), value);
    }

    private Context put(Key key, Object value) {
        map.put(key, value);
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
        return (Optional<T>) get(new Key(key, type));
    }

    @SuppressWarnings("unchecked") // we're sure the type will be the expected one thanks to put(Class<T>, T)
    public <T> Optional<T> get(java.lang.Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException(TYPE_CANNOT_BE_NULL);
        }
        return (Optional<T>) get(new Key(null, type));
    }

    public Optional<Object> get(Key key) {
        if (key == null) {
            throw new IllegalArgumentException(KEY_CANNOT_BE_NULL);
        }
        return Optional.ofNullable(map.get(key));
    }

    public <T> T require(java.lang.Class<T> type) {
        return get(type)
                .orElseThrow(() -> new IllegalStateException(String.format("no %s found", type.getName())));
    }

    public <T> T require(String key, java.lang.Class<T> type) {
        return get(key, type)
                .orElseThrow(() -> new IllegalStateException(String.format("no %s(%s) found", key, type.getName())));
    }


    @SuppressWarnings("unchecked") // we're sure the type will be the expected one thanks to put(Class<T>, T)
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
        return (T) putIfAbsentAndGet(new Key(key, type), value);
    }

    @SuppressWarnings("unchecked") // we're sure the type will be the expected one thanks to put(Class<T>, T)
    public <T> T putIfAbsentAndGet(java.lang.Class<T> type, T value) {
        if (type == null) {
            throw new IllegalArgumentException(TYPE_CANNOT_BE_NULL);
        }
        if (value == null) {
            throw new IllegalArgumentException(VALUE_CANNOT_BE_NULL);
        }
        return (T) putIfAbsentAndGet(new Key(null, type), value);
    }

    private Object putIfAbsentAndGet(Key key, Object value) {
        map.putIfAbsent(key, value);
        return get(key).orElseThrow(() -> new IllegalStateException("key " + key + " not found"));
    }

    public <T> boolean contains(String key, java.lang.Class<T> type) {
        if (key == null) {
            throw new IllegalArgumentException(KEY_CANNOT_BE_NULL);
        }
        if (type == null) {
            throw new IllegalArgumentException(TYPE_CANNOT_BE_NULL);
        }
        return contains(new Key(key, type));
    }

    public <T> boolean contains(java.lang.Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException(TYPE_CANNOT_BE_NULL);
        }
        return contains(new Key(null, type));
    }

    private boolean contains(Key key) {
        return map.containsKey(key);
    }

    public Context putAll(Context other) { // TODO should deep copy
        map.putAll(other.map);
        return this;
    }

    public <T> Context copyFrom(java.lang.Class<T> type, Node source) { // TODO should deep copy
        source.getContext().get(type).ifPresent(
                v -> put(type, v)
        );
        return this;
    }

    public List<Key> keys() {
        return map.keySet().stream()
                .sorted(Comparator.comparing(Key::toString))
                .collect(Collectors.toList());
    }

    public static class Key {
        private final String name;
        private final java.lang.Class<?> type;

        private Key(String name, java.lang.Class<?> type) {
            this.name = name;
            this.type = type;
        }

        @Override
        public String toString() {
            return String.format("%s(%s)", name, type.getName());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Key key = (Key) o;
            return Objects.equals(name, key.name) &&
                    type.equals(key.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, type);
        }
    }
}
