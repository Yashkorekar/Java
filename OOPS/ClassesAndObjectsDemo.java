package OOPS;

import java.util.Objects;

public class ClassesAndObjectsDemo {

    public static void main(String[] args) {
        System.out.println("=== OOPS: Define classes and objects ===");

        section1_WhatIsClassVsObject();
        section2_ConstructorsAndThis();
        section3_ObjectReferencesAliasing();
        section4_EqualityVsIdentity();
        section5_StaticVsInstanceInClassContext();
        section6_CommonInterviewTricks();
    }

    private static void section1_WhatIsClassVsObject() {
        System.out.println("\n--- 1) Class vs Object ---");

        // Class = blueprint/type.
        // Object = runtime instance of a class.

        Person p1 = new Person("Asha", 28);
        Person p2 = new Person("Ravi", 30);

        System.out.println(p1.introduce()); // output: Hi, I'm Asha (28)
        System.out.println(p2.introduce()); // output: Hi, I'm Ravi (30)

        // Each object has its own state.
        p1.setAge(29);
        System.out.println(p1.introduce()); // output: Hi, I'm Asha (29)
        System.out.println(p2.introduce()); // output: Hi, I'm Ravi (30)
    }

    private static void section2_ConstructorsAndThis() {
        System.out.println("\n--- 2) Constructors + this ---");

        // Constructors initialize new objects.
        // 'this' refers to the current object.

        Account a1 = new Account("ACC-1");
        Account a2 = new Account("ACC-2", 100);

        System.out.println(a1.summary()); // output: ACC-1 balance=0
        System.out.println(a2.summary()); // output: ACC-2 balance=100

        a2.deposit(50);
        System.out.println(a2.summary()); // output: ACC-2 balance=150

        // Interview edge: constructor overloading chains often use this(...)
        Account a3 = new Account("ACC-3");
        System.out.println(a3.summary()); // output: ACC-3 balance=0
    }

    private static void section3_ObjectReferencesAliasing() {
        System.out.println("\n--- 3) Object references + aliasing ---");

        Person original = new Person("Mina", 25);
        Person alias = original; // IMPORTANT: both variables refer to the SAME object

        System.out.println(original.introduce()); // output: Hi, I'm Mina (25)
        System.out.println(alias.introduce());    // output: Hi, I'm Mina (25)

        alias.setAge(26);

        // Because alias and original point to the same object, the change is visible through both.
        System.out.println(original.introduce()); // output: Hi, I'm Mina (26)
        System.out.println(alias.introduce());    // output: Hi, I'm Mina (26)

        // Key interview phrase: "In Java, variables of object types hold references."
        // Another key phrase: "Java is pass-by-value; for objects, the value is the reference."
    }

    private static void section4_EqualityVsIdentity() {
        System.out.println("\n--- 4) == vs equals() ---");

        Person x1 = new Person("Kiran", 31);
        Person x2 = new Person("Kiran", 31);
        Person x3 = x1;

        // '==' checks identity: do the two references point to the same object?
        System.out.println(x1 == x2); // output: false (different objects)
        System.out.println(x1 == x3); // output: true  (same object)

        // equals() checks logical equality if implemented.
        System.out.println(x1.equals(x2)); // output: true  (same data, equals overridden)

        // String is a classic interviewer trap: literals may be interned
        String s1 = "java";
        String s2 = "java";
        String s3 = new String("java");

        System.out.println(s1 == s2);      // output: true  (likely same interned literal)
        System.out.println(s1 == s3);      // output: false (new object)
        System.out.println(s1.equals(s3)); // output: true  (same characters)
    }

    private static void section5_StaticVsInstanceInClassContext() {
        System.out.println("\n--- 5) Static vs Instance (in class/object context) ---");

        // Static field: one per class (shared across all objects)
        // Instance field: one per object

        Counter c1 = new Counter();
        Counter c2 = new Counter();

        c1.increment();
        c1.increment();
        c2.increment();

        System.out.println(c1.instanceCount()); // output: 2
        System.out.println(c2.instanceCount()); // output: 1

        System.out.println(Counter.globalCount()); // output: 3 (shared)

        // Interview reminder: access static members via class name for clarity.
        // Counter.globalCount() rather than c1.globalCount().
    }

    private static void section6_CommonInterviewTricks() {
        System.out.println("\n--- 6) Common interview trick questions ---");

        // Q1) What are default values?
        DefaultValues dv = new DefaultValues();
        System.out.println("default int=" + dv.number);      // output: default int=0
        System.out.println("default boolean=" + dv.flag);    // output: default boolean=false
        System.out.println("default reference=" + dv.name);  // output: default reference=null

        // Q2) Can we access instance fields from a static method?
        // Not directly. You need an object reference.
        Person p = new Person("Sara", 22);
        System.out.println(Person.describe(p)); // output: Person{name='Sara', age=22}

        // Q3) What happens if you print an object?
        // If toString() is overridden, you'll get meaningful text.
        System.out.println(p); // output: Person{name='Sara', age=22}

        // Q4) Can a class have multiple objects? Yes.
        // Q5) Can an object reference be null? Yes.
        Person maybeNull = null;
        System.out.println(maybeNull == null); // output: true

        // Calling a method on null throws NullPointerException:
        try {
            maybeNull.introduce();
        } catch (NullPointerException ex) {
            System.out.println("calling method on null -> NPE"); // output: calling method on null -> NPE
        }
    }

    // ------------------------
    // Supporting classes (kept small, interview-friendly)
    // ------------------------

    /**
     * A simple POJO-style class.
     * Demonstrates fields, constructor, methods, encapsulation, toString/equals/hashCode.
     */
    static final class Person {
        private final String name;
        private int age;

        Person(String name, int age) {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("name must not be blank");
            }
            if (age < 0) {
                throw new IllegalArgumentException("age must be >= 0");
            }
            this.name = name;
            this.age = age;
        }

        void setAge(int age) {
            if (age < 0) {
                throw new IllegalArgumentException("age must be >= 0");
            }
            this.age = age;
        }

        String introduce() {
            return "Hi, I'm " + name + " (" + age + ")";
        }

        static String describe(Person p) {
            // Static method: no 'this'. Must operate on provided parameters.
            if (p == null) {
                return "null";
            }
            return p.toString();
        }

        @Override
        public String toString() {
            return "Person{name='" + name + "', age=" + age + "}";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Person other)) {
                return false;
            }
            return age == other.age && name.equals(other.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, age);
        }
    }

    /**
     * Constructor overloading + this(...) chaining.
     */
    static final class Account {
        private final String id;
        private long balance;

        Account(String id) {
            this(id, 0);
        }

        Account(String id, long openingBalance) {
            if (id == null || id.isBlank()) {
                throw new IllegalArgumentException("id must not be blank");
            }
            if (openingBalance < 0) {
                throw new IllegalArgumentException("openingBalance must be >= 0");
            }
            this.id = id;
            this.balance = openingBalance;
        }

        void deposit(long amount) {
            if (amount <= 0) {
                throw new IllegalArgumentException("amount must be > 0");
            }
            this.balance += amount;
        }

        String summary() {
            return id + " balance=" + balance;
        }
    }

    /**
     * Shows instance vs static fields.
     */
    static final class Counter {
        private static int global;
        private int local;

        void increment() {
            local++;
            global++;
        }

        int instanceCount() {
            return local;
        }

        static int globalCount() {
            return global;
        }
    }

    /**
     * Default values are applied only to fields, not local variables.
     */
    static final class DefaultValues {
        int number;
        boolean flag;
        String name;

        // If these were local variables inside a method, Java would force you to initialize them.
    }
}
