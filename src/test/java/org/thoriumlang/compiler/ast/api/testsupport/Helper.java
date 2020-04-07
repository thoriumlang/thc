package org.thoriumlang.compiler.ast.api.testsupport;

import org.thoriumlang.compiler.api.CompilationContext;
import org.thoriumlang.compiler.api.Compiler;
import org.thoriumlang.compiler.api.NoopCompilationListener;
import org.thoriumlang.compiler.ast.api.Class;
import org.thoriumlang.compiler.ast.api.CompilationUnit;
import org.thoriumlang.compiler.ast.api.Method;
import org.thoriumlang.compiler.ast.api.Type;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.input.Source;
import org.thoriumlang.compiler.input.SourceFiles;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Optional;

public class Helper {
    public static Root ast() {
        try {
            SourceFiles sources = new SourceFiles(
                    Paths.get(Helper.class.getResource("/org/thoriumlang/compiler/ast/api/example").toURI()),
                    p -> p.getFileName().toString().equals("Main.th")
            );
            HelperCompilationListener compilationListener = new HelperCompilationListener();

            new Compiler(compilationListener, Collections.emptyList()).compile(sources);

            return compilationListener
                    .root()
                    .orElseThrow(() -> new IllegalStateException("error while parsing"));
        }
        catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static Class getClass(String className) {
        return new CompilationUnit(ast())
                .findClass(className)
                .orElseThrow(() -> new IllegalArgumentException("no class found for name " + className));
    }

    public static Type getType(String typeName) {
        return new CompilationUnit(ast())
                .findType(typeName)
                .orElseThrow(() -> new IllegalArgumentException("no type found for name " + typeName));
    }

    public static Method getClassMethod(String method) {
        String[] parts = method.split("#");

        if (parts.length != 2) {
            throw new IllegalArgumentException("expected format: class#method");
        }

        return getClass(parts[0])
                .getType()
                .getMethods().stream()
                .filter(m -> m.getName().equals(parts[1]))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("no method found for name " + method));
    }

    public static Method getTypeMethod(String method) {
        String[] parts = method.split("#");

        if (parts.length != 2) {
            throw new IllegalArgumentException("expected format: class#method");
        }

        return getType(parts[0])
                .getMethods().stream()
                .filter(m -> m.getName().equals(parts[1]))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("no method found for name " + method));
    }

    private static class HelperCompilationListener extends NoopCompilationListener {
        private Root root;

        @Override
        public void onSourceFinished(Source source, CompilationContext context) {
            this.root = context.root().orElse(null);
        }

        public Optional<Root> root() {
            return Optional.ofNullable(root);
        }
    }
}
