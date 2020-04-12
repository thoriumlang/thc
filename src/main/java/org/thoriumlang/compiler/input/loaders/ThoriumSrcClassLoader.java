package org.thoriumlang.compiler.input.loaders;

import org.thoriumlang.compiler.SourceToAST;
import org.thoriumlang.compiler.api.CompilationListener;
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
    private final CompilationListener listener;

    public ThoriumSrcClassLoader(NodeIdGenerator nodeIdGenerator, Sources sources, CompilationListener listener) {
        this.nodeIdGenerator = nodeIdGenerator;
        this.sources = sources;
        this.listener = listener;
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
                triggerNode.getContext().require(SymbolTable.class).root(),
                listener
        ).convert(loadedSource.get());

        return ast.root().map(root -> new ThoriumType(triggerNode, root.getTopLevelNode()));
    }
}
