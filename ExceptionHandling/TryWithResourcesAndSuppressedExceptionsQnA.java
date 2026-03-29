package ExceptionHandling;

import java.io.IOException;

/**
 * Exception Handling: try-with-resources + suppressed exceptions (Q&A + runnable demos)
 *
 * Run:
 *   javac .\ExceptionHandling\TryWithResourcesAndSuppressedExceptionsQnA.java
 *   java ExceptionHandling.TryWithResourcesAndSuppressedExceptionsQnA
 */
public class TryWithResourcesAndSuppressedExceptionsQnA {

    /*
     * =============================
     * 1) What is try-with-resources?
     * =============================
     * - A language feature that automatically closes resources implementing AutoCloseable.
     * - Great for files, streams, sockets, JDBC objects, channels, and readers/writers.
     *
     * Interview points:
     * - It is usually preferred over manual finally-based cleanup.
     * - Resources are closed in REVERSE order of declaration.
     * - If both the try body and close() throw, the close exception becomes suppressed.
     */

    static final class DemoResource implements AutoCloseable {
        private final String name;
        private final boolean failOnClose;

        DemoResource(String name, boolean failOnClose) {
            this.name = name;
            this.failOnClose = failOnClose;
            System.out.println("open " + name);
        }

        void work(boolean failDuringWork) {
            System.out.println("work " + name);
            if (failDuringWork) {
                throw new IllegalStateException("work failed in " + name);
            }
        }

        @Override
        public void close() throws IOException {
            System.out.println("close " + name);
            if (failOnClose) {
                throw new IOException("close failed in " + name);
            }
        }
    }

    static void basicTryWithResourcesDemo() {
        System.out.println("=== Basic try-with-resources ===");

        try (DemoResource resource = new DemoResource("R1", false)) {
            resource.work(false);
        } catch (Exception ex) {
            System.out.println("unexpected: " + ex.getMessage());
        }
    }

    static void reverseCloseOrderDemo() {
        System.out.println("\n=== Reverse close order ===");

        try (DemoResource first = new DemoResource("first", false);
             DemoResource second = new DemoResource("second", false)) {
            first.work(false);
            second.work(false);
        } catch (Exception ex) {
            System.out.println("unexpected: " + ex.getMessage());
        }

        // Close order output is: second, then first.
    }

    static void suppressedExceptionDemo() {
        System.out.println("\n=== Suppressed exception demo ===");

        try (DemoResource resource = new DemoResource("R2", true)) {
            resource.work(true);
        } catch (Exception ex) {
            System.out.println("primary exception => " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
            for (Throwable suppressed : ex.getSuppressed()) {
                System.out.println("suppressed => " + suppressed.getClass().getSimpleName() + ": " + suppressed.getMessage());
            }
        }

        // Interview meaning:
        // - The work exception is primary.
        // - The close exception is not lost; it becomes suppressed.
    }

    public static void main(String[] args) {
        basicTryWithResourcesDemo();
        reverseCloseOrderDemo();
        suppressedExceptionDemo();

        System.out.println("\nInterview notes:");
        System.out.println("- Prefer try-with-resources for AutoCloseable resources.");
        System.out.println("- Do not manually close the same resource again outside the try-with-resources block.");
        System.out.println("- Learn suppressed exceptions because interviewers use them to test advanced understanding.");
    }
}