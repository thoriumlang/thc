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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class TypeSpecFunctionTest {
    @Test
    void constructor_arguments() {
        try {
            new TypeSpecFunction(null, TypeSpecSimple.NONE);
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("arguments cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void constructor_returnType() {
        try {
            new TypeSpecFunction(Collections.singletonList(TypeSpecSimple.NONE), null);
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("returnType cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void accept() {
        Assertions.assertThat(
                new TypeSpecFunction(
                        Collections.singletonList(new TypeSpecSimple("A", Collections.emptyList())),
                        new TypeSpecSimple("B", Collections.emptyList())
                ).accept(new BaseVisitor<String>() {
                    @Override
                    public String visitTypeFunction(List<TypeSpec> arguments, TypeSpec returnType) {
                        return String.format(
                                "(%s):%s",
                                arguments.stream()
                                        .map(TypeSpec::toString)
                                        .collect(Collectors.joining(",")),
                                returnType.toString()
                        );
                    }
                })
        ).isEqualTo("(A[]):B[]");
    }

    @Test
    void _toString() {
        Assertions.assertThat(
                new TypeSpecSimple(
                        "type",
                        Arrays.asList(
                                new TypeSpecSimple("A", Collections.emptyList()),
                                new TypeSpecSimple("B", Collections.emptyList())
                        )
                ).toString()
        ).isEqualTo("type[A[], B[]]");
    }
}
