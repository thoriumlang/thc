package org.thoriumlang.compiler.helpers;

import org.dom4j.Document;
import org.dom4j.Node;
import org.junit.jupiter.api.Assertions;

import java.util.Objects;

public final class XmlAssertions {
    private final XmlAssertions prev;
    private final Document document;
    private final String prefix;

    private XmlAssertions(Document document) {
        this(document, "", null);
    }

    public XmlAssertions(Document document, String prefix, XmlAssertions prev) {
        this.document = document;
        this.prefix = prefix;
        this.prev = prev;
    }

    public static XmlAssertions on(Document document) {
        return new XmlAssertions(document);
    }

    public XmlAssertions assertXpathEquals(String xpath, String value) {
        Node node = document.selectSingleNode(prefix + xpath);

        if (node == null) {
            Assertions.fail("Node " + prefix + xpath + " not found");
            return this;
        }

        Assertions.assertEquals(value, node.getStringValue());
        return this;
    }

    public XmlAssertions assertXpathExists(String xpath) {
        Node node = document.selectSingleNode(prefix + xpath);

        if (node == null) {
            Assertions.fail("Node " + prefix + xpath + " not found");
            return this;
        }

        return this;
    }

    public XmlAssertions assertOnPrefix(String xpath) {
        return new XmlAssertions(document, prefix + xpath, this);
    }

    public XmlAssertions removePrefix() {
        return Objects.requireNonNull(prev);
    }
}
