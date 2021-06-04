package org.thoriumlang.compiler.ast.transformations;

import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.helpers.XmlAssertions;
import org.thoriumlang.compiler.testsupport.AstHelper;

public class TypeNamesQualifyingTest {
    private static final NodeIdGenerator nodeIdGenerator = new NodeIdGenerator();

    @Test
    void typeSpecSimple() {
        Root root = AstHelper.from(TypeNamesQualifyingTest.class, "typeSpecSimple.th", nodeIdGenerator);
        TypeNameQualifying typeNamesQualifying = new TypeNameQualifying(nodeIdGenerator);

        Root newRoot = typeNamesQualifying.apply(root);

        XmlAssertions.on(newRoot)
                // qualifying
                .assertXpathEquals("//method[@name=\"uu\"]//simpleType/@type", "org.thoriumlang.Type")
                .assertXpathEquals("//method[@name=\"uu\"]//arguments//simpleType/@type", "org.thoriumlang.Arg")

                .assertXpathEquals("//method[@name=\"un\"]//simpleType/@type", "org.thoriumlang.Type")
                .assertXpathEquals("//method[@name=\"un\"]//arguments//simpleType/@type", "org.thoriumlang.compiler.ast.transformations.TypeNamesQualifyingTest.NsArg")

                .assertXpathEquals("//method[@name=\"uq\"]//simpleType/@type", "org.thoriumlang.Type")
                .assertXpathEquals("//method[@name=\"uq\"]//arguments//simpleType/@type", "q.Arg")

                .assertXpathEquals("//method[@name=\"nu\"]//simpleType/@type", "org.thoriumlang.compiler.ast.transformations.TypeNamesQualifyingTest.NsType")
                .assertXpathEquals("//method[@name=\"nu\"]//arguments//simpleType/@type", "org.thoriumlang.Arg")

                .assertXpathEquals("//method[@name=\"nn\"]//simpleType/@type", "org.thoriumlang.compiler.ast.transformations.TypeNamesQualifyingTest.NsType")
                .assertXpathEquals("//method[@name=\"nn\"]//arguments//simpleType/@type", "org.thoriumlang.compiler.ast.transformations.TypeNamesQualifyingTest.NsArg")

                .assertXpathEquals("//method[@name=\"nq\"]//simpleType/@type", "org.thoriumlang.compiler.ast.transformations.TypeNamesQualifyingTest.NsType")
                .assertXpathEquals("//method[@name=\"nq\"]//arguments//simpleType/@type", "q.Arg")

                .assertXpathEquals("//method[@name=\"qu\"]//simpleType/@type", "q.Type")
                .assertXpathEquals("//method[@name=\"qu\"]//arguments//simpleType/@type", "org.thoriumlang.Arg")

                .assertXpathEquals("//method[@name=\"qn\"]//simpleType/@type", "q.Type")
                .assertXpathEquals("//method[@name=\"qn\"]//arguments//simpleType/@type", "org.thoriumlang.compiler.ast.transformations.TypeNamesQualifyingTest.NsArg")

                .assertXpathEquals("//method[@name=\"qq\"]//simpleType/@type", "q.Type")
                .assertXpathEquals("//method[@name=\"qq\"]//arguments//simpleType/@type", "q.Arg")

                // minimal change
                .comparing(root)
                .assertXpathEquals("//method[@name=\"uq\"]//arguments/simpleType/@id")
                .assertXpathEquals("//method[@name=\"nq\"]//arguments/simpleType/@id")
                .assertXpathEquals("//method[@name=\"qq\"]/returnType/simpleType/@id")
                .assertXpathEquals("//method[@name=\"qq\"]//arguments/simpleType/@id");
    }

