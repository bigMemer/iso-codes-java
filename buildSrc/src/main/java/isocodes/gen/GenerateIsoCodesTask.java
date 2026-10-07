package isocodes.gen;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

/** Downloads one iso-codes release and generates Java sources from its JSON data. */
@CacheableTask
public abstract class GenerateIsoCodesTask extends DefaultTask {

    @Input
    public abstract Property<String> getIsoCodesVersion();

    @Input
    public abstract Property<String> getBasePackage();

    /** Where downloaded JSON files are kept between builds. */
    @Internal
    public abstract DirectoryProperty getDownloadDirectory();

    @OutputDirectory
    public abstract DirectoryProperty getOutputDirectory();

    @TaskAction
    public void generate() throws IOException {
        String version = getIsoCodesVersion().get();
        Path downloads = getDownloadDirectory().get().getAsFile().toPath().resolve(version);
        Path output = getOutputDirectory().get().getAsFile().toPath();
        deleteRecursively(output);
        new JavaGenerator(new UpstreamSource(version, downloads), version, getBasePackage().get(), getLogger())
                .generateAll(output);
    }

    private static void deleteRecursively(Path dir) throws IOException {
        if (!Files.exists(dir)) {
            return;
        }
        try (Stream<Path> paths = Files.walk(dir)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(path);
            }
        }
    }
}
