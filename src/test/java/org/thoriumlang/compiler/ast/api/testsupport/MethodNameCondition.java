package org.thoriumlang.compiler.ast.api.testsupport;

import org.assertj.core.api.Condition;
import org.thoriumlang.compiler.ast.api.Method;

public class MethodNameCondition extends Condition<Method> {
    public MethodNameCondition(String expectedName) {
        super(m -> m.getName().equals(expectedName), "%s", expectedName);
    }
}
