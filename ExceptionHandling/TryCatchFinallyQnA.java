package ExceptionHandling;

import java.io.Closeable;
import java.io.IOException;

/**
 * Exception Handling: try-catch-finally blocks (Q&A + runnable demos)
 *
 * Run:
 *   javac .\ExceptionHandling\TryCatchFinallyQnA.java
 *   java ExceptionHandling.TryCatchFinallyQnA
 */
public class TryCatchFinallyQnA {

    /*
     * =============================
     * 1) What is try-catch-finally?
     * =============================
     * try:
     * - Code that may throw an exception.
     *
     * catch:
     * - Handles exceptions thrown in the try block.
     * - You can have multiple catch blocks.
     * - Most specific catches must come before more general ones.
     *
     * finally:
     * - Runs whether an exception occurred or not.
     * - Used for cleanup (releasing resources, resetting state).
     *
     * Interview statement:
     * - finally executes even if you return from try/catch.
     * - finally usually executes even if you throw in try/catch.
     * - finally might NOT execute in extreme cases: JVM crash, power off,
     *   or System.exit(...) before finally can run.
     */

    /*
     * =============================
     * 2) Basic flow: no exception
     * =============================
     */
    static void noExceptionFlow() {
        System.out.println("noExceptionFlow: start");
        try {
            System.out.println("try");
        } catch (RuntimeException ex) {
            System.out.println("catch");
        } finally {
            System.out.println("finally");
        }
        System.out.println("noExceptionFlow: end");
    }

    /*
     * =============================
     * 3) Basic flow: exception thrown, caught
     * =============================
     */
    static void exceptionCaughtFlow() {
        System.out.println("exceptionCaughtFlow: start");
        try {
            System.out.println("try");
            throw new IllegalArgumentException("bad");
        } catch (IllegalArgumentException ex) {
            System.out.println("catch: " + ex.getMessage());
        } finally {
            System.out.println("finally");
        }
        System.out.println("exceptionCaughtFlow: end");
    }

    /*
     * =============================
     * 4) Exception thrown, NOT caught (propagates), finally still runs
     * =============================
     */
    static void exceptionNotCaughtFlow() {
        System.out.println("exceptionNotCaughtFlow: start");
        try {
            System.out.println("try");
            throw new NullPointerException("boom");
        } finally {
            System.out.println("finally");
        }
        // method ends by throwing; caller must handle
    }

    /*
     * =============================
     * 5) finally runs even with return (classic interviewer question)
     * =============================
     */
    static int returnWithFinally() {
        try {
            return 1;
        } finally {
            System.out.println("finally: runs even with return");
        }
    }

    /*
     * =============================
     * 6) DANGER: return/throw in finally overrides previous result
     * =============================
     * Interview trap:
     * - If finally returns, it overrides returns from try/catch.
     * - If finally throws, it can hide the original exception.
     *
     * Best practice:
     * - Avoid return/throw from finally.
     */

    static int returnOverriddenByFinally() {
        try {
            return 10;
        } finally {
            return 99; // overrides the try return
        }
    }

    static void exceptionHiddenByFinally() {
        try {
            throw new IllegalStateException("original");
        } finally {
            throw new RuntimeException("from finally");
        }
    }

    /*
     * =============================
     * 7) Multiple catch blocks + ordering + polymorphism
     * =============================
     * Rules:
     * - Put child exceptions first, parent exceptions later.
     * - Catching Exception first would make later catches unreachable.
     */

