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
import java.util.stream.Collectors;

class MethodTest {
    @Test
    void constructor_signature() {
        try {
            new Method(null, Collections.emptyList());
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("signature cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void constructor_statements() {
        try {
            new Method(
                    new MethodSignature(
                            Visibility.PUBLIC,
                            "method",
                            Collections.emptyList(),
                            Collections.emptyList(),
                            TypeSpecSimple.NONE
                    ),
                    null
            );
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("statements cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void accept() {
        Assertions.assertThat(
                new Method(
                        new MethodSignature(
                                Visibility.PUBLIC,
                                "method",
                                Collections.emptyList(),
                                Collections.emptyList(),
                                TypeSpecSimple.NONE
                        ),
                        Collections.singletonList(
                                new Statement(
                                        BooleanValue.TRUE,
                                        true
                                )
                        )
                ).accept(new BaseVisitor<String>() {
                    @Override
                    public String visitMethod(MethodSignature signature, List<Statement> statements) {
                        return String.format(
                                "{%s}:{%s}",
                                signature.toString(),
                                statements.stream()
                                        .map(Statement::toString)
                                        .collect(Collectors.joining(","))
                        );
                    }
                })
        ).isEqualTo("{PUBLIC method [] () : org.thoriumlang.None[]}:{true:true}");
    }

    @Test
    void _toString() {
        Assertions.assertThat(
                new Method(
                        new MethodSignature(
                                Visibility.PUBLIC,
                                "method",
                                Collections.emptyList(),
                                Collections.emptyList(),
                                TypeSpecSimple.NONE
                        ),
                        Collections.singletonList(
                                new Statement(
                                        BooleanValue.TRUE,
                                        true
                                )
                        )
                ).toString()
        ).isEqualTo("PUBLIC method [] () : org.thoriumlang.None[] { true:true }");
    }
}
