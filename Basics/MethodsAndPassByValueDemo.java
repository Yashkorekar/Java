package Basics;

public class MethodsAndPassByValueDemo {

    public static void main(String[] args) {
        System.out.println("=== Methods: signatures, overloading, pass-by-value ===");

        methodShapes();
        overloadingDemo();
        passByValueWithPrimitive();
        passByValueWithObjectReference();
        varargsDemo();
        interviewTakeaways();
    }

    private static void methodShapes() {
        System.out.println("\n--- Method shapes ---");

        greet("Asha");
        System.out.println("add(10, 20) = " + add(10, 20));
        System.out.println("isEven(8) = " + isEven(8));
    }

    private static void overloadingDemo() {
        System.out.println("\n--- Overloading ---");

        System.out.println("add(2, 3) = " + add(2, 3));
        System.out.println("add(2.5, 3.5) = " + add(2.5, 3.5));
        System.out.println("describe(7) = " + describe(7));
        System.out.println("describe(\"Java\") = " + describe("Java"));

        System.out.println("Overloading is resolved at compile time based on parameter types.");
    }

    private static void passByValueWithPrimitive() {
        System.out.println("\n--- Pass-by-value with primitive ---");

        int score = 10;
        System.out.println("before increment(score) => score = " + score);
        increment(score);
        System.out.println("after increment(score) => score = " + score);
    }

    private static void passByValueWithObjectReference() {
        System.out.println("\n--- Pass-by-value with object reference ---");

        NameBox box = new NameBox("Original");
        System.out.println("before renameBox => " + box);
        renameBox(box, "Renamed");
        System.out.println("after renameBox => " + box);

        reassignBox(box);
        System.out.println("after reassignBox => " + box);

        System.out.println("Java is still pass-by-value here: the copied value is the object reference.");
    }

    private static void varargsDemo() {
        System.out.println("\n--- Varargs ---");

        System.out.println("sumAll() = " + sumAll());
        System.out.println("sumAll(1, 2, 3, 4) = " + sumAll(1, 2, 3, 4));
        System.out.println("Varargs are treated like an array inside the method.");
    }

    private static void interviewTakeaways() {
        System.out.println("\n--- Interview takeaways ---");
        System.out.println("- Java is always pass-by-value.");
        System.out.println("- For objects, the copied value is the reference, not the object itself.");
        System.out.println("- Overloading is compile-time polymorphism.");
        System.out.println("- Prefer clear method names and small method responsibilities over clever signatures.");
    }

    static void greet(String name) {
        System.out.println("Hello, " + name);
    }

    static int add(int left, int right) {
        return left + right;
    }

    static double add(double left, double right) {
        return left + right;
    }

    static boolean isEven(int value) {
        return value % 2 == 0;
    }

    static String describe(int value) {
        return "int=" + value;
    }

    static String describe(String value) {
        return "text=" + value;
    }

    static void increment(int value) {
        value++;
        System.out.println("inside increment => value = " + value);
    }

    static void renameBox(NameBox box, String newValue) {
        box.value = newValue;
        System.out.println("inside renameBox => " + box);
    }

    static void reassignBox(NameBox box) {
        box = new NameBox("Temporary");
        System.out.println("inside reassignBox => " + box);
    }

    static int sumAll(int... numbers) {
        int sum = 0;
        for (int number : numbers) {
            sum += number;
        }
        return sum;
    }

    static final class NameBox {
        private String value;

        NameBox(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "NameBox{value='" + value + "'}";
        }
    }
}