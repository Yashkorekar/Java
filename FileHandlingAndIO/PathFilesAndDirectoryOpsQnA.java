package FileHandlingAndIO;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;

/**
 * File Handling & IO - Path, Files, and directory operations (Q&A + runnable demos)
 *
 * Run:
 *   javac .\FileHandlingAndIO\PathFilesAndDirectoryOpsQnA.java
 *   java FileHandlingAndIO.PathFilesAndDirectoryOpsQnA
 */
public class PathFilesAndDirectoryOpsQnA {

    static Path buildPathDemo(Path baseDir) {
        System.out.println("=== Path building demo ===");

        Path reportFile = baseDir.resolve("reports").resolve("2026").resolve("summary.txt");
        System.out.println("path => " + reportFile);
        System.out.println("fileName => " + reportFile.getFileName());
        System.out.println("parent => " + reportFile.getParent());
        return reportFile;
    }

    static void directoryAndFileOpsDemo(Path baseDir) throws IOException {
        System.out.println("\n=== Directory + file operations ===");

        Path source = baseDir.resolve("source");
        Path target = baseDir.resolve("target");
        Files.createDirectories(source);
        Files.createDirectories(target);

        Path sourceFile = source.resolve("notes.txt");
        Files.writeString(sourceFile, "alpha\nbeta\n", StandardCharsets.UTF_8);

        Path copied = target.resolve("notes-copy.txt");
        Files.copy(sourceFile, copied, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("copied exists => " + Files.exists(copied));

        Path moved = target.resolve("notes-moved.txt");
        Files.move(copied, moved, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("moved exists => " + Files.exists(moved));
        System.out.println("old copy exists => " + Files.exists(copied));
    }

    static void listWalkAndAttributesDemo(Path baseDir) throws IOException {
        System.out.println("\n=== list + walk + attributes ===");

        Path logsDir = baseDir.resolve("logs");
        Files.createDirectories(logsDir.resolve("archived"));
        Files.writeString(logsDir.resolve("app.log"), "started\n", StandardCharsets.UTF_8);
        Files.writeString(logsDir.resolve("archived").resolve("old.log"), "archived\n", StandardCharsets.UTF_8);

        try (var children = Files.list(logsDir)) {
            children.forEach(path -> System.out.println("list => " + path.getFileName()));
        }

        try (var tree = Files.walk(baseDir)) {
            System.out.println("walk count => " + tree.count());
        }

        BasicFileAttributes attrs = Files.readAttributes(logsDir.resolve("app.log"), BasicFileAttributes.class);
        System.out.println("size => " + attrs.size());
        System.out.println("isRegularFile => " + attrs.isRegularFile());
    }

    public static void main(String[] args) throws Exception {
        Path baseDir = Files.createTempDirectory("java_path_files_demo_");
        System.out.println("Working directory: " + baseDir.toAbsolutePath());

        buildPathDemo(baseDir);
        directoryAndFileOpsDemo(baseDir);
        listWalkAndAttributesDemo(baseDir);

        try (var cleanup = Files.walk(baseDir)) {
            cleanup.sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    });
        }

        System.out.println("\nInterview notes:");
        System.out.println("- Prefer Path/Files over older File APIs for modern Java code.");
        System.out.println("- Files.list and Files.walk return streams that should be closed.");
        System.out.println("- copy, move, and delete are common practical interview tasks.");
    }
}