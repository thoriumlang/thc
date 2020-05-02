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
package org.thoriumlang.compiler.testsupport;

import com.google.common.collect.ImmutableMap;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;
import org.thoriumlang.compiler.api.errors.CompilationError;
import org.thoriumlang.compiler.api.errors.SemanticError;
import org.thoriumlang.compiler.api.errors.SymbolAlreadyDefinedError;
import org.thoriumlang.compiler.api.errors.SymbolNotFoundError;
import org.thoriumlang.compiler.api.errors.TargetNotFoundError;
import org.thoriumlang.compiler.api.errors.TooManyAlternativesError;
import org.thoriumlang.compiler.api.errors.TypeNotInferableError;
import org.thoriumlang.compiler.ast.AST;
import org.thoriumlang.compiler.ast.context.Context;
import org.thoriumlang.compiler.ast.nodes.NodeId;
import org.thoriumlang.compiler.ast.nodes.NoneValue;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.ast.nodes.TypeSpecInferred;
import org.thoriumlang.compiler.ast.nodes.TypeSpecIntersection;
import org.thoriumlang.compiler.ast.nodes.TypeSpecUnion;
import org.thoriumlang.compiler.ast.nodes.Value;
import org.thoriumlang.compiler.symbols.JavaClass;
import org.thoriumlang.compiler.symbols.JavaInterface;
import org.thoriumlang.compiler.symbols.Symbol;
import org.thoriumlang.compiler.symbols.SymbolTable;
import org.thoriumlang.compiler.symbols.ThoriumLibType;
import org.thoriumlang.compiler.symbols.ThoriumType;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class JsonAST {
    private final static List<String> CONTEXT_KEYS_TO_IGNORE = Collections.singletonList(
            "null(org.thoriumlang.compiler.ast.context.Relatives)"
    );
    private final Root root;
    private final List<CompilationError> errors;
    private final GsonBuilder gson;

    public JsonAST(AST ast) {
        this.root = ast.root().orElseThrow(() -> new IllegalStateException("no root found"));
        this.errors = ast.errors();
        this.gson = new GsonBuilder();

        JsonSerializer<SemanticError> semanticErrorSerializer = (src, typeOfSrc, context) -> {
            JsonObject jsonObject = new JsonObject();

            jsonObject.add("nodeRef", context.serialize(src.getNode().getNodeId()));
            jsonObject.add("message", context.serialize(
                    src.format((sp, message) -> String.format("%s (%d)", message, sp.getStartLine()))
            ));

            return jsonObject;
        };

        gson
                .registerTypeAdapter(NodeId.class,
                        (JsonSerializer<NodeId>) (src, typeOfSrc, context) ->
                                context.serialize(Integer.valueOf(src.format("%d"))))
                .registerTypeAdapter(SemanticError.class,
                        (JsonSerializer<SemanticError>) (src, typeOfSrc, context) -> {
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.add("message", context.serialize(src.toString()));
                            jsonObject.add("nodeRef", context.serialize(src.getNode().getNodeId()));
                            return jsonObject;
                        })
                .registerTypeAdapter(NoneValue.class,
                        (JsonSerializer<NoneValue>) (src, typeOfSrc, context) -> {
                            JsonObject jsonObject = (JsonObject) context.serialize(src, Value.class);
                            jsonObject.add("none", context.serialize(true));
                            return jsonObject;
                        })
                .registerTypeAdapter(TypeSpecInferred.class,
                        (JsonSerializer<TypeSpecInferred>) (src, typeOfSrc, context) -> {
                            JsonObject jsonObject = (JsonObject) context.serialize(src, Value.class);
                            jsonObject.add("inferred", context.serialize(true));
                            return jsonObject;
                        })
                .registerTypeAdapter(Context.class,
                        (JsonSerializer<Context>) (src, typeOfSrc, context) -> {
                            JsonObject jsonObject = new JsonObject();

                            src.keys().stream()
                                    .filter(k -> !CONTEXT_KEYS_TO_IGNORE.contains(k.toString()))
                                    .forEach(k -> jsonObject.add(
                                            k.toString(),
                                            context.serialize(src.get(k).map(Object::toString).orElse(""))
                                    ));

                            return jsonObject;
                        })
                .registerTypeAdapter(SymbolAlreadyDefinedError.class, semanticErrorSerializer)
                .registerTypeAdapter(SymbolNotFoundError.class, semanticErrorSerializer)
                .registerTypeAdapter(TargetNotFoundError.class, semanticErrorSerializer)
                .registerTypeAdapter(TooManyAlternativesError.class, semanticErrorSerializer)
                .registerTypeAdapter(TypeNotInferableError.class, semanticErrorSerializer)
                .setFieldNamingStrategy(f -> {
                    if (f.getName().equals("types") && f.getDeclaringClass().equals(TypeSpecIntersection.class)) {
                        return "intersectionType";
                    }
                    if (f.getName().equals("types") && f.getDeclaringClass().equals(TypeSpecUnion.class)) {
                        return "unionType";
                    }
                    return f.getName();
                });
    }

    public Root root() {
        return root;
    }

    public List<CompilationError> errors() {
        return errors;
    }

    public String json() {
        return gson.setPrettyPrinting().create().toJson(
                new JsonContent(
                        errors,
                        root.getContext()
                                .require(SymbolTable.class)
                                .root()
                                .accept(new ScopesExtractionVisitor())
                                .stream()
                                .distinct()
                                .map(JsonSymbolTable::new)
                                .collect(Collectors.toList())
                )
        );
    }

    private static class JsonContent {
        public final List<JsonSymbolTable> symbolTables;
        public final List<CompilationError> errors;

        private JsonContent(List<CompilationError> errors, List<JsonSymbolTable> symbolTables) {
            this.symbolTables = symbolTables;
            this.errors = errors;
        }
    }

    private static class JsonSymbolTable {
        private static final Map<Class<? extends Symbol>, BiFunction<String, Symbol, Map<String, Object>>> mappers =
                ImmutableMap.of(
                        ThoriumType.class,
                        (name, symbol) -> ImmutableMap.of(
                                "name", name,
                                "kind", symbol.toString(),
                                "definition", ((ThoriumType) symbol).getNode()
                        ),
                        ThoriumLibType.class,
                        (name, symbol) -> ImmutableMap.of(
                                "name", name,
                                "definition", symbol.toString()
                        ),
                        JavaClass.class,
                        (name, symbol) -> ImmutableMap.of(
                                "name", name,
                                "definition", symbol.toString()
                        ),
                        JavaInterface.class,
                        (name, symbol) -> ImmutableMap.of(
                                "name", name,
                                "definition", symbol.toString()
                        )
                );
        public final String name;
        public final String parentTableName;
        public final List<Map<String, Object>> symbols;


        JsonSymbolTable(SymbolTable t) {
            this.name = t != t.root()
                    ? t.toString()
                    : "[root]";
            this.parentTableName = t != t.root()
                    ? t.enclosingScope().toString()
                    : null;
            this.symbols = t.accept(
                    (name, symbolTable, symbols, scopes) -> symbols.entrySet().stream()
                            .map(e ->
                                    mappers.getOrDefault(
                                            e.getValue().getClass(),
                                            (n, s) -> ImmutableMap.of(
                                                    "name", n,
                                                    "kind", s.getClass().getName() + ": " + s.toString(),
                                                    "nodeRef", s.getDefiningNode().getNodeId()
                                            )
                                    ).apply(e.getKey(), e.getValue())
                            )
                            .sorted(Comparator.comparing(m -> m.get("name").toString()))
                            .collect(Collectors.toList())
            );
        }
    }
}
