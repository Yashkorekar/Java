# Interview Questions and Revision

## 1. Most asked basic questions

### Q1. What is the difference between process and thread?
- A process is an executing program. A thread is an execution path within that process.

### Q2. What is the difference between concurrency and parallelism?
- Concurrency is managing multiple tasks with overlapping progress. Parallelism is actual simultaneous execution on multiple cores.

### Q3. What is the difference between `start()` and `run()`?
- `start()` creates a new thread and then calls `run()` there. `run()` directly is just a normal method call.

### Q4. What is a race condition?
- A race condition happens when correctness depends on unpredictable timing between threads.

### Q5. What does `synchronized` do?
- It provides mutual exclusion and visibility guarantees around shared state access.

### Q6. What does `volatile` do?
- It gives visibility and ordering guarantees for a field, but not full atomicity for compound operations.

### Q7. Why is `count++` not thread-safe?
- Because it is read, modify, write, not one atomic action.

### Q8. What is the difference between `wait()` and `sleep()`?
- `wait()` releases the monitor and waits for notification. `sleep()` pauses current thread without releasing a monitor.

### Q9. What is `join()` used for?
- To wait for another thread to finish.

### Q10. What is interruption?
- A cooperative cancellation signal that sets the interrupt flag and may unblock certain blocking calls.

## 2. Common intermediate questions

### Q11. When would you use `ReentrantLock` over `synchronized`?
- When you need timed lock attempts, explicit unlock control, multiple conditions, or other advanced locking features.

### Q12. What is `ExecutorService`?
- A service that manages task execution, pooling, and lifecycle beyond raw thread creation.

### Q13. What is the difference between `Runnable` and `Callable`?
- `Runnable` returns nothing; `Callable` returns a value and may throw checked exceptions.

### Q14. What is `Future`?
- A handle for a result that may be available later.

### Q15. What is `CompletableFuture`?
- A richer async abstraction for composing, transforming, and combining asynchronous computations.

### Q16. What is `ConcurrentHashMap`?
- A map designed for safe concurrent access with better scalability than locking a whole map for many workloads.

### Q17. What is `AtomicInteger`?
- A lock-free atomic integer helper for single-value atomic updates.

### Q18. What is `CountDownLatch`?
- A one-shot synchronization aid that lets threads wait until a counter reaches zero.

### Q19. What is `Semaphore`?
- A permit-based concurrency control mechanism.

### Q20. What is `ThreadLocal`?
- A per-thread storage mechanism that gives each thread its own value.

## 3. Advanced interview questions

### Q21. What is happens-before?
- It is the rule set that defines when one thread is guaranteed to observe another thread's actions.

### Q22. What is safe publication?
- Publishing an object so other threads see a properly constructed and visible state.

### Q23. What is deadlock?
- Two or more threads waiting forever on each other because of circular lock dependency.

### Q24. What is livelock?
- Threads keep responding to each other but still do not make progress.

### Q25. What is starvation?
- A thread cannot get enough CPU or resource access to make progress.

### Q26. When are virtual threads useful?
- For high-concurrency blocking I/O workloads, not as the primary answer for CPU-bound computation.

### Q27. Why should you not hold locks during remote calls?
- It increases contention, latency, and deadlock risk while reducing throughput.

## 4. Quick comparison table
| Concept | Best short answer |
| --- | --- |
| `synchronized` | lock + visibility |
| `volatile` | visibility + ordering only |
| `AtomicInteger` | atomic single-value updates |
| `ExecutorService` | managed task execution |
| `CompletableFuture` | async composition |
| `ConcurrentHashMap` | concurrent map |
| `BlockingQueue` | producer-consumer coordination |
| `CountDownLatch` | wait for N tasks |
| `Semaphore` | limited permits |

## 5. Final revision checklist
- Can you write thread creation code from memory?
- Can you explain `start()` vs `run()` without confusion?
- Can you explain `volatile` vs `synchronized` correctly?
- Can you explain `wait/notify` and why `wait()` goes in a loop?
- Can you explain `ExecutorService`, `Callable`, `Future`, and `CompletableFuture`?
- Can you explain `ConcurrentHashMap`, `AtomicInteger`, and `BlockingQueue`?
- Can you explain deadlock prevention and interruption?
