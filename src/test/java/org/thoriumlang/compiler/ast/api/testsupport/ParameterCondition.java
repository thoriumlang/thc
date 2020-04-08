package org.thoriumlang.compiler.ast.api.testsupport;

import org.assertj.core.api.Condition;
import org.thoriumlang.compiler.ast.api.Parameter;

public class ParameterCondition extends Condition<Parameter> {
    public ParameterCondition(String name, String type) {
        super(p -> p.getName().equals(name) && p.getType().getName().equals(type), "%s: %s", name, type);
    }
}
