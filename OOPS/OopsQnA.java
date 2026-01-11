package OOPS;

import java.util.Arrays;
import java.util.Objects;

/**
 * OOPS Q&A (not advanced) — theory + small code examples.
 *
 * NOTE ABOUT .class FILES:
 * - This file defines multiple classes/interfaces (some nested). The Java compiler creates
 *   one .class per class/interface. So seeing multiple .class files is normal.
 */
public class OopsQnA {

    /*
     * =============================
     * 1) CLASS vs OBJECT
     * =============================
     * Q: What is a class?
     * A: A blueprint/type that defines data (fields) and behavior (methods).
     *
     * Q: What is an object?
     * A: A runtime instance of a class created with 'new'. Objects have identity and state.
     */

    /*
     * =============================
     * 2) ENCAPSULATION
     * =============================
     * Q: What is encapsulation?
     * A: Hiding internal state (fields) and exposing controlled operations (methods).
     *    Typical pattern: private fields + public methods.
     *
     * Interview edge cases:
     * - Don't expose mutable fields directly.
     * - Validate inputs in setters/constructors.
     */

    static final class BankAccount {
        private final String id; // immutable identity
        private long balance;     // mutable state

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

        public long getBalance() {
            return balance;
        }

        @Override
        public String toString() {
            return "BankAccount{id='" + id + "', balance=" + balance + "}";
        }
    }

    /*
     * =============================
     * 3) CONSTRUCTORS + this
     * =============================
     * Q: What is a constructor?
     * A: Special method-like block that initializes an object. Name = class name.
     *
     * Q: What is 'this'?
     * A: Reference to the current object.
     *
     * Interview trick:
     * - this(...) must be the FIRST statement in a constructor.
     */

    static final class User {
        private final String name;
        private final int age;

        User(String name) {
            this(name, 0);
        }

