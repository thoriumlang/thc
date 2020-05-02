/*
 * Copyright 2019 Christophe Pollet
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
package org.thoriumlang.compiler.ast.context;

import java.util.List;
import java.util.Objects;

public class SourcePosition {
    private final Position start;
    private final Position end;
    private final String text;
    private final List<String> lines;

    public SourcePosition(Position start, Position end, List<String> lines) {
        if (end.compareTo(start) < 0) {
            throw new IllegalArgumentException("end cannot be before start");
        }
        this.start = start;
        this.end = end;
        this.text = String.join("\n", lines);
        this.lines = lines;
    }

    @Override
    public String toString() {
        return String.format("%d:%d", start.line, start.column);
    }

    public int getStartLine() {
        return start.line;
    }

    public int getEndLine() {
        return end.line;
    }

    public int getStartColumn() {
        return start.column;
    }

    public String getText() {
        return text;
    }

    public List<String> getLines() {
        return lines;
    }

    public int getLength() {
        if (start.line == end.line) {
            return end.column - start.column;
        }
        int firstLineToEnd = lines.get(0).length() - (start.column - 1) + 1;
        int lastLineFromStart = end.column;
        int inBetween = end.line > start.line + 1 ?
                lines
                        .subList(1, end.line - start.line)
                        .stream()
                        .map(l -> l.length() + 1)
                        .reduce(Integer::sum)
                        .orElse(0) :
                0;
        return firstLineToEnd + lastLineFromStart + inBetween;
    }

    public static class Position implements Comparable<Position> {
        private final int line;
        private final int column;

        public Position(int line, int column) {
            this.line = line;
            this.column = column;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Position position = (Position) o;
            return line == position.line &&
                    column == position.column;
        }

        @Override
        public int hashCode() {
            return Objects.hash(line, column);
        }

        @Override
        public int compareTo(Position other) {
            if (other.equals(this)) {
                return 0;
            }
            if (other.line > line || (other.line == line && other.column > column)) {
                return -1;
            }
            return 1;
        }
    }
}
