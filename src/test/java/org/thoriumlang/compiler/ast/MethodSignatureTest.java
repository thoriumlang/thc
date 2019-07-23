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
import java.util.List;
import java.util.stream.Collectors;

class MethodSignatureTest {
    @Test
    void constructor_visibility() {
        try {
            new MethodSignature(null, "name", Collections.emptyList(), Collections.emptyList(),
                    new TypeSpecSimple("test", Collections.emptyList()));
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("visibility cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void constructor_name() {
        try {
            new MethodSignature(Visibility.PRIVATE, null, Collections.emptyList(), Collections.emptyList(),
                    new TypeSpecSimple("test", Collections.emptyList()));
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("name cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void constructor_typeParameters() {
        try {
            new MethodSignature(
                    Visibility.PRIVATE,
                    "name",
                    null,
                    Collections.emptyList(),
                    new TypeSpecSimple("test", Collections.emptyList())
            );
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("typeParameters cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void constructor_parameters() {
        try {
            new MethodSignature(
                    Visibility.PRIVATE,
                    "name",
                    Collections.emptyList(),
                    null,
                    new TypeSpecSimple("test", Collections.emptyList())
            );
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("parameters cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void constructor_returnType() {
        try {
            new MethodSignature(Visibility.PRIVATE, "name", Collections.emptyList(), Collections.emptyList(), null);
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("returnType cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void accept() {
        Assertions.assertThat(
                new MethodSignature(
                        Visibility.PRIVATE,
                        "name",
                        Collections.singletonList(new TypeParameter("T")),
                        Collections.singletonList(new Parameter(
                                "name",
                                new TypeSpecSimple(
                                        "type",
                                        Collections.emptyList()
                                )
                        )),
                        new TypeSpecSimple("test", Collections.emptyList())
                ).accept(new BaseVisitor<String>() {
                    @Override
                    public String visitMethodSignature(Visibility visibility, String name,
                            List<TypeParameter> typeParameters, List<Parameter> parameters, TypeSpec returnType) {
                        return String.format(
                                "%s:%s:[%s]:(%s):%s",
                                visibility,
                                name,
                                typeParameters.stream()
                                        .map(TypeParameter::toString)
                                        .collect(Collectors.joining(",")),
                                parameters.stream()
                                        .map(Parameter::toString)
                                        .collect(Collectors.joining(",")),
                                returnType
                        );
                    }
                })
        ).isEqualTo("PRIVATE:name:[T]:(name: type[]):test[]");
    }

    @Test
    void _toString() {
        Assertions.assertThat(
                new MethodSignature(
                        Visibility.PRIVATE,
                        "name",
                        Collections.singletonList(new TypeParameter("T")),
                        Collections.singletonList(new Parameter(
                                "name",
                                new TypeSpecSimple("type", Collections.emptyList())
                        )),
                        new TypeSpecSimple("returnType", Collections.emptyList())
                ).toString()
        ).isEqualTo("PRIVATE name [T] (name: type[]) : returnType[]");
    }
}
