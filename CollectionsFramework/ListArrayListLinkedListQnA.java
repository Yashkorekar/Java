package CollectionsFramework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Collections Framework - List: ArrayList vs LinkedList operations (Q&A + runnable demos)
 *
 * Run:
 *   javac .\CollectionsFramework\ListArrayListLinkedListQnA.java
 *   java CollectionsFramework.ListArrayListLinkedListQnA
 */
public class ListArrayListLinkedListQnA {

    /*
     * =============================
     * 1) What is List?
     * =============================
     * List is an ordered collection that allows duplicates and supports positional access.
     * Common operations:
     * - add, add(index, element)
     * - get, set
     * - remove(index), remove(object)
     * - contains, indexOf, lastIndexOf
     * - subList
     * - iteration via for/for-each/iterator/listIterator
     */

    /*
     * =============================
     * 2) ArrayList vs LinkedList (interview summary)
     * =============================
     * ArrayList:
     * - Backed by a resizable array.
     * - get/set by index: O(1)
     * - add at end: amortized O(1)
     * - insert/remove in middle: O(n) due to shifting
     * - lower per-element memory overhead than LinkedList
     *
     * LinkedList:
     * - Doubly-linked nodes.
     * - get(index): O(n) (must traverse)
     * - add/remove at ends: O(1)
     * - insert/remove at an iterator position: O(1) (after you reach the node)
     * - higher memory overhead; poor cache locality
     *
     * Practical guidance:
     * - Prefer ArrayList for most cases.
     * - Use LinkedList when you do many inserts/removals near ends or via iterator.
     */

    /*
     * =============================
     * 3) Common edge cases interviewers ask
     * =============================
     * - remove(int index) vs remove(Object o): can be ambiguous with Integer autoboxing.
     *   Example: list.remove(1) removes index 1, not value Integer(1).
     *   Use list.remove(Integer.valueOf(1)) to remove by value.
     *
     * - Iterating + removing:
     *   - Using list.remove(...) inside for-each causes ConcurrentModificationException.
     *   - Correct: use Iterator.remove() or ListIterator.remove().
     *
     * - contains/indexOf use equals(), not ==.
     *
     * - null elements:
     *   - ArrayList and LinkedList allow null.
     *   - Be careful with equals (call "x".equals(item), not item.equals("x")).
     */

    static void basicOps(List<String> list, String name) {
        System.out.println("\n=== Basic ops on " + name + " ===");

        list.add("A");
        list.add("B");
        list.add("B");
        list.add(1, "X");
        System.out.println(list);
        // output: [A, X, B, B]

        System.out.println(list.get(0));
        // output: A

        System.out.println(list.set(0, "Z"));
        // output: A (returns old value)

        System.out.println(list);
        // output: [Z, X, B, B]

        System.out.println(list.contains("B"));
        // output: true

        System.out.println(list.indexOf("B"));
        System.out.println(list.lastIndexOf("B"));
        // output:
        // 2
        // 3

        System.out.println(list.remove(1));
        // output: X (removed by index)

        System.out.println(list.remove("B"));
        // output: true (removed first matching element)

        System.out.println(list);
        // output: [Z, B]

        list.clear();
        System.out.println(list.isEmpty());
        // output: true
    }

    static void removeIntVsInteger() {
        System.out.println("\n=== remove(int) vs remove(Integer) ===");

        List<Integer> nums = new ArrayList<>();
        nums.add(10);
        nums.add(20);
        nums.add(30);
        System.out.println(nums);
        // output: [10, 20, 30]

        nums.remove(1); // removes index 1 (value 20)
        System.out.println(nums);
        // output: [10, 30]

        nums.remove(Integer.valueOf(10)); // removes value 10
        System.out.println(nums);
        // output: [30]
    }

    static void safeRemovalWithIterator() {
        System.out.println("\n=== Safe removal during iteration (Iterator) ===");

        List<String> list = new ArrayList<>(Arrays.asList("a", "remove", "b", "remove"));
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            String v = it.next();
            if ("remove".equals(v)) {
                it.remove();
            }
        }
        System.out.println(list);
        // output: [a, b]

