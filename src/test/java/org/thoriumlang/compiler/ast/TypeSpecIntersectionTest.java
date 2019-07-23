package org.thoriumlang.compiler.ast;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

class TypeSpecIntersectionTest {
    @Test
    void constructor() {
        try {
            new TypeSpecIntersection(null);
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("types cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void accept() {
        List<TypeSpec> typeSpecs = Collections.singletonList(new TypeSpecSimple("type", Collections.emptyList()));
        Assertions.assertThat(
                new TypeSpecIntersection(typeSpecs).accept(new BaseVisitor<List<TypeSpec>>() {
                    @Override
                    public List<TypeSpec> visitTypeIntersection(List<TypeSpec> types) {
                        return types;
                    }
                })
        ).isEqualTo(typeSpecs);
    }

    @Test
    void _toString() {
        Assertions.assertThat(
                new TypeSpecIntersection(
                        Collections.singletonList(new TypeSpecSimple("type", Collections.emptyList()))
                ).toString()
        ).isEqualTo("i:[type[]]");
    }
}