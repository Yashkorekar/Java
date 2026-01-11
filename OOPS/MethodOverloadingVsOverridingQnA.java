package OOPS;

/**
 * Method Overloading vs Overriding (Q&A + runnable demos)
 *
 * Run:
 *   javac .\OOPS\MethodOverloadingVsOverridingQnA.java
 *   java OOPS.MethodOverloadingVsOverridingQnA
 */
public class MethodOverloadingVsOverridingQnA {

    /*
     * =============================
     * 1) Definition: Overloading
     * =============================
     * Overloading = same method name, different parameter list.
     * - Different number of params OR different types OR different order.
     * - Return type alone cannot overload.
     * - Resolved at COMPILE TIME (based on reference type + argument types).
     */

    /*
     * =============================
     * 2) Definition: Overriding
     * =============================
     * Overriding = subclass provides a new implementation of an inherited instance method.
     * - Same name + same parameter types (signature).
     * - Return type can be covariant (subtype of parent's return type).
     * - Resolved at RUNTIME (dynamic dispatch) based on the actual object.
     */

    /*
     * =============================
     * 3) Key mental model (high frequency interview question)
     * =============================
     * Overload selection:
     * - happens at compile time
     * - uses the declared type of the reference + compile-time types of args
     *
     * Override dispatch:
     * - happens at runtime
     * - uses the actual runtime class of the object
     */

    // ---------- Overloading examples + tricky resolution rules ----------

    static final class OverloadResolution {
        void f(int x) {
            System.out.println("f(int)");
        }

        void f(long x) {
            System.out.println("f(long)");
        }

        void f(Integer x) {
            System.out.println("f(Integer)");
        }

        void f(Object x) {
            System.out.println("f(Object)");
        }

        void f(int... xs) {
            System.out.println("f(int...)");
        }

        void g(String s) {
            System.out.println("g(String)");
        }

        void g(Object o) {
            System.out.println("g(Object)");
        }

        void g(CharSequence cs) {
            System.out.println("g(CharSequence)");
        }

        // NOTE: uncommenting this makes `g(null)` ambiguous (String vs CharSequence)
        // void g(StringBuilder sb) { }
    }

    /*
     * Overload resolution preference (simplified but accurate for interviews):
     * 1) Exact match
     * 2) Widening primitive (int -> long)
     * 3) Boxing/unboxing (int <-> Integer)
     * 4) Varargs
     *
     * Ambiguity:
     * - If more than one overload is equally specific, compilation fails.
     * - null can match any reference type, so null calls can be ambiguous.
     */

    // ---------- Overriding rules + edge cases ----------

    static class Parent {
        Number value() {
            return 1; // Number
        }

        public String speak() {
            return "parent";
        }

        protected void checked() throws java.io.IOException {
            // Parent declares checked exception
        }

        static String staticHello() {
            return "parent-static";
        }

        private String secret() {
            return "parent-secret";
        }

        final String finalMethod() {
            return "parent-final";
        }

        String callSecretFromParent() {
            // This calls Parent.secret() because private methods are not overridden.
            return secret();
        }
    }

    static final class Child extends Parent {
        // Covariant return type: Integer is a subtype of Number
        @Override
        Integer value() {
            return 2;
        }

        // Cannot reduce visibility; can increase it.
        @Override
        public String speak() {
            return "child";
        }

        // Overriding + exceptions:
        // - Can throw fewer/narrower checked exceptions.
        // - Can throw any unchecked exception.
        @Override
        protected void checked() {
            // No checked exception thrown here (narrower than IOException)
        }

        // This is NOT overriding. It's hiding (because static).
        static String staticHello() {
            return "child-static";
        }

        // This is NOT overriding Parent.secret() because Parent.secret() is private.
        // It's a new method in Child.
        String secret() {
            return "child-secret";
        }

        // finalMethod() cannot be overridden (compile-time error if attempted)
    }

    // ---------- Tricky: Overload + Override together ----------

    static class Printer {
        void print(Object o) {
            System.out.println("Printer.print(Object)");
        }

