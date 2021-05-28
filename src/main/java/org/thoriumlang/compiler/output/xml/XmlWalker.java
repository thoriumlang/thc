package org.thoriumlang.compiler.output.xml;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.thoriumlang.compiler.ast.nodes.Attribute;
import org.thoriumlang.compiler.ast.nodes.BooleanValue;
import org.thoriumlang.compiler.ast.nodes.Class;
import org.thoriumlang.compiler.ast.nodes.DirectAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.FunctionValue;
import org.thoriumlang.compiler.ast.nodes.HasName;
import org.thoriumlang.compiler.ast.nodes.HasTypeParameters;
import org.thoriumlang.compiler.ast.nodes.HasVisibility;
import org.thoriumlang.compiler.ast.nodes.IdentifierValue;
import org.thoriumlang.compiler.ast.nodes.IndirectAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.Method;
import org.thoriumlang.compiler.ast.nodes.MethodCallValue;
import org.thoriumlang.compiler.ast.nodes.MethodSignature;
import org.thoriumlang.compiler.ast.nodes.NestedValue;
import org.thoriumlang.compiler.ast.nodes.NewAssignmentValue;
import org.thoriumlang.compiler.ast.nodes.Node;
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
import org.thoriumlang.compiler.ast.visitor.Visitor;
import org.thoriumlang.compiler.output.Walker;

import java.util.Optional;

public class XmlWalker implements Walker<Document>, Visitor<Element> {
    private final Root root;

    public XmlWalker(Root root) {
        this.root = root;
    }

    @Override
    public Document walk() {
        Document document = DocumentHelper.createDocument();
        document.add(root.accept(this));
        return document;
    }

    @Override
    public Element visit(Root node) {
        Element element = newElement("root", node);
        element.addAttribute("namespace", node.getNamespace());

        if (!node.getUses().isEmpty()) {
            Element uses = DocumentHelper.createElement("uses");
            element.add(uses);
            node.getUses().stream()
                    .map(n -> n.accept(this))
                    .forEach(uses::add);
        }

        Element topLevelElement = node.getTopLevelNode().accept(this);
        element.add(topLevelElement);

        return element;
    }

    private Element newElement(String name, Node node) {
        Element element = DocumentHelper.createElement(name);
        element.addAttribute("id", node.getNodeId().format("%d"));
        return element;
    }

    @Override
    public Element visit(Use node) {
        Element element = newElement("use", node);
        element.addAttribute("from", node.getFrom());
        element.addAttribute("to", node.getTo());
        return element;
    }

    @Override
    public Element visit(Type node) {
        Element element = newElement("type", node);
        addVisibility(element, node);
        addName(element, node);

        Element superType = DocumentHelper.createElement("superType");
        element.add(superType);
        superType.add(node.getSuperType().accept(this));

        visit((HasTypeParameters) node).ifPresent(element::add);

        if (!node.getMethods().isEmpty()) {
            Element methods = DocumentHelper.createElement("methods");
            element.add(methods);
            node.getMethods().stream()
                    .map(m -> m.accept(this))
                    .forEach(methods::add);
        }

        return element;
    }

    private void addName(Element element, HasName node) {
        element.addAttribute("name", node.getName());
    }

    private void addVisibility(Element element, HasVisibility node) {
        element.addAttribute("visibility", node.getVisibility().toString());
    }

    public Optional<Element> visit(HasTypeParameters node) {
        if (node.getTypeParameters().isEmpty()) {
            return Optional.empty();
        }

        Element element = DocumentHelper.createElement("typeParameters");
        node.getTypeParameters().stream()
                .map(tp -> tp.accept(this))
                .forEach(element::add);
        return Optional.of(element);
    }

