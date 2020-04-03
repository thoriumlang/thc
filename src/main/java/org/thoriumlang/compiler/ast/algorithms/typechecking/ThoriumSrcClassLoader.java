package org.thoriumlang.compiler.ast.algorithms.typechecking;

import org.thoriumlang.compiler.input.Source;
import org.thoriumlang.compiler.SourceToAST;
import org.thoriumlang.compiler.ast.AST;
import org.thoriumlang.compiler.ast.nodes.Node;
import org.thoriumlang.compiler.input.Sources;
import org.thoriumlang.compiler.symbols.Symbol;
import org.thoriumlang.compiler.symbols.SymbolTable;
import org.thoriumlang.compiler.symbols.ThoriumType;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Optional;

public class ThoriumSrcClassLoader implements TypeLoader {
    private final Sources sources;

    public ThoriumSrcClassLoader(Sources sources) {
        this.sources = sources;
    }

    @Override
    public Optional<Symbol> load(String name, Node node) {
        Optional<Source> loadedSource = sources.load(name);

        if (!loadedSource.isPresent()) {
            return Optional.empty();
        }

        try {
            AST ast = new SourceToAST(
                    sources,
                    node.getContext().require(SymbolTable.class).root()
            ).apply(loadedSource.get());

            // TODO do something about errors

            ThoriumType symbol = new ThoriumType(
                    ast.root().getTopLevelNode()
            );

            return Optional.of(symbol);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
