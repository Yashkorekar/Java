package FileHandlingAndIO;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * File Handling & IO - Binary file IO (Q&A + runnable demos)
 *
 * Run:
 *   javac .\FileHandlingAndIO\BinaryFileIOQnA.java
 *   java FileHandlingAndIO.BinaryFileIOQnA
 */
public class BinaryFileIOQnA {

    /*
     * =============================
     * 1) Character stream vs byte stream
     * =============================
     * - Character streams are for text.
     * - Byte streams are for raw bytes such as images, PDFs, ZIP files, and binary protocols.
     */

    static void fileInputOutputStreamDemo(Path file) throws IOException {
        System.out.println("=== FileOutputStream/FileInputStream demo ===");

        byte[] payload = "JAVA".getBytes(StandardCharsets.UTF_8);
        try (FileOutputStream out = new FileOutputStream(file.toFile())) {
            out.write(payload);
        }

        byte[] buffer = new byte[16];
        int read;
        try (FileInputStream in = new FileInputStream(file.toFile())) {
            read = in.read(buffer);
        }

        System.out.println("raw bytes => " + Arrays.toString(Arrays.copyOf(buffer, read)));
        System.out.println("decoded => " + new String(buffer, 0, read, StandardCharsets.UTF_8));
    }

    static void dataInputOutputStreamDemo(Path file) throws IOException {
        System.out.println("\n=== DataInputStream/DataOutputStream demo ===");

        try (DataOutputStream out = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(file.toFile())))) {
            out.writeInt(42);
            out.writeDouble(99.75);
            out.writeBoolean(true);
            out.writeUTF("interview");
        }

        try (DataInputStream in = new DataInputStream(
                new BufferedInputStream(new FileInputStream(file.toFile())))) {
            int id = in.readInt();
            double amount = in.readDouble();
            boolean ok = in.readBoolean();
            String tag = in.readUTF();

            System.out.println("readInt => " + id);
            System.out.println("readDouble => " + amount);
            System.out.println("readBoolean => " + ok);
            System.out.println("readUTF => " + tag);
        }

        System.out.println("Important: read order must exactly match write order.");
    }

    public static void main(String[] args) throws Exception {
        Path baseDir = Files.createTempDirectory("java_binary_io_demo_");
        System.out.println("Working directory: " + baseDir.toAbsolutePath());

        Path rawFile = baseDir.resolve("raw.bin");
        Path typedFile = baseDir.resolve("typed.bin");

        fileInputOutputStreamDemo(rawFile);
        dataInputOutputStreamDemo(typedFile);

        Files.deleteIfExists(rawFile);
        Files.deleteIfExists(typedFile);
        Files.deleteIfExists(baseDir);

        System.out.println("\nInterview notes:");
        System.out.println("- Use byte streams for non-text data.");
        System.out.println("- Buffered wrappers reduce I/O overhead.");
        System.out.println("- Data streams are useful when both sides agree on binary format and order.");
    }
}