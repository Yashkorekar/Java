package OOPS;

/**
 * Abstraction (Q&A + very basic runnable demo)
 *
 * Run:
 *   javac .\OOPS\AbstractionQnA.java
 *   java OOPS.AbstractionQnA
 */
public class AbstractionQnA {

    /*
     * =============================
     * 1) What is abstraction?
     * =============================
     * Abstraction means showing only the important part and hiding the internal details.
     *
     * In Java, abstraction is usually shown with:
     * - abstract classes
     * - interfaces
     *
     * Beginner one-liner:
     * - "Abstraction tells WHAT an object does, not all the internal HOW."
     */

    static abstract class NotificationService {
        private final String serviceName;

        NotificationService(String serviceName) {
            this.serviceName = serviceName;
        }

        public String getServiceName() {
            return serviceName;
        }

        public void send(String message) {
            System.out.println(serviceName + " preparing to send message...");
            deliver(message);
            System.out.println(serviceName + " finished sending message.");
        }

        protected abstract void deliver(String message);
    }

    static final class EmailService extends NotificationService {

        EmailService() {
            super("EmailService");
        }

        @Override
        protected void deliver(String message) {
            System.out.println("Sending EMAIL with message: " + message);
        }
    }

    static final class SmsService extends NotificationService {

        SmsService() {
            super("SmsService");
        }

        @Override
        protected void deliver(String message) {
            System.out.println("Sending SMS with message: " + message);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Abstraction ===");

        System.out.println("1) We use the abstract type, not the low-level details directly.");
        NotificationService emailService = new EmailService();
        NotificationService smsService = new SmsService();

        System.out.println("\n2) Client code only knows: send a message.");
        System.out.println("   The internal delivery logic stays hidden inside each child class.");

        System.out.println("\n3) Sending through EmailService:");
        emailService.send("Welcome to the app");

        System.out.println("\n4) Sending through SmsService:");
        smsService.send("Your OTP is 123456");

        System.out.println("\nInterview summary:");
        System.out.println("- abstract class gives a common contract plus shared code");
        System.out.println("- child classes must provide the missing implementation");
        System.out.println("- caller uses the abstraction without depending on every internal step");
    }
}