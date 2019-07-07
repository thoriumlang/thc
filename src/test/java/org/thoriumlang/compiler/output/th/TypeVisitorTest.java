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
import org.thoriumlang.compiler.ast.MethodSignature;
import org.thoriumlang.compiler.ast.TypeSpecSingle;
import org.thoriumlang.compiler.ast.Visibility;

import java.util.Collections;

class TypeVisitorTest {
    @Test
    void visitEmptyType() {
        Assertions.assertThat(
                new TypeVisitor(new DefaultConfiguration())
                        .visitType("name", Collections.emptyList())
                        .toString())
                .isEqualTo("type name {}");
    }

    @Test
    void visitNonEmptyType() {
        Assertions.assertThat(
                new TypeVisitor(new DefaultConfiguration())
                        .visitType("name", Collections
                                .singletonList(
                                        new MethodSignature(Visibility.PRIVATE, "m", new TypeSpecSingle("type"))))
                        .toString())
                .isEqualTo("type name {\n  private m(): type;\n}");
    }
}
