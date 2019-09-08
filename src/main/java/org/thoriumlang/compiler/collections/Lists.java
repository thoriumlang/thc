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
package org.thoriumlang.compiler.collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Lists {
    private Lists() {
        // nothing
    }

    public static <T> Optional<T> last(List<T> list) {
        if (list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(list.get(list.size() - 1));
    }

    public static <T> List<T> withoutLast(List<T> list) {
        if (list.size() < 2) {
            return Collections.emptyList();
        }
        ArrayList<T> ret = new ArrayList<>(list);
        ret.remove(list.size() - 1);
        return ret;
    }

    public static <T> List<T> append(List<T> list, T element) {
        if (element == null) {
            return list;
        }
        ArrayList<T> ret = new ArrayList<>(list.size() + 1);
        ret.addAll(list);
        ret.add(element);
        return ret;
    }
}
