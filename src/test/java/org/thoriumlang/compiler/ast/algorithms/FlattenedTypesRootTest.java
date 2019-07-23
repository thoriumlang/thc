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
import org.thoriumlang.compiler.ast.MethodSignature;
import org.thoriumlang.compiler.ast.Root;
import org.thoriumlang.compiler.ast.Type;
import org.thoriumlang.compiler.ast.TypeSpecIntersection;
import org.thoriumlang.compiler.ast.TypeSpecSimple;
import org.thoriumlang.compiler.ast.Visibility;

import java.util.Arrays;
import java.util.Collections;

class FlattenedTypesRootTest {
    @Test
    void accept() {
        Root root = new Root(
                "namespace",
                Collections.emptyList(),
                new Type(
                        "type",
                        Collections.emptyList(),
                        TypeSpecSimple.OBJECT,
                        Collections.singletonList(
                                new MethodSignature(
                                        Visibility.PRIVATE,
                                        "name",
                                        Collections.emptyList(),
                                        Collections.emptyList(),
                                        new TypeSpecIntersection(
                                                Arrays.asList(
                                                        new TypeSpecSimple("TA", Collections.emptyList()),
                                                        new TypeSpecIntersection(
                                                                Arrays.asList(
                                                                        new TypeSpecSimple(
                                                                                "TB",
                                                                                Collections.emptyList()
                                                                        ),
                                                                        new TypeSpecSimple(
                                                                                "TC",
                                                                                Collections.emptyList()
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
        Root expectedRoot = new Root(
                "namespace",
                Collections.emptyList(),
                new Type(
                        "type",
                        Collections.emptyList(),
                        TypeSpecSimple.OBJECT,
                        Collections.singletonList(
                                new MethodSignature(
                                        Visibility.PRIVATE,
                                        "name",
                                        Collections.emptyList(),
                                        Collections.emptyList(),
                                        new TypeSpecIntersection(
                                                Arrays.asList(
                                                        new TypeSpecSimple("TA", Collections.emptyList()),
                                                        new TypeSpecSimple("TB", Collections.emptyList()),
                                                        new TypeSpecSimple("TC", Collections.emptyList())
                                                )
                                        )
                                )
                        )
                )
        );

        Assertions.assertThat(new FlattenedTypesRoot(root).root())
                .isEqualTo(expectedRoot);
    }
}
