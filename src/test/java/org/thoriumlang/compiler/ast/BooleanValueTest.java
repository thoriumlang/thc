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

class BooleanValueTest {
    @Test
    void accept_true() {
        Assertions.assertThat(
                BooleanValue.TRUE
                        .accept(new BaseVisitor<Boolean>() {
                            @Override
                            public Boolean visitBooleanValue(boolean value) {
                                return value;
                            }
                        })
        ).isEqualTo(true);
    }

    @Test
    void accept_false() {
        Assertions.assertThat(
                BooleanValue.FALSE
                        .accept(new BaseVisitor<Boolean>() {
                            @Override
                            public Boolean visitBooleanValue(boolean value) {
                                return value;
                            }
                        })
        ).isEqualTo(false);
    }

    @Test
    void _toString() {
        Assertions.assertThat(
                BooleanValue.TRUE.toString()
        ).isEqualTo("true");
        Assertions.assertThat(
                BooleanValue.FALSE.toString()
        ).isEqualTo("false");
    }
}
