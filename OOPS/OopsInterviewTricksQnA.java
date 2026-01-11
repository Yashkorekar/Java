package OOPS;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * OOPS interview tricks Q&A (still not “advanced frameworks”):
 * - super vs this
 * - constructor chaining rules
 * - overriding rules (checked exceptions, covariant returns, visibility)
 * - Object contract: equals/hashCode + HashMap behavior
 *
 * Answers are in comments; code prints outputs to make it concrete.
 */
public class OopsInterviewTricksQnA {

    /*
     * =============================
     * 1) super vs this
     * =============================
     * Q: What is 'this'?
     * A: Reference to the current object instance.
     *    - Use it to access current fields/methods: this.name, this.doSomething()
     *    - Use it in constructors to chain: this(...)
     *
     * Q: What is 'super'?
     * A: Reference to the parent class part of the current object.
     *    - Use it to call parent methods/fields: super.toString(), super.someMethod()
     *    - Use it in constructors to call parent constructor: super(...)
     *
     * Interview gotchas:
     * - You cannot use this/super in a static context (no instance exists).
     * - this(...) or super(...) must be the FIRST statement in a constructor.
     * - If you don't write super(...), Java inserts super() implicitly.
     */

    static class Parent {
        Parent() {
            System.out.println("Parent(): ctor");
        }

        Parent(String msg) {
            System.out.println("Parent(String): " + msg);
        }

        String who() {
            return "Parent";
        }

        String greet() {
            return "hello from parent";
        }
    }

    static class Child extends Parent {
        private final String name;

        // Constructor chaining with this(...)
        Child() {
            this("default");
        }

        Child(String name) {
            // super(...) must come before any other statements.
            super("called from Child(String)");
            this.name = name;
            System.out.println("Child(String): name=" + this.name);
        }

        @Override
        String who() {
            return "Child";
        }

        String parentGreetingViaSuper() {
            return super.greet();
        }

        String selfGreetingViaThis() {
            return "hello from child name=" + this.name;
        }
    }

    /*
     * =============================
     * 2) Constructor chaining rules (very common)
     * =============================
     * Rules interviewers expect:
     * - Every constructor calls either this(...) OR super(...).
     * - If you write neither, compiler adds super().
     * - this(...) and super(...) cannot both appear in the same constructor.
     * - The call must be FIRST statement.
     *
     * Also important:
     * - During object construction, parent constructor runs BEFORE child constructor body.
     */

    /*
     * =============================
     * 3) Overriding rules (checked exceptions, covariant returns, visibility)
     * =============================
     * Overriding requirements:
     * - Same method signature (name + params). Return type must be compatible.
     * - Runtime dispatch chooses the child implementation.
     *
     * Visibility rule:
     * - You cannot reduce visibility:
     *   public -> protected/private (NOT allowed)
     *   protected -> private (NOT allowed)
     *
     * Checked exceptions rule:
     * - Overriding method can throw:
     *   - same checked exceptions, OR
     *   - fewer checked exceptions, OR
     *   - narrower (subclasses) of the declared checked exceptions.
     * - It cannot throw broader/new checked exceptions.
     * - It may throw any UNCHECKED exceptions (RuntimeException).
     *
     * Covariant return:
     * - Child can return a subtype of the parent return type.
     */

    static class Service {
        // Example checked exception in signature
        public Number compute(int x) throws java.io.IOException {
            if (x < 0) {
                throw new java.io.IOException("negative");
            }
            return x;
        }
    }

    static class FastService extends Service {
        // Covariant return: Integer is a subtype of Number
        // Checked exception: we can throw fewer (or none). Here we throw none.
        @Override
        public Integer compute(int x) {
            if (x < 0) {
                // Unchecked exceptions are allowed even if parent declared checked ones.
                throw new IllegalArgumentException("negative");
            }
            return x * 2;
        }
    }

    /*
     * Static/private/final gotchas:
     * - private methods are NOT overridden (they are not visible to child).
     * - static methods are NOT overridden (they are hidden).
     * - final methods cannot be overridden.
     */

    static class StaticTrap {
        static String hello() {
            return "parent-static";
        }

        final String finalHello() {
            return "parent-final";
        }

        private String secret() {
            return "parent-secret";
        }

        String callSecret() {
            return secret(); // calls parent's private method
        }
    }

    static class StaticTrapChild extends StaticTrap {
        static String hello() {
            return "child-static"; // hides, does not override
        }

        // Cannot override finalHello() (would not compile)

        // This is a NEW method, not an override, because parent's secret() is private.
        private String secret() {
            return "child-secret";
        }

        String callChildSecretDirectly() {
            return secret();
        }
    }

