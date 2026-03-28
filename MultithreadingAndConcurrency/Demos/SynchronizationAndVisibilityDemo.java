package MultithreadingAndConcurrency.Demos;

public class SynchronizationAndVisibilityDemo {

    static final class UnsafeCounter {
        private int count;

        void increment() {
            count++;
        }

        int getCount() {
            return count;
        }
    }

    static final class SafeCounter {
        private int count;

        synchronized void increment() {
            count++;
        }

        synchronized int getCount() {
            return count;
        }
    }

    static final class VolatileFlagWorker {
        private volatile boolean running = true;

        void stop() {
            running = false;
        }

        void work() {
            while (running) {
                Thread.onSpinWait();
            }
            System.out.println("Volatile flag observed as false, worker stops");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        raceConditionDemo();
        synchronizedDemo();
        volatileDemo();
    }

    private static void raceConditionDemo() throws InterruptedException {
        System.out.println("=== Race condition demo ===");

        UnsafeCounter counter = new UnsafeCounter();
        Thread t1 = new Thread(() -> incrementManyTimes(counter::increment));
        Thread t2 = new Thread(() -> incrementManyTimes(counter::increment));

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("Unsafe counter expected 200000, actual " + counter.getCount());
        System.out.println();
    }

    private static void synchronizedDemo() throws InterruptedException {
        System.out.println("=== synchronized demo ===");

        SafeCounter counter = new SafeCounter();
        Thread t1 = new Thread(() -> incrementManyTimes(counter::increment));
        Thread t2 = new Thread(() -> incrementManyTimes(counter::increment));

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("Safe counter expected 200000, actual " + counter.getCount());
        System.out.println();
    }

    private static void volatileDemo() throws InterruptedException {
        System.out.println("=== volatile visibility demo ===");

        VolatileFlagWorker worker = new VolatileFlagWorker();
        Thread thread = new Thread(worker::work, "volatile-worker");
        thread.start();

        Thread.sleep(100);
        worker.stop();
        thread.join();
        System.out.println();
    }

    private static void incrementManyTimes(Runnable incrementLogic) {
        for (int i = 0; i < 100_000; i++) {
            incrementLogic.run();
        }
    }
}
