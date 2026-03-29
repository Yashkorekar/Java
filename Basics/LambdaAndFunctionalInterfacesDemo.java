package Basics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class LambdaAndFunctionalInterfacesDemo {

    public static void main(String[] args) {
        System.out.println("=== Lambdas and functional interfaces ===");

        lambdaSyntaxDemo();
        builtInFunctionalInterfacesDemo();
        methodReferenceDemo();
        effectivelyFinalDemo();
        comparatorWithLambdaDemo();
        interviewTakeaways();
    }

    private static void lambdaSyntaxDemo() {
        System.out.println("\n--- Lambda syntax basics ---");

        GreetingFormatter formatter = name -> "Hello, " + name;
        GreetingFormatter loudFormatter = name -> ("Hello, " + name).toUpperCase();

        System.out.println(formatter.format("Asha"));
        System.out.println(loudFormatter.format("Ravi"));
    }

    private static void builtInFunctionalInterfacesDemo() {
        System.out.println("\n--- Built-in functional interfaces ---");

        Predicate<Integer> isEven = value -> value % 2 == 0;
        Function<String, Integer> length = text -> text.length();
        Consumer<String> printer = text -> System.out.println("consume => " + text);
        Supplier<String> source = () -> "generated-value";

        System.out.println("isEven(8) => " + isEven.test(8));
        System.out.println("length('lambda') => " + length.apply("lambda"));
        printer.accept(source.get());
    }

    private static void methodReferenceDemo() {
        System.out.println("\n--- Method references ---");

        Function<String, String> trim = String::trim;
        Function<String, Integer> parseInt = Integer::parseInt;

        System.out.println("trim => '" + trim.apply("  java  ") + "'");
        System.out.println("parseInt => " + parseInt.apply("42"));
    }

    private static void effectivelyFinalDemo() {
        System.out.println("\n--- Effectively final capture ---");

        String prefix = "item=";
        List<String> values = List.of("A", "B", "C");
        values.forEach(value -> System.out.println(prefix + value));

        System.out.println("Captured local variables in lambdas must be final or effectively final.");
    }

    private static void comparatorWithLambdaDemo() {
        System.out.println("\n--- Comparator with lambda ---");

        List<String> names = new ArrayList<>(List.of("Ravi", "Asha", "Meera", "Dev"));
        names.sort(Comparator.comparingInt(String::length).thenComparing(name -> name));
        System.out.println(names);
    }

    private static void interviewTakeaways() {
        System.out.println("\n--- Interview takeaways ---");
        System.out.println("- A lambda needs a target functional interface with exactly one abstract method.");
        System.out.println("- Lambdas are not the same thing as anonymous inner classes in scope behavior.");
        System.out.println("- Method references are syntax sugar when an existing method already matches the target shape.");
    }

    @FunctionalInterface
    interface GreetingFormatter {
        String format(String name);
    }
}