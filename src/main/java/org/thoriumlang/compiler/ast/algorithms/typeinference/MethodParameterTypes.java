package org.thoriumlang.compiler.ast.algorithms.typeinference;

import org.thoriumlang.compiler.ast.nodes.Node;
import org.thoriumlang.compiler.ast.nodes.TypeSpec;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class MethodParameterTypes {
    private final List<String> parameterTypes;

    public MethodParameterTypes(List<TypeSpec> parameterTypes) {
        this.parameterTypes = toListOfString(
                Objects.requireNonNull(parameterTypes, "parameterTypes cannot be null")
        );
    }

    private static List<String> toListOfString(List<TypeSpec> types) {
        return types.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    public Optional<Node> findBestMatch(Map<Node, List<TypeSpec>> potentialMatches) {
        List<Node> matchingNodes = potentialMatches.entrySet().stream()
                .filter(e -> typesMatch(toListOfString(e.getValue()), parameterTypes))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (matchingNodes.size() != 1) {
            return Optional.empty(); // TODO return an error instead!
        }

        return Optional.of(matchingNodes.get(0));
    }

    private boolean typesMatch(List<String> left, List<String> right) {
        Iterator<String> potentialMatchTypesIterator = left.iterator();
        Iterator<String> parameterTypesIterator = right.iterator();

        return streamOfPairs(potentialMatchTypesIterator, parameterTypesIterator).stream()
                .map(Pair::matches)
                .reduce(true, (a, b) -> a && b);
    }

    private List<Pair> streamOfPairs(Iterator<String> left, Iterator<String> right) {
        List<Pair> list = new ArrayList<>();

        while (left.hasNext() && right.hasNext()) {
            list.add(new Pair(left.next(), right.next()));
        }

        if (left.hasNext() || right.hasNext()) {
            throw new IllegalArgumentException(
                    "potentialMatches types count does not always match parameters types count"
            );
        }

        return list;
    }

    private static class Pair {
        private final String left;
        private final String right;

        private Pair(String left, String right) {
            this.left = left;
            this.right = right;
        }

        private boolean matches() {
            return left.equals(right);
        }
    }
}
