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

import java.util.Collections;

@Disabled
class UnionTypeTest {
    @Test
    void constructor_class() {
        try {
            new UnionType(null);
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("types cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void notSame_equals_hashCode() {
        CompilationUnit unit = Helper.main();
        Type type1 = new UnionType(Collections.singletonList(
                unit.findClass("Class").orElseThrow(() -> new IllegalStateException("type not found"))
        ));
        Type type2 = new UnionType(Collections.singletonList(
                unit.findClass("Class").orElseThrow(() -> new IllegalStateException("type not found"))
        ));

        Assertions.assertThat(type1)
                .isNotSameAs(type2)
                .isEqualTo(type2)
                .hasSameHashCodeAs(type2);
    }

//    @Test
//    void getName() throws IOException {
//        AST typeAst = Helper.typeAst();
//        AST classAst = Helper.mainAST();
//
//        classAst.root().getTopLevelNode()
//                .getContext()
//                .get(SymbolTable.class).orElseThrow(() -> new IllegalStateException("no symbol table found"))
//                .put(new ThoriumType("Type", typeAst.root().getTopLevelNode()));
//
//        CompilationUnit compilationUnit = new CompilationUnit(classAst);
//
//        Assertions.assertThat(compilationUnit
//                .findClass("Class")
//                .flatMap(c -> c.findMethod("methodWithUnionType"))
//                .map(Method::getReturnType))
//                .get()
//                .extracting(Type::getName)
//                .isEqualTo("(Class & Type)");
//    }
//
//    @Test
//    void getName_mixed() throws IOException {
//        AST typeAst = Helper.typeAst();
//        AST otherTypeAst = Helper.otherTypeAst();
//        AST classAst = Helper.mainAST();
//
//        SymbolTable symbolTable = classAst.root().getTopLevelNode()
//                .getContext()
//                .get(SymbolTable.class).orElseThrow(() -> new IllegalStateException("no symbol table found"));
//
//        symbolTable.put(new ThoriumType("Type", typeAst.root().getTopLevelNode()));
//        symbolTable.put(new ThoriumType("OtherType", otherTypeAst.root().getTopLevelNode()));
//
//        CompilationUnit compilationUnit = new CompilationUnit(classAst);
//
//        Assertions.assertThat(compilationUnit
//                .findClass("Class")
//                .flatMap(c -> c.findMethod("methodWithIntersectionOfUnionType"))
//                .map(Method::getReturnType))
//                .get()
//                .extracting(Type::getName)
//                .isEqualTo("((Class & Type) | OtherType)");
//    }

//    @Test
//    void getMethods() throws IOException {
//        Assertions.assertThat(type().getMethods())
//                .haveExactly(2, new Condition<>(
//                        m -> m.getName().equals("method"),
//                        "expected two method with name 'method'"
//                ));
//    }

//    private Type type() throws IOException {
//        AST typeAst = Helper.typeAst();
//        AST otherTypeAst = Helper.otherTypeAst();
//        AST classAst = Helper.mainAST();
//
//        SymbolTable symbolTable = classAst.root().getTopLevelNode()
//                .getContext()
//                .get(SymbolTable.class).orElseThrow(() -> new IllegalStateException("no symbol table found"));
//
//        symbolTable.put(new ThoriumType("Type", typeAst.root().getTopLevelNode()));
//        symbolTable.put(new ThoriumType("OtherType", otherTypeAst.root().getTopLevelNode()));
//
//        CompilationUnit compilationUnit = new CompilationUnit(classAst);
//
//        return compilationUnit
//                .findClass("Class")
//                .flatMap(c -> c.findMethod("methodWithUnionType"))
//                .map(Method::getReturnType)
//                .orElseThrow(() -> new IllegalStateException("type not found"));
//    }
}
