package org.thoriumlang.compiler.output.xml;

import org.dom4j.Document;
import org.junit.jupiter.api.Test;
import org.thoriumlang.compiler.ast.nodes.Attribute;
import org.thoriumlang.compiler.ast.nodes.BooleanValue;
import org.thoriumlang.compiler.ast.nodes.Class;
import org.thoriumlang.compiler.ast.nodes.DirectAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.FunctionValue;
import org.thoriumlang.compiler.ast.nodes.IdentifierValue;
import org.thoriumlang.compiler.ast.nodes.IndirectAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.Method;
import org.thoriumlang.compiler.ast.nodes.MethodCallValue;
import org.thoriumlang.compiler.ast.nodes.MethodSignature;
import org.thoriumlang.compiler.ast.nodes.Mode;
import org.thoriumlang.compiler.ast.nodes.NestedValue;
import org.thoriumlang.compiler.ast.nodes.NewAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.NodeIdGenerator;
import org.thoriumlang.compiler.ast.nodes.NoneValue;
import org.thoriumlang.compiler.ast.nodes.NumberValue;
import org.thoriumlang.compiler.ast.nodes.Parameter;
import org.thoriumlang.compiler.ast.nodes.Reference;
import org.thoriumlang.compiler.ast.nodes.Root;
import org.thoriumlang.compiler.ast.nodes.Statement;
import org.thoriumlang.compiler.ast.nodes.StringValue;
import org.thoriumlang.compiler.ast.nodes.Type;
import org.thoriumlang.compiler.ast.nodes.TypeParameter;
import org.thoriumlang.compiler.ast.nodes.TypeSpecFunction;
import org.thoriumlang.compiler.ast.nodes.TypeSpecInferred;
import org.thoriumlang.compiler.ast.nodes.TypeSpecIntersection;
import org.thoriumlang.compiler.ast.nodes.TypeSpecSimple;
import org.thoriumlang.compiler.ast.nodes.TypeSpecUnion;
import org.thoriumlang.compiler.ast.nodes.Use;
import org.thoriumlang.compiler.ast.nodes.Visibility;
import org.thoriumlang.compiler.helpers.XmlAssertions;

import java.util.Arrays;
import java.util.Collections;

class XmlWalkerTest {
    @Test
    void walkType() {
        NodeIdGenerator nodeIdGenerator = new NodeIdGenerator();
        XmlWalker xmlWalker = new XmlWalker(new Root(
                nodeIdGenerator.next(),
                "some.namespace",
                Collections.singletonList(
                        new Use(nodeIdGenerator.next(), "some.other.namespace.Type", "Type")
                ),
                new Type(
                        nodeIdGenerator.next(),
                        Visibility.PUBLIC,
                        "MyType",
                        Collections.singletonList(new TypeParameter(nodeIdGenerator.next(), "T")),
                        new TypeSpecSimple(
                                nodeIdGenerator.next(),
                                "SuperType",
                                Collections.singletonList(
                                        new TypeSpecSimple(
                                                nodeIdGenerator.next(),
                                                "OtherType",
                                                Collections.emptyList()
                                        )
                                )
                        ),
                        Collections.singletonList(
                                new MethodSignature(
                                        nodeIdGenerator.next(),
                                        Visibility.PRIVATE,
                                        "myMethod",
                                        Collections.singletonList(new TypeParameter(nodeIdGenerator.next(), "U")),
                                        Arrays.asList(
                                                new Parameter(
                                                        nodeIdGenerator.next(),
                                                        "myParameter",
                                                        new TypeSpecSimple(
                                                                nodeIdGenerator.next(),
                                                                "PT",
                                                                Collections.emptyList()
                                                        )
                                                )
                                        ),
                                        new TypeSpecIntersection(nodeIdGenerator.next(), Arrays.asList(
                                                new TypeSpecSimple(
                                                        nodeIdGenerator.next(),
                                                        "R1",
                                                        Collections.emptyList()
                                                ),
                                                new TypeSpecSimple(
                                                        nodeIdGenerator.next(),
                                                        "R2",
                                                        Collections.emptyList()
                                                )
                                        ))
                                )
                        )
                )
        ));
        Document doc = xmlWalker.walk();

        XmlAssertions.on(doc)
                .assertXpathEquals("/root/@namespace", "some.namespace")
                .assertXpathEquals("/root/uses/use[1]/@from", "some.other.namespace.Type")
                .assertXpathEquals("/root/uses/use[1]/@to", "Type")
                .assertXpathEquals("/root/type/@name", "MyType")
                .assertXpathEquals("/root/type/@visibility", "PUBLIC")
                .assertXpathEquals("/root/type/superType/simpleType/@type", "SuperType")
                .assertXpathEquals("/root/type/superType/simpleType/arguments[1]/simpleType/@type", "OtherType")
                .assertXpathEquals("/root/type/typeParameters/typeParameter[1]/@name", "T")
                .assertXpathEquals("/root/type/methods/method[1]/@name", "myMethod")
                .assertXpathEquals("/root/type/methods/method[1]/@visibility", "PRIVATE")
                .assertXpathEquals("/root/type/methods/method[1]/returnType/intersectionType/simpleType[1]/@type", "R1")
                .assertXpathEquals("/root/type/methods/method[1]/returnType/intersectionType/simpleType[2]/@type", "R2")
                .assertXpathEquals("/root/type/methods/method[1]/typeParameters/typeParameter[1]/@name", "U")
                .assertXpathEquals("/root/type/methods/method[1]/parameters/parameter[1]/@name", "myParameter")
                .assertXpathEquals("/root/type/methods/method[1]/parameters/parameter[1]/simpleType/@type", "PT");
    }

