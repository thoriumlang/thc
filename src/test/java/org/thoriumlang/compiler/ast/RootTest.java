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

class RootTest {
    @Test
    void constructor_namespace() {
        try {
            new Root(
                    null,
                    Collections.emptyList(),
                    new Type(
                            Visibility.PUBLIC,
                            "name",
                            Collections.emptyList(),
                            TypeSpecSimple.OBJECT,
                            Collections.emptyList()
                    )
            );
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("namespace cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void constructor_uses() {
        try {
            new Root(
                    "namespace",
                    null,
                    new Type(
                            Visibility.PUBLIC,
                            "name",
                            Collections.emptyList(),
                            TypeSpecSimple.OBJECT,
                            Collections.emptyList()
                    )
            );
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("uses cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void constructor_type() {
        try {
            new Root("namespace", Collections.emptyList(), null);
        }
        catch (NullPointerException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("type cannot be null");
            return;
        }
        Assertions.fail("NPE not thrown");
    }

    @Test
    void accept() {
        Assertions.assertThat(
                new Root(
                        "namespace",
                        Collections.singletonList(new Use("from")),
                        new Type(
                                Visibility.NAMESPACE,
                                "name",
                                Collections.emptyList(),
                                TypeSpecSimple.OBJECT,
                                Collections.emptyList()
                        )
                ).accept(new BaseVisitor<String>() {
                    @Override
                    public String visitRoot(String namespace, Type type, List<Use> uses) {
                        return namespace + ":" + uses + ":" + type;
                    }
                })
        ).isEqualTo("namespace:[USE from : from]:NAMESPACE TYPE name[] : org.thoriumlang.Object[]:");
    }

    @Test
    void _toString() {
        Assertions.assertThat(
                new Root(
                        "namespace",
                        Collections.singletonList(new Use("from")),
                        new Type(
                                Visibility.PUBLIC,
                                "name",
                                Collections.emptyList(),
                                TypeSpecSimple.OBJECT,
                                Collections.emptyList()
                        )
                ).toString()
        ).isEqualTo("NAMESPACE namespace\nUSE from : from\nPUBLIC TYPE name[] : org.thoriumlang.Object[]:");
    }
}
