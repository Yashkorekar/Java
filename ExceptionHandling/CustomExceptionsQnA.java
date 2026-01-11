package ExceptionHandling;

import java.util.Objects;

/**
 * Exception Handling: Custom exceptions (Q&A + runnable demos)
 *
 * Run:
 *   javac .\ExceptionHandling\CustomExceptionsQnA.java
 *   java ExceptionHandling.CustomExceptionsQnA
 */
public class CustomExceptionsQnA {

    /*
     * =============================
     * 1) Why create custom exceptions?
     * =============================
     * Reasons interviewers expect:
     * - Domain clarity: express business errors (e.g., InsufficientBalance)
     * - Better error handling: callers can catch a specific type
     * - Preserve cause chain: wrap lower-level exceptions with context
     *
     * Best practices:
     * - Pick checked vs unchecked intentionally.
     * - Provide constructors (message, message+cause).
     * - Keep them simple; no heavy logic.
     */

    /*
     * =============================
     * 2) Checked vs unchecked custom exceptions
     * =============================
     * Custom checked exception: extends Exception
     * - Forces callers to handle/declare.
     *
     * Custom unchecked exception: extends RuntimeException
     * - Used for invalid arguments/state or when you don't want to force handling.
     */

    static final class InvalidUserInputException extends RuntimeException {
        InvalidUserInputException(String message) {
            super(message);
        }
    }

    static final class PaymentFailedException extends Exception {
        PaymentFailedException(String message) {
            super(message);
        }

        PaymentFailedException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /*
     * =============================
     * 3) Domain exception example (unchecked)
     * =============================
     */

    static final class InsufficientBalanceException extends RuntimeException {
        InsufficientBalanceException(String message) {
            super(message);
        }
    }

    static final class Wallet {
        private long balance;

        Wallet(long opening) {
            if (opening < 0) {
                throw new InvalidUserInputException("opening must be >= 0");
            }
            this.balance = opening;
        }

        long getBalance() {
            return balance;
        }

        void withdraw(long amount) {
            if (amount <= 0) {
                throw new InvalidUserInputException("amount must be > 0");
            }
            if (amount > balance) {
                throw new InsufficientBalanceException("need=" + amount + ", have=" + balance);
            }
            balance -= amount;
        }
    }

    /*
     * =============================
     * 4) Wrapping exceptions with cause (checked example)
     * =============================
     * Interview points:
     * - Preserve the original exception in `cause`.
     * - Add context: what you were trying to do.
     */

    static void lowLevelOperation(boolean fail) {
        if (fail) {
            throw new IllegalStateException("DB connection refused");
        }
    }

    static void pay(boolean fail) throws PaymentFailedException {
        try {
            lowLevelOperation(fail);
            System.out.println("pay: success");
        } catch (RuntimeException ex) {
            throw new PaymentFailedException("pay: failed due to system error", ex);
        }
    }

    /*
     * =============================
     * 5) Creating informative messages
     * =============================
     * Good messages include relevant context (ids, amounts), but avoid sensitive data.
     */

    static void requireNonBlank(String fieldName, String value) {
        Objects.requireNonNull(fieldName);
        if (value == null || value.isBlank()) {
            throw new InvalidUserInputException(fieldName + " must not be blank");
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Custom exceptions QnA ===");

        System.out.println("\n--- 3) Domain exception example (unchecked) ---");
        Wallet w = new Wallet(100);
        System.out.println("balance=" + w.getBalance());
        // output: balance=100

        try {
            w.withdraw(150);
        } catch (InsufficientBalanceException ex) {
            System.out.println("caught: " + ex.getMessage());
        }
        // output: caught: need=150, have=100

        System.out.println("\n--- 4) Wrapping + cause chain (checked) ---");
        try {
            pay(false);
            // output: pay: success

            pay(true);
        } catch (PaymentFailedException ex) {
            System.out.println("caught PaymentFailedException: " + ex.getMessage());
            System.out.println("cause type: " + ex.getCause().getClass().getSimpleName());
            System.out.println("cause message: " + ex.getCause().getMessage());
            // output:
            // caught PaymentFailedException: pay: failed due to system error
            // cause type: IllegalStateException
            // cause message: DB connection refused
        }

        System.out.println("\n--- 5) Invalid input (unchecked) ---");
        try {
            requireNonBlank("userId", " ");
        } catch (InvalidUserInputException ex) {
            System.out.println("caught: " + ex.getMessage());
            // output: caught: userId must not be blank
        }
    }
}
