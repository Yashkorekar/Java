package ExceptionHandling;

import java.io.IOException;

/**
 * Exception Handling: throw vs throws (Q&A + runnable demos)
 *
 * Run:
 *   javac .\ExceptionHandling\ThrowVsThrowsQnA.java
 *   java ExceptionHandling.ThrowVsThrowsQnA
 */
public class ThrowVsThrowsQnA {

    /*
     * =============================
     * 1) throw vs throws
     * =============================
     * throw:
     * - Used INSIDE a method/constructor to actually throw an exception object.
     *   Example: throw new IllegalArgumentException("bad");
     *
     * throws:
     * - Used in method signature to DECLARE that a method may throw exceptions.
     *   Example: void read() throws IOException
     *
     * Interview points:
     * - You can throw checked or unchecked exceptions using throw.
     * - For checked exceptions, you must either:
     *   (a) catch them, OR
     *   (b) declare them using throws.
     */

    // ---------- Example: throw for validation (unchecked exception) ----------

    static int parsePositiveInt(String s) {
        if (s == null) {
            throw new IllegalArgumentException("s must not be null");
        }
        int n = Integer.parseInt(s.trim());
        if (n <= 0) {
            throw new IllegalArgumentException("n must be > 0");
        }
        return n;
    }

    // ---------- Example: throws for checked exception ----------

    static void apiThatMayFail(boolean fail) throws IOException {
        if (fail) {
            throw new IOException("disk error");
        }
        System.out.println("apiThatMayFail: ok");
    }

    // ---------- Re-throwing / wrapping exceptions ----------

    static void callAndWrap(boolean fail) {
        try {
            apiThatMayFail(fail);
        } catch (IOException ex) {
            // Wrap checked exception into unchecked to avoid throws in this method.
            throw new RuntimeException("wrapped: " + ex.getMessage(), ex);
        }
    }

    /*
     * =============================
     * 2) throws with multiple exceptions
     * =============================
     * You can declare multiple exceptions:
     *   void m() throws IOException, SQLException
     *
     * But prefer to keep it minimal and meaningful.
     */

    public static void main(String[] args) {
        System.out.println("=== throw vs throws QnA ===");

        System.out.println("\n--- 1) throw (unchecked) for validation ---");
        try {
            System.out.println(parsePositiveInt("5"));
            // output: 5

            System.out.println(parsePositiveInt("0"));
            // not reached
        } catch (IllegalArgumentException ex) {
            System.out.println("caught: " + ex.getMessage());
            // output: caught: n must be > 0
        }

        System.out.println("\n--- 1b) throws (checked) declared in signature ---");
        try {
            apiThatMayFail(false);
            // output: apiThatMayFail: ok

            apiThatMayFail(true);
            // not reached
        } catch (IOException ex) {
            System.out.println("caught IOException: " + ex.getMessage());
            // output: caught IOException: disk error
        }

        System.out.println("\n--- 1c) catching checked + wrapping into unchecked ---");
        try {
            callAndWrap(true);
        } catch (RuntimeException ex) {
            System.out.println("caught RuntimeException: " + ex.getMessage());
            System.out.println("cause: " + ex.getCause().getClass().getSimpleName());
            // output:
            // caught RuntimeException: wrapped: disk error
            // cause: IOException
        }
    }
}
