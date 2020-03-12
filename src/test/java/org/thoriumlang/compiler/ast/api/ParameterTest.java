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
class ParameterTest {
    @Test
    void constructor_node() {
        try {
            new Parameter(null);
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("node cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void getName() {
        Assertions.assertThat(classMethodParameter().getName())
                .isEqualTo("parameter");
    }

//    @Test
//    void classMethodParameter_getMethod() throws IOException {
//        Assertions.assertThat(classMethodParameter().getMethod())
//                .extracting(Method::getName)
//                .isEqualTo("method");
//    }
//
//    @Test
//    void typeMethodParameter_getMethod() throws IOException {
//        Assertions.assertThat(typeMethodParameter().getMethod())
//                .extracting(Method::getName)
//                .isEqualTo("method");
//    }

    @Test
    void getType_simpleClass() {
        Assertions.assertThat(classMethodParameter().getType())
                .extracting(Type::getName)
                .isEqualTo("Class");
    }

//    @Test
//    void getType_simpleType() throws IOException {
//        Assertions.assertThat(typeMethodParameter().getType())
//                .extracting(Type::getName)
//                .isEqualTo("Type");
//    }

//    @Test
//    void getType_intersection() throws IOException {
//        AST typeAst = Helper.typeAst();
//        AST classAst = Helper.mainAST();
//
//        classAst.root()
//                .getContext()
//                .get(SymbolTable.class).orElseThrow(() -> new IllegalStateException("no symbol table found"))
//                .put(new ThoriumType("Type", typeAst.root().getTopLevelNode()));
//
//        Parameter parameter = new CompilationUnit(classAst)
//                .findClass("Class")
//                .flatMap(c -> c.findMethod("methodWithIntersectionType"))
//                .flatMap(m -> m.findParameter("parameter"))
//                .orElseThrow(() -> new IllegalStateException("parameter not found"));
//
//        Assertions.assertThat(parameter.getType())
//                .extracting(Type::getName)
//                .isEqualTo("(Class | Type)");
//    }
//
//    @Test
//    void getType_union() throws IOException {
//        AST typeAst = Helper.typeAst();
//        AST classAst = Helper.mainAST();
//
//        classAst.root()
//                .getContext()
//                .get(SymbolTable.class).orElseThrow(() -> new IllegalStateException("no symbol table found"))
//                .put(new ThoriumType("Type", typeAst.root().getTopLevelNode()));
//
//        Parameter parameter = new CompilationUnit(classAst)
//                .findClass("Class")
//                .flatMap(c -> c.findMethod("methodWithUnionType"))
//                .flatMap(m -> m.findParameter("parameter"))
//                .orElseThrow(() -> new IllegalStateException("parameter not found"));
//
//        Assertions.assertThat(parameter.getType())
//                .extracting(Type::getName)
//                .isEqualTo("(Class & Type)");
//    }

    private Parameter classMethodParameter() {
        return Helper.main()
                .findClass("Class")
                .flatMap(c -> c.findMethod("method"))
                .flatMap(c -> c.findParameter("parameter"))
                .orElseThrow(() -> new IllegalStateException("parameter not found"));
    }

//    private Parameter typeMethodParameter() throws IOException {
//        return Helper.typeUnit()
//                .findType("Type")
//                .flatMap(c -> c.findMethod("method"))
//                .flatMap(c -> c.findParameter("parameter"))
//                .orElseThrow(() -> new IllegalStateException("parameter not found"));
//    }
}
