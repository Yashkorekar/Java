package OOPS;

/**
 * Enums, records, and sealed classes (Q&A + runnable demos)
 *
 * Run:
 *   javac .\OOPS\EnumsRecordsAndSealedClassesQnA.java
 *   java OOPS.EnumsRecordsAndSealedClassesQnA
 */
public class EnumsRecordsAndSealedClassesQnA {

    enum OrderStatus {
        NEW(true),
        PAID(true),
        SHIPPED(false),
        DELIVERED(false),
        CANCELLED(false);

        private final boolean cancellable;

        OrderStatus(boolean cancellable) {
            this.cancellable = cancellable;
        }

        boolean canCancel() {
            return cancellable;
        }
    }

    record Money(String currency, long cents) {
        Money {
            if (currency == null || currency.isBlank()) {
                throw new IllegalArgumentException("currency must not be blank");
            }
            if (cents < 0) {
                throw new IllegalArgumentException("cents must be >= 0");
            }
        }

        String display() {
            return currency + " " + (cents / 100.0);
        }
    }

    sealed interface PaymentResult permits PaymentSuccess, PaymentFailure {
    }

    record PaymentSuccess(String reference) implements PaymentResult {
    }

    record PaymentFailure(String reason) implements PaymentResult {
    }

    private static void enumDemo() {
        System.out.println("=== Enum demo ===");
        OrderStatus status = OrderStatus.PAID;
        System.out.println("status = " + status);
        System.out.println("canCancel = " + status.canCancel());
    }

    private static void recordDemo() {
        System.out.println("\n=== Record demo ===");
        Money price = new Money("USD", 1599);
        System.out.println(price);
        System.out.println(price.display());
        System.out.println("Records automatically provide constructor, accessors, equals, hashCode, and toString.");
    }

    private static void sealedTypeDemo() {
        System.out.println("\n=== Sealed type demo ===");
        System.out.println(describe(new PaymentSuccess("TXN-1")));
        System.out.println(describe(new PaymentFailure("bank timeout")));
    }

    private static String describe(PaymentResult result) {
        if (result instanceof PaymentSuccess success) {
            return "success ref=" + success.reference();
        }
        if (result instanceof PaymentFailure failure) {
            return "failure reason=" + failure.reason();
        }
        throw new IllegalStateException("unknown payment result");
    }

    public static void main(String[] args) {
        enumDemo();
        recordDemo();
        sealedTypeDemo();

        System.out.println("\nInterview notes:");
        System.out.println("- Enums are much stronger than string constants because they can hold behavior and state.");
        System.out.println("- Records are good for immutable data carriers.");
        System.out.println("- Sealed types restrict which subclasses are allowed, making domain models tighter.");
    }
}