# Thread Basics and Lifecycle

## 1. What multithreading means
- Multithreading means a single process runs multiple threads of execution.
- These threads share process memory, especially heap objects.
- This makes programs more responsive and better at overlapping work, but also introduces data races and coordination problems.

## 2. Concurrency vs parallelism
- Concurrency means multiple tasks make progress over time.
- Parallelism means multiple tasks run literally at the same time on multiple cores.
- A single-core system can still be concurrent through scheduling.

## 3. Why we use threads
- Keep UI or request threads responsive.
- Handle multiple client requests.
- Overlap I/O waits.
- Run background tasks.
- Improve throughput for suitable workloads.

## 4. Thread creation styles in Java
- Extend `Thread`.
- Implement `Runnable`.
- Use `Callable<V>` when a result or checked exception is needed.
- Submit tasks to an `ExecutorService` instead of manually creating raw threads in most real applications.

## 5. `start()` vs `run()`
- `start()` asks the JVM to create a new thread and then call `run()` on that new thread.
- Calling `run()` directly is just a normal method call on the current thread.
- This is one of the most common beginner mistakes.

## 6. Thread lifecycle at a high level
- `NEW`: thread object created, not started.
- `RUNNABLE`: eligible to run or currently running.
- `BLOCKED`: waiting to enter a synchronized block or method.
- `WAITING`: waiting indefinitely, such as `wait()` or `join()` without timeout.
- `TIMED_WAITING`: sleeping or waiting with timeout.
- `TERMINATED`: finished execution.

## 7. Common thread methods
- `start()` to begin execution on a new thread.
- `run()` contains task logic.
- `sleep(ms)` pauses current thread for some time.
- `join()` waits for another thread to finish.
- `interrupt()` requests cooperative cancellation or wake-up.
- `isInterrupted()` and `Thread.interrupted()` inspect interrupt status.

## 8. `sleep()` and `yield()`
- `sleep()` pauses the current thread and moves it to timed waiting.
- `yield()` is only a scheduling hint and usually not useful for correctness.
- Never use `sleep()` as the real synchronization mechanism for correctness.

## 9. `join()`
- `join()` is used when one thread must wait for another to finish.
- It is common in demos, tests, and simple coordination.
- In larger systems, other coordination tools are often better.

## 10. Daemon threads
- Daemon threads are background threads that do not keep the JVM alive by themselves.
- When all user threads end, the JVM can exit even if daemon threads are still running.
- Use daemon threads for housekeeping, not for must-finish business work.

## 11. Interruption
- Interruption is the standard cooperative cancellation mechanism in Java.
- `interrupt()` does not forcefully kill a thread.
- It sets the interrupt flag, and blocking methods like `sleep`, `wait`, and `join` may throw `InterruptedException`.
- Good code either stops work or restores the interrupt flag with `Thread.currentThread().interrupt()`.

## 12. What not to use
- `Thread.stop()`, `suspend()`, and `resume()` are unsafe and deprecated.
- Do not rely on thread priorities for correctness.
- Avoid raw thread creation for every task in server applications.

## 13. Beginner-friendly example
- Imagine an e-commerce site.
- One thread handles a checkout request.
- Another thread sends email in background.
- Another thread updates analytics.
- Without proper coordination, shared order state can become inconsistent.

## 14. Good default rule
- Learn raw threads for interviews.
- Prefer executors and higher-level concurrency abstractions in real code.

## 15. Quick revision checklist
- Can you explain process vs thread?
- Can you explain concurrency vs parallelism?
- Can you explain `start()` vs `run()`?
- Can you explain thread lifecycle states?
- Can you explain interruption and why it is cooperative?