    /*
     * =============================
     * 4) equals/hashCode contract + HashMap behavior (super common)
     * =============================
     * equals() contract (Object):
     * - reflexive: x.equals(x) is true
     * - symmetric: x.equals(y) == y.equals(x)
     * - transitive
     * - consistent (doesn't change unless fields used change)
     * - x.equals(null) is false
     *
     * hashCode() contract:
     * - If x.equals(y) is true, then x.hashCode() MUST equal y.hashCode().
     * - If x.equals(y) is false, hash codes may still collide (allowed).
     *
     * HashMap uses:
     * - hashCode() to choose a bucket
     * - equals() to find the correct key within that bucket
     *
     * Interview trap:
     * - Using mutable objects as HashMap keys and then mutating fields used by equals/hashCode.
     *   After mutation, you may not be able to get/remove the entry.
     */

    static final class EmployeeKey {
        private String id; // mutable on purpose to demonstrate the bug

        EmployeeKey(String id) {
            this.id = Objects.requireNonNull(id);
        }

        void setId(String id) {
            this.id = Objects.requireNonNull(id);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof EmployeeKey other)) {
                return false;
            }
            return id.equals(other.id);
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }

        @Override
        public String toString() {
            return "EmployeeKey{" + id + "}";
        }
    }

    /*
     * Similar “must know” concept:
     * - Overload vs override dispatch (compile-time vs runtime)
     *   - Overloading is chosen at compile time based on reference types.
     *   - Overriding is chosen at runtime based on actual object type.
     */

    static class Printer {
        String print(Object o) {
            return "Object";
        }

        String print(String s) {
            return "String";
        }
    }

    static class FancyPrinter extends Printer {
        @Override
        String print(Object o) {
            return "Fancy(Object)";
        }

        @Override
        String print(String s) {
            return "Fancy(String)";
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== OOPS Interview Tricks Q&A (demo) ===");

        System.out.println("\n--- 1) super vs this demo ---");
        Child child = new Child("Asha");
        System.out.println(child.who());
        System.out.println(child.parentGreetingViaSuper());
        System.out.println(child.selfGreetingViaThis());
        // expected output (order is important):
        // Parent(String): called from Child(String)
        // Child(String): name=Asha
        // Child
        // hello from parent
        // hello from child name=Asha

        System.out.println("\n--- 2) constructor chaining demo ---");
        Child defaultChild = new Child();
        System.out.println(defaultChild.who());
        // expected output includes:
        // Parent(String): called from Child(String)
        // Child(String): name=default
        // Child

        System.out.println("\n--- 3) overriding rules demo ---");
        Service s1 = new Service();
        Service s2 = new FastService();

        System.out.println(s2.compute(5));
        // output: 10  (FastService overrides)

        try {
            System.out.println(s1.compute(-1));
        } catch (java.io.IOException ex) {
            System.out.println("Service threw checked exception");
            // output: Service threw checked exception
        }

        try {
            System.out.println(s2.compute(-1));
        } catch (IllegalArgumentException ex) {
            System.out.println("FastService threw unchecked exception");
            // output: FastService threw unchecked exception
        }

        System.out.println("\n--- 3b) static/final/private gotchas demo ---");
        StaticTrap ref = new StaticTrapChild();
        System.out.println(StaticTrap.hello());
        System.out.println(StaticTrapChild.hello());
        // output:
        // parent-static
        // child-static

        System.out.println(ref.finalHello());
        // output: parent-final

        System.out.println(ref.callSecret());
        // output: parent-secret

        StaticTrapChild concrete = new StaticTrapChild();
        System.out.println(concrete.callChildSecretDirectly());
        // output: child-secret

        System.out.println("\n--- 4) equals/hashCode + HashMap demo ---");
        Map<EmployeeKey, String> map = new HashMap<>();
        EmployeeKey k = new EmployeeKey("E-1");
        map.put(k, "Alice");

        System.out.println(map.get(new EmployeeKey("E-1")));
        // output: Alice (because equals/hashCode match)

        // Now mutate the key AFTER putting it into the map -> classic bug
        k.setId("E-2");

        System.out.println(map.get(new EmployeeKey("E-1")));
        // output: null (entry exists, but in a different bucket now)

        System.out.println(map.get(new EmployeeKey("E-2")));
        // output: null (also often null; because stored entry is "lost" to lookup)

        System.out.println("map size=" + map.size());
        // output: map size=1 (entry is still there, just not retrievable by normal key lookup)

        System.out.println("\n--- 5) overload vs override dispatch demo ---");
        Printer p = new FancyPrinter();
        Object obj = "hi";

        // Overload is chosen at compile time based on *reference type*:
        System.out.println(p.print(obj));
        // output: Fancy(Object)  (because compile-time signature is print(Object))

        // Here, compile-time chooses print(String), then runtime chooses FancyPrinter implementation.
        System.out.println(p.print("hi"));
        // output: Fancy(String)

        System.out.println("\nDone.");
    }
}
