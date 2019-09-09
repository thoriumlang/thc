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
package org.thoriumlang.compiler;


import org.thoriumlang.compiler.exceptions.NoThRootFound;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SourceFiles {
    private static final BiPredicate<Path, BasicFileAttributes> thSourcesMatcher = (path, basicFileAttributes) ->
            basicFileAttributes.isRegularFile() && path.getFileName().toString().matches(".*\\.th");
    private static final BiPredicate<Path, BasicFileAttributes> thRootMatcher = (path, basicFileAttributes) ->
            basicFileAttributes.isRegularFile() && path.getFileName().toString().matches("\\.throot");

    private final Path root;
    private final BiPredicate<Path, BasicFileAttributes> filter;

    public SourceFiles(Path root, BiPredicate<Path, BasicFileAttributes> filter) {
        this.root = root;
        this.filter = filter;
    }

    public SourceFiles(Path root) {
        this(root, (x, y) -> true);
    }

    public List<SourceFile> files() throws IOException {
        List<String> thRoots = findThRoots();

        if (thRoots.isEmpty()) {
            throw new NoThRootFound(root);
        }

        try (Stream<Path> sources = Files.find(
                root,
                999,
                (p, bfa) -> thSourcesMatcher.test(p, bfa) && filter.test(p, bfa)
        )) {
            return sources
                    .map(p -> new SourceFile(
                            namespace(
                                    p.getParent().normalize(),
                                    p.normalize().toString(),
                                    thRoots
                            ),
                            p
                    ))
                    .collect(Collectors.toList());
        }

    }

    private String namespace(Path path, String fullPath, List<String> thRoots) {
        if (!thRoots.contains(path.toString())) {
            return namespace(path.getParent(), fullPath, thRoots);
        }
        return fullPath
                .substring(0, fullPath.lastIndexOf('/'))
                .substring(path.toString().length())
                .replace(File.separator, ".")
                .replaceFirst("^\\.", "");
    }

    private List<String> findThRoots() throws IOException {
        try (Stream<Path> paths = Files.find(root, 999, thRootMatcher)) {
            return paths
                    .map(p -> p.getParent().toString())
                    .collect(Collectors.toList());
        }
    }
}
