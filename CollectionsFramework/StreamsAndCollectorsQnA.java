package CollectionsFramework;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Collections Framework - Streams and collectors (Q&A + runnable demos)
 *
 * Run:
 *   javac .\CollectionsFramework\StreamsAndCollectorsQnA.java
 *   java CollectionsFramework.StreamsAndCollectorsQnA
 */
public class StreamsAndCollectorsQnA {

    static void filterMapCollectDemo() {
        System.out.println("=== filter + map + collect ===");

        List<String> names = List.of("Asha", "Ravi", "Meera", "Dev");
        List<String> result = names.stream()
                .filter(name -> name.length() >= 5)
                .map(String::toUpperCase)
                .toList();

        System.out.println(result);
        System.out.println("Streams do not modify the source collection.");
    }

    static void lazyEvaluationDemo() {
        System.out.println("\n=== Lazy evaluation ===");

        Stream<String> pipeline = List.of("a", "bb", "ccc").stream()
                .filter(value -> {
                    System.out.println("filter " + value);
                    return value.length() >= 2;
                })
                .map(value -> {
                    System.out.println("map " + value);
                    return value.toUpperCase();
                });

        System.out.println("pipeline created");
        System.out.println("terminal result => " + pipeline.findFirst().orElse("none"));
    }

    static void collectorDemo() {
        System.out.println("\n=== Collectors demo ===");

        List<String> words = List.of("java", "jvm", "gc", "jit", "stream");

        Map<Integer, List<String>> grouped = words.stream()
                .collect(Collectors.groupingBy(String::length));
        String joined = words.stream().collect(Collectors.joining(", "));

        System.out.println("grouped => " + grouped);
        System.out.println("joined => " + joined);
    }

    static void optionalAndPrimitiveStreamDemo() {
        System.out.println("\n=== Optional + primitive stream ===");

        Optional<String> firstLong = List.of("a", "bb", "ccc").stream()
                .filter(value -> value.length() >= 3)
                .findFirst();
        int sum = IntStream.of(1, 2, 3, 4).sum();

        System.out.println("firstLong => " + firstLong.orElse("missing"));
        System.out.println("sum => " + sum);
        System.out.println("Primitive streams help avoid boxing overhead in numeric pipelines.");
    }

    static void reuseTrapDemo() {
        System.out.println("\n=== Stream reuse trap ===");

        Stream<Integer> stream = List.of(1, 2, 3).stream();
        System.out.println("count => " + stream.count());

        try {
            System.out.println("reuse => " + stream.findFirst().orElse(-1));
        } catch (IllegalStateException ex) {
            System.out.println("caught => " + ex.getClass().getSimpleName());
        }
    }

    public static void main(String[] args) {
        filterMapCollectDemo();
        lazyEvaluationDemo();
        collectorDemo();
        optionalAndPrimitiveStreamDemo();
        reuseTrapDemo();

        System.out.println("\nInterview notes:");
        System.out.println("- Intermediate operations are lazy; terminal operations trigger execution.");
        System.out.println("- Streams are single-use pipelines, not reusable containers.");
        System.out.println("- Keep side effects out of stream pipelines unless you have a very good reason.");
    }
}