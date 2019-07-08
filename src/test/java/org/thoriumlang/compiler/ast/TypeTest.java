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

class TypeTest {
    @Test
    void constructor_name() {
        try {
            new Type(null, Collections.emptyList());
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("name cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void constructor_methods() {
        try {
            new Type("name", null);
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("methods cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void accept() {
        Assertions.assertThat(
                new Type(
                        "name",
                        Collections.singletonList(
                                new MethodSignature(
                                        Visibility.PRIVATE,
                                        "name",
                                        Collections.emptyList(),
                                        new TypeSpecSingle("type")
                                )
                        )
                ).accept(new BaseVisitor<String>() {
                    @Override
                    public String visitType(String name, List<MethodSignature> methods) {
                        return name + ":" + methods;
                    }
                })
        ).isEqualTo("name:[PRIVATE name (  ) : type]");
    }

    @Test
    void _toString() {
        Assertions.assertThat(
                new Type(
                        "name",
                        Collections.singletonList(
                                new MethodSignature(
                                        Visibility.PRIVATE,
                                        "name",
                                        Collections.singletonList(new Parameter(
                                                "parameter", new TypeSpecSingle("type")
                                        )),
                                        new TypeSpecSingle("returnType")
                                )
                        )
                ).toString()
        ).isEqualTo("TYPE name:\nPRIVATE name ( parameter: type ) : returnType");
    }
}