    @Test
    void typeSpecUnion() {
        Root root = AstHelper.from(TypeNamesQualifyingTest.class, "typeSpecUnion.th", nodeIdGenerator);
        TypeNameQualifying typeNamesQualifying = new TypeNameQualifying(nodeIdGenerator);

        Root newRoot = typeNamesQualifying.apply(root);

        XmlAssertions.on(newRoot)
                // qualifying
                .assertXpathEquals("//method[@name=\"uu\"]//simpleType[1]/@type", "org.thoriumlang.Type")
                .assertXpathEquals("//method[@name=\"uu\"]//simpleType[2]/@type", "org.thoriumlang.Type")

                .assertXpathEquals("//method[@name=\"un\"]//simpleType[1]/@type", "org.thoriumlang.Type")
                .assertXpathEquals("//method[@name=\"un\"]//simpleType[2]/@type", "org.thoriumlang.compiler.ast.transformations.TypeNamesQualifyingTest.NsType")

                .assertXpathEquals("//method[@name=\"uq\"]//simpleType[1]/@type", "org.thoriumlang.Type")
                .assertXpathEquals("//method[@name=\"uq\"]//simpleType[2]/@type", "q.Type")

                .assertXpathEquals("//method[@name=\"nu\"]//simpleType[1]/@type", "org.thoriumlang.compiler.ast.transformations.TypeNamesQualifyingTest.NsType")
                .assertXpathEquals("//method[@name=\"nu\"]//simpleType[2]/@type", "org.thoriumlang.Type")

                .assertXpathEquals("//method[@name=\"nn\"]//simpleType[1]/@type", "org.thoriumlang.compiler.ast.transformations.TypeNamesQualifyingTest.NsType")
                .assertXpathEquals("//method[@name=\"nn\"]//simpleType[2]/@type", "org.thoriumlang.compiler.ast.transformations.TypeNamesQualifyingTest.NsType")

                .assertXpathEquals("//method[@name=\"nq\"]//simpleType[1]/@type", "org.thoriumlang.compiler.ast.transformations.TypeNamesQualifyingTest.NsType")
                .assertXpathEquals("//method[@name=\"nq\"]//simpleType[2]/@type", "q.Type")

                .assertXpathEquals("//method[@name=\"qu\"]//simpleType[1]/@type", "q.Type")
                .assertXpathEquals("//method[@name=\"qu\"]//simpleType[2]/@type", "org.thoriumlang.Type")

                .assertXpathEquals("//method[@name=\"qn\"]//simpleType[1]/@type", "q.Type")
                .assertXpathEquals("//method[@name=\"qn\"]//simpleType[2]/@type", "org.thoriumlang.compiler.ast.transformations.TypeNamesQualifyingTest.NsType")

                .assertXpathEquals("//method[@name=\"qq\"]//simpleType[1]/@type", "q.Type")
                .assertXpathEquals("//method[@name=\"qq\"]//simpleType[2]/@type", "q.Type")

                // minimal change
                .comparing(root)
                .assertXpathEquals("//method[@name=\"uq\"]//unionType/simpleType[2]/@id")
                .assertXpathEquals("//method[@name=\"nq\"]//unionType/simpleType[2]/@id")
                .assertXpathEquals("//method[@name=\"qq\"]//unionType/simpleType[2]/@id")
                .assertXpathEquals("//method[@name=\"qq\"]/@id");
    }

    @Test
    void typeSpecIntersection() {
        Root root = AstHelper.from(TypeNamesQualifyingTest.class, "typeSpecIntersection.th", nodeIdGenerator);
        TypeNameQualifying typeNamesQualifying = new TypeNameQualifying(nodeIdGenerator);

        Root newRoot = typeNamesQualifying.apply(root);

        XmlAssertions.on(newRoot)
                // qualifying
                .assertXpathEquals("//method[@name=\"uu\"]//simpleType[1]/@type", "org.thoriumlang.Type")
                .assertXpathEquals("//method[@name=\"uu\"]//simpleType[2]/@type", "org.thoriumlang.Type")

                .assertXpathEquals("//method[@name=\"un\"]//simpleType[1]/@type", "org.thoriumlang.Type")
                .assertXpathEquals("//method[@name=\"un\"]//simpleType[2]/@type", "org.thoriumlang.compiler.ast.transformations.TypeNamesQualifyingTest.NsType")

                .assertXpathEquals("//method[@name=\"uq\"]//simpleType[1]/@type", "org.thoriumlang.Type")
                .assertXpathEquals("//method[@name=\"uq\"]//simpleType[2]/@type", "q.Type")

                .assertXpathEquals("//method[@name=\"nu\"]//simpleType[1]/@type", "org.thoriumlang.compiler.ast.transformations.TypeNamesQualifyingTest.NsType")
                .assertXpathEquals("//method[@name=\"nu\"]//simpleType[2]/@type", "org.thoriumlang.Type")

                .assertXpathEquals("//method[@name=\"nn\"]//simpleType[1]/@type", "org.thoriumlang.compiler.ast.transformations.TypeNamesQualifyingTest.NsType")
                .assertXpathEquals("//method[@name=\"nn\"]//simpleType[2]/@type", "org.thoriumlang.compiler.ast.transformations.TypeNamesQualifyingTest.NsType")

                .assertXpathEquals("//method[@name=\"nq\"]//simpleType[1]/@type", "org.thoriumlang.compiler.ast.transformations.TypeNamesQualifyingTest.NsType")
                .assertXpathEquals("//method[@name=\"nq\"]//simpleType[2]/@type", "q.Type")

                .assertXpathEquals("//method[@name=\"qu\"]//simpleType[1]/@type", "q.Type")
                .assertXpathEquals("//method[@name=\"qu\"]//simpleType[2]/@type", "org.thoriumlang.Type")

                .assertXpathEquals("//method[@name=\"qn\"]//simpleType[1]/@type", "q.Type")
                .assertXpathEquals("//method[@name=\"qn\"]//simpleType[2]/@type", "org.thoriumlang.compiler.ast.transformations.TypeNamesQualifyingTest.NsType")

                .assertXpathEquals("//method[@name=\"qq\"]//simpleType[1]/@type", "q.Type")
                .assertXpathEquals("//method[@name=\"qq\"]//simpleType[2]/@type", "q.Type")

                // minimal change
                .comparing(root)
                .assertXpathEquals("//method[@name=\"uq\"]//intersectionType/simpleType[2]/@id")
                .assertXpathEquals("//method[@name=\"nq\"]//intersectionType/simpleType[2]/@id")
                .assertXpathEquals("//method[@name=\"qq\"]//intersectionType/simpleType[2]/@id")
                .assertXpathEquals("//method[@name=\"qq\"]/@id");
    }

