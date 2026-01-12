package FileHandlingAndIO;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * File Handling & IO - Text files (Q&A + runnable demos)
 *
 * Topics covered:
 * - Read/write text files using FileReader/FileWriter
 * - Read/write using BufferedReader/BufferedWriter
 * - Read/write using Files class
 *
 * Run:
 *   javac .\FileHandlingAndIO\TextFileIOQnA.java
 *   java FileHandlingAndIO.TextFileIOQnA
 */
public class TextFileIOQnA {

    /*
     * Q: When to use FileReader/FileWriter?
     * A: Simple character-stream IO. BUT they use the platform default charset.
     *    For interview: prefer specifying charset explicitly (Files + UTF-8) when possible.
     *
     * Q: Why BufferedReader/BufferedWriter?
     * A: Fewer syscalls; convenient APIs like readLine()/newLine().
     *
     * Q: Why java.nio.file.Files?
     * A: Modern NIO API: Path-based, explicit charset methods (readString/writeString),
     *    easy options (APPEND, CREATE, TRUNCATE_EXISTING), and stream support (Files.lines()).
     */

    private static void fileReaderFileWriterDemo(Path file) throws IOException {
        System.out.println("=== FileWriter/FileReader demo ===");

        // FileWriter: write chars, uses platform default charset.
        try (FileWriter fw = new FileWriter(file.toFile(), false)) {
            fw.write("Hello from FileWriter\n");
            fw.write("Line2\n");
        }

        // Append mode
        try (FileWriter fw = new FileWriter(file.toFile(), true)) {
            fw.write("(appended) Line3\n");
        }

        // FileReader: read chars, uses platform default charset.
        StringBuilder sb = new StringBuilder();
        try (FileReader fr = new FileReader(file.toFile())) {
            char[] buffer = new char[16];
            int read;
            while ((read = fr.read(buffer)) != -1) {
                sb.append(buffer, 0, read);
            }
        }

        System.out.println(sb);
        // output (content):
        // Hello from FileWriter
        // Line2
        // (appended) Line3
    }

    private static void bufferedReaderBufferedWriterDemo(Path file) throws IOException {
        System.out.println("\n=== BufferedWriter/BufferedReader demo ===");

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file.toFile(), false))) {
            bw.write("Asha");
            bw.newLine();
            bw.write("Meera");
            bw.newLine();
            bw.write("Ravi");
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file.toFile()))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println("readLine => " + line);
            }
        }

        // output:
        // readLine => Asha
        // readLine => Meera
        // readLine => Ravi
    }

    private static void filesClassDemo(Path file) throws IOException {
        System.out.println("\n=== Files class demo (preferred) ===");

        Files.writeString(file, "First\nSecond\n", StandardCharsets.UTF_8);
        Files.writeString(file, "Third\n", StandardCharsets.UTF_8, StandardOpenOption.APPEND);

        String content = Files.readString(file, StandardCharsets.UTF_8);
        System.out.println("readString =>\n" + content);

        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        System.out.println("readAllLines => " + lines);

        long lineCount;
        try (var stream = Files.lines(file, StandardCharsets.UTF_8)) {
            lineCount = stream.count();
        }
        System.out.println("Files.lines().count => " + lineCount);

        System.out.println("exists => " + Files.exists(file));
        System.out.println("size(bytes) => " + Files.size(file));

        // output (example):
        // readString =>
        // First
        // Second
        // Third
        // readAllLines => [First, Second, Third]
        // Files.lines().count => 3
    }

    public static void main(String[] args) throws Exception {
        Path baseDir = Files.createTempDirectory("java_file_io_demo_");
        System.out.println("Working directory: " + baseDir.toAbsolutePath());

        Path file1 = baseDir.resolve("fr_fw.txt");
        Path file2 = baseDir.resolve("buffered.txt");
        Path file3 = baseDir.resolve("files_utf8.txt");

        fileReaderFileWriterDemo(file1);
        bufferedReaderBufferedWriterDemo(file2);
        filesClassDemo(file3);

        // Cleanup: delete created files and temp folder
        Files.deleteIfExists(file1);
        Files.deleteIfExists(file2);
        Files.deleteIfExists(file3);
        Files.deleteIfExists(baseDir);
    }
}
