package OOPS;

/**
 * Constructor Overloading + Constructor Chaining (Q&A + runnable demos)
 *
 * You can run:
 *   javac .\OOPS\ConstructorOverloadingAndChainingQnA.java
 *   java OOPS.ConstructorOverloadingAndChainingQnA
 */
public class ConstructorOverloadingAndChainingQnA {

    /*
     * =============================
     * 1) What is constructor overloading?
     * =============================
     * Q: What does "constructor overloading" mean?
     * A: Having multiple constructors in the same class with different parameter lists.
     *    - Same constructor name (class name), different signature (params count/types/order).
     *
     * Why use it?
     * - Provide multiple ways to create a valid object.
     * - Keep invariants by centralizing validation in one "primary" constructor.
     *
     * Best practice:
     * - Chain smaller constructors to a primary one using this(...)
     *   to avoid duplicated validation logic.
     */

    static final class Person {
        private final String name;
        private final int age;

        // Overload 1: minimal info
        Person(String name) {
            this(name, 0); // constructor chaining within same class
        }

        // Overload 2: full info (treat as primary constructor)
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

        @Override
        public String toString() {
            return "Person{name='" + name + "', age=" + age + "}";
        }
    }

    /*
     * =============================
     * 2) What is constructor chaining?
     * =============================
     * Q: What is constructor chaining?
     * A: Calling one constructor from another.
     *    Two kinds:
     *    - this(...) : call another constructor in the SAME class
     *    - super(...): call a constructor of the PARENT class
     *
     * Critical rule (very common interview question):
     * - this(...) or super(...) MUST be the FIRST statement in a constructor.
     * - You cannot call both this(...) and super(...) explicitly.
     *   (If you call this(...), that called constructor will eventually call super(...)).
     */

    /*
     * Example (this(...) + super(...) together):
     * - When you create new ChainChild(), it calls ChainChild() -> this("C-DEFAULT")
     * - That constructor calls super("P-1")
     * So you still end up calling a parent constructor, just indirectly.
     */

    static class ChainParent {
        ChainParent() {
            System.out.println("ChainParent(): ctor");
        }

        ChainParent(String parentId) {
            System.out.println("ChainParent(String): ctor parentId=" + parentId);
        }
    }

    static final class ChainChild extends ChainParent {
        ChainChild() {
            this("C-DEFAULT");
        }

        ChainChild(String childId) {
            super("P-1");
            System.out.println("ChainChild(String): ctor childId=" + childId);
        }

        // Won't compile (rule demo):
        // ChainChild(int x) {
        //     super("P-1");
        //     this("C-DEFAULT");
        // }
        // ERROR: call to this must be first statement in constructor
    }

    /*
     * =============================
     * 3) Implicit super() and default constructors
     * =============================
     * Q: What happens if you don't call super(...)?
     * A: The compiler inserts an implicit super() as the first statement.
     *
     * Q: When do you get a default constructor?
     * A: Only if you define NO constructors at all.
     *    - If you define any constructor, Java does NOT generate a no-arg constructor.
     */

    static class ParentNoArg {
        ParentNoArg() {
            System.out.println("ParentNoArg: ctor");
        }
    }

    static final class ChildImplicitSuper extends ParentNoArg {
        ChildImplicitSuper() {
            // implicit super() happens here
            System.out.println("ChildImplicitSuper: ctor");
        }
    }

    /*
     * =============================
     * 4) Parent has NO no-arg constructor (classic compile-time trap)
     * =============================
     * Q: What if parent defines ONLY parameterized constructors?
     * A: Then child MUST explicitly call super(args).
     *    Otherwise compilation fails because implicit super() doesn't exist.
     */

    static class ParentOnlyArgs {
        ParentOnlyArgs(String id) {
            System.out.println("ParentOnlyArgs: ctor id=" + id);
        }
    }

