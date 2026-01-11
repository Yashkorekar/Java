package ExceptionHandling;

/**
 * Exception Handling: Multiple catch blocks (Q&A + runnable demos)
 *
 * Run:
 *   javac .\ExceptionHandling\MultipleCatchBlocksQnA.java
 *   java ExceptionHandling.MultipleCatchBlocksQnA
 */
public class MultipleCatchBlocksQnA {

    /*
     * =============================
     * 1) Multiple catch blocks: what and why?
     * =============================
     * Use multiple catch blocks when you want different handling for different exception types.
     *
     * Core rules interviewers ask:
     * - Order matters: catch more specific exceptions FIRST.
     *   Example: catch (IOException) before catch (Exception).
     * - If a catch is unreachable (because an earlier catch already handles it), compiler error.
     * - Only exceptions that the try block can throw are allowed (otherwise unreachable).
     */

    /*
     * =============================
     * 2) Example: same try block throws different exceptions
     * =============================
     */

    static void parseAndProcess(String s) {
        try {
            System.out.println("try: input=" + s);

            // Possible exception 1: NullPointerException
            if (s == null) {
                throw new NullPointerException("input is null");
            }

            // Possible exception 2: NumberFormatException (subclass of IllegalArgumentException)
            int n = Integer.parseInt(s.trim());

            // Possible exception 3: ArithmeticException
            int result = 100 / n;
            System.out.println("result=" + result);

        } catch (NullPointerException ex) {
            System.out.println("catch NPE: " + ex.getMessage());
        } catch (NumberFormatException ex) {
            System.out.println("catch NFE: " + ex.getMessage());
        } catch (ArithmeticException ex) {
            System.out.println("catch AE: " + ex.getMessage());
        } catch (RuntimeException ex) {
            // Safety net for other runtime exceptions
            System.out.println("catch RuntimeException: " + ex.getClass().getSimpleName());
        } finally {
            System.out.println("finally\n");
        }
    }

    /*
     * =============================
     * 3) Ordering: why specific-first is required
     * =============================
     * Won't compile (example only):
     *
     *   try { ... }
     *   catch (Exception e) { }
     *   catch (RuntimeException e) { } // ERROR: unreachable (already caught by Exception)
     */

    /*
     * =============================
     * 4) Multi-catch vs multiple catch blocks
     * =============================
     * Multi-catch syntax:
     *   catch (A | B ex)
     *
     * Rules:
     * - A and B must not be related by inheritance (otherwise redundant/unreachable).
     * - ex is effectively final (you cannot reassign it).
     */

    static void multiCatch(int mode) {
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
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Multiple catch blocks QnA ===\n");

        parseAndProcess(null);
        // output:
        // try: input=null
        // catch NPE: input is null
        // finally

        parseAndProcess("abc");
        // output:
        // try: input=abc
        // catch NFE: For input string: "abc"
        // finally

        parseAndProcess("0");
        // output:
        // try: input=0
        // catch AE: / by zero
        // finally

        parseAndProcess("5");
        // output:
        // try: input=5
        // result=20
        // finally

        System.out.println("--- Multi-catch quick demo ---");
        multiCatch(1);
        // output: multi-catch: IllegalArgumentException: bad arg

        multiCatch(2);
        // output: multi-catch: UnsupportedOperationException: not supported

        multiCatch(0);
        // output: ok
    }
}
