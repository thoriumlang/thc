package org.thoriumlang.compiler.ast.transformations;

import io.vavr.control.Either;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.api.errors.SemanticError;
import org.thoriumlang.compiler.api.errors.SemanticErrorFormatter;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.ast.symbols.SymbolTable;
import org.thoriumlang.compiler.testsupport.AstHelper;

import java.util.List;

class SymbolTableCreationTest {
    private static final NodeIdGenerator nodeIdGenerator = new NodeIdGenerator();
    private static final SemanticErrorFormatter FORMATTER = (sourcePosition, message) -> message;

    @Test
    void use() {
        Root root = AstHelper.from(SymbolTableCreationTest.class, "use.th", nodeIdGenerator);

        Either<List<SemanticError>, SymbolTable> maybeSymbolTable = new SymbolTableCreation().apply(root);

        Assertions.assertThat(maybeSymbolTable.isRight()).isTrue();

        SymbolTable symbolTable = maybeSymbolTable.get();

        Assertions.assertThat(symbolTable.findInCurrentScope("org.thoriumlang.MyType"))
                .isPresent();
        Assertions.assertThat(symbolTable.findInCurrentScope("org.thoriumlang.compiler.ast.transformations.SymbolTableCreationTest.MyOtherType"))
                .isPresent();
    }

    @Test
    void duplicateUse() {
        Root root = AstHelper.from(SymbolTableCreationTest.class, "duplicateUse.th", nodeIdGenerator);

        Either<List<SemanticError>, SymbolTable> maybeSymbolTable = new SymbolTableCreation().apply(root);

        Assertions.assertThat(maybeSymbolTable.isLeft()).isTrue();
        Assertions.assertThat(maybeSymbolTable.getLeft())
                .hasSize(1)
                .map(e -> e.format(FORMATTER))
                .contains("Symbol org.thoriumlang.String is already defined at 1:1");
    }

    @Test
    void type() {
        Root root = AstHelper.from(SymbolTableCreationTest.class, "type.th", nodeIdGenerator);

        Either<List<SemanticError>, SymbolTable> maybeSymbolTable = new SymbolTableCreation().apply(root);

        Assertions.assertThat(maybeSymbolTable.isRight()).isTrue();

        Assertions.assertThat(maybeSymbolTable.get()
                .findInCurrentScope("org.thoriumlang.compiler.ast.transformations.SymbolTableCreationTest.MyOtherType")
        )
                .isPresent();
    }

    @Test
    void redefiningType() {
        Root root = AstHelper.from(SymbolTableCreationTest.class, "redefiningType.th", nodeIdGenerator);

        Either<List<SemanticError>, SymbolTable> maybeSymbolTable = new SymbolTableCreation().apply(root);

        Assertions.assertThat(maybeSymbolTable.isLeft()).isTrue();
        Assertions.assertThat(maybeSymbolTable.getLeft())
                .hasSize(1)
                .map(e -> e.format(FORMATTER))
                .contains(
                        "Symbol org.thoriumlang.compiler.ast.transformations.SymbolTableCreationTest.MyType is already defined at 1:1"
                );
    }

    @Test
    void duplicateTypes() {
        Root root = AstHelper.from(SymbolTableCreationTest.class, "duplicateTypes.th", nodeIdGenerator);

        Either<List<SemanticError>, SymbolTable> maybeSymbolTable = new SymbolTableCreation().apply(root);

        Assertions.assertThat(maybeSymbolTable.isLeft()).isTrue();
        Assertions.assertThat(maybeSymbolTable.getLeft())
                .hasSize(3)
                .map(e -> e.format(FORMATTER))
                .contains(
                        "Symbol A is already defined at 3:13",
                        "Symbol T is already defined at 4:14",
                        "Symbol p1 is already defined at 4:23"
                );
    }

    @Test
    void parametersAreSeenFromMethod() {
        Root root = AstHelper.from(SymbolTableCreationTest.class, "parametersAreSeenFromMethod.th", nodeIdGenerator);

        Either<List<SemanticError>, SymbolTable> maybeSymbolTable = new SymbolTableCreation().apply(root);

        Assertions.assertThat(maybeSymbolTable.isRight()).isTrue();
    }

