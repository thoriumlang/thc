package org.thoriumlang.compiler.input.loaders;

import org.thoriumlang.compiler.SourceToAST;
import org.thoriumlang.compiler.ast.AST;
import org.thoriumlang.compiler.ast.nodes.Node;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.input.Source;
import org.thoriumlang.compiler.input.Sources;
import org.thoriumlang.compiler.symbols.Name;
import org.thoriumlang.compiler.symbols.Symbol;
import org.thoriumlang.compiler.symbols.SymbolTable;
import org.thoriumlang.compiler.symbols.ThoriumType;

import java.util.Optional;

public class ThoriumSrcClassLoader implements TypeLoader {
    private final NodeIdGenerator nodeIdGenerator;
    private final Sources sources;

    public ThoriumSrcClassLoader(NodeIdGenerator nodeIdGenerator, Sources sources) {
        this.nodeIdGenerator = nodeIdGenerator;
        this.sources = sources;
    }

    @Override
    public Optional<Symbol> load(Name name, Node triggerNode) {
        Optional<Source> loadedSource = sources.load(name);

        if (!loadedSource.isPresent()) {
            return Optional.empty();
        }

        AST ast = new SourceToAST(
                nodeIdGenerator,
                sources,
                triggerNode.getContext().require(SymbolTable.class).root()
        ).convert(loadedSource.get());

        ThoriumType symbol = new ThoriumType(triggerNode, ast.root().getTopLevelNode());
        // TODO do something about errors

        return Optional.of(symbol);
    }
}