    static final class ChildMustCallSuper extends ParentOnlyArgs {
        ChildMustCallSuper(String id) {
            super(id);
            System.out.println("ChildMustCallSuper: ctor");
        }
    }

    /*
     * =============================
     * 5) Initialization order (fields + init blocks + constructors)
     * =============================
     * High-frequency interview question:
     * When creating a Child object, order is roughly:
     * 1) Parent static init (once per class load)
     * 2) Child static init  (once per class load)
     * 3) Parent instance field init + instance init blocks
     * 4) Parent constructor
     * 5) Child instance field init + instance init blocks
     * 6) Child constructor
     */

    static class InitParent {
        static {
            System.out.println("InitParent: static");
        }

        private final String pField = printAndReturn("InitParent: field init");

        {
            System.out.println("InitParent: instance init block");
        }

        InitParent() {
            System.out.println("InitParent: ctor");
        }

        @SuppressWarnings("unused")
        String getpField() {
            return pField;
        }
    }

    static final class InitChild extends InitParent {
        static {
            System.out.println("InitChild: static");
        }

        private final String cField = printAndReturn("InitChild: field init");

        {
            System.out.println("InitChild: instance init block");
        }

        InitChild() {
            System.out.println("InitChild: ctor");
        }

        @SuppressWarnings("unused")
        String getcField() {
            return cField;
        }
    }

    private static String printAndReturn(String s) {
        System.out.println(s);
        return s;
    }

    /*
     * =============================
     * 6) Overload resolution tricky cases
     * =============================
     * Interview traps:
     * - null is ambiguous if there are multiple reference-type overloads.
     * - primitive widening beats boxing in many cases.
     * - varargs is the "least preferred" and can cause ambiguity.
     */

    static final class OverloadTricks {
        OverloadTricks(Object o) {
            System.out.println("OverloadTricks(Object)");
        }

        OverloadTricks(String s) {
            System.out.println("OverloadTricks(String)");
        }

        OverloadTricks(int x) {
            System.out.println("OverloadTricks(int)");
        }

        OverloadTricks(long x) {
            System.out.println("OverloadTricks(long)");
        }

        OverloadTricks(Integer x) {
            System.out.println("OverloadTricks(Integer)");
        }

        OverloadTricks(int... xs) {
            System.out.println("OverloadTricks(int...)");
        }

        // NOTE (edge case):
        // new OverloadTricks(null); // COMPILER ERROR: reference overload ambiguity (Object vs String)
    }

    /*
     * =============================
     * 7) this(...) / super(...) MUST be first statement
     * =============================
     * This is a compile-time rule.
     * Example (won't compile, so only in comment):
     *
     *   Child() {
     *       System.out.println("hi");
     *       this(1); // ERROR: call to this must be first statement
     *   }
     */

    /*
     * =============================
     * 8) Circular constructor chaining (compile-time error)
     * =============================
     * Another classic trap:
     *   A() { this(1); }
     *   A(int x) { this(); }
     * This creates a cycle and does not compile.
     */

    /*
     * =============================
     * 9) Exceptions in constructors + chaining
     * =============================
     * Q: Can constructors throw exceptions?
     * A: Yes.
     *    - Often used for validation (fail-fast).
     *    - If a constructor throws, the object is NOT successfully created.
     *
     * Interview edge case:
     * - If super(...) throws, your subclass object construction fails.
     */

    static class ParentMayThrow {
        ParentMayThrow(String token) {
            if (token == null || token.isBlank()) {
                throw new IllegalArgumentException("token must not be blank");
            }
            System.out.println("ParentMayThrow: ctor ok");
        }
    }

    static final class ChildCallsThrowingParent extends ParentMayThrow {
        ChildCallsThrowingParent(String token) {
            super(token);
            System.out.println("ChildCallsThrowingParent: ctor");
        }
    }