    @Test
    void typeParametersAreSeenFromAttributesAndMethodParametersAndBody() {
        Root root = AstHelper.from(SymbolTableCreationTest.class, "typeParametersAreSeenFromMethodParametersAndBody.th", nodeIdGenerator);

        Either<List<SemanticError>, SymbolTable> maybeSymbolTable = new SymbolTableCreation().apply(root);

        Assertions.assertThat(maybeSymbolTable.isRight()).isTrue();
    }


    @Test
    void attributesAreSeenFromMethod() {
        Root root = AstHelper.from(SymbolTableCreationTest.class, "attributesAreSeenFromMethod.th", nodeIdGenerator);

        Either<List<SemanticError>, SymbolTable> maybeSymbolTable = new SymbolTableCreation().apply(root);

        Assertions.assertThat(maybeSymbolTable.isRight()).isTrue();
    }

    @Test
    void undeclaredVariable() {
        Root root = AstHelper.from(SymbolTableCreationTest.class, "undeclaredVariable.th", nodeIdGenerator);

        Either<List<SemanticError>, SymbolTable> maybeSymbolTable = new SymbolTableCreation().apply(root);

        Assertions.assertThat(maybeSymbolTable.isLeft()).isTrue();
        Assertions.assertThat(maybeSymbolTable.getLeft())
                .hasSize(1)
                .map(e -> e.format(FORMATTER))
                .contains("Symbol p is not defined");
    }

    @Test
    void undeclaredType() {
        Root root = AstHelper.from(SymbolTableCreationTest.class, "undeclaredType.th", nodeIdGenerator);

        Either<List<SemanticError>, SymbolTable> maybeSymbolTable = new SymbolTableCreation().apply(root);

        Assertions.assertThat(maybeSymbolTable.isLeft()).isTrue();
        Assertions.assertThat(maybeSymbolTable.getLeft())
                .hasSize(3)
                .map(e -> e.format(FORMATTER))
                .contains(
                        "Type UndeclaredAttributeType is not defined",
                        "Type UndeclaredMethodReturnType is not defined",
                        "Type UndeclaredParameterType is not defined"
                );
    }

    @Test
    void undeclaredTypeParameter() {
        Root root = AstHelper.from(SymbolTableCreationTest.class, "undeclaredTypeParameter.th", nodeIdGenerator);

        Either<List<SemanticError>, SymbolTable> maybeSymbolTable = new SymbolTableCreation().apply(root);

        Assertions.assertThat(maybeSymbolTable.isLeft()).isTrue();
        Assertions.assertThat(maybeSymbolTable.getLeft())
                .hasSize(1)
                .map(e -> e.format(FORMATTER))
                .contains("Type T is not defined");
    }

    @Test
    void declaredAssignmentTarget() {
        Root root = AstHelper.from(SymbolTableCreationTest.class, "declaredAssignmentTarget.th", nodeIdGenerator);

        Either<List<SemanticError>, SymbolTable> maybeSymbolTable = new SymbolTableCreation().apply(root);

        Assertions.assertThat(maybeSymbolTable.isRight()).isTrue();
    }

    @Test
    void duplicateVariable() {
        Root root = AstHelper.from(SymbolTableCreationTest.class, "duplicateVariable.th", nodeIdGenerator);

        Either<List<SemanticError>, SymbolTable> maybeSymbolTable = new SymbolTableCreation().apply(root);

        Assertions.assertThat(maybeSymbolTable.isLeft()).isTrue();
        Assertions.assertThat(maybeSymbolTable.getLeft())
                .hasSize(1)
                .map(e -> e.format(FORMATTER))
                .contains("Symbol a is already defined at 5:9");
    }


    @Test
    void variableCanHideAttribute() {
        Root root = AstHelper.from(SymbolTableCreationTest.class, "variableCanHideAttribute.th", nodeIdGenerator);

        Either<List<SemanticError>, SymbolTable> maybeSymbolTable = new SymbolTableCreation().apply(root);

        Assertions.assertThat(maybeSymbolTable.isRight()).isTrue();
    }

    @Test
    void undeclaredAssignmentTarget() {
        Root root = AstHelper.from(SymbolTableCreationTest.class, "undeclaredAssignmentTarget.th", nodeIdGenerator);

        Either<List<SemanticError>, SymbolTable> maybeSymbolTable = new SymbolTableCreation().apply(root);

        Assertions.assertThat(maybeSymbolTable.isLeft()).isTrue();
        Assertions.assertThat(maybeSymbolTable.getLeft())
                .hasSize(1)
                .map(e -> e.format(FORMATTER))
                .contains("Symbol undefinedTarget is not defined");
    }
}