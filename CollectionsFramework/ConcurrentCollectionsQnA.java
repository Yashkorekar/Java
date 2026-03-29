package CollectionsFramework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Collections Framework - Concurrent collections (Q&A + runnable demos)
 *
 * Run:
 *   javac .\CollectionsFramework\ConcurrentCollectionsQnA.java
 *   java CollectionsFramework.ConcurrentCollectionsQnA
 */
public class ConcurrentCollectionsQnA {

    /*
     * =============================
     * 1) Synchronized wrapper vs concurrent collection
     * =============================
     * - Collections.synchronizedList(...) wraps a normal list with synchronized methods.
     * - ConcurrentHashMap / CopyOnWriteArrayList are designed with concurrent access patterns in mind.
     */

    static void synchronizedListDemo() {
        System.out.println("=== synchronizedList wrapper ===");

        List<Integer> list = Collections.synchronizedList(new ArrayList<>(Arrays.asList(1, 2, 3)));
        list.add(4);
        System.out.println(list);

        synchronized (list) {
            for (Integer value : list) {
                System.out.print(value + " ");
            }
        }
        System.out.println();

        System.out.println("Iteration over a synchronized wrapper still needs external synchronization.");
    }

    static void concurrentHashMapDemo() {
        System.out.println("\n=== ConcurrentHashMap demo ===");

        Map<String, Integer> map = new ConcurrentHashMap<>();
        map.put("A", 1);
        map.putIfAbsent("A", 99);
        map.compute("B", (key, value) -> value == null ? 1 : value + 1);
        map.compute("B", (key, value) -> value == null ? 1 : value + 1);

        System.out.println("after updates => " + map);

        map.forEach((key, value) -> {
            if ("A".equals(key)) {
                map.put("C", 3);
            }
        });
        System.out.println("after weakly consistent iteration => " + map);
    }

    static void copyOnWriteArrayListDemo() {
        System.out.println("\n=== CopyOnWriteArrayList demo ===");

        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>(Arrays.asList("A", "B", "C"));
        for (String value : list) {
            if ("B".equals(value)) {
                list.add("D");
            }
        }

        System.out.println(list);
        System.out.println("CopyOnWriteArrayList iterates over a snapshot: great for read-heavy, expensive for frequent writes.");
    }

    public static void main(String[] args) {
        synchronizedListDemo();
        concurrentHashMapDemo();
        copyOnWriteArrayListDemo();

        System.out.println("\nInterview notes:");
        System.out.println("- ConcurrentHashMap does not allow null keys or null values.");
        System.out.println("- Its iterators are weakly consistent, not fail-fast like HashMap/ArrayList.");
        System.out.println("- Concurrent collections reduce contention in common patterns, but compound workflows may still need higher-level coordination.");
    }
}