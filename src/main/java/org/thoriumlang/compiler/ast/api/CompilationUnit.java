package org.thoriumlang.compiler.ast.api;

import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.ast.visitor.BaseVisitor;
import org.thoriumlang.compiler.symbols.Name;
import org.thoriumlang.compiler.symbols.Symbol;
import org.thoriumlang.compiler.symbols.SymbolTable;
import org.thoriumlang.compiler.symbols.ThoriumType;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CompilationUnit {
    private final SymbolTable symbolTable;

    public CompilationUnit(Root root) {
        this.symbolTable = Objects.requireNonNull(root, "root cannot be null")
                .getContext()
                .require(SymbolTable.class);
    }

    public Optional<Class> findClass(String className) {
        List<Symbol> symbol = symbolTable.find(new Name(className));

        if (symbol.size() != 1) {
            return Optional.empty();
        }

        ThoriumType thoriumType = (ThoriumType) symbol.get(0);

        return thoriumType.getNode().accept(new BaseVisitor<Optional<Class>>() {
            @Override
            public Optional<Class> visit(org.thoriumlang.compiler.ast.nodes.Type node) {
                return Optional.empty();
            }

            @Override
            public Optional<Class> visit(org.thoriumlang.compiler.ast.nodes.Class node) {
                return Optional.of(new Class(node));
            }
        });
    }

    public Optional<TypeType> findType(String typeName) {
        List<Symbol> symbol = symbolTable.find(new Name(typeName));

        if (symbol.size() != 1) {
            return Optional.empty();
        }

        ThoriumType thoriumType = (ThoriumType) symbol.get(0);

        return thoriumType.getNode().accept(new BaseVisitor<Optional<TypeType>>() {
            @Override
            public Optional<TypeType> visit(org.thoriumlang.compiler.ast.nodes.Type node) {
                return Optional.of(new TypeType(node));
            }

            @Override
            public Optional<TypeType> visit(org.thoriumlang.compiler.ast.nodes.Class node) {
                return Optional.empty();
            }
        });
    }
}
