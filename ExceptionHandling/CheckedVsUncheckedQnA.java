package ExceptionHandling;

import java.io.IOException;

/**
 * Exception Handling: Checked vs Unchecked exceptions (Q&A + runnable demos)
 *
 * Run:
 *   javac .\ExceptionHandling\CheckedVsUncheckedQnA.java
 *   java ExceptionHandling.CheckedVsUncheckedQnA
 */
public class CheckedVsUncheckedQnA {

    /*
     * =============================
     * 1) What is a checked exception?
     * =============================
     * A checked exception is any exception that extends Exception BUT NOT RuntimeException.
     * Examples: IOException, SQLException.
     *
     * Rule:
     * - Compiler forces you to handle it:
     *   - either catch it, OR
     *   - declare it with throws.
     *
     * When to use (typical guidance):
     * - For recoverable conditions the caller can reasonably handle.
     */

    /*
     * =============================
     * 2) What is an unchecked exception?
     * =============================
     * Unchecked exceptions are RuntimeException and its subclasses.
     * Examples: NullPointerException, IllegalArgumentException, IllegalStateException.
     *
     * Rule:
     * - No compile-time requirement to catch/declare.
     *
     * When to use:
     * - Programming errors / invalid arguments / invalid state.
     */

    // ---------- Checked example ----------

    static void checkedExample(boolean fail) throws IOException {
        if (fail) {
            throw new IOException("network down");
        }
        System.out.println("checkedExample: ok");
    }

    // ---------- Unchecked example ----------

    static void uncheckedExample(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be > 0");
        }
        System.out.println("uncheckedExample: ok n=" + n);
    }

    /*
     * =============================
     * 3) Overriding rule (interview classic): checked exceptions cannot be widened
     * =============================
     * If parent method declares a checked exception, child override:
     * - may declare the same checked exception,
     * - or a SUBTYPE (narrower),
     * - or declare none,
     * - but CANNOT declare a broader/new checked exception.
     *
     * Unchecked exceptions can be added freely.
     */

    static class Parent {
        void m() throws IOException {
            // parent declares checked
        }
    }

    static final class ChildOk extends Parent {
        @Override
        void m() {
            // OK: declares none (narrower)
        }
    }

    // Won't compile (example only):
    // static final class ChildBad extends Parent {
    //     @Override
    //     void m() throws Exception { }
    // }
    // ERROR: overridden method does not throw Exception

    public static void main(String[] args) {
        System.out.println("=== Checked vs Unchecked QnA ===");

        System.out.println("\n--- 1) Checked exception: must catch or declare ---");
        try {
            checkedExample(false);
            // output: checkedExample: ok

            checkedExample(true);
            // not reached
        } catch (IOException ex) {
            System.out.println("caught checked: " + ex.getMessage());
            // output: caught checked: network down
        }

        System.out.println("\n--- 2) Unchecked exception: no need to catch/declare ---");
        try {
            uncheckedExample(2);
            // output: uncheckedExample: ok n=2

            uncheckedExample(0);
            // not reached
        } catch (IllegalArgumentException ex) {
            System.out.println("caught unchecked: " + ex.getMessage());
            // output: caught unchecked: n must be > 0
        }

        System.out.println("\n--- 3) Overriding + checked exceptions rule ---");
        Parent p = new ChildOk();
        try {
            p.m();
            System.out.println("ChildOk.m() ok");
            // output: ChildOk.m() ok
        } catch (IOException ex) {
            System.out.println("unexpected");
        }

        // Interview note:
        // - Catching RuntimeException everywhere is usually a code smell.
        // - Prefer catching the exceptions you can handle meaningfully.
    }
}
