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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
class MethodTest {
    @Test
    void constructor_method() {
        try {
            new Method((org.thoriumlang.compiler.ast.nodes.Method) null);
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("node cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void constructor_methodSignature() {
        try {
            new Method((org.thoriumlang.compiler.ast.nodes.MethodSignature) null);
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("node cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

//    @Test
//    void typeMethod_notSame_equals_hashCode() throws IOException {
//        CompilationUnit unit = Helper.typeUnit();
//        Method method1 = unit.findType("Type")
//                .flatMap(t -> t.findMethod("method"))
//                .orElseThrow(() -> new IllegalStateException("method not found"));
//        Method method2 = unit.findType("Type")
//                .flatMap(t -> t.findMethod("method"))
//                .orElseThrow(() -> new IllegalStateException("method not found"));
//
//        Assertions.assertThat(method1)
//                .isNotSameAs(method2)
//                .isEqualTo(method2)
//                .hasSameHashCodeAs(method2);
//    }

    @Test
    void classMethod_notSame_equals_hashCode() {
        CompilationUnit unit = Helper.main();
        Method method1 = unit.findClass("Class")
                .flatMap(c -> c.findMethod("method"))
                .orElseThrow(() -> new IllegalStateException("method not found"));
        Method method2 = unit.findClass("Class")
                .flatMap(c -> c.findMethod("method"))
                .orElseThrow(() -> new IllegalStateException("method not found"));

        Assertions.assertThat(method1)
                .isNotSameAs(method2)
                .isEqualTo(method2)
                .hasSameHashCodeAs(method2);
    }

    @Test
    void getName() {
        Assertions.assertThat(classMethod().getName())
                .isEqualTo("method");
    }

    @Test
    void classMethod_getDeclaringClass() {
        Assertions.assertThat(classMethod().getDeclaringClass())
                .get()
                .extracting(Class::getName)
                .isEqualTo("Class");
    }

//    @Test
//    void typeMethod_getDeclaringClass() throws IOException {
//        Assertions.assertThat(typeMethod().getDeclaringClass())
//                .isNotPresent();
//    }

    @Test
    void getParameters() {
        Assertions.assertThat(classMethod().getParameters())
                .haveExactly(1, new Condition<>(
                        p -> p.getName().equals("parameter"),
                        "expected one parameter with name 'parameter'"
                ));
    }

    @Test
    void findParameter() {
        Assertions.assertThat(classMethod().findParameter("parameter"))
                .get()
                .extracting(Parameter::getName)
                .isEqualTo("parameter");
    }

    @Test
    void getReturnType_simpleClass() {
        Assertions.assertThat(classMethod().getReturnType())
                .extracting(Type::getName)
                .isEqualTo("Class");
    }

//    @Test
//    void getReturnType_simpleType() throws IOException {
//        Assertions.assertThat(typeMethod().getReturnType())
//                .extracting(Type::getName)
//                .isEqualTo("Type");
//    }

//    @Test
//    void getReturnType_intersection() throws IOException {
//        AST typeAst = Helper.typeAst();
//        AST classAst = Helper.mainAST();
//
//        classAst.root()
//                .getContext()
//                .get(SymbolTable.class).orElseThrow(() -> new IllegalStateException("no symbol table found"))
//                .put(new ThoriumType("Type", typeAst.root().getTopLevelNode()));
//
//        Method method = new CompilationUnit(classAst)
//                .findClass("Class")
//                .flatMap(c -> c.findMethod("methodWithIntersectionType"))
//                .orElseThrow(() -> new IllegalStateException("method not found"));
//
//        Assertions.assertThat(method.getReturnType())
//                .extracting(Type::getName)
//                .isEqualTo("(Class | Type)");
//    }
//
//    @Test
//    void getReturnType_union() throws IOException {
//        AST typeAst = Helper.typeAst();
//        AST classAst = Helper.mainAST();
//
//        classAst.root()
//                .getContext()
//                .get(SymbolTable.class).orElseThrow(() -> new IllegalStateException("no symbol table found"))
//                .put(new ThoriumType("Type", typeAst.root().getTopLevelNode()));
//
//        Method method = new CompilationUnit(classAst)
//                .findClass("Class")
//                .flatMap(c -> c.findMethod("methodWithUnionType"))
//                .orElseThrow(() -> new IllegalStateException("method not found"));
//
//        Assertions.assertThat(method.getReturnType())
//                .extracting(Type::getName)
//                .isEqualTo("(Class & Type)");
//    }

    @Test
    void getSignature() {
        Assertions.assertThat(classMethod().getSignature())
                .isEqualTo("method(Class)Class");
    }

    private Method classMethod() {
        return Helper.main()
                .findClass("Class")
                .flatMap(e -> e.findMethod("method"))
                .orElseThrow(() -> new IllegalStateException("method not found"));
    }

//    private Method typeMethod() throws IOException {
//        return Helper.typeUnit()
//                .findType("Type")
//                .flatMap(e -> e.findMethod("method"))
//                .orElseThrow(() -> new IllegalStateException("method not found"));
//    }
}
