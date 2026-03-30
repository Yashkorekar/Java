package Basics;

public class MethodsAndPassByValueDemo {

    /*
     * Theory:
     * - A method signature is primarily its name plus parameter types.
     * - Overloading is compile-time selection based on the argument list.
     * - Java always passes arguments by value, even when the value is an object reference.
     * - Varargs are compiled as arrays and must appear last in the parameter list.
     * - Small, explicit method names beat clever overload combinations in real code.
     */

    public static void main(String[] args) {
        System.out.println("=== Methods: signatures, overloading, pass-by-value ===");

        methodShapes();
        overloadingDemo();
        passByValueWithPrimitive();
        passByValueWithObjectReference();
        swapTrapDemo();
        varargsDemo();
        interviewTakeaways();
        interviewTrapQuestions();
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

    private static void swapTrapDemo() {
        System.out.println("\n--- Swap trap ---");

        int left = 10;
        int right = 20;
        System.out.println("before swap(left, right) => left=" + left + ", right=" + right);
        swap(left, right);
        System.out.println("after swap(left, right) => left=" + left + ", right=" + right);
    }

    private static void varargsDemo() {
        System.out.println("\n--- Varargs ---");

        System.out.println("sumAll() = " + sumAll());
        System.out.println("sumAll(1, 2, 3, 4) = " + sumAll(1, 2, 3, 4));
        int[] values = { 5, 6, 7 };
        System.out.println("sumAll(array) = " + sumAll(values));
        System.out.println("Varargs are treated like an array inside the method.");
    }

    private static void interviewTakeaways() {
        System.out.println("\n--- Interview takeaways ---");
        System.out.println("- Java is always pass-by-value.");
        System.out.println("- For objects, the copied value is the reference, not the object itself.");
        System.out.println("- Overloading is compile-time polymorphism.");
        System.out.println("- Prefer clear method names and small method responsibilities over clever signatures.");
    }

    private static void interviewTrapQuestions() {
        System.out.println("\n--- Trap questions interviewers ask ---");
        System.out.println("Q: Does Java pass objects by reference?");
        System.out.println("A: No. Java passes the reference value by value.");
        System.out.println("Q: Why does a swap method not swap caller variables?");
        System.out.println("A: The method only swaps its local copies of the argument values.");
        System.out.println("Q: Can you overload a method by changing only the return type?");
        System.out.println("A: No. Return type alone is not enough for overloading.");
        System.out.println("Q: Is varargs something special at runtime?");
        System.out.println("A: Inside the method, it is just an array.");
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

    static void swap(int left, int right) {
        int temp = left;
        left = right;
        right = temp;
        System.out.println("inside swap => left=" + left + ", right=" + right);
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