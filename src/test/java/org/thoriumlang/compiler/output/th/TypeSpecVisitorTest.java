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

class TypeSpecVisitorTest {
    @Test
    void visitTypeSingle() {
        Assertions.assertThat(new TypeSpecVisitor(new DefaultConfiguration()).visitTypeSingle("type"))
                .isEqualTo("type");
    }

    @Test
    void visitTypeUnion() {
        Assertions.assertThat(new TypeSpecVisitor(new DefaultConfiguration()).visitTypeUnion(Arrays.asList(
                new TypeSpecSingle("TA"), new TypeSpecSingle("TB")
        )))
                .isEqualTo("(TA & TB)");
    }

    @Test
    void visitTypeIntersection() {
        Assertions.assertThat(new TypeSpecVisitor(new DefaultConfiguration()).visitTypeIntersection(Arrays.asList(
                new TypeSpecSingle("TA"), new TypeSpecSingle("TB")
        )))
                .isEqualTo("(TA | TB)");
    }

    @Test
    void visitTypeOptionalWithoutExpansion() {
        Assertions.assertThat(new TypeSpecVisitor(new Configuration() {
            @Override
            public boolean expandOptional() {
                return false;
            }
        }).visitTypeOptional(new TypeSpecSingle("TA")))
                .isEqualTo("(TA)?");
    }

    @Test
    void visitTypeOptionalWithExpansion() {
        Assertions.assertThat(new TypeSpecVisitor(new Configuration() {
            @Override
            public boolean expandOptional() {
                return true;
            }
        }).visitTypeOptional(new TypeSpecSingle("TA")))
                .isEqualTo("(TA | None)");
    }
}
