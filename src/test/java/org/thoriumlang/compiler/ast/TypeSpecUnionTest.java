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

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

class TypeSpecUnionTest {
    @Test
    void constructor() {
        try {
            new TypeSpecUnion(null);
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("types cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void accept() {
        List<TypeSpec> typeSpecs = Collections.singletonList(new TypeSpecSingle("type"));
        Assertions.assertThat(
                new TypeSpecUnion(typeSpecs).accept(new BaseVisitor<List<TypeSpec>>() {
                    @Override
                    public List<TypeSpec> visitTypeUnion(List<TypeSpec> types) {
                        return types;
                    }
                })
        ).isEqualTo(typeSpecs);
    }

    @Test
    void _toString() {
        Assertions.assertThat(
                new TypeSpecUnion(
                        Collections.singletonList(new TypeSpecSingle("type"))
                ).toString()
        ).isEqualTo("u:[type]");
    }
}
