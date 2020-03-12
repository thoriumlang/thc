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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
class TypeTypeTest {
    @Test
    void constructor_type() {
        try {
            new TypeType(null);
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("node cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

//    @Test
//    void notSame_equals_hashCode() throws IOException {
//        CompilationUnit unit = Helper.typeUnit();
//        Type type1 = unit.findType("Type").orElseThrow(() -> new IllegalStateException("type not found"));
//        Type type2 = unit.findType("Type").orElseThrow(() -> new IllegalStateException("type not found"));
//
//        Assertions.assertThat(type1)
//                .isNotSameAs(type2)
//                .isEqualTo(type2)
//                .hasSameHashCodeAs(type2);
//    }

//    @Test
//    void getName() throws IOException {
//        Assertions.assertThat(type())
//                .extracting(Type::getName)
//                .isEqualTo("Type");
//    }
//
//    @Test
//    void getMethods() throws IOException {
//        Assertions.assertThat(type().getMethods())
//                .haveExactly(1, new Condition<>(
//                        m -> m.getName().equals("method"),
//                        "expected one method with name 'method'"
//                ));
//    }
//
//    @Test
//    void getMethod_exists() throws IOException {
//        Assertions.assertThat(type().findMethod("method"))
//                .get()
//                .extracting(Method::getName)
//                .isEqualTo("method");
//    }
//
//    @Test
//    void getMethod_doesNotExist() throws IOException {
//        Assertions.assertThat(type().findMethod("unknownMethod"))
//                .isNotPresent();
//    }

//    private Type type() throws IOException {
//        return Helper.typeUnit()
//                .findType("Type")
//                .orElseThrow(() -> new IllegalStateException("type not found"));
//    }
}
