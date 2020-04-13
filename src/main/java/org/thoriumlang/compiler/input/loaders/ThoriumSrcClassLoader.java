package org.thoriumlang.compiler.input.loaders;

import org.thoriumlang.compiler.api.Compiler;
import org.thoriumlang.compiler.ast.AST;
import org.thoriumlang.compiler.ast.nodes.Node;
import org.thoriumlang.compiler.input.Source;
import org.thoriumlang.compiler.input.Sources;
import org.thoriumlang.compiler.symbols.Name;
import org.thoriumlang.compiler.symbols.Symbol;
import org.thoriumlang.compiler.symbols.ThoriumType;

import java.util.Optional;

public class ThoriumSrcClassLoader implements TypeLoader {
    private final Sources sources;
    private final Compiler compiler;

    public ThoriumSrcClassLoader(Sources sources, Compiler compiler) {
        this.sources = sources;
        this.compiler = compiler;
    }

    @Override
    public Optional<Symbol> load(Name name, Node triggerNode) {
        Optional<Source> loadedSource = sources.load(name);

        if (!loadedSource.isPresent()) {
            return Optional.empty();
        }

        AST ast = compiler.compile(sources, loadedSource.get());

        return ast.root().map(root -> new ThoriumType(triggerNode, root.getTopLevelNode()));
    }
}
