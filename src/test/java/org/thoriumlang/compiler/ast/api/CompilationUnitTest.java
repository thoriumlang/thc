package org.thoriumlang.compiler.ast.api;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.ast.api.testsupport.Helper;

class CompilationUnitTest {
    @Test
    void constructor() {
        Assertions.assertThatThrownBy(() -> new CompilationUnit(null))
                .hasMessage("root cannot be null");
    }

    @Test
    void findClass_notFound() {
        Assertions.assertThat(new CompilationUnit(Helper.ast()).findClass("Unknown"))
                .isEmpty();
    }

    @Test
    void findClass_type() {
        Assertions.assertThat(new CompilationUnit(Helper.ast()).findClass("example.Person"))
                .isEmpty();
    }

    @Test
    void findClass_found() {
        Assertions.assertThat(new CompilationUnit(Helper.ast()).findClass("example.LegalPerson"))
                .isNotEmpty();
    }

    @Test
    void findType_notFound() {
        Assertions.assertThat(new CompilationUnit(Helper.ast()).findType("Unknown"))
                .isEmpty();
    }

    @Test
    void findType_class() {

    }

    @Test
    void findType_found() {
        Assertions.assertThat(new CompilationUnit(Helper.ast()).findType("example.Person"))
                .isNotEmpty();
    }
}