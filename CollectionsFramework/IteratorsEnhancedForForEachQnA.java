package CollectionsFramework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * Collections Framework - Iteration: Iterator, enhanced for-loop, forEach lambda (Q&A + runnable demos)
 *
 * Run:
 *   javac .\CollectionsFramework\IteratorsEnhancedForForEachQnA.java
 *   java CollectionsFramework.IteratorsEnhancedForForEachQnA
 */
public class IteratorsEnhancedForForEachQnA {

    /*
     * =============================
     * 1) Iterator
     * =============================
     * Iterator<T> provides:
     * - hasNext(), next()
     * - remove() (removes the last element returned by next)
     *
     * Key interview rule:
     * - If you want to remove while iterating, use Iterator.remove().
     *   Don't call collection.remove(...) inside for-each.
     */

    static void iteratorRemoveDemo() {
        System.out.println("=== Iterator.remove() demo ===");

        List<String> list = new ArrayList<>(Arrays.asList("keep", "drop", "keep"));
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            String v = it.next();
            if ("drop".equals(v)) {
                it.remove();
            }
        }

        System.out.println(list);
        // output: [keep, keep]
    }

    /*
     * =============================
     * 2) Enhanced for-loop (for-each)
     * =============================
     * for (T x : collection) uses an Iterator under the hood.
     *
     * Interview trap:
     * - Structural modification (add/remove) of the collection while iterating causes
     *   ConcurrentModificationException (fail-fast) in most standard collections.
     */

    static void enhancedForFailFastDemo() {
        System.out.println("\n=== Enhanced for-loop fail-fast demo ===");

        List<String> list = new ArrayList<>(Arrays.asList("A", "B", "C"));
        try {
            for (String s : list) {
                if ("A".equals(s)) {
                    list.add("D"); // structural modification
                }
            }
            System.out.println("no exception (unexpected)");
        } catch (java.util.ConcurrentModificationException ex) {
            System.out.println("caught: ConcurrentModificationException");
        }
        // output: caught: ConcurrentModificationException
    }

    /*
     * =============================
     * 3) ListIterator (Lists only)
     * =============================
     * ListIterator supports:
     * - forward/backward traversal
     * - add(), set(), remove() during iteration
     *
     * Great interview point:
     * - For inserting while iterating through a List, ListIterator is the right tool.
     */

    static void listIteratorDemo() {
        System.out.println("\n=== ListIterator demo ===");

        List<String> list = new ArrayList<>(Arrays.asList("A", "C"));
        ListIterator<String> it = list.listIterator();

        it.next();    // after A
        it.add("B");  // insert at cursor

        System.out.println(list);
        // output: [A, B, C]

        it.next();      // points past C
        it.set("Z");    // replace last returned element (C)
        System.out.println(list);
        // output: [A, B, Z]
    }

    /*
     * =============================
     * 4) forEach lambda (Java 8+)
     * =============================
     * collection.forEach(x -> ...) iterates internally.
     *
     * Interview edge case:
     * - Modifying the collection structurally inside forEach also triggers
     *   ConcurrentModificationException for most collections.
     * - If you need filtering/removal, prefer removeIf(...) or explicit iterator.
     */

    static void forEachLambdaDemo() {
        System.out.println("\n=== forEach lambda demo ===");

        List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3));
        list.forEach(x -> System.out.print(x + " "));
        System.out.println();
        // output: 1 2 3

        try {
            list.forEach(x -> {
                if (x == 2) {
                    list.add(99); // structural modification
                }
            });
            System.out.println("no exception (unexpected)");
        } catch (java.util.ConcurrentModificationException ex) {
            System.out.println("caught: ConcurrentModificationException");
        }
        // output: caught: ConcurrentModificationException
    }

    /*
     * =============================
     * 5) Iterating Maps
     * =============================
     * Interview best practice:
     * - Prefer entrySet() when you need both key and value.
     */

    static void mapIterationDemo() {
        System.out.println("\n=== Map iteration demo ===");

        Map<String, Integer> map = new HashMap<>();
        map.put("A", 1);
        map.put("B", 2);

        for (Map.Entry<String, Integer> e : map.entrySet()) {
            System.out.println(e.getKey() + "->" + e.getValue());
        }
        // output: order not guaranteed, example:
        // A->1
        // B->2

        map.forEach((k, v) -> System.out.println("lambda " + k + "->" + v));
        // output: order not guaranteed
    }

    public static void main(String[] args) {
        iteratorRemoveDemo();
        enhancedForFailFastDemo();
        listIteratorDemo();
        forEachLambdaDemo();
        mapIterationDemo();

        // Interview note:
        // - fail-fast is NOT a concurrency guarantee; it's a best-effort bug detector.
        // - For concurrent iteration/modification, use concurrent collections (e.g., ConcurrentHashMap)
        //   or copy-on-write structures depending on the use case.
    }
}
