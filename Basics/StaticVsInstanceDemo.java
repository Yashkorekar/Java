package Basics;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class StaticVsInstanceDemo {

    public static void main(String[] args) {
        System.out.println("=== WRONG: static mutable state (global) ===");
        wrongStaticExample();

        System.out.println("\n=== RIGHT: instance-based (per configuration) ===");
        rightInstanceExample();

        System.out.println("\n=== Interview takeaway ===");
        System.out.println("- static is fine for constants and stateless utilities.");
        System.out.println("- static mutable state is shared across the whole JVM: tests interfere, multi-tenant bugs, concurrency risks.");
        System.out.println("- prefer instance + dependency injection when behavior depends on config/state or needs isolation.");
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
