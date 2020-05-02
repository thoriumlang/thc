package org.thoriumlang.compiler.api.errors;

import com.google.common.base.Strings;
import org.antlr.v4.runtime.RecognitionException;
import org.thoriumlang.compiler.ast.context.SourcePosition;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

class DefaultErrorFormatter implements SemanticErrorFormatter, SyntaxErrorFormatter{
    @Override
    public String format(SourcePosition sourcePosition, String message, RecognitionException exception) {
        return format(sourcePosition, message);
    }

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
