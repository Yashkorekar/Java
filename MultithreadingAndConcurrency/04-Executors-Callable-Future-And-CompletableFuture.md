# Executors, Callable, Future, and CompletableFuture

## 1. Why executors are preferred
- Creating raw threads manually for every task does not scale well.
- Thread creation is expensive.
- Unbounded thread growth can crash or degrade systems.
- Executors let you reuse threads and manage task execution policy.

## 2. `Executor` vs `ExecutorService`
- `Executor` is the simple interface with `execute(Runnable)`.
- `ExecutorService` adds lifecycle control and richer task APIs such as `submit`, `invokeAll`, and `shutdown`.

## 3. Common executor types
- Fixed thread pool: limited worker count.
- Cached thread pool: grows dynamically; can be dangerous if workload explodes.
- Single-thread executor: one worker, preserves task ordering.
- Scheduled executor: delayed and periodic tasks.
- Work-stealing pool / fork-join style: useful for divide-and-conquer CPU tasks.

## 4. `Runnable` vs `Callable`
- `Runnable` does not return a result.
- `Callable<V>` returns a value and can throw checked exceptions.

## 5. `Future`
- `Future` represents a pending result.
- `get()` blocks until completion.
- `cancel()` requests cancellation.
- `isDone()` and `isCancelled()` inspect state.

## 6. Limits of plain `Future`
- Hard to chain tasks.
- Hard to compose multiple async stages.
- Hard to manage success and failure fluently.

## 7. `CompletableFuture`
- Supports async pipelines and composition.
- Common methods:
  - `supplyAsync`
  - `runAsync`
  - `thenApply`
  - `thenAccept`
  - `thenCompose`
  - `thenCombine`
  - `exceptionally`
  - `handle`
  - `allOf`
  - `anyOf`

## 8. `thenApply` vs `thenCompose`
- `thenApply` transforms a value.
- `thenCompose` flattens nested async work when the next step also returns a `CompletableFuture`.
- This is like `map` vs `flatMap` thinking.

## 9. Thread pools and workload type
- CPU-bound tasks generally want roughly around core count, depending on workload.
- I/O-bound tasks can use more threads because threads spend time waiting.
- Virtual threads make I/O-bound task handling much easier in modern Java.

## 10. Rejection policies
- Thread pools can reject tasks when saturated.
- Common strategies include aborting, running in caller thread, discarding, or discarding oldest.
- Interviewers sometimes ask what happens when queue and pool are full.

## 11. Shutdown lifecycle
- `shutdown()` stops accepting new tasks and lets running tasks finish.
- `shutdownNow()` attempts to interrupt running tasks and returns queued tasks.
- Always shut down executors you create explicitly.

## 12. Virtual threads
- Virtual threads are lightweight threads designed mainly for high-concurrency I/O workloads.
- They are not a solution for CPU-bound parallel work.
- Good mental model: they make blocking-style code more scalable for I/O-heavy applications.
- Typical API: `Executors.newVirtualThreadPerTaskExecutor()`.

## 13. Parallel streams and fork/join note
- Parallel streams use the common fork-join pool by default.
- They are convenient, but not always predictable for application-level concurrency control.
- Do not use them blindly on blocking I/O work.

## 14. Common interview traps
- Blocking on `future.get()` everywhere can destroy async benefits.
- Forgetting to specify the right executor for `CompletableFuture` pipelines.
- Using cached pools carelessly under unbounded load.
- Forgetting to shut down executors.

## 15. Quick revision checklist
- Can you explain `Runnable` vs `Callable`?
- Can you explain `submit()` vs `execute()`?
- Can you explain plain `Future` vs `CompletableFuture`?
- Can you explain `thenApply` vs `thenCompose`?
- Can you explain when virtual threads help?