    @Test
    void doesNotUpdateTypeWhenNotNeeded() {
        Root root = AstHelper.from(TypeNamesQualifyingTest.class, "doesNotUpdateTypeWhenNotNeeded.th", nodeIdGenerator);
        TypeNameQualifying typeNamesQualifying = new TypeNameQualifying(nodeIdGenerator);

        Root newRoot = typeNamesQualifying.apply(root);

        XmlAssertions.on(newRoot)
                .assertOnPrefix("/root")
                .comparing(root)
                .assertXpathEquals("/@id");
    }

    @Test
    void doesNotUpdateClassWhenNotNeeded() {
        Root root = AstHelper.from(TypeNamesQualifyingTest.class, "doesNotUpdateClassWhenNotNeeded.th", nodeIdGenerator);
        TypeNameQualifying typeNamesQualifying = new TypeNameQualifying(nodeIdGenerator);

        Root newRoot = typeNamesQualifying.apply(root);

        XmlAssertions.on(newRoot)
                .assertOnPrefix("/root")
                .comparing(root)
                .assertXpathEquals("/@id");
    }

    @Test
    void aliases() {
        Root root = AstHelper.from(TypeNamesQualifyingTest.class, "aliases.th", nodeIdGenerator);
        TypeNameQualifying typeNamesQualifying = new TypeNameQualifying(nodeIdGenerator);

        Root newRoot = typeNamesQualifying.apply(root);

        XmlAssertions.on(newRoot)
                .assertXpathEquals("//superType/simpleType/@type", "org.thoriumlang.MyOriginalType");
    }

    @Test
    void superTypeOnType() {
        Root root = AstHelper.from(TypeNamesQualifyingTest.class, "superTypeOnType.th", nodeIdGenerator);
        TypeNameQualifying typeNamesQualifying = new TypeNameQualifying(nodeIdGenerator);

        Root newRoot = typeNamesQualifying.apply(root);

        XmlAssertions.on(newRoot)
                .assertOnPrefix("/root/type/superType")
                .assertXpathEquals("/simpleType/@type", "org.thoriumlang.SuperType")
                .assertOnPrefix("/simpleType/arguments[1]")
                .assertXpathEquals("/simpleType/@type", "org.thoriumlang.OtherSuperType");
    }

    @Test
    void superTypeOnClass() {
        Root root = AstHelper.from(TypeNamesQualifyingTest.class, "superTypeOnClass.th", nodeIdGenerator);
        TypeNameQualifying typeNamesQualifying = new TypeNameQualifying(nodeIdGenerator);

        Root newRoot = typeNamesQualifying.apply(root);

        XmlAssertions.on(newRoot)
                .assertOnPrefix("/root/class/superType")
                .assertXpathEquals("/simpleType/@type", "org.thoriumlang.SuperType")
                .assertOnPrefix("/simpleType/arguments[1]")
                .assertXpathEquals("/simpleType/@type", "org.thoriumlang.OtherSuperType");
    }

    @Test
    void returnTypeOnTypeMethods() {
        Root root = AstHelper.from(TypeNamesQualifyingTest.class, "returnTypeOnTypeMethods.th", nodeIdGenerator);
        TypeNameQualifying typeNamesQualifying = new TypeNameQualifying(nodeIdGenerator);

        Root newRoot = typeNamesQualifying.apply(root);

        XmlAssertions.on(newRoot)
                .assertOnPrefix("/root/type/methods[1]/method/returnType")
                .assertXpathEquals("/simpleType/@type", "org.thoriumlang.ReturnType");
    }

