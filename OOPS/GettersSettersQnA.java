package OOPS;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;

/**
 * Create getter/setter methods (Q&A + runnable demos)
 *
 * Run:
 *   javac .\OOPS\GettersSettersQnA.java
 *   java OOPS.GettersSettersQnA
 */
public class GettersSettersQnA {

    /*
     * =============================
     * 1) What are getters and setters?
     * =============================
     * Getter = method that returns internal state (read access).
     * Setter = method that updates internal state (write access).
     *
     * Interview point:
     * - The goal is NOT to create getters/setters for everything.
     * - The goal is to protect invariants and encapsulate state.
     */

    /*
     * =============================
     * 2) Why use getters/setters instead of public fields?
     * =============================
     * - Validation: block invalid state (fail fast)
     * - Consistency: update multiple fields together
     * - Encapsulation: hide representation (you can change internals later)
     * - Security: avoid leaking mutable references
     */

    /*
     * =============================
     * 3) Basic example: validation in setter
     * =============================
     * Edge cases interviewers ask:
     * - Validate null/blank
     * - Validate ranges
     * - Decide exception type: IllegalArgumentException is common
     */

    static final class BankAccount {
        private final String id;
        private long balance;

        BankAccount(String id, long openingBalance) {
            if (id == null || id.isBlank()) {
                throw new IllegalArgumentException("id must not be blank");
            }
            if (openingBalance < 0) {
                throw new IllegalArgumentException("openingBalance must be >= 0");
            }
            this.id = id;
            this.balance = openingBalance;
        }

        // Getter
        public String getId() {
            return id;
        }

        public long getBalance() {
            return balance;
        }

        // No setBalance(...) on purpose: balance should be changed via domain methods
        public void deposit(long amount) {
            if (amount <= 0) {
                throw new IllegalArgumentException("amount must be > 0");
            }
            balance += amount;
        }

        public void withdraw(long amount) {
            if (amount <= 0) {
                throw new IllegalArgumentException("amount must be > 0");
            }
            if (amount > balance) {
                throw new IllegalStateException("insufficient balance");
            }
            balance -= amount;
        }

        @Override
        public String toString() {
            return "BankAccount{id='" + id + "', balance=" + balance + "}";
        }
    }

    /*
     * =============================
     * 4) Common pitfall: leaking a mutable field
     * =============================
     * If you return a reference to a mutable internal object (arrays/collections/date objects),
     * caller can modify your internal state without using setters.
     */

    static final class ScoresBad {
        private final int[] scores;

        ScoresBad(int[] scores) {
            this.scores = Objects.requireNonNull(scores);
        }

        // BAD getter: exposes internal array reference
        int[] getScores() {
            return scores;
        }
    }

    static final class ScoresGood {
        private final int[] scores;

        ScoresGood(int[] scores) {
            if (scores == null) {
                throw new NullPointerException("scores");
            }
            // Defensive copy on input
            this.scores = scores.clone();
        }

        // GOOD getter: defensive copy on output
        int[] getScoresCopy() {
            return scores.clone();
        }
    }

    /*
     * =============================
     * 5) Boolean getter naming (isX vs getX)
     * =============================
     * Convention:
     * - boolean property: isActive() is common
     * - wrapper Boolean: getActive() is also common
     *
     * Interview note:
     * - Frameworks/beans introspection often expects these conventions.
     */

    static final class FeatureFlag {
        private boolean enabled;

        FeatureFlag(boolean enabled) {
            this.enabled = enabled;
        }

        boolean isEnabled() {
            return enabled;
        }

        void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    /*
     * =============================
     * 6) Fluent setters (method chaining)
     * =============================
     * Two styles:
     * - Traditional setters return void
     * - Fluent setters return this
     *
     * Interview trade-off:
     * - Fluent APIs are nice for builders.
     * - But classic JavaBeans expects void setters (for some tooling).
     */

    static final class UserProfile {
        private String name;
        private int age;

        UserProfile setName(String name) {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("name must not be blank");
            }
            this.name = name;
            return this;
        }

        UserProfile setAge(int age) {
            if (age < 0) {
                throw new IllegalArgumentException("age must be >= 0");
            }
            this.age = age;
            return this;
        }

        String getName() {
            return name;
        }

        int getAge() {
            return age;
        }

        @Override
        public String toString() {
            return "UserProfile{name='" + name + "', age=" + age + "}";
        }
    }

    /*
     * =============================
     * 7) Immutability: prefer getters only
     * =============================
     * Senior interview point:
     * - Prefer immutable classes when possible:
     *   - private final fields
     *   - validate in constructor
     *   - getters only, no setters
     *
     * Note:
     * - In modern Java, records can replace some DTO-style classes.
     */

    static final class ImmutableEmployee {
        private final String id;
        private final LocalDate joinDate;

        ImmutableEmployee(String id, LocalDate joinDate) {
            if (id == null || id.isBlank()) {
                throw new IllegalArgumentException("id must not be blank");
            }
            this.id = id;
            this.joinDate = Objects.requireNonNull(joinDate);
        }

        String getId() {
            return id;
        }

        LocalDate getJoinDate() {
            return joinDate;
        }

        @Override
        public String toString() {
            return "ImmutableEmployee{id='" + id + "', joinDate=" + joinDate + "}";
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Getters/Setters QnA ===");

        System.out.println("\n--- 3) Validation + domain methods (no direct setter) ---");
        BankAccount acc = new BankAccount("A-1", 100);
        acc.deposit(50);
        acc.withdraw(25);
        System.out.println(acc);
        // output: BankAccount{id='A-1', balance=125}

        System.out.println("\n--- 4) Mutable leak: bad getter vs defensive copy ---");
        int[] input = { 1, 2, 3 };

        ScoresBad bad = new ScoresBad(input);
        int[] leaked = bad.getScores();
        leaked[0] = 999; // mutates internal state of ScoresBad!
        System.out.println(Arrays.toString(bad.getScores()));
        // output: [999, 2, 3]

        int[] freshInput = { 1, 2, 3 };
        ScoresGood good = new ScoresGood(freshInput);
        int[] copy = good.getScoresCopy();
        copy[0] = 777; // only mutates the copy
        System.out.println(Arrays.toString(good.getScoresCopy()));
        // output: [1, 2, 3]

        System.out.println("\n--- 5) Boolean getter naming ---");
        FeatureFlag flag = new FeatureFlag(false);
        System.out.println(flag.isEnabled());
        // output: false
        flag.setEnabled(true);
        System.out.println(flag.isEnabled());
        // output: true

        System.out.println("\n--- 6) Fluent setters ---");
        UserProfile up = new UserProfile().setName("Ravi").setAge(28);
        System.out.println(up);
        // output: UserProfile{name='Ravi', age=28}

        System.out.println("\n--- 7) Immutable class (getters only) ---");
        ImmutableEmployee emp = new ImmutableEmployee("E-1", LocalDate.of(2025, 1, 1));
        System.out.println(emp.getId());
        System.out.println(emp.getJoinDate());
        // output:
        // E-1
        // 2025-01-01
    }
}
