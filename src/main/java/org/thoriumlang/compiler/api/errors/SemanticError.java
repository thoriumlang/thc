/*
 * Copyright 2020 Christophe Pollet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thoriumlang.compiler.api.errors;

import com.google.common.base.Strings;
import org.thoriumlang.compiler.ast.context.SourcePosition;
import org.thoriumlang.compiler.ast.nodes.Node;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

// TODO have one for each type of error? symbol not found, symbol already defined, etc.?
public class SemanticError implements CompilationError {
    private final Node node;
    private final String message;

    public SemanticError(String message, Node node) {
        this.node = node;
        this.message = message;
    }

    public Node getNode() {
        return node;
    }

    public String format(SemanticErrorFormatter formatter) {
        return formatter.format(node.getContext().require(SourcePosition.class), message);
    }

    @Override
    public String toString() {
        return format(new DefaultFormatter());
    }

    private static class DefaultFormatter implements SemanticErrorFormatter {
        @Override
        public String format(SourcePosition sourcePosition, String message) {
            int padding = lineNumberPrefixWidth(sourcePosition.getEndLine()) + sourcePosition.getStartColumn() - 1;
            int underlineLength = Math.min(
                    sourcePosition.getLines().get(0).length() - sourcePosition.getStartLine() - 2,
                    sourcePosition.getLength()
            );
            boolean errorSpansMultipleLines = sourcePosition.getLength() > underlineLength;

            String underline = Strings.repeat(" ", padding) +
                    Strings.repeat("^", underlineLength) +
                    (errorSpansMultipleLines ? " °°°" : "");

            List<String> lines =
                    sourcePosition.getLines()
                            .stream()
                            .map(prefixWithLineNumber(sourcePosition.getStartLine(), sourcePosition.getEndLine()))
                            .collect(Collectors.toList());
            lines.add(1, underline);

            return String.format("%s%n%s%non line %d, column %d",
                    message,
                    String.join("\n", lines),
                    sourcePosition.getStartLine(),
                    sourcePosition.getStartColumn()
            );
        }

        private Function<String, String> prefixWithLineNumber(int firstLine, int lastLine) {
            AtomicInteger lineNumber = new AtomicInteger(firstLine);
            return line -> String.format(lineNumberPrefixFormat(lastLine) + "%s", lineNumber.getAndIncrement(), line);
        }

        private String lineNumberPrefixFormat(int lastLine) {
            return String.format("  %%%dd. ", numberWidth(lastLine));
        }

        private int numberWidth(int number) {
            return (int) Math.floor(Math.log10(number)) + 1;
        }

        private int lineNumberPrefixWidth(int lastLine) {
            return String.format(lineNumberPrefixFormat(lastLine), lastLine).length();
        }
    }
}