    @Override
    public Element visit(Class node) {
        Element element = newElement("class", node);
        addVisibility(element, node);
        addName(element, node);

        Element superType = DocumentHelper.createElement("superType");
        element.add(superType);
        superType.add(node.getSuperType().accept(this));

        visit((HasTypeParameters) node).ifPresent(element::add);

        if (!node.getAttributes().isEmpty()) {
            Element attributes = DocumentHelper.createElement("attributes");
            element.add(attributes);
            node.getAttributes().stream()
                    .map(a -> a.accept(this))
                    .forEach(attributes::add);
        }

        if (!node.getMethods().isEmpty()) {
            Element methods = DocumentHelper.createElement("methods");
            element.add(methods);
            node.getMethods().stream()
                    .map(m -> m.accept(this))
                    .forEach(methods::add);
        }

        return element;
    }

    @Override
    public Element visit(TypeSpecIntersection node) {
        Element element = newElement("intersectionType", node);
        node.getTypes().stream()
                .map(t -> t.accept(this))
                .forEach(element::add);
        return element;
    }

    @Override
    public Element visit(TypeSpecUnion node) {
        Element element = newElement("unionType", node);
        node.getTypes().stream()
                .map(t -> t.accept(this))
                .forEach(element::add);
        return element;
    }

    @Override
    public Element visit(TypeSpecSimple node) {
        Element element = newElement("simpleType", node);
        element.addAttribute("type", node.getType());

        if (!node.getArguments().isEmpty()) {
            Element arguments = DocumentHelper.createElement("arguments");
            element.add(arguments);
            node.getArguments().stream()
                    .map(a -> a.accept(this))
                    .forEach(arguments::add);

        }

        return element;
    }

    @Override
    public Element visit(TypeSpecFunction node) {
        Element element = newElement("functionType", node);

        Element returnType = DocumentHelper.createElement("returnType");
        element.add(returnType);
        returnType.add(node.getReturnType().accept(this));

        if (!node.getArguments().isEmpty()) {
            Element arguments = DocumentHelper.createElement("arguments");
            element.add(arguments);
            node.getArguments().stream()
                    .map(a -> a.accept(this))
                    .forEach(arguments::add);
        }

        return element;
    }

    @Override
    public Element visit(TypeSpecInferred node) {
        return newElement("inferredType", node);
    }

    @Override
    public Element visit(MethodSignature node) {
        Element element = newElement("method", node);
        addVisibility(element, node);
        addName(element, node);

        Element returnType = DocumentHelper.createElement("returnType");
        element.add(returnType);
        returnType.add(node.getReturnType().accept(this));

        visit((HasTypeParameters) node).ifPresent(element::add);

        if (!node.getParameters().isEmpty()) {
            Element parameters = DocumentHelper.createElement("parameters");
            element.add(parameters);
            node.getParameters().stream()
                    .map(p -> p.accept(this))
                    .forEach(parameters::add);
        }

        return element;
    }

    @Override
    public Element visit(Parameter node) {
        Element element = newElement("parameter", node);
        addName(element, node);
        element.add(node.getType().accept(this));
        return element;
    }

    @Override
    public Element visit(TypeParameter node) {
        Element element = newElement("typeParameter", node);
        addName(element, node);
        return element;
    }

    @Override
    public Element visit(StringValue node) {
        Element element = newElement("string", node);
        element.add(DocumentHelper.createCDATA(node.getValue()));
        return element;
    }

    @Override
    public Element visit(NumberValue node) {
        Element element = newElement("number", node);
        element.add(DocumentHelper.createCDATA(node.getValue()));
        return element;
    }

    @Override
    public Element visit(BooleanValue node) {
        Element element = newElement("boolean", node);
        element.add(DocumentHelper.createCDATA(String.valueOf(node.getValue())));
        return element;
    }

    @Override
    public Element visit(NoneValue node) {
        return newElement("none", node);
    }

    @Override
    public Element visit(IdentifierValue node) {
        Element element = newElement("identifier", node);
        element.add(node.getReference().accept(this));
        return element;
    }

    @Override
    public Element visit(NewAssignmentValue node) {
        Element element = newElement("assignment", node);
        addName(element, node);
        element.addAttribute("type", node.getMode().name().toLowerCase());

        Element type = DocumentHelper.createElement("type");
        element.add(type);
        type.add(node.getType().accept(this));

        Element value = DocumentHelper.createElement("value");
        element.add(value);
        value.add(node.getValue().accept(this));

        return element;
    }

