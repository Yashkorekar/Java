# Multithreading and Concurrency (Index)

This folder is a full interview-prep and self-learning section for Java multithreading and concurrency.

## Scope
- Thread creation and lifecycle
- `start()` vs `run()`
- `sleep`, `join`, interrupt, daemon threads
- race conditions, visibility, atomicity, and the Java Memory Model
- `synchronized`, intrinsic locks, and monitor behavior
- `volatile`, happens-before, and safe publication
- `wait`, `notify`, `notifyAll`, and coordination patterns
- `Lock`, `ReentrantLock`, `ReadWriteLock`, `StampedLock`, and `Condition`
- `ExecutorService`, thread pools, `Callable`, `Future`, `CompletableFuture`
- virtual threads and modern Java concurrency direction
- concurrent collections, atomic classes, `LongAdder`, blocking queues
- `CountDownLatch`, `CyclicBarrier`, `Semaphore`, `Phaser`, `ThreadLocal`
- deadlock, livelock, starvation, thread safety patterns, performance, and debugging

## Suggested order
1. `01-Thread-Basics-And-Lifecycle.md`
2. `02-Java-Memory-Model-Synchronization-And-volatile.md`
3. `03-Locks-Wait-Notify-And-Coordination.md`
4. `04-Executors-Callable-Future-And-CompletableFuture.md`
5. `05-Concurrent-Collections-Atomics-And-Utilities.md`
6. `06-Thread-Safety-Deadlock-And-Best-Practices.md`
7. `07-Interview-Questions-And-Revision.md`

## Runnable demos
- `Demos/ThreadBasicsDemo.java`
- `Demos/SynchronizationAndVisibilityDemo.java`
- `Demos/ExecutorCompletableFutureDemo.java`
- `Demos/ConcurrentCollectionsAndUtilitiesDemo.java`

## Beginner-first mental model
- A process is a running program.
- A thread is a path of execution inside that process.
- Multiple threads can share the same heap objects.
- Shared mutable state is where concurrency bugs start.
- Concurrency is mainly about coordination, visibility, correctness, and performance trade-offs.

## Most common interview traps
- Confusing concurrency with parallelism.
- Thinking `volatile` makes compound operations atomic.
- Thinking `HashMap` is safe for concurrent writes.
- Calling `run()` directly and expecting a new thread.
- Ignoring interruption and cancellation.
- Creating raw threads everywhere instead of using executors.
- Using too many threads for CPU-bound work.

## One-line memory anchors
- `synchronized` gives mutual exclusion and visibility.
- `volatile` gives visibility and ordering, not full atomicity.
- `ExecutorService` is usually preferred over manual thread creation.
- `CompletableFuture` is for async composition, not magic.
- Concurrent collections solve specific sharing problems, not all concurrency problems.
