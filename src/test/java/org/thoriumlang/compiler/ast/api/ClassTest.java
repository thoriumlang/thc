/*
 * Copyright 2020 Christophe Pollet
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
package org.thoriumlang.compiler.ast.api;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ClassTest {
    @Test
    void constructor_node() {
        try {
            new Class(null);
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("node cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void notSame_equals_hashCode() {
        CompilationUnit unit = Helper.main();
        Class class1 = unit.findClass("NaturalPerson").orElseThrow(() -> new IllegalStateException("class not found"));
        Class class2 = unit.findClass("NaturalPerson").orElseThrow(() -> new IllegalStateException("class not found"));

        Assertions.assertThat(class1)
                .isNotSameAs(class2)
                .isEqualTo(class2)
                .hasSameHashCodeAs(class2);
    }

    @Test
    void getName() {
        Assertions.assertThat(clazz())
                .extracting(Class::getName)
                .isEqualTo("NaturalPerson");
    }

    @Test
    void getMethods() {
        Assertions.assertThat(clazz().getMethods())
                .haveExactly(1, new Condition<>(
                        m -> m.getName().equals("method"),
                        "expected one method with name 'method'"
                ));
    }

    @Test
    void getMethod_exists() {
        Assertions.assertThat(clazz().findMethod("method"))
                .get()
                .extracting(Method::getName)
                .isEqualTo("method");
    }

    @Test
    void getMethod_doesNotExist() {
        Assertions.assertThat(clazz().findMethod("unknownMethod"))
                .isNotPresent();
    }

    @Test
    void getType() {
        Assertions.assertThat(clazz())
                .extracting(Type::getName)
                .isEqualTo("NaturalPerson");
    }

    private Class clazz() {
        return Helper.main()
                .findClass("NaturalPerson")
                .orElseThrow(() -> new IllegalStateException("class not found"));
    }
}
