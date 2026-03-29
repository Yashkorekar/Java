package ExceptionHandling;

import java.io.IOException;

/**
 * Exception Handling: propagation, translation, and best practices (Q&A + runnable demos)
 *
 * Run:
 *   javac .\ExceptionHandling\ExceptionPropagationAndBestPracticesQnA.java
 *   java ExceptionHandling.ExceptionPropagationAndBestPracticesQnA
 */
public class ExceptionPropagationAndBestPracticesQnA {

    /*
     * =============================
     * 1) Throwable hierarchy
     * =============================
     * Throwable
     * ├── Error            -> JVM/system-level serious problems, usually not handled in business code
     * └── Exception
     *     ├── RuntimeException -> unchecked exceptions
     *     └── checked exceptions
     *
     * Interview-safe answer:
     * - Catch Exception only when you have a strong boundary reason.
     * - Avoid catching Error in normal application logic.
     */

    static void lowLevelRead(boolean fail) throws IOException {
        if (fail) {
            throw new IOException("disk read failed");
        }
        System.out.println("lowLevelRead: success");
    }

    static void midLevelService(boolean fail) throws IOException {
        lowLevelRead(fail);
    }

    static void propagationDemo(boolean fail) {
        System.out.println("=== Propagation demo ===");

        try {
            midLevelService(fail);
        } catch (IOException ex) {
            System.out.println("caught at boundary => " + ex.getMessage());
            StackTraceElement[] trace = ex.getStackTrace();
            System.out.println("top stack frame => " + trace[0]);
        }
    }

    static final class ConfigLoadException extends RuntimeException {
        ConfigLoadException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    static String loadConfigFile(boolean fail) throws IOException {
        if (fail) {
            throw new IOException("config file missing");
        }
        return "app.mode=prod";
    }

    static String translateToDomainException(boolean fail) {
        try {
            return loadConfigFile(fail);
        } catch (IOException ex) {
            throw new ConfigLoadException("application startup failed while loading config", ex);
        }
    }

    static void swallowingExceptionBadExample() {
        System.out.println("\n=== Swallowing exception (bad example) ===");

        try {
            Integer.parseInt("not-a-number");
        } catch (NumberFormatException ex) {
            // BAD: swallowing the exception hides the real problem.
        }

        System.out.println("program continued, but the failure was silently lost");
    }

    static void boundaryHandlingGoodExample() {
        System.out.println("\n=== Boundary handling (good example) ===");

        try {
            System.out.println(translateToDomainException(true));
        } catch (ConfigLoadException ex) {
            System.out.println("message => " + ex.getMessage());
            System.out.println("cause => " + ex.getCause().getClass().getSimpleName());
        }
    }

    public static void main(String[] args) {
        propagationDemo(false);
        System.out.println();
        propagationDemo(true);

        swallowingExceptionBadExample();
        boundaryHandlingGoodExample();

        System.out.println("\nBest practices:");
        System.out.println("- Catch exceptions where you can add meaning or recover.");
        System.out.println("- Preserve the cause chain when wrapping exceptions.");
        System.out.println("- Log or handle once at the right boundary; avoid duplicate noisy logging at every layer.");
        System.out.println("- Do not use exceptions for normal control flow.");
    }
}