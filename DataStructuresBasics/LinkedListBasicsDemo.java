package DataStructuresBasics;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Beginner-first LinkedList drills.
 *
 * Run:
 *   javac .\DataStructuresBasics\LinkedListBasicsDemo.java
 *   java DataStructuresBasics.LinkedListBasicsDemo
 */
public class LinkedListBasicsDemo {

    public static void main(String[] args) {
        creationAndBasicOperations();
        iterationWays();
        queueAndStackUsage();
        commonInterviewChecklist();
    }

    private static void creationAndBasicOperations() {
        System.out.println("=== LinkedList: creation and basic operations ===");

        LinkedList<String> cities = new LinkedList<>();
        cities.add("Delhi");
        cities.add("Mumbai");
        cities.add("Pune");
        System.out.println("Initial list: " + cities);

        cities.addFirst("Chennai");
        cities.addLast("Bengaluru");
        cities.add(2, "Hyderabad");
        System.out.println("After addFirst, addLast, add(index): " + cities);

        System.out.println("First element: " + cities.getFirst());
        System.out.println("Last element: " + cities.getLast());
        System.out.println("Element at index 2: " + cities.get(2));

        cities.set(1, "Kolkata");
        System.out.println("After set(1, Kolkata): " + cities);

        cities.removeFirst();
        cities.removeLast();
        cities.remove("Hyderabad");
        System.out.println("After removals: " + cities);

        System.out.println("Contains Pune: " + cities.contains("Pune"));
        System.out.println("Size: " + cities.size());
        System.out.println();
    }

    private static void iterationWays() {
        System.out.println("=== LinkedList: ways to iterate and print ===");

        LinkedList<String> list = new LinkedList<>(Arrays.asList("A", "B", "C", "D"));

        System.out.println("1) Print whole list using println: " + list);
        System.out.println();

        System.out.println("2) Iterate using normal for loop");
        for (int index = 0; index < list.size(); index++) {
            System.out.println("index=" + index + ", value=" + list.get(index));
        }
        System.out.println();

        System.out.println("3) Iterate using enhanced for loop");
        for (String value : list) {
            System.out.println("value=" + value);
        }
        System.out.println();

        System.out.println("4) Iterate using Iterator");
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            System.out.println("value=" + iterator.next());
        }
        System.out.println();

        System.out.println("5) Iterate using descendingIterator");
        Iterator<String> descendingIterator = list.descendingIterator();
        while (descendingIterator.hasNext()) {
            System.out.println("value=" + descendingIterator.next());
        }
        System.out.println();

        System.out.println("6) Iterate using ListIterator forward and backward");
        ListIterator<String> listIterator = list.listIterator();
        while (listIterator.hasNext()) {
            System.out.println("forward=" + listIterator.next());
        }
        while (listIterator.hasPrevious()) {
            System.out.println("backward=" + listIterator.previous());
        }
        System.out.println();
    }

    private static void queueAndStackUsage() {
        System.out.println("=== LinkedList: queue and stack usage ===");

        LinkedList<Integer> queue = new LinkedList<>();
        queue.offer(10);
        queue.offer(20);
        queue.offer(30);
        System.out.println("Queue style list: " + queue);
        System.out.println("peek: " + queue.peek());
        System.out.println("poll: " + queue.poll());
        System.out.println("After poll: " + queue);
        System.out.println();

        LinkedList<Integer> stack = new LinkedList<>();
        stack.push(100);
        stack.push(200);
        stack.push(300);
        System.out.println("Stack style list: " + stack);
        System.out.println("peek: " + stack.peek());
        System.out.println("pop: " + stack.pop());
        System.out.println("After pop: " + stack);
        System.out.println();
    }

    private static void commonInterviewChecklist() {
        System.out.println("=== LinkedList interview checklist ===");
        System.out.println("- LinkedList stores elements in nodes, not contiguous array positions.");
        System.out.println("- addFirst/addLast/removeFirst/removeLast are very common methods.");
        System.out.println("- LinkedList can work as List, Queue, and Deque/Stack.");
        System.out.println("- Index-based access is slower than ArrayList in most cases.");
        System.out.println("- for-each, Iterator, and ListIterator are common iteration styles.");
    }
}