    @Override
    public Element visit(IndirectAssignmentValue node) {
        Element element = newElement("assignment", node);
        element.addAttribute("type", "indirect");
        element.add(node.getReference().accept(this));
        element.add(node.getIndirectValue().accept(this));
        return element;
    }

    @Override
    public Element visit(DirectAssignmentValue node) {
        Element element = newElement("assignment", node);
        element.addAttribute("type", "direct");
        element.add(node.getReference().accept(this));
        element.add(node.getValue().accept(this));
        return element;
    }

    @Override
    public Element visit(MethodCallValue node) {
        Element element = newElement("methodCall", node);
        element.add(node.getMethodReference().accept(this));

        if (!node.getTypeArguments().isEmpty()) {
            Element typeArguments = DocumentHelper.createElement("typeArguments");
            element.add(typeArguments);
            node.getTypeArguments().stream()
                    .map(ta -> ta.accept(this))
                    .forEach(typeArguments::add);
        }

        if (!node.getMethodArguments().isEmpty()) {
            Element methodArguments = DocumentHelper.createElement("methodArguments");
            element.add(methodArguments);
            node.getMethodArguments().stream()
                    .map(ma -> ma.accept(this))
                    .forEach(methodArguments::add);
        }

        return element;
    }

    @Override
    public Element visit(NestedValue node) {
        Element element = newElement("nestedValue", node);

        Element outer = DocumentHelper.createElement("outer");
        element.add(outer);
        outer.add(node.getOuter().accept(this));

        Element inner = DocumentHelper.createElement("inner");
        element.add(inner);
        inner.add(node.getInner().accept(this));

        return element;
    }

    @Override
    public Element visit(FunctionValue node) {
        Element element = newElement("functionValue", node);

        Element returnType = DocumentHelper.createElement("returnType");
        element.add(returnType);
        returnType.add(node.getReturnType().accept(this));

        if (!node.getTypeParameters().isEmpty()) {
            Element typeParameters = DocumentHelper.createElement("typeParameters");
            element.add(typeParameters);
            node.getTypeParameters().stream()
                    .map(n -> n.accept(this))
                    .forEach(typeParameters::add);
        }

        if (!node.getParameters().isEmpty()) {
            Element parameters = DocumentHelper.createElement("parameters");
            element.add(parameters);
            node.getParameters().stream()
                    .map(n -> n.accept(this))
                    .forEach(parameters::add);
        }

        if (!node.getStatements().isEmpty()) {
            Element statements = DocumentHelper.createElement("statements");
            element.add(statements);
            node.getStatements().stream()
                    .map(n -> n.accept(this))
                    .forEach(statements::add);
        }

        return element;
    }

    @Override
    public Element visit(Statement node) {
        Element element = newElement("statement", node);
        element.addAttribute("last", String.valueOf(node.isLast()));
        element.add(node.getValue().accept(this));
        return element;
    }

    @Override
    public Element visit(Method node) {
        Element element = node.getSignature().accept(this);

        if (!node.getStatements().isEmpty()) {
            Element statements = DocumentHelper.createElement("statements");
            element.add(statements);
            node.getStatements().stream()
                    .map(s -> s.accept(this))
                    .forEach(statements::add);
        }

        return element;
    }

    @Override
    public Element visit(Attribute node) {
        Element element = newElement("attribute", node);
        addName(element, node);

        Element type = DocumentHelper.createElement("type");
        element.add(type);
        type.add(node.getType().accept(this));

        Element value = DocumentHelper.createElement("value");
        element.add(value);
        value.add(node.getValue().accept(this));

        return element;
    }

    @Override
    public Element visit(Reference node) {
        Element element = newElement("reference", node);
        addName(element, node);
        element.addAttribute("allowForwardReference", String.valueOf(node.allowForwardReference()));
        return element;
    }
}
