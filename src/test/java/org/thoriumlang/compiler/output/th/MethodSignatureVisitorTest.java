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
import org.thoriumlang.compiler.ast.Parameter;
import org.thoriumlang.compiler.ast.TypeParameter;
import org.thoriumlang.compiler.ast.TypeSpecSimple;
import org.thoriumlang.compiler.ast.Visibility;
import org.thoriumlang.compiler.tree.BasePrintableWrapper;
import org.thoriumlang.compiler.tree.Node;

import java.util.Arrays;
import java.util.Collections;

class MethodSignatureVisitorTest {
    @Test
    void visitMethodSignature() {
        Assertions.assertThat(
                new MethodSignatureVisitor(
                        new Node<>(new BasePrintableWrapper() {
                        })
                )
                        .visitMethodSignature(
                                Visibility.PRIVATE,
                                "m",
                                Arrays.asList(
                                        new TypeParameter("T"),
                                        new TypeParameter("U")
                                ),
                                Arrays.asList(
                                        new Parameter("p1", new TypeSpecSimple(
                                                "type1",
                                                Collections.emptyList()
                                        )),
                                        new Parameter("p2", new TypeSpecSimple(
                                                "type2",
                                                Collections.emptyList()
                                        ))
                                ),
                                new TypeSpecSimple("type", Collections.emptyList()))
        )
                .isEqualTo("private m[T, U](p1: type1, p2: type2): type;");
    }
}
