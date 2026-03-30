package Basics;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class StaticVsInstanceDemo {

    /*
     * Theory:
     * - static members belong to the class, not to any one object.
     * - instance members need an object because they work with per-object state.
     * - static is a good fit for constants and stateless utility behavior.
     * - static mutable state becomes global shared state for that class loader.
     * - instance-based design keeps state explicit, isolated, and easier to test.
     */

    public static void main(String[] args) {
        System.out.println("=== WRONG: static mutable state (global) ===");
        wrongStaticExample();

        System.out.println("\n=== RIGHT: instance-based (per configuration) ===");
        rightInstanceExample();

        nullReferenceStaticDispatchTrap();
        interviewTakeaways();
        interviewTrapQuestions();
    }

    private static void wrongStaticExample() {
        // Imagine a backend serving two tenants (or two tests) with different discount rules.
        // This implementation uses a static mutable field: changing it affects everyone.

        WrongDiscountCalculator.setDiscountPercent(10);
        BigDecimal tenantA = WrongDiscountCalculator.finalPrice(new BigDecimal("100.00"));
        System.out.println("Tenant A (expects 10% off): " + tenantA);

        // Somewhere else in the app (or in another test), the discount is changed:
        WrongDiscountCalculator.setDiscountPercent(50);
        BigDecimal tenantB = WrongDiscountCalculator.finalPrice(new BigDecimal("100.00"));
        System.out.println("Tenant B (expects 50% off): " + tenantB);

        // Surprise: Tenant A is now broken if it calls again later.
        BigDecimal tenantAAgain = WrongDiscountCalculator.finalPrice(new BigDecimal("100.00"));
        System.out.println("Tenant A again (BUG: now also 50% off): " + tenantAAgain);

        // This is exactly how flaky tests happen: one test changes global static state, another test fails.
    }

    private static void rightInstanceExample() {
        // Each tenant (or test) gets its own calculator instance.
        DiscountCalculator tenantACalc = new DiscountCalculator(10);
        DiscountCalculator tenantBCalc = new DiscountCalculator(50);

        BigDecimal tenantA = tenantACalc.finalPrice(new BigDecimal("100.00"));
        BigDecimal tenantB = tenantBCalc.finalPrice(new BigDecimal("100.00"));
        BigDecimal tenantAAgain = tenantACalc.finalPrice(new BigDecimal("100.00"));

        System.out.println("Tenant A (10% off): " + tenantA);
        System.out.println("Tenant B (50% off): " + tenantB);
        System.out.println("Tenant A again (still 10% off): " + tenantAAgain);

        // Bonus: this is naturally thread-safe because the state is immutable (final) per instance.
    }

    private static void nullReferenceStaticDispatchTrap() {
        System.out.println("\n=== Trap: calling static through a null reference ===");

        CallTarget target = null;
        System.out.println("callStaticThroughReference(null) => " + callStaticThroughReference(target));

        try {
            System.out.println("callInstanceThroughReference(null) => " + callInstanceThroughReference(target));
        } catch (NullPointerException ex) {
            System.out.println("callInstanceThroughReference(null) throws " + ex.getClass().getSimpleName());
        }

        System.out.println("Legal syntax is not good style here: prefer ClassName.staticMethod().");
    }

    @SuppressWarnings("static-access")
    private static String callStaticThroughReference(Object target) {
        return ((CallTarget) target).describeType();
    }

    private static String callInstanceThroughReference(Object target) {
        return ((CallTarget) target).describeInstance();
    }

    private static void interviewTakeaways() {
        System.out.println("\n=== Interview takeaway ===");
        System.out.println("- static is fine for constants and stateless utilities.");
        System.out.println("- static mutable state is shared across the whole JVM: tests interfere, multi-tenant bugs, concurrency risks.");
        System.out.println("- prefer instance + dependency injection when behavior depends on config/state or needs isolation.");
    }

    private static void interviewTrapQuestions() {
        System.out.println("\n=== Trap questions interviewers ask ===");
        System.out.println("Q: Can a static method access an instance field directly?");
        System.out.println("A: No. It needs an object reference first.");
        System.out.println("Q: Are static methods overridden polymorphically?");
        System.out.println("A: No. They are hidden, not overridden.");
        System.out.println("Q: Is static mutable state effectively global state?");
        System.out.println("A: Yes. It is shared for that class and can leak across callers, tests, and threads.");
        System.out.println("Q: Does calling a static method through a null reference always throw NullPointerException?");
        System.out.println("A: No. Static dispatch uses the reference type, not an instance dereference.");
    }

    static final class CallTarget {
        static String describeType() {
            return "static dispatch is resolved from the declared type";
        }

        String describeInstance() {
            return "instance dispatch needs a real object";
        }
    }

    /**
     * WRONG on purpose:
     * - static mutable configuration shared globally
     * - hidden dependency: finalPrice depends on whatever last setDiscountPercent() was called
     */
    static final class WrongDiscountCalculator {
        private static int discountPercent; // GLOBAL mutable state

        static void setDiscountPercent(int percent) {
            if (percent < 0 || percent > 100) {
                throw new IllegalArgumentException("percent must be 0..100");
            }
            discountPercent = percent;
        }

        static BigDecimal finalPrice(BigDecimal original) {
            BigDecimal multiplier = BigDecimal.valueOf(100 - discountPercent)
                    .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            return original.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
        }
    }

    /**
     * RIGHT:
     * - per-instance immutable config
     * - explicit dependency: discountPercent is fixed for this calculator
     */
    static final class DiscountCalculator {
        private final int discountPercent;

        DiscountCalculator(int discountPercent) {
            if (discountPercent < 0 || discountPercent > 100) {
                throw new IllegalArgumentException("discountPercent must be 0..100");
            }
            this.discountPercent = discountPercent;
        }

        BigDecimal finalPrice(BigDecimal original) {
            BigDecimal multiplier = BigDecimal.valueOf(100 - discountPercent)
                    .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            return original.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
        }
    }
}
