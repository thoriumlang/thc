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

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.ast.TypeSpecSingle;

import java.util.Arrays;
import java.util.Collections;

class TypeSpecVisitorTest {
    @Test
    void visitTypeSingle() {
        Assertions.assertThat(new TypeSpecVisitor().visitTypeSingle(
                "type",
                Arrays.asList(
                        new TypeSpecSingle("A", Collections.singletonList(
                                new TypeSpecSingle("B", Collections.emptyList())
                        )),
                        new TypeSpecSingle("C", Collections.emptyList())
                )
        ))
                .isEqualTo("type[A[B], C]");
    }

    @Test
    void visitTypeUnion() {
        Assertions.assertThat(new TypeSpecVisitor().visitTypeUnion(Arrays.asList(
                new TypeSpecSingle("TA", Collections.emptyList()),
                new TypeSpecSingle("TB", Collections.emptyList())
        )))
                .isEqualTo("(TA & TB)");
    }

    @Test
    void visitTypeIntersection() {
        Assertions.assertThat(new TypeSpecVisitor().visitTypeIntersection(Arrays.asList(
                new TypeSpecSingle("TA", Collections.emptyList()),
                new TypeSpecSingle("TB", Collections.emptyList())
        )))
                .isEqualTo("(TA | TB)");
    }
}
