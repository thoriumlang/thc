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

import java.io.IOException;

@Disabled
class CompilationUnitTest {
    @Test
    void constructor_ast() throws IOException {
        try {
            new CompilationUnit(null);
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("ast cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void getTopLevelKind_class() {
        Assertions.assertThat(Helper.main().getTopLevelKind())
                .isEqualTo(CompilationUnit.TopLevelKind.CLASS);
    }

//    @Test
//    void getTopLevelKind_type() throws IOException {
//        Assertions.assertThat(Helper.typeUnit().getTopLevelKind())
//                .isEqualTo(CompilationUnit.TopLevelKind.TYPE);
//    }

    @Test
    void findClass_exists() {
        CompilationUnit compilationUnit = Helper.main();

        Assertions.assertThat(compilationUnit.findClass("Class"))
                .get()
                .extracting(Class::getName)
                .isEqualTo("Class");
    }

    @Test
    void findClass_doesNotExist() {
        CompilationUnit compilationUnit = Helper.main();

        Assertions.assertThat(compilationUnit.findClass("UnknownClass"))
                .isNotPresent();
    }

//    @Test
//    void findType_exists() throws IOException {
//        CompilationUnit compilationUnit = Helper.typeUnit();
//
//        Assertions.assertThat(compilationUnit.findType("Type"))
//                .get()
//                .extracting(Type::getName)
//                .isEqualTo("Type");
//    }
//
//    @Test
//    void findType_doesNotExist() throws IOException {
//        CompilationUnit compilationUnit = Helper.typeUnit();
//
//        Assertions.assertThat(compilationUnit.findType("UnknownType"))
//                .isNotPresent();
//    }
}
