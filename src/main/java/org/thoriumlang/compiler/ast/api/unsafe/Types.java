package org.thoriumlang.compiler.ast.api.unsafe;

import org.thoriumlang.compiler.ast.api.ClassType;
import org.thoriumlang.compiler.ast.api.Type;
import org.thoriumlang.compiler.ast.api.TypeType;
import org.thoriumlang.compiler.ast.nodes.Class;
import org.thoriumlang.compiler.ast.visitor.BaseVisitor;
import org.thoriumlang.compiler.symbols.AliasSymbol;
import org.thoriumlang.compiler.symbols.JavaClass;
import org.thoriumlang.compiler.symbols.JavaInterface;
import org.thoriumlang.compiler.symbols.Name;
import org.thoriumlang.compiler.symbols.Symbol;
import org.thoriumlang.compiler.symbols.SymbolTable;
import org.thoriumlang.compiler.symbols.ThoriumLibType;
import org.thoriumlang.compiler.symbols.ThoriumType;

import java.util.Optional;

public class Types {
    private Types() {
        // nothing
    }

    public static Optional<Type> find(SymbolTable symbolTable, Name name){
        Optional<Symbol> symbol = symbolTable.find(name);

        if (!symbol.isPresent()) {
            return Optional.empty();
        }

        Symbol actualSymbol = symbol.get();
        if (actualSymbol instanceof ThoriumType) {
            return Optional.of(((ThoriumType) actualSymbol).getNode().accept(
                    new BaseVisitor<Type>() {
                        @Override
                        public Type visit(org.thoriumlang.compiler.ast.nodes.Type node) {
                            return new TypeType(node);
                        }

                        @Override
                        public Type visit(Class node) {
                            return new ClassType(node);
                        }
                    }
            ));
        }
        if (actualSymbol instanceof AliasSymbol) {
            return find(symbolTable, new Name(((AliasSymbol) actualSymbol).getTarget()));
        }
        if (actualSymbol instanceof ThoriumLibType) {
            return Optional.of(new EmptyType());
        }
        if (actualSymbol instanceof JavaClass) {
            return Optional.of(new EmptyType());
        }
        if (actualSymbol instanceof JavaInterface) {
            return Optional.of(new EmptyType());
        }

        throw new IllegalStateException(symbol.get().getClass() + " is not supported for " + name);
    }
}
