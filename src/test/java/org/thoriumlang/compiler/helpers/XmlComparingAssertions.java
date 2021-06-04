package org.thoriumlang.compiler.helpers;

import org.dom4j.Document;

public class XmlComparingAssertions {
    private final XmlAssertions parent;
    private final Document expected;
    private final String prefix;

    public XmlComparingAssertions(XmlAssertions parent, Document expected, String prefix) {
        this.parent = parent;
        this.expected = expected;
        this.prefix = prefix;
    }

    public XmlAssertions unwrap() {
        return parent;
    }

    public XmlComparingAssertions assertXpathEquals(String xpath) {
        parent.assertXpathEquals(xpath, extractValue(xpath));
        return this;
    }

    private String extractValue(String xpath) {
        return expected.selectSingleNode(prefix + xpath).getStringValue();
    }
}
