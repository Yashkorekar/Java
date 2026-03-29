package OOPS;

/**
 * Interfaces, abstract classes, and composition (Q&A + runnable demos)
 *
 * Run:
 *   javac .\OOPS\InterfacesAbstractClassesAndCompositionQnA.java
 *   java OOPS.InterfacesAbstractClassesAndCompositionQnA
 */
public class InterfacesAbstractClassesAndCompositionQnA {

    interface PaymentMethod {
        String pay(long amount);
    }

    interface Auditable {
        default String auditLabel() {
            return "AUDIT-DEFAULT";
        }
    }

    static final class CardPayment implements PaymentMethod, Auditable {
        @Override
        public String pay(long amount) {
            return "card paid=" + amount;
        }
    }

    abstract static class ReportGenerator {
        final String generate() {
            return header() + " -> " + body();
        }

        String header() {
            return "REPORT";
        }

        abstract String body();
    }

    static final class CsvReportGenerator extends ReportGenerator {
        @Override
        String body() {
            return "csv-body";
        }
    }

    interface Left {
        default String side() {
            return "left";
        }
    }

    interface Right {
        default String side() {
            return "right";
        }
    }

    static final class BothSides implements Left, Right {
        @Override
        public String side() {
            return Left.super.side() + "/" + Right.super.side();
        }
    }

    static final class Engine {
        String start() {
            return "engine started";
        }
    }

    static final class Car {
        private final Engine engine;

        Car(Engine engine) {
            this.engine = engine;
        }

        String drive() {
            return engine.start() + " -> car moving";
        }
    }

    private static void interfaceDemo() {
        System.out.println("=== Interface demo ===");
        PaymentMethod paymentMethod = new CardPayment();
        System.out.println(paymentMethod.pay(2500));
        System.out.println(((CardPayment) paymentMethod).auditLabel());
    }

    private static void abstractClassDemo() {
        System.out.println("\n=== Abstract class demo ===");
        ReportGenerator generator = new CsvReportGenerator();
        System.out.println(generator.generate());
    }

    private static void defaultMethodConflictDemo() {
        System.out.println("\n=== Default method conflict ===");
        System.out.println(new BothSides().side());
        System.out.println("If two interfaces provide the same default method, the class must resolve it explicitly.");
    }

    private static void compositionVsInheritanceDemo() {
        System.out.println("\n=== Composition vs inheritance ===");
        Car car = new Car(new Engine());
        System.out.println(car.drive());
        System.out.println("Composition means one object uses another object instead of inheriting everything from it.");
    }

    public static void main(String[] args) {
        interfaceDemo();
        abstractClassDemo();
        defaultMethodConflictDemo();
        compositionVsInheritanceDemo();

        System.out.println("\nInterview notes:");
        System.out.println("- A class can extend one class but implement multiple interfaces.");
        System.out.println("- Use interfaces for contracts, abstract classes for shared state/behavior.");
        System.out.println("- Prefer composition when inheritance is not a true is-a relationship.");
    }
}