    @Test
    void walkClass() {
        NodeIdGenerator nodeIdGenerator = new NodeIdGenerator();
        XmlWalker xmlWalker = new XmlWalker(new Root(
                nodeIdGenerator.next(),
                "some.namespace",
                Collections.emptyList(),
                new Class(
                        nodeIdGenerator.next(),
                        Visibility.PUBLIC,
                        "MyClass",
                        Collections.singletonList(new TypeParameter(
                                nodeIdGenerator.next(),
                                "T"
                        )),
                        new TypeSpecUnion(
                                nodeIdGenerator.next(),
                                Arrays.asList(
                                        new TypeSpecSimple(nodeIdGenerator.next(), "SuperType1",
                                                Collections.emptyList()),
                                        new TypeSpecSimple(nodeIdGenerator.next(), "SuperType2",
                                                Collections.emptyList())
                                )
                        ),
                        Arrays.asList(
                                new Method(
                                        nodeIdGenerator.next(),
                                        new MethodSignature(
                                                nodeIdGenerator.next(),
                                                Visibility.PRIVATE,
                                                "myMethod",
                                                Collections.emptyList(),
                                                Collections.emptyList(),
                                                new TypeSpecSimple(nodeIdGenerator.next(), "None",
                                                        Collections.emptyList())
                                        ),
                                        Arrays.asList(
                                                new Statement(
                                                        nodeIdGenerator.next(),
                                                        new StringValue(nodeIdGenerator.next(), "my string"),
                                                        false
                                                ),
                                                new Statement(
                                                        nodeIdGenerator.next(),
                                                        new NumberValue(nodeIdGenerator.next(), "42"),
                                                        false
                                                ),
                                                new Statement(
                                                        nodeIdGenerator.next(),
                                                        new BooleanValue(nodeIdGenerator.next(), false),
                                                        false
                                                ),
                                                new Statement(
                                                        nodeIdGenerator.next(),
                                                        new NoneValue(nodeIdGenerator.next()),
                                                        false
                                                ),
                                                new Statement(
                                                        nodeIdGenerator.next(),
                                                        new IdentifierValue(
                                                                nodeIdGenerator.next(),
                                                                new Reference(
                                                                        nodeIdGenerator.next(),
                                                                        "ref",
                                                                        true
                                                                )
                                                        ),
                                                        false
                                                ),
                                                new Statement(
                                                        nodeIdGenerator.next(),
                                                        new MethodCallValue(
                                                                nodeIdGenerator.next(),
                                                                new Reference(
                                                                        nodeIdGenerator.next(),
                                                                        "otherMethod",
                                                                        true
                                                                ),
                                                                Collections.singletonList(
                                                                        new TypeSpecSimple(
                                                                                nodeIdGenerator.next(),
                                                                                "Type",
                                                                                Collections.emptyList()
                                                                        )
                                                                ),
                                                                Collections.singletonList(
                                                                        new StringValue(
                                                                                nodeIdGenerator.next(),
                                                                                "my parameter"
                                                                        )
                                                                )
                                                        ),
                                                        false
                                                ),
                                                new Statement(
                                                        nodeIdGenerator.next(),
                                                        new NestedValue(
                                                                nodeIdGenerator.next(),
                                                                new StringValue(nodeIdGenerator.next(), "val"),
                                                                new MethodCallValue(
                                                                        nodeIdGenerator.next(),
                                                                        new Reference(
                                                                                nodeIdGenerator.next(),
                                                                                "toString",
                                                                                true
                                                                        ),
                                                                        Collections.emptyList(),
                                                                        Collections.emptyList()
                                                                )
                                                        ),
                                                        false
                                                ),
                                                new Statement(
                                                        nodeIdGenerator.next(),
                                                        new DirectAssignmentValue(
                                                                nodeIdGenerator.next(),
                                                                new Reference(
                                                                        nodeIdGenerator.next(),
                                                                        "ref",
                                                                        false
                                                                ),
                                                                new StringValue(nodeIdGenerator.next(), "my val")
                                                        ),
                                                        false
                                                ),
                                                new Statement(
                                                        nodeIdGenerator.next(),
                                                        new IndirectAssignmentValue(
                                                                nodeIdGenerator.next(),
                                                                new IdentifierValue(
                                                                        nodeIdGenerator.next(),
                                                                        new Reference(
                                                                                nodeIdGenerator.next(),
                                                                                "ref",
                                                                                false
                                                                        )
                                                                ),
                                                                new Reference(
                                                                        nodeIdGenerator.next(),
                                                                        "attr",
                                                                        false
                                                                ),
                                                                new StringValue(nodeIdGenerator.next(), "val")
                                                        ),
                                                        false
                                                ),
                                                new Statement(
                                                        nodeIdGenerator.next(),
                                                        new NewAssignmentValue(
                                                                nodeIdGenerator.next(),
                                                                "name",
                                                                new TypeSpecInferred(nodeIdGenerator.next()),
                                                                new StringValue(nodeIdGenerator.next(), "str"),
                                                                Mode.VAL
                                                        ),
                                                        true
                                                )
                                        )
                                )
                        ),
                        Arrays.asList(
                                new Attribute(
                                        nodeIdGenerator.next(),
                                        "attribute1",
                                        new TypeSpecFunction(
                                                nodeIdGenerator.next(),
                                                Collections.singletonList(
                                                        new TypeSpecSimple(
                                                                nodeIdGenerator.next(),
                                                                "ArgType",
                                                                Collections.emptyList()
                                                        )
                                                ),
                                                new TypeSpecSimple(
                                                        nodeIdGenerator.next(),
                                                        "Type",
                                                        Collections.emptyList()
                                                )
                                        ),
                                        new FunctionValue(
                                                nodeIdGenerator.next(),
                                                Collections.singletonList(
                                                        new TypeParameter(
                                                                nodeIdGenerator.next(),
                                                                "T"
                                                        )
                                                ),
                                                Collections.singletonList(
                                                        new Parameter(
                                                                nodeIdGenerator.next(),
                                                                "param",
                                                                new TypeSpecSimple(
                                                                        nodeIdGenerator.next(),
                                                                        "Type",
                                                                        Collections.emptyList()
                                                                )
                                                        )
                                                ),
                                                new TypeSpecSimple(
                                                        nodeIdGenerator.next(),
                                                        "RetType",
                                                        Collections.emptyList()
                                                ),
                                                Collections.singletonList(
                                                        new Statement(
                                                                nodeIdGenerator.next(),
                                                                new StringValue(nodeIdGenerator.next(), "val"),
                                                                true
                                                        )
                                                )
                                        ),
                                        Mode.VAL
                                )
                        )
                )
        ));

        Document doc = xmlWalker.walk();

        XmlAssertions.on(doc)
                .assertXpathEquals("/root/@namespace", "some.namespace")
                .assertXpathEquals("/root/class/@name", "MyClass")
                .assertXpathEquals("/root/class/@visibility", "PUBLIC")
                .assertXpathEquals("/root/class/typeParameters/typeParameter[1]/@name", "T")
                .assertXpathEquals("/root/class/superType/unionType/simpleType[1]/@type", "SuperType1")
                .assertXpathEquals("/root/class/superType/unionType/simpleType[2]/@type", "SuperType2")
                .assertXpathEquals("/root/class/methods/method[1]/@name", "myMethod")
                .assertXpathEquals("/root/class/methods/method[1]/@visibility", "PRIVATE")
                .assertXpathEquals("/root/class/methods/method[1]/returnType/simpleType/@type", "None")
                .assertOnPrefix("/root/class/methods/method[1]/statements")
                .assertXpathEquals("/statement[1]/@last", "false")
                .assertXpathEquals("/statement[1]/string/text()", "my string")
                .assertXpathEquals("/statement[2]/@last", "false")
                .assertXpathEquals("/statement[2]/number/text()", "42")
                .assertXpathEquals("/statement[3]/@last", "false")
                .assertXpathEquals("/statement[3]/boolean/text()", "false")
                .assertXpathEquals("/statement[4]/@last", "false")
                .assertXpathExists("/statement[4]/none")
                .assertXpathEquals("/statement[5]/@last", "false")
                .assertXpathEquals("/statement[5]/identifier/reference/@allowForwardReference", "true")
                .assertXpathEquals("/statement[5]/identifier/reference/@name", "ref")
                .assertXpathEquals("/statement[6]/@last", "false")
                .assertXpathEquals("/statement[6]/methodCall/reference/@name", "otherMethod")
                .assertXpathEquals("/statement[6]/methodCall/typeArguments/simpleType[1]/@type", "Type")
                .assertXpathEquals("/statement[6]/methodCall/methodArguments/string[1]/text()", "my parameter")
                .assertXpathExists("/statement[7]/nestedValue/inner/methodCall")
                .assertXpathExists("/statement[7]/nestedValue/outer/string")
                .assertXpathEquals("/statement[8]/assignment/@type", "direct")
                .assertXpathExists("/statement[8]/assignment/reference")
                .assertXpathExists("/statement[8]/assignment/string")
                .assertXpathEquals("/statement[9]/assignment/@type", "indirect")
                .assertXpathExists("/statement[9]/assignment/reference")
                .assertXpathExists("/statement[9]/assignment/identifier")
                .assertXpathEquals("/statement[10]/assignment/@type", "val")
                .assertXpathEquals("/statement[10]/assignment/@name", "name")
                .assertXpathExists("/statement[10]/assignment/type/inferredType")
                .assertXpathExists("/statement[10]/assignment/value/string")
                .removePrefix()
                .assertOnPrefix("/root/class/attributes")
                .assertXpathEquals("/attribute[1]/@name", "attribute1")
                .assertXpathEquals("/attribute[1]/type/functionType/returnType/simpleType/@type", "Type")
                .assertXpathEquals("/attribute[1]/type/functionType/arguments/simpleType[1]/@type", "ArgType")
                .assertXpathEquals("/attribute[1]/value/functionValue/parameters/parameter[1]/simpleType[1]/@type", "Type")
                .assertXpathEquals("/attribute[1]/value/functionValue/statements/statement[1]/string/text()", "val")
                .assertXpathEquals("/attribute[1]/value/functionValue/typeParameters/typeParameter[1]/@name", "T")
                .assertXpathEquals("/attribute[1]/value/functionValue/returnType/simpleType/@type", "RetType");
    }
}