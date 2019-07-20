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

class UseTest {
    @Test
    void constructor_from1() {
        try {
            new Use(null);
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("from cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void constructor_from2() {
        try {
            new Use(null, "to");
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("from cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void constructor_to() {
        try {
            new Use("from", null);
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("to cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void accept1() {
        Assertions.assertThat(
                new Use("f.r.o.m").accept(new BaseVisitor<String>() {
                    @Override
                    public String visitUse(String from, String to) {
                        return from + ":" + to;
                    }
                })
        ).isEqualTo("f.r.o.m:m");
    }

    @Test
    void accept2() {
        Assertions.assertThat(
                new Use("f.r.o.m", "to").accept(new BaseVisitor<String>() {
                    @Override
                    public String visitUse(String from, String to) {
                        return from + ":" + to;
                    }
                })
        ).isEqualTo("f.r.o.m:to");
    }

    @Test
    void _toString() {
        Assertions.assertThat(
                new Use("from", "to").toString()
        ).isEqualTo("USE from : to");
    }
}