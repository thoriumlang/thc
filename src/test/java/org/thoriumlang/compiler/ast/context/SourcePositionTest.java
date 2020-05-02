package org.thoriumlang.compiler.ast.context;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

class SourcePositionTest {
    private static Stream<Arguments> endBeforeStartProvider() {
        return Stream.of(
                Arguments.arguments(2, 1, 1, 1),
                Arguments.arguments(1, 2, 1, 1)
        );
    }

    @ParameterizedTest
    @MethodSource("endBeforeStartProvider")
    void endBeforeStart(int startLine, int startColumn, int endLine, int endColumn) {
        Assertions.assertThatThrownBy(() -> new SourcePosition(
                new SourcePosition.Position(startLine, startColumn),
                new SourcePosition.Position(endLine, endColumn),
                Collections.singletonList("text")
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("end cannot be before start");
    }

    @Test
    void oneLine() {
        String text = "line1";
        SourcePosition sourcePosition = new SourcePosition(
                new SourcePosition.Position(1,2),
                new SourcePosition.Position(1,5),
                Collections.singletonList(text)
        );
        Assertions.assertThat(sourcePosition.getStartLine()).isEqualTo(1);
        Assertions.assertThat(sourcePosition.getEndLine()).isEqualTo(1);
        Assertions.assertThat(sourcePosition.getStartColumn()).isEqualTo(2);
        Assertions.assertThat(sourcePosition.getLength()).isEqualTo(3);
        Assertions.assertThat(sourcePosition.getText()).isEqualTo(text);
        Assertions.assertThat(sourcePosition.getLines()).containsExactly("line1");
        Assertions.assertThat(sourcePosition.toString()).isEqualTo("1:2");
    }

    @Test
    void twoLines() {
        String text = "line1\nline2";
        SourcePosition sourcePosition = new SourcePosition(
                new SourcePosition.Position(1, 1),
                new SourcePosition.Position(2, 5),
                Arrays.asList(text.split("\n"))
        );
        Assertions.assertThat(sourcePosition.getStartLine()).isEqualTo(1);
        Assertions.assertThat(sourcePosition.getEndLine()).isEqualTo(2);
        Assertions.assertThat(sourcePosition.getStartColumn()).isEqualTo(1);
        Assertions.assertThat(sourcePosition.getLength()).isEqualTo(text.length());
        Assertions.assertThat(sourcePosition.getText()).isEqualTo(text);
        Assertions.assertThat(sourcePosition.getLines()).containsExactly("line1", "line2");
        Assertions.assertThat(sourcePosition.toString()).isEqualTo("1:1");
    }

    @Test
    void multiLine() {
        String text = "line1\nline2\nline3";
        SourcePosition sourcePosition = new SourcePosition(
                new SourcePosition.Position(1, 1),
                new SourcePosition.Position(3, 4),
                Arrays.asList(text.split("\n"))
        );
        Assertions.assertThat(sourcePosition.getStartLine()).isEqualTo(1);
        Assertions.assertThat(sourcePosition.getEndLine()).isEqualTo(3);
        Assertions.assertThat(sourcePosition.getStartColumn()).isEqualTo(1);
        Assertions.assertThat(sourcePosition.getLength()).isEqualTo(text.length() - 1);
        Assertions.assertThat(sourcePosition.getText()).isEqualTo(text);
        Assertions.assertThat(sourcePosition.getLines()).containsExactly("line1", "line2", "line3");
        Assertions.assertThat(sourcePosition.toString()).isEqualTo("1:1");
    }
}