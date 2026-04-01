package OOPS;

/**
 * Polymorphism (Q&A + very basic runnable demo)
 *
 * Run:
 *   javac .\OOPS\PolymorphismQnA.java
 *   java OOPS.PolymorphismQnA
 */
public class PolymorphismQnA {

    /*
     * =============================
     * 1) What is polymorphism?
     * =============================
     * Polymorphism means "one reference, many forms".
     *
     * In simple words:
     * - the same parent type can point to different child objects
     * - the same method call can behave differently depending on the actual object
     *
     * Most interview questions here focus on runtime polymorphism via overriding.
     */

    static class Animal {
        public void makeSound() {
            System.out.println("Some generic animal sound");
        }
    }

    static final class Dog extends Animal {
        @Override
        public void makeSound() {
            System.out.println("Dog says: Woof!");
        }
    }

    static final class Cat extends Animal {
        @Override
        public void makeSound() {
            System.out.println("Cat says: Meow!");
        }
    }

    static final class Printer {
        public void print(String value) {
            System.out.println("Printing text: " + value);
        }

        public void print(int value) {
            System.out.println("Printing number: " + value);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Polymorphism ===");

        System.out.println("1) Runtime polymorphism using method overriding:");
        Animal firstAnimal = new Dog();
        Animal secondAnimal = new Cat();

        firstAnimal.makeSound();
        secondAnimal.makeSound();

        System.out.println("\n2) Same reference type: Animal");
        System.out.println("   Different actual objects: Dog and Cat");
        System.out.println("   Same method call: makeSound()");
        System.out.println("   Different runtime behavior based on the real object");

        System.out.println("\n3) Compile-time polymorphism using overloading:");
        Printer printer = new Printer();
        printer.print("hello");
        printer.print(123);

        System.out.println("\nInterview summary:");
        System.out.println("- overriding gives runtime polymorphism");
        System.out.println("- overloading is also called compile-time polymorphism");
        System.out.println("- parent references can hold child objects");
    }
}