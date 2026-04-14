package OOPS;

/**
 * Encapsulation (Q&A + very basic runnable demo)
 *
 * Run:
 *   javac .\OOPS\EncapsulationQnA.java
 *   java OOPS.EncapsulationQnA
 */
public class EncapsulationQnA {

    /*
     * =============================
     * 1) What is encapsulation?
     * =============================
     * Encapsulation means:
     * - keep data and behavior together inside a class
     * - hide internal state of object and exposing behaviour through methods
     * - protect the internal state from direct uncontrolled access
     * - expose safe operations through methods
     *
     * Beginner one-liner:
     * - "Encapsulation hides the internal details and gives controlled access."
     */

    static final class BankAccount {
        private final String ownerName;
        private double balance;

        BankAccount(String ownerName, double openingBalance) {
            if (ownerName == null || ownerName.isBlank()) {
                throw new IllegalArgumentException("ownerName cannot be blank");
            }
            if (openingBalance < 0) {
                throw new IllegalArgumentException("openingBalance cannot be negative");
            }

            this.ownerName = ownerName;
            this.balance = openingBalance;
        }

        public String getOwnerName() {
            return ownerName;
        }

        public double getBalance() {
            return balance;
        }

        public void deposit(double amount) {
            if (amount <= 0) {
                throw new IllegalArgumentException("deposit amount must be positive");
            }
            balance += amount;
        }

        public boolean withdraw(double amount) {
            if (amount <= 0) {
                throw new IllegalArgumentException("withdraw amount must be positive");
            }
            if (amount > balance) {
                return false;
            }

            balance -= amount;
            return true;
        }

        public String summary() {
            return "BankAccount{owner='" + ownerName + "', balance=" + balance + "}";
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Encapsulation ===");

        System.out.println("1) We create an object with valid data.");
        BankAccount account = new BankAccount("Asha", 1000.0);
        System.out.println(account.summary());

        System.out.println("\n2) We do not change balance directly because balance is private.");
        System.out.println("   Instead, we use methods that check the rules.");

        System.out.println("\n3) Deposit 250.0");
        account.deposit(250.0);
        System.out.println("   New balance: " + account.getBalance());

        System.out.println("\n4) Withdraw 400.0");
        boolean withdrew = account.withdraw(400.0);
        System.out.println("   Withdraw success: " + withdrew);
        System.out.println("   New balance: " + account.getBalance());

        System.out.println("\n5) Try to withdraw too much.");
        withdrew = account.withdraw(2000.0);
        System.out.println("   Withdraw success: " + withdrew);
        System.out.println("   Balance is still protected: " + account.getBalance());

        System.out.println("\nInterview summary:");
        System.out.println("- private fields protect the object from invalid direct changes");
        System.out.println("- public methods give controlled access to the state");
        System.out.println("- getters/setters are not the goal by themselves; data safety is the goal");
    }
}