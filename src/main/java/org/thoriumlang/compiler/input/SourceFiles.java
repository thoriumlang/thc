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
package org.thoriumlang.compiler.input;


import org.thoriumlang.compiler.symbols.Name;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SourceFiles implements Sources {
    private static final BiPredicate<Path, BasicFileAttributes> thSourcesMatcher = (path, basicFileAttributes) ->
            basicFileAttributes.isRegularFile() && path.getFileName().toString().matches(".*\\.th$");
    private static final BiPredicate<Path, BasicFileAttributes> thRootMatcher = (path, basicFileAttributes) ->
            basicFileAttributes.isRegularFile() && path.getFileName().toString().matches("^\\.throot$");

    private final Path root;
    private final Path searchPath;
    private final BiPredicate<Path, BasicFileAttributes> filter;

    public SourceFiles(Path searchPath, Predicate<Path> filter) {
        this.root = findRoot(searchPath).orElseThrow(() -> new IllegalArgumentException("No .throot file found"));
        this.searchPath = searchPath;
        this.filter = (path, basicFileAttributes) -> filter.test(path);
    }

    public SourceFiles(Path root) {
        this(root, x -> true);
    }

    private Optional<Path> findRoot(Path path) {
        Optional<Path> thRootPath = find(path, thRootMatcher);

        if (thRootPath.isPresent()) {
            return thRootPath;
        }

        Path parent = path.getParent();
        if (parent != null) {
            return findRoot(parent);
        }

        return Optional.empty();
    }

    @SuppressWarnings("SameParameterValue")
    private Optional<Path> find(Path path, BiPredicate<Path, BasicFileAttributes> matcher) {
        try (Stream<Path> files = Files.find(path, 1, matcher)) {
            return files.findFirst();
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public List<Source> sources() {
        List<Path> thRoots = findThRoots();

        try (Stream<Path> sourcePaths = findRecursive(
                searchPath,
                (p, bfa) -> thSourcesMatcher.test(p, bfa) && filter.test(p, bfa)
        )) {
            return sourcePaths
                    .map(path -> new SourceFile(
                            namespace(
                                    path.getParent().normalize(),
                                    path.normalize(),
                                    thRoots
                            ),
                            path
                    ))
                    .collect(Collectors.toList());
        }
    }

    private List<Path> findThRoots() {
        try (Stream<Path> paths = findRecursive(root, thRootMatcher)) {
            return paths
                    .map(Path::getParent)
                    .collect(Collectors.toList());
        }
    }

    private Stream<Path> findRecursive(Path start, BiPredicate<Path, BasicFileAttributes> matcher) {
        try {
            return Files.find(start, 999, matcher);
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String namespace(Path path, Path fullPath, List<Path> thRoots) {
        if (!thRoots.contains(path)) {
            return namespace(path.getParent(), fullPath, thRoots);
        }
        return fullPath
                .getParent()
                .toString()
                .substring(path.toString().length())
                .replace(File.separator, ".")
                .replaceFirst("^\\.", "");
    }

    @Override
    public Optional<Source> load(Name name) {
        for (Path rootPath : findThRoots()) {
            Path path = rootPath.resolve(name.getFullName().replace(".", File.separator) + ".th");

            if (Files.exists(path)) {
                return Optional.of(
                        new SourceFile(
                                namespace(
                                        path.getParent(),
                                        path,
                                        Collections.singletonList(rootPath)
                                ),
                                path
                        )
                );
            }
        }
        return Optional.empty();
    }
}
