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

import org.thoriumlang.compiler.antlr.ThoriumBaseVisitor;
import org.thoriumlang.compiler.antlr.ThoriumParser;
import org.thoriumlang.compiler.ast.TypeParameter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TypeParameterDefVisitor extends ThoriumBaseVisitor<List<TypeParameter>> {
    public static final TypeParameterDefVisitor INSTANCE = new TypeParameterDefVisitor();

    private TypeParameterDefVisitor() {
        // nothing
    }

    @Override
    public List<TypeParameter> visitTypeParameterDef(ThoriumParser.TypeParameterDefContext ctx) {
        if (ctx.IDENTIFIER() == null) {
            return Collections.emptyList();
        }
        return ctx.IDENTIFIER().stream()
                .map(i -> new TypeParameter(i.getSymbol().getText()))
                .collect(Collectors.toList());
    }
}
