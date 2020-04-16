/*
 * Copyright 2019 Christophe Pollet
 *
 * Licensed under the Apache License, Version 2.0 (the s.get());
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
package org.thoriumlang.compiler.ast.visitor;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.ast.nodes.Attribute;
import org.thoriumlang.compiler.ast.nodes.BooleanValue;
import org.thoriumlang.compiler.ast.nodes.Class;
import org.thoriumlang.compiler.ast.nodes.DirectAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.FunctionValue;
import org.thoriumlang.compiler.ast.nodes.IdentifierValue;
import org.thoriumlang.compiler.ast.nodes.IndirectAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.Method;
import org.thoriumlang.compiler.ast.nodes.MethodCallValue;
import org.thoriumlang.compiler.ast.nodes.MethodSignature;
import org.thoriumlang.compiler.ast.nodes.Mode;
import org.thoriumlang.compiler.ast.nodes.NestedValue;
import org.thoriumlang.compiler.ast.nodes.NewAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.nodes.NoneValue;
import org.thoriumlang.compiler.ast.nodes.NumberValue;
import org.thoriumlang.compiler.ast.nodes.Parameter;
import org.thoriumlang.compiler.ast.nodes.Reference;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.ast.nodes.Statement;
import org.thoriumlang.compiler.ast.nodes.StringValue;
import org.thoriumlang.compiler.ast.nodes.Type;
import org.thoriumlang.compiler.ast.nodes.TypeParameter;
import org.thoriumlang.compiler.ast.nodes.TypeSpecFunction;
import org.thoriumlang.compiler.ast.nodes.TypeSpecInferred;
import org.thoriumlang.compiler.ast.nodes.TypeSpecIntersection;
import org.thoriumlang.compiler.ast.nodes.TypeSpecSimple;
import org.thoriumlang.compiler.ast.nodes.TypeSpecUnion;
import org.thoriumlang.compiler.ast.nodes.Use;
import org.thoriumlang.compiler.ast.nodes.Visibility;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Supplier;

class NodesMatchingVisitorTest {
    private NodeIdGenerator nodeIdGenerator;
    private Supplier<String> s;

    @BeforeEach
    void setup() {
        nodeIdGenerator = new NodeIdGenerator();
        s = new Supplier<String>() {
            final NodeIdGenerator nodeIdGenerator = new NodeIdGenerator();

            @Override
            public String get() {
                return nodeIdGenerator.next().toString();
            }
        };
    }

    @Test
    void clazz() {
        Root root = new Root(
                nodeIdGenerator.next(),
                s.get(),
                Collections.singletonList(
                        new Use(nodeIdGenerator.next(), s.get(), s.get())
                ),
                new Class(
                        nodeIdGenerator.next(),
                        Visibility.NAMESPACE,
                        s.get(),
                        Collections.singletonList(
                                new TypeParameter(nodeIdGenerator.next(), s.get())
                        ),
                        new TypeSpecSimple(nodeIdGenerator.next(), s.get(), Collections.singletonList(
                                new TypeSpecSimple(nodeIdGenerator.next(), s.get(), Collections.emptyList())
                        )),
                        Collections.singletonList(
                                new Method(
                                        nodeIdGenerator.next(),
                                        new MethodSignature(
                                                nodeIdGenerator.next(),
                                                Visibility.NAMESPACE,
                                                s.get(),
                                                Collections.singletonList(
                                                        new TypeParameter(
                                                                nodeIdGenerator.next(),
                                                                s.get()
                                                        )
                                                ),
                                                Collections.singletonList(
                                                        new Parameter(
                                                                nodeIdGenerator.next(),
                                                                s.get(),
                                                                new TypeSpecSimple(
                                                                        nodeIdGenerator.next(),
                                                                        s.get(),
                                                                        Collections.emptyList()
                                                                )
                                                        )
                                                ),
                                                new TypeSpecIntersection(
                                                        nodeIdGenerator.next(),
                                                        Arrays.asList(
                                                                new TypeSpecSimple(
                                                                        nodeIdGenerator.next(),
                                                                        s.get(),
                                                                        Collections.emptyList()
                                                                ),
                                                                new TypeSpecUnion(
                                                                        nodeIdGenerator.next(),
                                                                        Collections.singletonList(
                                                                                new TypeSpecSimple(
                                                                                        nodeIdGenerator.next(),
                                                                                        s.get(),
                                                                                        Collections.emptyList()
                                                                                )
                                                                        )
                                                                ),
                                                                new TypeSpecInferred(nodeIdGenerator.next()),
                                                                new TypeSpecFunction(
                                                                        nodeIdGenerator.next(),
                                                                        Collections.singletonList(
                                                                                new TypeSpecSimple(
                                                                                        nodeIdGenerator.next(),
                                                                                        s.get(),
                                                                                        Collections.emptyList()
                                                                                )
                                                                        ),
                                                                        new TypeSpecSimple(
                                                                                nodeIdGenerator.next(),
                                                                                s.get(),
                                                                                Collections.emptyList()
                                                                        )
                                                                )
                                                        )
                                                )

                                        ),
                                        Arrays.asList(
                                                new Statement(
                                                        nodeIdGenerator.next(),
                                                        new NoneValue(nodeIdGenerator.next()),
                                                        false
                                                ),
                                                new Statement(
                                                        nodeIdGenerator.next(),
                                                        new BooleanValue(nodeIdGenerator.next(), false),
                                                        false
                                                ),
                                                new Statement(
                                                        nodeIdGenerator.next(),
                                                        new NumberValue(nodeIdGenerator.next(), s.get()),
                                                        false
                                                ),
                                                new Statement(
                                                        nodeIdGenerator.next(),
                                                        new StringValue(nodeIdGenerator.next(), s.get()),
                                                        false
                                                ),
                                                new Statement(
                                                        nodeIdGenerator.next(),
                                                        new NestedValue(
                                                                nodeIdGenerator.next(),
                                                                new NoneValue(nodeIdGenerator.next()),
                                                                new NoneValue(nodeIdGenerator.next())
                                                        ),
                                                        false
                                                ),
                                                new Statement(
                                                        nodeIdGenerator.next(),
                                                        new FunctionValue(
                                                                nodeIdGenerator.next(),
                                                                Collections.singletonList(
                                                                        new TypeParameter(
                                                                                nodeIdGenerator.next(),
                                                                                s.get()
                                                                        )
                                                                ),
                                                                Collections.singletonList(
                                                                        new Parameter(
                                                                                nodeIdGenerator.next(),
                                                                                s.get(),
                                                                                new TypeSpecSimple(
                                                                                        nodeIdGenerator.next(),
                                                                                        s.get(),
                                                                                        Collections.emptyList()
                                                                                )
                                                                        )
                                                                ),
                                                                new TypeSpecSimple(
                                                                        nodeIdGenerator.next(),
                                                                        s.get(),
                                                                        Collections.emptyList()
                                                                ),
                                                                Collections.singletonList(
                                                                        new Statement(
                                                                                nodeIdGenerator.next(),
                                                                                new NoneValue(nodeIdGenerator.next()),
                                                                                true
                                                                        )
                                                                )
                                                        ),
                                                        true
                                                ),
                                                new Statement(
                                                        nodeIdGenerator.next(),
                                                        new IndirectAssignmentValue(
                                                                nodeIdGenerator.next(),
                                                                new NoneValue(nodeIdGenerator.next()),
                                                                new Reference(nodeIdGenerator.next(), s.get()),
                                                                new NoneValue(nodeIdGenerator.next())
                                                        ),
                                                        false
                                                ),
                                                new Statement(
                                                        nodeIdGenerator.next(),
                                                        new MethodCallValue(
                                                                nodeIdGenerator.next(),
                                                                s.get(),
                                                                Collections.singletonList(
                                                                        new TypeSpecSimple(
                                                                                nodeIdGenerator.next(),
                                                                                s.get(),
                                                                                Collections.emptyList())
                                                                ),
                                                                Collections.singletonList(
                                                                        new NoneValue(nodeIdGenerator.next())
                                                                )
                                                        ),
                                                        false
                                                ),
                                                new Statement(
                                                        nodeIdGenerator.next(),
                                                        new NewAssignmentValue(
                                                                nodeIdGenerator.next(),
                                                                s.get(),
                                                                new TypeSpecSimple(
                                                                        nodeIdGenerator.next(),
                                                                        s.get(),
                                                                        Collections.emptyList()),
                                                                new NoneValue(nodeIdGenerator.next()),
                                                                Mode.VAL
                                                        ),
                                                        false
                                                ),
                                                new Statement(
                                                        nodeIdGenerator.next(),
                                                        new NewAssignmentValue(
                                                                nodeIdGenerator.next(),
                                                                s.get(),
                                                                new TypeSpecSimple(
                                                                        nodeIdGenerator.next(),
                                                                        s.get(),
                                                                        Collections.emptyList()),
                                                                new NoneValue(nodeIdGenerator.next()),
                                                                Mode.VAR
                                                        ),
                                                        false
                                                ),
                                                new Statement(
                                                        nodeIdGenerator.next(),
                                                        new DirectAssignmentValue(
                                                                nodeIdGenerator.next(),
                                                                new Reference(nodeIdGenerator.next(), s.get()),
                                                                new NoneValue(nodeIdGenerator.next())
                                                        ),
                                                        false
                                                ),
                                                new Statement(
                                                        nodeIdGenerator.next(),
                                                        new IdentifierValue(
                                                                nodeIdGenerator.next(),
                                                                new Reference(nodeIdGenerator.next(), s.get())
                                                        ),
                                                        true
                                                )
                                        )
                                )
                        ),
                        Arrays.asList(
                                new Attribute(
                                        nodeIdGenerator.next(),
                                        s.get(),
                                        new TypeSpecSimple(nodeIdGenerator.next(), s.get(), Collections.emptyList()),
                                        new NoneValue(nodeIdGenerator.next()),
                                        Mode.VAL
                                ),
                                new Attribute(
                                        nodeIdGenerator.next(),
                                        s.get(),
                                        new TypeSpecSimple(nodeIdGenerator.next(), s.get(), Collections.emptyList()),
                                        new NoneValue(nodeIdGenerator.next()),
                                        Mode.VAR
                                )
                        )
                )
        );

        long nextNodeId = Long.parseLong(nodeIdGenerator.next().toString().substring(1));

        Assertions.assertThat(
                new NodesMatchingVisitor(n -> true).visit(root)
        ).hasSize((int) nextNodeId - 1);
    }

    @Test
    void type() {
        Root root = new Root(
                nodeIdGenerator.next(),
                s.get(),
                Collections.singletonList(
                        new Use(nodeIdGenerator.next(), s.get(), s.get())
                ),
                new Type(
                        nodeIdGenerator.next(),
                        Visibility.NAMESPACE,
                        s.get(),
                        Collections.singletonList(
                                new TypeParameter(nodeIdGenerator.next(), s.get())
                        ),
                        new TypeSpecSimple(nodeIdGenerator.next(), s.get(), Collections.singletonList(
                                new TypeSpecSimple(nodeIdGenerator.next(), s.get(), Collections.emptyList())
                        )),
                        Collections.singletonList(
                                new MethodSignature(
                                        nodeIdGenerator.next(),
                                        Visibility.NAMESPACE,
                                        s.get(),
                                        Collections.singletonList(
                                                new TypeParameter(
                                                        nodeIdGenerator.next(),
                                                        s.get()
                                                )
                                        ),
                                        Collections.singletonList(
                                                new Parameter(
                                                        nodeIdGenerator.next(),
                                                        s.get(),
                                                        new TypeSpecSimple(
                                                                nodeIdGenerator.next(),
                                                                s.get(),
                                                                Collections.emptyList()
                                                        )
                                                )
                                        ),
                                        new TypeSpecIntersection(
                                                nodeIdGenerator.next(),
                                                Arrays.asList(
                                                        new TypeSpecSimple(
                                                                nodeIdGenerator.next(), s.get(),
                                                                Collections.emptyList()
                                                        ),
                                                        new TypeSpecUnion(
                                                                nodeIdGenerator.next(),
                                                                Collections.singletonList(
                                                                        new TypeSpecSimple(
                                                                                nodeIdGenerator.next(),
                                                                                s.get(),
                                                                                Collections.emptyList()
                                                                        )
                                                                )
                                                        ),
                                                        new TypeSpecInferred(nodeIdGenerator.next()),
                                                        new TypeSpecFunction(
                                                                nodeIdGenerator.next(),
                                                                Collections.singletonList(
                                                                        new TypeSpecSimple(
                                                                                nodeIdGenerator.next(),
                                                                                s.get(),
                                                                                Collections.emptyList()
                                                                        )
                                                                ),
                                                                new TypeSpecSimple(
                                                                        nodeIdGenerator.next(),
                                                                        s.get(),
                                                                        Collections.emptyList()
                                                                )
                                                        )
                                                )
                                        )

                                )
                        )
                )
        );

        long nextNodeId = Long.parseLong(nodeIdGenerator.next().toString().substring(1));

        Assertions.assertThat(
                new NodesMatchingVisitor(n -> true).visit(root)
        ).hasSize((int) nextNodeId - 1);
    }
}
