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
package org.thoriumlang.compiler.antlr4;

class Factory {
    public static final Factory INSTANCE = new Factory();
    private final TypeDefVisitor typeDefVisitor;
    private final ClassDefVisitor classDefVisitor;
    private final UseVisitor useVisitor;

    private Factory() {
        TypeSpecVisitor typeSpecVisitor = new TypeSpecVisitor(
                new FqIdentifierVisitor()
        );
        TypeParameterDefVisitor typeParameterDefVisitor = new TypeParameterDefVisitor();
        MethodParameterVisitor methodParameterVisitor = new MethodParameterVisitor(
                typeSpecVisitor
        );
        StatementVisitor statementVisitorForNotLast = new StatementVisitor(false);
        StatementVisitor statementVisitorForLast = new StatementVisitor(true);
        ValueVisitor valueVisitor = new ValueVisitor(
                typeParameterDefVisitor,
                methodParameterVisitor,
                typeSpecVisitor,
                statementVisitorForNotLast,
                statementVisitorForLast
        );
        statementVisitorForNotLast.setValueVisitor(valueVisitor);
        statementVisitorForLast.setValueVisitor(valueVisitor);

        typeDefVisitor = new TypeDefVisitor(
                new MethodSignatureVisitor(
                        methodParameterVisitor,
                        typeSpecVisitor,
                        typeParameterDefVisitor
                ),
                typeParameterDefVisitor,
                typeSpecVisitor
        );
        classDefVisitor = new ClassDefVisitor(
                new MethodDefVisitor(
                        typeParameterDefVisitor,
                        methodParameterVisitor,
                        typeSpecVisitor,
                        statementVisitorForNotLast,
                        statementVisitorForLast
                ),
                new AttributeDefVisitor(
                        typeSpecVisitor,
                        valueVisitor
                ),
                typeParameterDefVisitor,
                typeSpecVisitor
        );
        useVisitor = new UseVisitor();
    }

    TypeDefVisitor typeDefVisitor() {
        return typeDefVisitor;
    }

    ClassDefVisitor classDefVisitor() {
        return classDefVisitor;
    }

    UseVisitor useVisitor() {
        return useVisitor;
    }
}
