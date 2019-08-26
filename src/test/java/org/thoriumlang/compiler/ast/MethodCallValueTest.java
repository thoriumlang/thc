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

class MethodCallValueTest {
    @Test
    void constructor_methodName() {
        try {
            new MethodCallValue(null, Collections.emptyList(), Collections.emptyList());
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("methodName cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void constructor_typeArguments() {
        try {
            new MethodCallValue("methodName", null, Collections.emptyList());
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("typeArguments cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void constructor_methodArguments() {
        try {
            new MethodCallValue("methodName", Collections.emptyList(), null);
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("methodArguments cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void accept() {
        Assertions.assertThat(
                new MethodCallValue(
                        "methodName",
                        Collections.singletonList(new TypeSpecSimple("T", Collections.emptyList())),
                        Collections.singletonList(NoneValue.INSTANCE)
                )
                        .accept(new BaseVisitor<String>() {
                            @Override
                            public String visitMethodCallValue(String methodName, List<TypeSpec> typeArguments,
                                    List<Value> methodArguments) {
                                return String.format(
                                        "%s:%s:%s",
                                        methodName,
                                        typeArguments,
                                        methodArguments
                                );
                            }
                        })
        ).isEqualTo("methodName:[T[]]:[none]");
    }

    @Test
    void _toString() {
        Assertions.assertThat(
                new MethodCallValue(
                        "methodName",
                        Collections.singletonList(new TypeSpecSimple("T", Collections.emptyList())),
                        Collections.singletonList(NoneValue.INSTANCE)
                ).toString()
        ).isEqualTo("methodName[T[]](none)");
    }
}
