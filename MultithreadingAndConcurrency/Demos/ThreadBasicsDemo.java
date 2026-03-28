package MultithreadingAndConcurrency.Demos;

public class ThreadBasicsDemo {

    static final class NamedWorker extends Thread {
        NamedWorker(String name) {
            super(name);
        }

        @Override
        public void run() {
            System.out.println(getName() + " running on thread " + Thread.currentThread().getName());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        startVsRunDemo();
        runnableAndJoinDemo();
        interruptDemo();
    }

    private static void startVsRunDemo() throws InterruptedException {
        System.out.println("=== start() vs run() ===");

        Runnable task = () -> System.out.println("Runnable.run() executes on thread " + Thread.currentThread().getName());
        task.run();

        NamedWorker newThread = new NamedWorker("real-thread");
        newThread.start();
        newThread.join();
        System.out.println();
    }

    private static void runnableAndJoinDemo() throws InterruptedException {
        System.out.println("=== Runnable and join() ===");

        Thread worker = new Thread(() -> {
            for (int step = 1; step <= 3; step++) {
                System.out.println(Thread.currentThread().getName() + " step " + step);
            }
        }, "lambda-worker");

        worker.start();
        worker.join();
        System.out.println("Main thread waited for lambda-worker to finish");
        System.out.println();
    }

    private static void interruptDemo() throws InterruptedException {
        System.out.println("=== interrupt() demo ===");

        Thread sleepingWorker = new Thread(() -> {
            try {
                System.out.println("Worker going to sleep");
                Thread.sleep(5_000);
            } catch (InterruptedException ex) {
                System.out.println("Worker interrupted while sleeping");
                Thread.currentThread().interrupt();
            }
            System.out.println("Interrupt flag after handling: " + Thread.currentThread().isInterrupted());
        }, "sleeping-worker");

        sleepingWorker.start();
        Thread.sleep(100);
        sleepingWorker.interrupt();
        sleepingWorker.join();
        System.out.println();
    }
}
