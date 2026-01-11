package CollectionsFramework;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Collections Framework - Queue / Deque / PriorityQueue / LinkedList (Q&A + runnable demos)
 *
 * Run:
 *   javac .\CollectionsFramework\QueueDequePriorityQueueLinkedListQnA.java
 *   java CollectionsFramework.QueueDequePriorityQueueLinkedListQnA
 */
public class QueueDequePriorityQueueLinkedListQnA {

    /*
     * =============================
     * 1) Queue basics
     * =============================
     * Queue methods come in pairs:
     * - add(e) / remove() / element()  -> throw exception on failure (e.g., empty queue)
     * - offer(e) / poll() / peek()     -> return false/null on failure
     */

    static void queueAddRemoveVsOfferPollPeek() {
        System.out.println("=== Queue: add/remove/element vs offer/poll/peek ===");

        Queue<Integer> q = new ArrayDeque<>();

        System.out.println("offer 1 => " + q.offer(1));
        System.out.println("offer 2 => " + q.offer(2));

        System.out.println("peek => " + q.peek());   // 1
        System.out.println("element => " + q.element()); // 1

        System.out.println("poll => " + q.poll());   // 1
        System.out.println("remove => " + q.remove()); // 2

        System.out.println("poll on empty => " + q.poll()); // null

        try {
            System.out.println("remove on empty => " + q.remove());
        } catch (Exception e) {
            System.out.println("remove on empty throws => " + e.getClass().getSimpleName());
        }

        try {
            System.out.println("element on empty => " + q.element());
        } catch (Exception e) {
            System.out.println("element on empty throws => " + e.getClass().getSimpleName());
        }

        // output (order may vary slightly for exception prints):
        // offer 1 => true
        // offer 2 => true
        // peek => 1
        // element => 1
        // poll => 1
        // remove => 2
        // poll on empty => null
        // remove on empty throws => NoSuchElementException
        // element on empty throws => NoSuchElementException
    }

    /*
     * =============================
     * 2) PriorityQueue
     * =============================
     * - Implements a min-heap by default.
     * - peek/poll give the smallest element (natural order) or by Comparator.
     * - Iteration order is NOT sorted.
     */

    static void priorityQueueDemo() {
        System.out.println("\n=== PriorityQueue demo ===");

        PriorityQueue<Integer> pq = new PriorityQueue<>();
        pq.offer(5);
        pq.offer(1);
        pq.offer(3);

        System.out.println("peek => " + pq.peek()); // 1
        System.out.println("poll => " + pq.poll()); // 1
        System.out.println("poll => " + pq.poll()); // 3
        System.out.println("poll => " + pq.poll()); // 5

        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
        maxHeap.offer(5);
        maxHeap.offer(1);
        maxHeap.offer(3);

        System.out.println("maxHeap poll => " + maxHeap.poll()); // 5
        System.out.println("maxHeap poll => " + maxHeap.poll()); // 3
        System.out.println("maxHeap poll => " + maxHeap.poll()); // 1
    }

    static void priorityQueueIterationTrap() {
        System.out.println("\n=== PriorityQueue iteration trap (not sorted) ===");

        PriorityQueue<Integer> pq = new PriorityQueue<>();
        pq.offer(10);
        pq.offer(4);
        pq.offer(15);
        pq.offer(7);
        pq.offer(3);
        pq.offer(20);
        pq.offer(1);
        pq.offer(8);

        System.out.print("iteration order => ");
        for (Integer x : pq) {
            System.out.print(x + " ");
        }
        System.out.println();
        System.out.println("toString => " + pq);
        // output: NOT guaranteed sorted order (it may look partially sorted by coincidence)

        System.out.print("polling gives sorted => ");
        while (!pq.isEmpty()) {
            System.out.print(pq.poll() + " ");
        }
        System.out.println();
        // output: 1 2 3 4
    }

    /*
     * =============================
     * 3) Deque (double-ended queue)
     * =============================
     * - Use ArrayDeque for stack/queue in most cases (fast, no node allocations).
     * - LinkedList also implements Deque but has node overhead.
     */

    static void dequeDemo() {
        System.out.println("\n=== Deque demo (ArrayDeque) ===");

        Deque<String> dq = new ArrayDeque<>();
        dq.addFirst("B");
        dq.addLast("C");
        dq.addFirst("A");

        System.out.println(dq); // [A, B, C]

        System.out.println("removeFirst => " + dq.removeFirst()); // A
        System.out.println("removeLast => " + dq.removeLast());   // C
        System.out.println("peekFirst => " + dq.peekFirst());     // B
        System.out.println("peekLast => " + dq.peekLast());       // B

        // Stack style:
        dq.push("X");
        dq.push("Y");
        System.out.println("after push => " + dq);
        System.out.println("pop => " + dq.pop());
        System.out.println("after pop => " + dq);
    }

    static void linkedListAsQueueAndDeque() {
        System.out.println("\n=== LinkedList as Queue/Deque ===");

        LinkedList<Integer> ll = new LinkedList<>();
        ll.offer(1);
        ll.offer(2);
        System.out.println("as queue peek => " + ll.peek());
        System.out.println("as queue poll => " + ll.poll());
        System.out.println("after poll => " + ll);

        ll.addFirst(10);
        ll.addLast(20);
        System.out.println("as deque => " + ll);
    }

    public static void main(String[] args) {
        queueAddRemoveVsOfferPollPeek();
        priorityQueueDemo();
        priorityQueueIterationTrap();
        dequeDemo();
        linkedListAsQueueAndDeque();

        // Interview notes:
        // - ArrayDeque does NOT allow null elements.
        // - LinkedList allows nulls, but is typically slower for stack/queue operations.
    }
}
