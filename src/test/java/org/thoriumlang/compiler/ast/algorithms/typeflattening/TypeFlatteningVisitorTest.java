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
package org.thoriumlang.compiler.ast.algorithms.typeflattening;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.ast.NodeIdGenerator;
import org.thoriumlang.compiler.ast.TypeSpecIntersection;
import org.thoriumlang.compiler.ast.TypeSpecSimple;
import org.thoriumlang.compiler.ast.TypeSpecUnion;

import java.util.Arrays;
import java.util.Collections;

class TypeFlatteningVisitorTest {
    private NodeIdGenerator nodeIdGenerator;

    @BeforeEach
    void setup() {
        this.nodeIdGenerator = new NodeIdGenerator();
    }

    @Test
    void visitTypeSpecIntersection() {
        TypeFlatteningVisitor visitor = new TypeFlatteningVisitor(nodeIdGenerator);

        TypeSpecIntersection spec = new TypeSpecIntersection(
                nodeIdGenerator.next(),
                Arrays.asList(
                        new TypeSpecSimple(nodeIdGenerator.next(), "TA", Collections.emptyList()),
                        new TypeSpecIntersection(
                                nodeIdGenerator.next(),
                                Arrays.asList(
                                        new TypeSpecSimple(nodeIdGenerator.next(), "TB", Collections.emptyList()),
                                        new TypeSpecSimple(nodeIdGenerator.next(), "TC", Collections.emptyList()),
                                        new TypeSpecIntersection(
                                                nodeIdGenerator.next(),
                                                Arrays.asList(
                                                        new TypeSpecSimple(
                                                                nodeIdGenerator.next(),
                                                                "TD",
                                                                Collections.emptyList()
                                                        ),
                                                        new TypeSpecSimple(
                                                                nodeIdGenerator.next(),
                                                                "TE",
                                                                Collections.emptyList()
                                                        )
                                                )
                                        ))
                        )
                )
        );

        Assertions.assertThat(spec.accept(visitor).toString())
                .isEqualTo(
                        new TypeSpecIntersection(
                                nodeIdGenerator.next(),
                                Arrays.asList(
                                        new TypeSpecSimple(nodeIdGenerator.next(), "TA", Collections.emptyList()),
                                        new TypeSpecSimple(nodeIdGenerator.next(), "TB", Collections.emptyList()),
                                        new TypeSpecSimple(nodeIdGenerator.next(), "TC", Collections.emptyList()),
                                        new TypeSpecSimple(nodeIdGenerator.next(), "TD", Collections.emptyList()),
                                        new TypeSpecSimple(nodeIdGenerator.next(), "TE", Collections.emptyList())
                                )
                        ).toString()
                );
    }

    @Test
    void visitTypeSpecUnion() {
        TypeFlatteningVisitor visitor = new TypeFlatteningVisitor(nodeIdGenerator);

        TypeSpecUnion spec = new TypeSpecUnion(
                nodeIdGenerator.next(),
                Arrays.asList(
                        new TypeSpecSimple(nodeIdGenerator.next(), "TA", Collections.emptyList()),
                        new TypeSpecUnion(
                                nodeIdGenerator.next(),
                                Arrays.asList(
                                        new TypeSpecSimple(nodeIdGenerator.next(), "TB", Collections.emptyList()),
                                        new TypeSpecSimple(nodeIdGenerator.next(), "TC", Collections.emptyList()),
                                        new TypeSpecUnion(
                                                nodeIdGenerator.next(),
                                                Arrays.asList(
                                                        new TypeSpecSimple(
                                                                nodeIdGenerator.next(),
                                                                "TD",
                                                                Collections.emptyList()
                                                        ),
                                                        new TypeSpecSimple(
                                                                nodeIdGenerator.next(),
                                                                "TE",
                                                                Collections.emptyList()
                                                        )
                                                )
                                        ))
                        )
                )
        );

        Assertions.assertThat(spec.accept(visitor).toString())
                .isEqualTo(
                        new TypeSpecUnion(
                                nodeIdGenerator.next(),
                                Arrays.asList(
                                        new TypeSpecSimple(nodeIdGenerator.next(), "TA", Collections.emptyList()),
                                        new TypeSpecSimple(nodeIdGenerator.next(), "TB", Collections.emptyList()),
                                        new TypeSpecSimple(nodeIdGenerator.next(), "TC", Collections.emptyList()),
                                        new TypeSpecSimple(nodeIdGenerator.next(), "TD", Collections.emptyList()),
                                        new TypeSpecSimple(nodeIdGenerator.next(), "TE", Collections.emptyList())
                                )
                        ).toString()
                );
    }
}
