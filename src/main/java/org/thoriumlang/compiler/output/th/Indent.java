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
package org.thoriumlang.compiler.output.th;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

class Indent implements Function<String, String> {
    public static final Indent INSTANCE = new Indent("  ");
    private final String level;

    Indent(String indent) {
        this.level = indent;
    }

    @Override
    public String apply(String string) {
        return Arrays.stream(string.split("\n"))
                .map(s -> level + s)
                .collect(Collectors.joining("\n"));
    }
}