        void print(String s) {
            System.out.println("Printer.print(String)");
        }
    }

    static final class FancyPrinter extends Printer {
        @Override
        void print(Object o) {
            System.out.println("FancyPrinter.print(Object)");
        }

        // Not overriding print(String) here on purpose.
        // So calls that resolve to print(String) will still run Printer.print(String).
    }

    /*
     * Interview trap explanation:
     * Printer p = new FancyPrinter();
     * p.print("hi")
     * - Overload selection happens first: compiler picks print(String)
     * - Then runtime override dispatch: which implementation of print(String)?
     *   FancyPrinter does NOT override print(String), so it runs Printer.print(String)
     */

    // ---------- @Override gotcha: signature mismatch ----------

    static class SignatureParent {
        void work(int x) {
            System.out.println("SignatureParent.work(int)");
        }
    }

    static final class SignatureChild extends SignatureParent {
        // This is OVERLOADING (different params), not overriding.
        // If you add @Override here, it will NOT compile (good!)
        void work(Integer x) {
            System.out.println("SignatureChild.work(Integer)");
        }

        @Override
        void work(int x) {
            System.out.println("SignatureChild.work(int)");
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Method Overloading vs Overriding QnA ===");

        System.out.println("\n--- A) Overloading resolution basics ---");
        OverloadResolution o = new OverloadResolution();

        o.f(1);
        // output: f(int) (exact)

        o.f(1L);
        // output: f(long) (exact)

        o.f(Integer.valueOf(1));
        // output: f(Integer) (exact)

        o.f((Object) 1);
        // output: f(Object)

        o.f();
        // output: f(int...) (varargs)

        o.f(1, 2, 3);
        // output: f(int...) (varargs)

        o.g("x");
        // output: g(String) (most specific)

        o.g((CharSequence) "x");
        // output: g(CharSequence)

        o.g((Object) "x");
        // output: g(Object)

        o.g(null);
        // output: g(String)
        // Reason: String is more specific than Object/CharSequence.
        // If there were two equally specific reference overloads, this could be ambiguous.

        System.out.println("\n--- B) Overriding basics (runtime dispatch) ---");
        Parent p = new Child();
        System.out.println(p.speak());
        // output: child

        System.out.println(p.value());
        // output: 2

        // Exception rule demo: Parent.checked() declares IOException, Child.checked() declares none.
        p.checked();
        // output: (no print) but this compiles and runs

        System.out.println("\n--- C) Static methods: hiding, not overriding ---");
        System.out.println(Parent.staticHello());
        System.out.println(Child.staticHello());
        // output:
        // parent-static
        // child-static

        Parent p2 = new Child();
        System.out.println(p2.staticHello());
        // output: parent-static
        // Reason: static call is resolved by REFERENCE TYPE at compile time, not runtime object.

        System.out.println("\n--- D) Private methods are not overridden ---");
        Child c = new Child();
        System.out.println(c.secret());
        // output: child-secret

        System.out.println(c.callSecretFromParent());
        // output: parent-secret
        // Reason: Parent.callSecretFromParent() calls Parent.secret() (private, not polymorphic).

        System.out.println("\n--- E) Overload + override interplay ---");
        Printer pr = new FancyPrinter();
        pr.print("hi");
        // output: Printer.print(String)
        // Reason: compiler chooses print(String); FancyPrinter does not override it.

        pr.print((Object) "hi");
        // output: FancyPrinter.print(Object)
        // Reason: compiler chooses print(Object); overridden at runtime.

        System.out.println("\n--- F) @Override prevents accidental overloading ---");
        SignatureParent sp = new SignatureChild();
        sp.work(10);
        // output: SignatureChild.work(int)

        // The Integer overload is NOT visible through the parent reference:
        // sp.work(Integer.valueOf(10)); // does not compile (method not found in SignatureParent)

        SignatureChild sc = new SignatureChild();
        sc.work(Integer.valueOf(10));
        // output: SignatureChild.work(Integer)
    }
}
