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
package org.thoriumlang.compiler.ast.algorithms;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.ast.TypeSpecIntersection;
import org.thoriumlang.compiler.ast.TypeSpecSingle;
import org.thoriumlang.compiler.ast.TypeSpecUnion;

import java.util.Arrays;

class FlatteningTypesVisitorTest {
    @Test
    void visitTypeSpecIntersection() {
        FlatteningTypesVisitor visitor = new FlatteningTypesVisitor();

        TypeSpecIntersection spec = new TypeSpecIntersection(
                Arrays.asList(
                        new TypeSpecSingle("TA"),
                        new TypeSpecIntersection(
                                Arrays.asList(new TypeSpecSingle("TB"), new TypeSpecSingle("TC"),
                                        new TypeSpecIntersection(
                                                Arrays.asList(new TypeSpecSingle("TD"), new TypeSpecSingle("TE"))
                                        ))
                        )
                )
        );

        Assertions.assertThat(spec.accept(visitor))
                .isEqualTo(new TypeSpecIntersection(
                        Arrays.asList(
                                new TypeSpecSingle("TA"),
                                new TypeSpecSingle("TB"),
                                new TypeSpecSingle("TC"),
                                new TypeSpecSingle("TD"),
                                new TypeSpecSingle("TE")
                        )
                ));
    }

    @Test
    void visitTypeSpecUnion() {
        FlatteningTypesVisitor visitor = new FlatteningTypesVisitor();

        TypeSpecUnion spec = new TypeSpecUnion(
                Arrays.asList(
                        new TypeSpecSingle("TA"),
                        new TypeSpecUnion(
                                Arrays.asList(new TypeSpecSingle("TB"), new TypeSpecSingle("TC"),
                                        new TypeSpecUnion(
                                                Arrays.asList(new TypeSpecSingle("TD"), new TypeSpecSingle("TE"))
                                        ))
                        )
                )
        );

        Assertions.assertThat(spec.accept(visitor))
                .isEqualTo(new TypeSpecUnion(
                        Arrays.asList(
                                new TypeSpecSingle("TA"),
                                new TypeSpecSingle("TB"),
                                new TypeSpecSingle("TC"),
                                new TypeSpecSingle("TD"),
                                new TypeSpecSingle("TE")
                        )
                ));
    }
}