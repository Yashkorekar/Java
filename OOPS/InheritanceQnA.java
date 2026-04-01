package OOPS;

/**
 * Inheritance (Q&A + very basic runnable demo)
 *
 * Run:
 *   javac .\OOPS\InheritanceQnA.java
 *   java OOPS.InheritanceQnA
 */
public class InheritanceQnA {

    /*
     * =============================
     * 1) What is inheritance?
     * =============================
     * Inheritance means one class can reuse fields and methods of another class.
     *
     * Parent class = superclass / base class
     * Child class = subclass / derived class
     *
     * Beginner one-liner:
     * - "Inheritance models an IS-A relationship."
     */

    static class Animal {
        protected final String name;

        Animal(String name) {
            this.name = name;
        }

        public void eat() {
            System.out.println(name + " is eating.");
        }

        public String identify() {
            return name + " is an animal.";
        }
    }

    static final class Dog extends Animal {

        Dog(String name) {
            super(name);
        }

        public void bark() {
            System.out.println(name + " says: Woof!");
        }

        @Override
        public String identify() {
            return name + " is a dog, and a dog is an animal.";
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Inheritance ===");

        System.out.println("1) Dog extends Animal, so Dog gets Animal behavior.");
        Dog dog = new Dog("Bruno");

        System.out.println("\n2) Calling a method inherited from the parent class:");
        dog.eat();

        System.out.println("\n3) Calling a method defined in the child class:");
        dog.bark();

        System.out.println("\n4) Calling an overridden method:");
        System.out.println(dog.identify());

        System.out.println("\nWhat this shows:");
        System.out.println("- Dog IS-A Animal");
        System.out.println("- Dog reuses Animal code instead of writing everything again");
        System.out.println("- Dog can also add its own special behavior");
    }
}