    @Test
    void returnTypeOnClassMethods() {
        Root root = AstHelper.from(TypeNamesQualifyingTest.class, "returnTypeOnClassMethods.th", nodeIdGenerator);
        TypeNameQualifying typeNamesQualifying = new TypeNameQualifying(nodeIdGenerator);

        Root newRoot = typeNamesQualifying.apply(root);

        XmlAssertions.on(newRoot)
                .assertOnPrefix("/root/class/methods[1]/method/returnType")
                .assertXpathEquals("/simpleType/@type", "org.thoriumlang.ReturnType");
    }

    @Test
    void methodParameters() {
        Root root = AstHelper.from(TypeNamesQualifyingTest.class, "methodParameters.th", nodeIdGenerator);
        TypeNameQualifying typeNamesQualifying = new TypeNameQualifying(nodeIdGenerator);

        Root newRoot = typeNamesQualifying.apply(root);

        XmlAssertions.on(newRoot)
                .assertOnPrefix("/root/type/methods[1]/method/parameters[1]/parameter")
                .assertXpathEquals("/simpleType/@type", "org.thoriumlang.ParameterType");
    }

    @Test
    void newAssignmentValue() {
        Root root = AstHelper.from(TypeNamesQualifyingTest.class, "newAssignmentValue.th", nodeIdGenerator);
        TypeNameQualifying typeNamesQualifying = new TypeNameQualifying(nodeIdGenerator);

        Root newRoot = typeNamesQualifying.apply(root);

        XmlAssertions.on(newRoot)
                .assertXpathEquals("//assignment[@name=\"myString\"]//simpleType/@type", "org.thoriumlang.String");
    }

    @Test
    void methodCallValue() {
        Root root = AstHelper.from(TypeNamesQualifyingTest.class, "methodCallValue.th", nodeIdGenerator);
        TypeNameQualifying typeNamesQualifying = new TypeNameQualifying(nodeIdGenerator);

        Root newRoot = typeNamesQualifying.apply(root);

        XmlAssertions.on(newRoot)
                .assertXpathEquals("//method[@name=\"myMethodCallingWithTypeArgs\"]//typeArguments//simpleType/@type", "org.thoriumlang.MyType")
                .assertXpathEquals("//method[@name=\"myMethodCallingWithArguments\"]//methodArguments//typeArguments//simpleType/@type", "org.thoriumlang.MyType");
    }

    @Test
    void attribute() {
        Root root = AstHelper.from(TypeNamesQualifyingTest.class, "attribute.th", nodeIdGenerator);
        TypeNameQualifying typeNamesQualifying = new TypeNameQualifying(nodeIdGenerator);

        Root newRoot = typeNamesQualifying.apply(root);

        XmlAssertions.on(newRoot)
                .assertXpathEquals("//attribute[@name=\"attr\"]//simpleType/@type", "org.thoriumlang.MyType")
                .assertXpathEquals("//attribute[@name=\"attrWithValue\"]//simpleType/@type", "org.thoriumlang.MyType");
    }

    @Test
    void functionType() {
        Root root = AstHelper.from(TypeNamesQualifyingTest.class, "functionType.th", nodeIdGenerator);
        TypeNameQualifying typeNamesQualifying = new TypeNameQualifying(nodeIdGenerator);

        Root newRoot = typeNamesQualifying.apply(root);

        XmlAssertions.on(newRoot)
                .assertXpathEquals("//attribute[@name=\"attrFn\"]//simpleType/@type", "org.thoriumlang.MyType")
                .assertXpathEquals("//attribute[@name=\"attrFnWithParams\"]//arguments/simpleType[1]/@type", "org.thoriumlang.MyType");
    }

    @Test
    void functionValue() {
        Root root = AstHelper.from(TypeNamesQualifyingTest.class, "functionValue.th", nodeIdGenerator);
        TypeNameQualifying typeNamesQualifying = new TypeNameQualifying(nodeIdGenerator);

        Root newRoot = typeNamesQualifying.apply(root);

        XmlAssertions.on(newRoot)
                .assertXpathEquals("//attribute[@name=\"myFn\"]//parameter[@name=\"p\"]/simpleType/@type", "org.thoriumlang.MyType")
                .assertXpathEquals("//attribute[@name=\"myFn\"]//returnType//simpleType/@type", "org.thoriumlang.MyType")
                .assertXpathEquals("//attribute[@name=\"myFn\"]//statements/statement[1]//simpleType/@type", "org.thoriumlang.MyType")
        ;
    }
}
