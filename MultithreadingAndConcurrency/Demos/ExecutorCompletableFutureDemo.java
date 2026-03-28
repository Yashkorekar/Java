package MultithreadingAndConcurrency.Demos;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ExecutorCompletableFutureDemo {

    public static void main(String[] args) throws Exception {
        executorServiceDemo();
        completableFutureDemo();
    }

    private static void executorServiceDemo() throws Exception {
        System.out.println("=== ExecutorService demo ===");

        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            pool.execute(() -> System.out.println("Runnable executed by " + Thread.currentThread().getName()));

            Future<Integer> squareFuture = pool.submit(() -> 12 * 12);
            System.out.println("Callable result: " + squareFuture.get());

            List<Callable<String>> tasks = List.of(
                () -> "Task-1 done by " + Thread.currentThread().getName(),
                () -> "Task-2 done by " + Thread.currentThread().getName()
            );

            for (Future<String> future : pool.invokeAll(tasks)) {
                System.out.println(future.get());
            }
        } finally {
            shutdown(pool);
        }
        System.out.println();
    }

    private static void completableFutureDemo() throws InterruptedException, ExecutionException {
        System.out.println("=== CompletableFuture demo ===");

        CompletableFuture<String> future = CompletableFuture
            .supplyAsync(() -> "java concurrency")
            .thenApply(String::toUpperCase)
            .thenCompose(value -> CompletableFuture.supplyAsync(() -> value + " WITH COMPLETABLEFUTURE"));

        System.out.println("CompletableFuture result: " + future.get());

        CompletableFuture<String> recovery = CompletableFuture.supplyAsync(() -> {
            boolean shouldFail = true;
            if (!shouldFail) {
                return "Success";
            }
                throw new IllegalStateException("Something failed");
            })
            .exceptionally(ex -> "Recovered from: " + ex.getClass().getSimpleName());

        System.out.println(recovery.get());
        System.out.println();
    }

    private static void shutdown(ExecutorService pool) throws InterruptedException {
        pool.shutdown();
        if (!pool.awaitTermination(2, TimeUnit.SECONDS)) {
            pool.shutdownNow();
        }
    }
}