    static void multiCatchOrderDemo(String input) {
        try {
            if (input == null) {
                throw new NullPointerException("input");
            }
            if (input.isBlank()) {
                throw new IllegalArgumentException("blank");
            }
            System.out.println("ok: " + input);
        } catch (NullPointerException ex) {
            System.out.println("caught NPE: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            System.out.println("caught IAE: " + ex.getMessage());
        } catch (RuntimeException ex) {
            System.out.println("caught RuntimeException: " + ex.getClass().getSimpleName());
        } finally {
            System.out.println("finally always runs");
        }
    }

    /*
     * =============================
     * 8) Multi-catch (Java 7+)
     * =============================
     * - catch (A | B ex) is allowed only when A and B are unrelated by inheritance.
     * - The caught exception parameter is effectively final.
     */

    static void multiCatchDemo(int mode) {
        try {
            if (mode == 1) {
                throw new IllegalArgumentException("bad arg");
            }
            if (mode == 2) {
                throw new UnsupportedOperationException("not supported");
            }
            System.out.println("ok");
        } catch (IllegalArgumentException | UnsupportedOperationException ex) {
            System.out.println("multi-catch: " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
        } finally {
            System.out.println("finally");
        }
    }

    /*
     * =============================
     * 9) Resource cleanup: finally vs try-with-resources
     * =============================
     * For Closeable/AutoCloseable resources, prefer try-with-resources.
     * But it's important to understand finally-based cleanup (legacy code).
     */

    static final class DemoResource implements Closeable {
        private final String name;
        private final boolean throwOnClose;

        DemoResource(String name, boolean throwOnClose) {
            this.name = name;
            this.throwOnClose = throwOnClose;
            System.out.println(name + ": open");
        }

        void work(boolean throwInWork) {
            System.out.println(name + ": work");
            if (throwInWork) {
                throw new RuntimeException(name + ": fail in work");
            }
        }

        @Override
        public void close() throws IOException {
            System.out.println(name + ": close");
            if (throwOnClose) {
                throw new IOException(name + ": fail in close");
            }
        }
    }

    static void cleanupWithFinally(boolean throwInWork) {
        DemoResource r = null;
        try {
            r = new DemoResource("R1", false);
            r.work(throwInWork);
        } finally {
            // Cleanup must handle possible null
            if (r != null) {
                try {
                    r.close();
                } catch (IOException ex) {
                    System.out.println("close failed: " + ex.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("=== try-catch-finally QnA ===");

        System.out.println("\n--- 2) No exception flow ---");
        noExceptionFlow();
        // output:
        // noExceptionFlow: start
        // try
        // finally
        // noExceptionFlow: end

        System.out.println("\n--- 3) Exception caught flow ---");
        exceptionCaughtFlow();
        // output:
        // exceptionCaughtFlow: start
        // try
        // catch: bad
        // finally
        // exceptionCaughtFlow: end

        System.out.println("\n--- 4) Exception not caught (finally still runs) ---");
        try {
            exceptionNotCaughtFlow();
        } catch (NullPointerException ex) {
            System.out.println("caller caught: " + ex.getMessage());
        }
        // output:
        // exceptionNotCaughtFlow: start
        // try
        // finally
        // caller caught: boom

        System.out.println("\n--- 5) finally + return ---");
        int v = returnWithFinally();
        System.out.println("returned: " + v);
        // output:
        // finally: runs even with return
        // returned: 1

        System.out.println("\n--- 6) finally overrides return (avoid this) ---");
        System.out.println(returnOverriddenByFinally());
        // output: 99

        System.out.println("\n--- 6b) finally hides exceptions (avoid this) ---");
        try {
            exceptionHiddenByFinally();
        } catch (RuntimeException ex) {
            System.out.println("caught: " + ex.getMessage());
        }
        // output:
        // caught: from finally

        System.out.println("\n--- 7) Multiple catch blocks order ---");
        multiCatchOrderDemo(null);
        // output:
        // caught NPE: input
        // finally always runs

        multiCatchOrderDemo(" ");
        // output:
        // caught IAE: blank
        // finally always runs

        multiCatchOrderDemo("ok");
        // output:
        // ok: ok
        // finally always runs

        System.out.println("\n--- 8) Multi-catch ---");
        multiCatchDemo(1);
        // output:
        // multi-catch: IllegalArgumentException: bad arg
        // finally

        multiCatchDemo(2);
        // output:
        // multi-catch: UnsupportedOperationException: not supported
        // finally

        multiCatchDemo(0);
        // output:
        // ok
        // finally

        System.out.println("\n--- 9) Cleanup with finally (legacy style) ---");
        cleanupWithFinally(false);
        // output:
        // R1: open
        // R1: work
        // R1: close

        System.out.println("\n--- 9b) Cleanup with finally when try throws ---");
        try {
            cleanupWithFinally(true);
        } catch (RuntimeException ex) {
            System.out.println("caller saw: " + ex.getMessage());
        }
        // output:
        // R1: open
        // R1: work
        // R1: close
        // caller saw: R1: fail in work

        // Next topics (when you're ready):
        // - throw vs throws
        // - checked vs unchecked
        // - try-with-resources + suppressed exceptions
        // - custom exceptions + best practices
    }
}
