package FileHandlingAndIO;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * File Handling & IO - NIO channels and buffers (Q&A + runnable demos)
 *
 * Run:
 *   javac .\FileHandlingAndIO\NIOChannelsAndBuffersQnA.java
 *   java FileHandlingAndIO.NIOChannelsAndBuffersQnA
 */
public class NIOChannelsAndBuffersQnA {

    static void byteBufferLifecycleDemo() {
        System.out.println("=== ByteBuffer lifecycle ===");

        ByteBuffer buffer = ByteBuffer.allocate(8);
        System.out.println("initial position=" + buffer.position() + ", limit=" + buffer.limit() + ", capacity=" + buffer.capacity());

        buffer.put((byte) 'A');
        buffer.put((byte) 'B');
        buffer.put((byte) 'C');
        System.out.println("after put position=" + buffer.position() + ", limit=" + buffer.limit());

        buffer.flip();
        System.out.println("after flip position=" + buffer.position() + ", limit=" + buffer.limit());

        while (buffer.hasRemaining()) {
            System.out.print((char) buffer.get() + " ");
        }
        System.out.println();

        buffer.clear();
        System.out.println("after clear position=" + buffer.position() + ", limit=" + buffer.limit());
    }

    static void fileChannelDemo(Path file) throws IOException {
        System.out.println("\n=== FileChannel demo ===");

        try (FileChannel out = FileChannel.open(file,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE)) {
            ByteBuffer writeBuffer = ByteBuffer.wrap("NIO channel demo".getBytes(StandardCharsets.UTF_8));
            out.write(writeBuffer);
        }

        try (FileChannel in = FileChannel.open(file, StandardOpenOption.READ)) {
            ByteBuffer readBuffer = ByteBuffer.allocate(64);
            in.read(readBuffer);
            readBuffer.flip();
            String text = StandardCharsets.UTF_8.decode(readBuffer).toString();
            System.out.println("read => " + text);
        }
    }

    public static void main(String[] args) throws Exception {
        Path baseDir = Files.createTempDirectory("java_nio_demo_");
        System.out.println("Working directory: " + baseDir.toAbsolutePath());

        Path file = baseDir.resolve("channel.txt");

        byteBufferLifecycleDemo();
        fileChannelDemo(file);

        Files.deleteIfExists(file);
        Files.deleteIfExists(baseDir);

        System.out.println("\nInterview notes:");
        System.out.println("- Buffer state transitions matter: write mode -> flip -> read mode -> clear/compact.");
        System.out.println("- FileChannel + ByteBuffer is a classic intermediate Java IO interview topic.");
        System.out.println("- Memory-mapped files exist too, but first be solid on normal channels and buffers.");
    }
}