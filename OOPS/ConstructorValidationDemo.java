package OOPS;

/**
 * Demonstrates why constructor validation matters.
 *
 * BAD version:
 * - allows null/blank id
 * - allows negative balance
 * - bugs show up later (NPE/invalid state)
 *
 * GOOD version:
 * - validates inputs in constructor
 * - fails fast: you cannot create an invalid object
 */
public class ConstructorValidationDemo {

    public static void main(String[] args) {
        System.out.println("=== Constructor validation demo ===");

        badVersion();
        goodVersion();
    }

    private static void badVersion() {
        System.out.println("\n--- BAD: no constructor validation ---");

        BadAccount a = new BadAccount(null, -500);
        System.out.println(a);
        // output: BadAccount{id=null, balance=-500}

        // Problem 1: Invalid business state exists in memory.
        // Any method relying on 'id' can explode later.
        try {
            System.out.println("id length=" + a.idLength());
            // would crash before printing
        } catch (NullPointerException ex) {
            System.out.println("NPE happened later (id was null)");
            // output: NPE happened later (id was null)
        }

        // Problem 2: You can violate invariants (e.g., negative balance) without noticing.
        if (a.balance < 0) {
            System.out.println("invalid state: negative balance exists");
            // output: invalid state: negative balance exists
        }
    }

    private static void goodVersion() {
        System.out.println("\n--- GOOD: validate in constructor (fail fast) ---");

        try {
            new GoodAccount(null, -500);
        } catch (IllegalArgumentException ex) {
            System.out.println("blocked invalid object creation: " + ex.getMessage());
            // output: blocked invalid object creation: id must not be blank
        }

        GoodAccount ok = new GoodAccount("A-1", 100);
        System.out.println(ok);
        // output: GoodAccount{id='A-1', balance=100}

        System.out.println("id length=" + ok.idLength());
        // output: id length=3
    }

    // --------------------
    // BAD version
    // --------------------

    static final class BadAccount {
        private final String id;
        private final long balance;

        BadAccount(String id, long openingBalance) {
            // BAD: no validation at all
            this.id = id;
            this.balance = openingBalance;
        }

        int idLength() {
            // Will throw NullPointerException if id is null
            return id.length();
        }

        @Override
        public String toString() {
            return "BadAccount{id=" + id + ", balance=" + balance + "}";
        }
    }

    // --------------------
    // GOOD version
    // --------------------

    static final class GoodAccount {
        private final String id;
        private final long balance;

        GoodAccount(String id, long openingBalance) {
            if (id == null || id.isBlank()) {
                throw new IllegalArgumentException("id must not be blank");
            }
            if (openingBalance < 0) {
                throw new IllegalArgumentException("openingBalance must be >= 0");
            }
            this.id = id;
            this.balance = openingBalance;
        }

        int idLength() {
            return id.length();
        }

        @Override
        public String toString() {
            return "GoodAccount{id='" + id + "', balance=" + balance + "}";
        }
    }
}
