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

class IndirectAssignmentValueTest {
    @Test
    void constructor_indirectValue() {
        try {
            new IndirectAssignmentValue(null, "identifier", NoneValue.INSTANCE);
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("indirectValue cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void constructor_identifier() {
        try {
            new IndirectAssignmentValue(NoneValue.INSTANCE, null, NoneValue.INSTANCE);
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("identifier cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void constructor_value() {
        try {
            new IndirectAssignmentValue(NoneValue.INSTANCE, "identifier", null);
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("value cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void accept() {
        Assertions.assertThat(
                new IndirectAssignmentValue(
                        new NumberValue(1),
                        "identifier",
                        NoneValue.INSTANCE
                )
                        .accept(new BaseVisitor<String>() {
                            @Override
                            public String visitIndirectAssignmentValue(Value indirectValue, String identifier, Value value) {
                                return String.format(
                                        "%s:%s:%s",
                                        indirectValue.toString(),
                                        identifier,
                                        value.toString()
                                );
                            }
                        })
        ).isEqualTo("1:identifier:none");
    }

    @Test
    void _toString() {
        Assertions.assertThat(
                new IndirectAssignmentValue(
                        NoneValue.INSTANCE,
                        "identifier",
                        NoneValue.INSTANCE
                ).toString()
        ).isEqualTo("INDIRECT none.identifier = none");
    }
}
