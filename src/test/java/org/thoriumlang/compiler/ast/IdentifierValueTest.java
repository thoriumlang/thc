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

class IdentifierValueTest {
    @Test
    void constructor_value() {
        try {
            new IdentifierValue(null);
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
                new IdentifierValue("id")
                        .accept(new BaseVisitor<String>() {
                            @Override
                            public String visitIdentifierValue(String value) {
                                return value;
                            }
                        })
        ).isEqualTo("id");
    }

    @Test
    void _toString() {
        Assertions.assertThat(
                new IdentifierValue("id").toString()
        ).isEqualTo("id");
    }
}