        User(String name, int age) {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("name must not be blank");
            }
            if (age < 0) {
                throw new IllegalArgumentException("age must be >= 0");
            }
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return "User{name='" + name + "', age=" + age + "}";
        }
    }

    /*
     * =============================
     * 4) INHERITANCE
     * =============================
     * Q: What is inheritance?
     * A: "is-a" relationship. Child class extends parent class.
     *
     * Interview edge cases:
     * - Prefer composition over inheritance unless there is a true is-a relationship.
     * - Java supports single inheritance for classes.
     */

    static class Animal {
        String speak() {
            return "...";
        }
    }

    static final class Dog extends Animal {
        @Override
        String speak() {
            return "woof";
        }
    }

    /*
     * =============================
     * 5) POLYMORPHISM (runtime)
     * =============================
     * Q: What is polymorphism?
     * A: Treating different objects through a common supertype (parent/interface).
     *
     * Example:
     * Animal a = new Dog();
     * a.speak() calls Dog.speak() at runtime (dynamic dispatch).
     */

    /*
     * =============================
     * 6) METHOD OVERLOADING vs OVERRIDING
     * =============================
     * Overloading:
     * - Same method name, different parameter list.
     * - Resolved at compile time.
     *
     * Overriding:
     * - Child provides new implementation of parent method with same signature.
     * - Resolved at runtime.
     */

    static final class MathUtil {
        static int add(int a, int b) {
            return a + b;
        }

        static long add(long a, long b) {
            return a + b;
        }

        static String add(String a, String b) {
            return a + b;
        }
    }

    /*
     * =============================
     * 7) ABSTRACTION: abstract class vs interface
     * =============================
     * Q: What is abstraction?
     * A: Expose what something does, hide how it does it.
     *
     * Interface:
     * - Defines a contract (methods).
     * - A class can implement multiple interfaces.
     *
     * Abstract class:
     * - Can hold common code + state.
     * - A class can extend only one abstract/class.
     */

    interface PaymentProcessor {
        String name();
        boolean charge(long cents);
    }

    static final class MockPaymentProcessor implements PaymentProcessor {
        @Override
        public String name() {
            return "mock";
        }

        @Override
        public boolean charge(long cents) {
            return cents > 0;
        }
    }

    /*
     * =============================
     * 8) STATIC vs INSTANCE (quick)
     * =============================
     * Q: What does static mean?
     * A: Belongs to the class, not to a particular object.
     *
     * Use static for:
     * - constants (public static final)
     * - stateless utilities
     *
     * Avoid static mutable state (test interference, concurrency issues).
     */

    /*
     * =============================
     * 9) FINAL keyword
     * =============================
     * final variable: cannot be reassigned.
     * final method: cannot be overridden.
     * final class: cannot be extended.
     */

    /*
     * =============================
     * 10) equals, hashCode, toString
     * =============================
     * Q: Why override equals/hashCode?
     * A: To define logical equality and make HashMap/HashSet work correctly.
     *
     * Interview edge:
     * - If you override equals(), you must override hashCode().
     */

    static final class Point {
        private final int x;
        private final int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Point other)) {
                return false;
            }
            return x == other.x && y == other.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public String toString() {
            return "Point(" + x + "," + y + ")";
        }
    }

    /*
     * =============================
     * 11) COMPOSITION vs INHERITANCE
     * =============================
     * Composition = "has-a".
     * Prefer when you want reuse without tight coupling.
     */

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

        String start() {
            return engine.start();
        }
    }

    /*
     * =============================
     * 12) ACCESS MODIFIERS (basic)
     * =============================
     * private   - only inside the class
     * (default) - package-private: same package
     * protected - package + subclasses
     * public    - everywhere
     *
     * Interview tricky points:
     * - Overriding cannot reduce visibility (public -> protected is NOT allowed).
     * - Fields can be hidden, but hiding is NOT overriding.
     */

    /*
     * =============================
     * 13) PASS-BY-VALUE (very common confusion)
     * =============================
     * Q: Is Java pass-by-value or pass-by-reference?
        * A: Java is ALWAYS pass-by-value.
        *
        * Mental model (most interview-friendly):
        * - A variable holds a VALUE.
        * - When you call a method, Java COPIES that value into the parameter.
        *
        * Case 1: primitives
        * - The value is the primitive itself (int, long, double...).
        * - So the method receives a copy of the number.
        *
        * Case 2: objects (including arrays, Strings, collections, your classes)
        * - The value stored in the variable is a REFERENCE (an address-like handle) to an object.
        * - Java copies that reference into the parameter.
        * - Result:
        *   (a) You CAN mutate the same object via that copied reference (if the object is mutable).
        *   (b) You CANNOT rebind the caller's variable to a different object (because only the local
        *       parameter changes).
        *
        * What "pass-by-reference" would mean (Java does NOT do this):
        * - The method would receive something like the caller's variable itself (an alias to it),
        *   so reassigning the parameter would also change the caller's variable.
     *
     * Interview checks:
     * - A method can mutate the object through the reference.
     * - A method cannot rebind the caller's variable to a different object.
     */

    static final class Holder {
        int value;

        Holder(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "Holder{" + value + "}";
        }
    }

    static void mutatePrimitive(int x) {
        x = x + 10;
    }

    static void mutateObject(Holder h) {
        h.value = h.value + 10;
    }

    static void reassignObject(Holder h) {
        h = new Holder(999); // only reassigns the local copy of the reference
    }

    static void swapHolders(Holder a, Holder b) {
        // Common interview trick: you cannot swap caller variables in Java.
        Holder tmp = a;
        a = b;
        b = tmp;
    }

    static void mutateArray(int[] a) {
        a[0] = a[0] + 10;
    }

    static void reassignArray(int[] a) {
        a = new int[] { 999, 999 }; // only reassigns the local copy of the reference
    }

    /*
     * =============================
     * 14) OBJECT CREATION & INITIALIZATION ORDER
     * =============================
     * Interview order question (high frequency):
     * When you do `new Child()`, the order is:
     * 1) Parent static init (once per class load)
     * 2) Child static init (once per class load)
     * 3) Parent instance init blocks + field init
     * 4) Parent constructor
     * 5) Child instance init blocks + field init
     * 6) Child constructor
     */

    static class ParentInit {
        static {
            System.out.println("ParentInit: static init");
        }

        {
            System.out.println("ParentInit: instance init");
        }

        ParentInit() {
            System.out.println("ParentInit: constructor");
        }
    }

    static final class ChildInit extends ParentInit {
        static {
            System.out.println("ChildInit: static init");
        }

        {
            System.out.println("ChildInit: instance init");
        }

        ChildInit() {
            System.out.println("ChildInit: constructor");
        }
    }

    /*
     * =============================
     * 15) UPCASTING / DOWNCASTING + instanceof
     * =============================
     * Upcasting: Child -> Parent (safe, implicit)
     * Downcasting: Parent -> Child (only safe if the object is actually a Child)
     */

    static class Base {
        String who() {
            return "Base";
        }
    }

    static final class Sub extends Base {
        @Override
        String who() {
            return "Sub";
        }

        String subOnly() {
            return "subOnly";
        }
    }

    /*
     * =============================
     * 16) FIELD HIDING vs METHOD OVERRIDING
     * =============================
     * Interview trap:
     * - Methods are polymorphic (resolved at runtime).
     * - Fields are NOT polymorphic (resolved by reference type at compile time).
     */

    static class ParentName {
        String label = "parent-field";

        String label() {
            return "parent-method";
        }

        static String staticHello() {
            return "parent-static";
        }
    }

    static final class ChildName extends ParentName {
        String label = "child-field"; // hides field

        @Override
        String label() {
            return "child-method";
        }

        static String staticHello() {
            return "child-static"; // hides static method
        }
    }

    /*
     * =============================
     * 17) IMMUTABILITY + DEFENSIVE COPY (common in senior interviews)
     * =============================
     * Q: What is an immutable object?
     * A: Its state cannot change after construction.
     *
     * Edge case:
     * - If you store a reference to a mutable object (like int[]), you must do defensive copies.
     */

    static final class ImmutableScores {
        private final int[] scores;

        ImmutableScores(int[] scores) {
            if (scores == null) {
                throw new NullPointerException("scores");
            }
            this.scores = scores.clone();
        }

        int[] getScoresCopy() {
            return scores.clone();
        }
    }

    /*
     * =============================
     * 18) ABSTRACT CLASS (basic example)
     * =============================
     * Abstract class cannot be instantiated.
     * It can contain abstract methods + concrete methods.
     */

    static abstract class Shape {
        abstract double area();

        String type() {
            return getClass().getSimpleName();
        }
    }

    static final class Circle extends Shape {
        private final double radius;

        Circle(double radius) {
            if (radius < 0) {
                throw new IllegalArgumentException("radius must be >= 0");
            }
            this.radius = radius;
        }

        @Override
        double area() {
            return Math.PI * radius * radius;
        }
    }

    /*
     * =============================
     * 19) Object class basics
     * =============================
     * Every class implicitly extends Object.
     * Frequently asked methods:
     * - toString(), equals(), hashCode(), getClass()
     */

    public static void main(String[] args) {
        // This main exists so you can run and see concrete behavior.

        System.out.println("=== OOPS Q&A demo output ===");

        BankAccount acc = new BankAccount("A-1", 100);
        acc.deposit(50);
        acc.withdraw(30);
        System.out.println(acc);
        // output: BankAccount{id='A-1', balance=120}

        User u1 = new User("Ravi");
        User u2 = new User("Asha", 28);
        System.out.println(u1);
        System.out.println(u2);
        // output: User{name='Ravi', age=0}
        // output: User{name='Asha', age=28}

        Animal a = new Dog();
        System.out.println(a.speak());
        // output: woof

        System.out.println(MathUtil.add(1, 2));
        System.out.println(MathUtil.add(1L, 2L));
        System.out.println(MathUtil.add("a", "b"));
        // output: 3
        // output: 3
        // output: ab

        PaymentProcessor pp = new MockPaymentProcessor();
        System.out.println(pp.name() + " charged=" + pp.charge(10));
        // output: mock charged=true

        Point p1 = new Point(1, 2);
        Point p2 = new Point(1, 2);
        System.out.println(p1 == p2);
        System.out.println(p1.equals(p2));
        // output: false
        // output: true

        Car car = new Car(new Engine());
        System.out.println(car.start());
        // output: engine started

        System.out.println("\n--- Extra: pass-by-value demo ---");
        int x = 5;
        mutatePrimitive(x);
        System.out.println(x);
        // output: 5  (primitive not changed in caller)

        Holder h = new Holder(5);
        mutateObject(h);
        System.out.println(h);
        // output: Holder{15} (object mutated via reference)

        reassignObject(h);
        System.out.println(h);
        // output: Holder{15} (caller reference not rebound)

        Holder ha = new Holder(1);
        Holder hb = new Holder(2);
        swapHolders(ha, hb);
        System.out.println("after swap attempt: ha=" + ha + ", hb=" + hb);
        // output: after swap attempt: ha=Holder{1}, hb=Holder{2}

        int[] nums = { 1, 2, 3 };
        mutateArray(nums);
        System.out.println(Arrays.toString(nums));
        // output: [11, 2, 3]

        reassignArray(nums);
        System.out.println(Arrays.toString(nums));
        // output: [11, 2, 3]  (caller reference not rebound)

        System.out.println("\n--- Extra: initialization order demo ---");
        new ChildInit();
        // output:
        // ParentInit: static init
        // ChildInit: static init
        // ParentInit: instance init
        // ParentInit: constructor
        // ChildInit: instance init
        // ChildInit: constructor

        System.out.println("\n--- Extra: casting + instanceof demo ---");
        Base up = new Sub();
        System.out.println(up.who());
        // output: Sub

        if (up instanceof Sub sub) {
            System.out.println(sub.subOnly());
            // output: subOnly
        }

        try {
            Base plain = new Base();
            Sub wrong = (Sub) plain; // runtime ClassCastException
            System.out.println(wrong);
        } catch (ClassCastException ex) {
            System.out.println("bad cast -> ClassCastException");
            // output: bad cast -> ClassCastException
        }

        System.out.println("\n--- Extra: field vs method + static hiding demo ---");
        ParentName ref = new ChildName();
        System.out.println(ref.label);   // field access uses reference type
        System.out.println(ref.label()); // method call uses runtime type
        // output:
        // parent-field
        // child-method

        System.out.println(ParentName.staticHello());
        System.out.println(ChildName.staticHello());
        // output:
        // parent-static
        // child-static

        System.out.println("\n--- Extra: immutability + defensive copy demo ---");
        int[] arr = { 1, 2, 3 };
        ImmutableScores scores = new ImmutableScores(arr);
        arr[0] = 999; // try to mutate external array

        int[] copy = scores.getScoresCopy();
        System.out.println(copy[0]);
        // output: 1  (internal state protected)

        System.out.println("\n--- Extra: abstract class demo ---");
        Shape shape = new Circle(2);
        System.out.println(shape.type());
        System.out.printf("%.2f%n", shape.area());
        // output:
        // Circle
        // 12.57

        // If you want: pick any question above and try rewriting the example yourself.
    }
}
