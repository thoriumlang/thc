package org.thoriumlang.compiler.testsupport;

import org.thoriumlang.compiler.input.Source;
import org.thoriumlang.compiler.input.SourceFiles;

import java.nio.file.Paths;
import java.util.List;

public final class SourceProvider {
    private SourceProvider() {
        // nothing
    }

    /**
     * Provides a source from a filename, lying in the class's resource folder. The class's resource folder is the
     * class fully qualified name.
     *
     * @param context  the class from which we load the source file.
     * @param filename the filename to load.
     * @return the {@link Source} instance.
     * @throws IllegalArgumentException when the source file is not found.
     */
    public static Source provide(Class<?> context, String filename) {
        List<Source> sources = new SourceFiles(
                Paths
                        .get(context.getResource("/").getPath())
                        .resolve(Paths.get(context.getName().replaceAll("\\.", "/"))),
                p -> p.getFileName().toString().equals(filename)
        ).sources();

        if (sources.size() != 1) {
            throw new IllegalArgumentException("Expected to find one " + filename + ", found " + sources.size());
        }

        return sources.get(0);
    }
}
