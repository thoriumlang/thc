package org.thoriumlang.compiler;

import org.thoriumlang.compiler.api.CompilationContext;
import org.thoriumlang.compiler.api.errors.CompilationError;
import org.thoriumlang.compiler.api.Plugin;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.collections.Lists;
import org.thoriumlang.compiler.output.html.HtmlWalker;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class HtmlOutputPlugin implements Plugin {
    @Override
    public List<CompilationError> execute(CompilationContext context) {
        Root root = context.root();
        try (FileOutputStream fos = new FileOutputStream("/tmp/" + root.getTopLevelNode().getName() + ".html")) {
            fos.write(
                    new HtmlWalker(
                            root,
                            context.errors()
                                    .stream()
                                    .collect(Collectors.toMap(
                                            CompilationError::getNode,
                                            Collections::singletonList,
                                            Lists::merge
                                    ))
                    ).walk().getBytes()
            );
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return Collections.emptyList();
    }
}