        // Interview note:
        // - If you did list.remove(v) in a for-each loop, you would get ConcurrentModificationException.
    }

    static void listIteratorOps() {
        System.out.println("\n=== ListIterator operations ===");

        List<String> list = new LinkedList<>(Arrays.asList("A", "C"));
        ListIterator<String> it = list.listIterator();

        it.next();       // cursor moves after "A"
        it.add("B");     // insert at cursor position
        System.out.println(list);
        // output: [A, B, C]

        // Move and replace
        it.next(); // moves over "C"
        it.set("Z");
        System.out.println(list);
        // output: [A, B, Z]
    }

    static void subListDemo() {
        System.out.println("\n=== subList view (edge case) ===");

        List<String> base = new ArrayList<>(Arrays.asList("A", "B", "C", "D"));
        List<String> view = base.subList(1, 3); // view of [B, C]

        System.out.println(view);
        // output: [B, C]

        view.set(0, "X");
        System.out.println(base);
        // output: [A, X, C, D]

        // Interview trap:
        // - subList is a VIEW backed by the original list.
        // - Structural changes to base (add/remove) can make the view invalid and throw exceptions later.
        // Safer pattern if you need an independent list:
        //   List<String> copy = new ArrayList<>(base.subList(1, 3));
    }

    static void arraysAsListTrap() {
        System.out.println("\n=== Arrays.asList() trap (fixed-size list) ===");

        List<String> fixed = Arrays.asList("A", "B", "C");
        System.out.println(fixed);
        // output: [A, B, C]

        fixed.set(0, "Z"); // allowed (replaces element)
        System.out.println(fixed);
        // output: [Z, B, C]

        try {
            fixed.add("D"); // throws UnsupportedOperationException
        } catch (UnsupportedOperationException ex) {
            System.out.println("add blocked: UnsupportedOperationException");
        }
        // output: add blocked: UnsupportedOperationException

        // Interview tip:
        // - If you need a real resizable list: new ArrayList<>(Arrays.asList(...))
    }

    static void listOfImmutabilityTrap() {
        System.out.println("\n=== List.of() immutability trap ===");

        List<String> immutable = List.of("A", "B");
        System.out.println(immutable);
        // output: [A, B]

        try {
            immutable.set(0, "Z");
        } catch (UnsupportedOperationException ex) {
            System.out.println("set blocked: UnsupportedOperationException");
        }
        // output: set blocked: UnsupportedOperationException

        try {
            immutable.add("C");
        } catch (UnsupportedOperationException ex) {
            System.out.println("add blocked: UnsupportedOperationException");
        }
        // output: add blocked: UnsupportedOperationException
    }

    static void failFastDemo() {
        System.out.println("\n=== Fail-fast iteration (ConcurrentModificationException) ===");

        List<String> list = new ArrayList<>(Arrays.asList("A", "B", "C"));
        try {
            for (String s : list) {
                if ("A".equals(s)) {
                    list.add("D"); // structural modification during for-each
                }
            }
            System.out.println("no exception (unexpected)");
        } catch (java.util.ConcurrentModificationException ex) {
            System.out.println("caught: ConcurrentModificationException");
        }
        // output: caught: ConcurrentModificationException

        // Correct approach: use Iterator.remove() (see safeRemovalWithIterator())
    }

    static void removeIfDemo() {
        System.out.println("\n=== removeIf demo (safe structural removal) ===");

        List<String> list = new ArrayList<>(Arrays.asList("keep", "drop", "keep", "drop"));
        boolean changed = list.removeIf("drop"::equals);
        System.out.println(changed);
        System.out.println(list);
        // output:
        // true
        // [keep, keep]
    }

    static void toArrayDemo() {
        System.out.println("\n=== toArray() demo (common interview question) ===");

        List<String> list = new ArrayList<>(Arrays.asList("A", "B"));

        Object[] a1 = list.toArray();
        System.out.println(a1.getClass().getSimpleName() + " length=" + a1.length);
        // output: Object[] length=2

        String[] a2 = list.toArray(new String[0]);
        System.out.println(a2.getClass().getSimpleName() + " -> " + Arrays.toString(a2));
        // output: String[] -> [A, B]

        // Interview tip:
        // - Prefer toArray(new T[0]) or toArray(T[]::new) (Java 11+) for typed arrays.
    }

    static void unmodifiableViewDemo() {
        System.out.println("\n=== Collections.unmodifiableList() (view) ===");

        List<String> base = new ArrayList<>(Arrays.asList("A", "B"));
        List<String> view = Collections.unmodifiableList(base);
        System.out.println(view);
        // output: [A, B]

        base.add("C");
        System.out.println(view);
        // output: [A, B, C]  (view reflects base)

        try {
            view.add("D");
        } catch (UnsupportedOperationException ex) {
            System.out.println("view add blocked: UnsupportedOperationException");
        }
        // output: view add blocked: UnsupportedOperationException
    }

    public static void main(String[] args) {
        System.out.println("=== Collections Framework: List (ArrayList vs LinkedList) ===");

        basicOps(new ArrayList<>(), "ArrayList");
        basicOps(new LinkedList<>(), "LinkedList");

        removeIntVsInteger();
        safeRemovalWithIterator();
        listIteratorOps();
        subListDemo();
        arraysAsListTrap();
        listOfImmutabilityTrap();
        unmodifiableViewDemo();
        failFastDemo();
        removeIfDemo();
        toArrayDemo();
        System.out.println("\nDone.");
    }
}