    /*
     * =============================
     * 10) "Don't call overridable methods from constructors" (senior-level trap)
     * =============================
     * Why it's risky:
     * - During parent construction, child fields are not initialized yet.
     * - If parent constructor calls an overridable method, the child override runs early.
     * - That override might read child fields before initialization -> surprising values / NPE.
     */

    static class ParentCallsOverride {
        ParentCallsOverride() {
            System.out.println("ParentCallsOverride: ctor start");
            System.out.println("ParentCallsOverride: valueFromOverride=" + value());
            System.out.println("ParentCallsOverride: ctor end");
        }

        String value() {
            return "parent";
        }
    }

    static final class ChildOverrideHazard extends ParentCallsOverride {
        private final String childField = initChildField();

        private static String initChildField() {
            System.out.println("ChildOverrideHazard: field init");
            return "child-ready";
        }

        @Override
        String value() {
            // childField is NOT initialized yet when ParentCallsOverride ctor runs.
            return childField;
        }

        ChildOverrideHazard() {
            System.out.println("ChildOverrideHazard: ctor");
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Constructor Overloading + Chaining QnA ===");

        System.out.println("\n--- 1) Overloading + chaining via this(...) ---");
        Person p1 = new Person("Ravi");
        Person p2 = new Person("Asha", 28);
        System.out.println(p1);
        System.out.println(p2);
        // output:
        // Person{name='Ravi', age=0}
        // Person{name='Asha', age=28}

        System.out.println("\n--- 2) Constructor chaining: this(...) then super(...) ---");
        new ChainChild();
        // output:
        // ChainParent(String): ctor parentId=P-1
        // ChainChild(String): ctor childId=C-DEFAULT

        new ChainChild("C-99");
        // output:
        // ChainParent(String): ctor parentId=P-1
        // ChainChild(String): ctor childId=C-99

        System.out.println("\n--- 3) Implicit super() demo ---");
        new ChildImplicitSuper();
        // output:
        // ParentNoArg: ctor
        // ChildImplicitSuper: ctor

        System.out.println("\n--- 4) Parent without no-arg ctor (explicit super required) ---");
        new ChildMustCallSuper("P-1");
        // output:
        // ParentOnlyArgs: ctor id=P-1
        // ChildMustCallSuper: ctor

        System.out.println("\n--- 5) Initialization order demo ---");
        new InitChild();
        // output (exact order):
        // InitParent: static
        // InitChild: static
        // InitParent: field init
        // InitParent: instance init block
        // InitParent: ctor
        // InitChild: field init
        // InitChild: instance init block
        // InitChild: ctor

        System.out.println("\n--- 6) Overload resolution tricks ---");
        new OverloadTricks("x");
        // output: OverloadTricks(String)

        new OverloadTricks((Object) "x");
        // output: OverloadTricks(Object)

        new OverloadTricks(1);
        // output: OverloadTricks(int)

        new OverloadTricks(1L);
        // output: OverloadTricks(long)

        new OverloadTricks(Integer.valueOf(1));
        // output: OverloadTricks(Integer)

        new OverloadTricks();
        // output: OverloadTricks(int...)

        new OverloadTricks(1, 2, 3);
        // output: OverloadTricks(int...)

        System.out.println("\n--- 9) Exceptions during chaining ---");
        try {
            new ChildCallsThrowingParent(" ");
        } catch (IllegalArgumentException ex) {
            System.out.println("construction failed: " + ex.getMessage());
        }
        // output: construction failed: token must not be blank

        new ChildCallsThrowingParent("ok");
        // output:
        // ParentMayThrow: ctor ok
        // ChildCallsThrowingParent: ctor

        System.out.println("\n--- 10) Overridable method called from ctor hazard ---");
        new ChildOverrideHazard();
        // output (notice null during parent ctor):
        // ParentCallsOverride: ctor start
        // ParentCallsOverride: valueFromOverride=null
        // ParentCallsOverride: ctor end
        // ChildOverrideHazard: field init
        // ChildOverrideHazard: ctor
    }
}
