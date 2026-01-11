package OOPS;

/**
 * this keyword usage (Q&A + runnable demos)
 *
 * Run:
 *   javac .\OOPS\ThisKeywordUsageQnA.java
 *   java OOPS.ThisKeywordUsageQnA
 */
public class ThisKeywordUsageQnA {

    /*
     * =============================
     * 1) What is `this`?
     * =============================
     * `this` is a reference to the CURRENT object (the current instance).
     *
     * Interview one-liner:
     * - `this` refers to the object whose method/constructor is currently executing.
     */

    /*
     * =============================
     * 2) Disambiguate fields vs parameters (shadowing)
     * =============================
     * When a parameter/local variable has the same name as a field, the field is "shadowed".
     * Use `this.field` to refer to the instance field.
     */

    static final class ShadowDemo {
        private String name;

        ShadowDemo(String name) {
            // `name` on RHS is parameter; `this.name` is field
            this.name = name;
        }

        void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "ShadowDemo{name='" + name + "'}";
        }
    }

    /*
     * =============================
     * 3) Call another constructor in same class: this(...)
     * =============================
     * this(...) is constructor chaining (within same class).
     * Critical rules (very common interviewer questions):
     * - this(...) MUST be the FIRST statement in a constructor.
     * - You cannot call both this(...) and super(...).
     * - You cannot reference `this` (or instance fields/methods) in the arguments to this(...)
     *   because the object is not fully constructed yet.
     */

    static final class CtorChain {
        private final String id;
        private final int level;

        CtorChain(String id) {
            this(id, 0);
        }

        CtorChain(String id, int level) {
            if (id == null || id.isBlank()) {
                throw new IllegalArgumentException("id must not be blank");
            }
            if (level < 0) {
                throw new IllegalArgumentException("level must be >= 0");
            }
            this.id = id;
            this.level = level;
        }

        @Override
        public String toString() {
            return "CtorChain{id='" + id + "', level=" + level + "}";
        }

        // Won't compile (examples only):
        // CtorChain() {
        //     System.out.println("hi");
        //     this("X");
        // }
        // ERROR: call to this must be first statement in constructor
        //
        // CtorChain() {
        //     this(this.id); // ERROR: cannot reference this before supertype constructor has been called
        // }
    }

    /*
     * =============================
     * 4) Pass current object as argument
     * =============================
     * You can pass `this` to another method/object to let it operate on the current instance.
     * Common use cases:
     * - callbacks/listeners
     * - registration in a registry
     * - helper methods that operate on the current instance
     */

    static final class Registry {
        void register(Worker w) {
            System.out.println("registered: " + w.name);
        }
    }

    static final class Worker {
        private final String name;
        private final Registry registry;

        Worker(String name, Registry registry) {
            this.name = name;
            this.registry = registry;
        }

        void start() {
            registry.register(this);
        }
    }

    /*
     * =============================
     * 5) Return `this` for fluent APIs / method chaining
     * =============================
     * Many builders return `this` so calls can be chained:
     *   builder.setA(...).setB(...).build();
     */

    static final class PersonBuilder {
        private String name;
        private int age;

        PersonBuilder setName(String name) {
            this.name = name;
            return this;
        }

        PersonBuilder setAge(int age) {
            this.age = age;
            return this;
        }

        BuiltPerson build() {
            return new BuiltPerson(name, age);
        }
    }

    static final class BuiltPerson {
        private final String name;
        private final int age;

        BuiltPerson(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return "BuiltPerson{name='" + name + "', age=" + age + "}";
        }
    }

    /*
     * =============================
     * 6) OuterClass.this (inner class disambiguation)
     * =============================
     * In a non-static inner class, `this` refers to the INNER instance.
     * To reference the outer instance explicitly, use OuterClass.this
     */

    static final class Outer {
        private final String name;

        Outer(String name) {
            this.name = name;
        }

        final class Inner {
            private final String name;

            Inner(String name) {
                this.name = name;
            }

            void printBoth() {
                System.out.println("inner name=" + this.name);
                System.out.println("outer name=" + Outer.this.name);
            }
        }
    }

    /*
     * =============================
     * 7) `this` in static context (NOT allowed)
     * =============================
     * `this` exists only for instances.
     * - You cannot use `this` inside a static method or static initializer.
     *
     * Example (won't compile):
     *   static void x() { System.out.println(this); }
     */

    /*
     * =============================
     * 8) `this` in lambda vs anonymous class (classic tricky question)
     * =============================
     * Inside a lambda:
     * - `this` refers to the enclosing instance (same as outside the lambda).
     *
     * Inside an anonymous class:
     * - `this` refers to the anonymous class instance.
     */

    static final class LambdaVsAnonymous {
        private final String tag;

        LambdaVsAnonymous(String tag) {
            this.tag = tag;
        }

        Runnable lambda() {
            return () -> System.out.println("lambda this.tag=" + this.tag);
        }

        Runnable anonymous() {
            return new Runnable() {
                @Override
                public void run() {
                    // Here, `this` is the anonymous Runnable instance, not LambdaVsAnonymous.
                    System.out.println("anonymous this.class=" + this.getClass().getName());
                    System.out.println("enclosing tag=" + LambdaVsAnonymous.this.tag);
                }
            };
        }
    }

    public static void main(String[] args) {
        System.out.println("=== this keyword usage QnA ===");

        System.out.println("\n--- 2) Field shadowing ---");
        ShadowDemo s = new ShadowDemo("Ravi");
        System.out.println(s);
        // output: ShadowDemo{name='Ravi'}
        s.setName("Asha");
        System.out.println(s);
        // output: ShadowDemo{name='Asha'}

        System.out.println("\n--- 3) this(...) constructor chaining ---");
        System.out.println(new CtorChain("ID-1"));
        System.out.println(new CtorChain("ID-2", 5));
        // output:
        // CtorChain{id='ID-1', level=0}
        // CtorChain{id='ID-2', level=5}

        System.out.println("\n--- 4) Passing `this` as argument ---");
        Registry reg = new Registry();
        Worker w = new Worker("worker-1", reg);
        w.start();
        // output: registered: worker-1

        System.out.println("\n--- 5) Returning `this` (fluent API) ---");
        BuiltPerson bp = new PersonBuilder().setName("Meera").setAge(30).build();
        System.out.println(bp);
        // output: BuiltPerson{name='Meera', age=30}

        System.out.println("\n--- 6) Outer.this in inner class ---");
        Outer outer = new Outer("OUTER");
        Outer.Inner inner = outer.new Inner("INNER");
        inner.printBoth();
        // output:
        // inner name=INNER
        // outer name=OUTER

        System.out.println("\n--- 8) lambda vs anonymous: what does `this` mean? ---");
        LambdaVsAnonymous demo = new LambdaVsAnonymous("TAG-1");
        demo.lambda().run();
        // output: lambda this.tag=TAG-1

        demo.anonymous().run();
        // output:
        // anonymous this.class=OOPS.ThisKeywordUsageQnA$LambdaVsAnonymous$1
        // enclosing tag=TAG-1
    }
}
