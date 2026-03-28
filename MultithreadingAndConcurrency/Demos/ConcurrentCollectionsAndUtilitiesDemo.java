package MultithreadingAndConcurrency.Demos;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrentCollectionsAndUtilitiesDemo {

    public static void main(String[] args) throws InterruptedException {
        concurrentHashMapDemo();
        atomicIntegerDemo();
        countDownLatchDemo();
        blockingQueueDemo();
    }

    private static void concurrentHashMapDemo() throws InterruptedException {
        System.out.println("=== ConcurrentHashMap demo ===");

        Map<String, Integer> counts = new ConcurrentHashMap<>();

        Thread t1 = new Thread(() -> incrementWord(counts, "java"));
        Thread t2 = new Thread(() -> incrementWord(counts, "java"));

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println(counts);
        System.out.println();
    }

    private static void atomicIntegerDemo() throws InterruptedException {
        System.out.println("=== AtomicInteger demo ===");

        AtomicInteger counter = new AtomicInteger();
        Thread t1 = new Thread(() -> incrementAtomic(counter));
        Thread t2 = new Thread(() -> incrementAtomic(counter));

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("Atomic counter value: " + counter.get());
        System.out.println();
    }

    private static void countDownLatchDemo() throws InterruptedException {
        System.out.println("=== CountDownLatch demo ===");

        CountDownLatch latch = new CountDownLatch(3);

        Runnable task = () -> {
            System.out.println(Thread.currentThread().getName() + " finished setup work");
            latch.countDown();
        };

        new Thread(task, "worker-1").start();
        new Thread(task, "worker-2").start();
        new Thread(task, "worker-3").start();

        latch.await();
        System.out.println("All workers finished, main thread continues");
        System.out.println();
    }

    private static void blockingQueueDemo() throws InterruptedException {
        System.out.println("=== BlockingQueue demo ===");

        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>();

        Thread producer = new Thread(() -> {
            try {
                for (int value = 1; value <= 3; value++) {
                    queue.put(value);
                    System.out.println("Produced " + value);
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }, "producer");

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 3; i++) {
                    System.out.println("Consumed " + queue.take());
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }, "consumer");

        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
        System.out.println();
    }

    private static void incrementWord(Map<String, Integer> counts, String word) {
        for (int i = 0; i < 10_000; i++) {
            counts.compute(word, (key, value) -> value == null ? 1 : value + 1);
        }
    }

    private static void incrementAtomic(AtomicInteger counter) {
        for (int i = 0; i < 10_000; i++) {
            counter.incrementAndGet();
        }
    }
}
