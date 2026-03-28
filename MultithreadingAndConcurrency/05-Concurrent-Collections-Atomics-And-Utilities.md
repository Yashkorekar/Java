# Concurrent Collections, Atomics, and Utilities

## 1. Why ordinary collections are risky
- `ArrayList`, `HashMap`, and `LinkedList` are not generally safe for concurrent modification.
- Without external synchronization, concurrent access can corrupt state or throw exceptions.

## 2. `ConcurrentHashMap`
- Thread-safe map for concurrent access.
- Better than synchronizing a whole `HashMap` for many workloads.
- Compound actions still need care. Thread-safe container does not make all multi-step logic automatically safe.
- Methods like `compute`, `merge`, and `putIfAbsent` are useful.

## 3. `CopyOnWriteArrayList`
- Good when reads are frequent and writes are rare.
- Iteration is safe without external locking.
- Every write copies the whole array, so write-heavy workloads are expensive.

## 4. Concurrent queues
- `ConcurrentLinkedQueue`: non-blocking queue.
- `LinkedBlockingQueue` and `ArrayBlockingQueue`: blocking queues.
- Blocking queues are very common for producer-consumer systems.

## 5. `BlockingQueue`
- Producers can `put`.
- Consumers can `take`.
- Automatically handles waiting when empty or full.
- Often the cleanest way to coordinate work between threads.

## 6. Atomic classes
- `AtomicInteger`, `AtomicLong`, `AtomicBoolean`, `AtomicReference`.
- Useful for lock-free atomic updates on single values.
- `incrementAndGet`, `getAndIncrement`, `compareAndSet` are common methods.

## 7. `LongAdder` and `LongAccumulator`
- Better than `AtomicLong` for very high contention counters.
- Common in metrics and hot counters.
- Trade exact single-step semantics for better scalability in some patterns.

## 8. Coordination utilities recap
- `CountDownLatch`: wait until a fixed number of events finish.
- `CyclicBarrier`: threads meet at a barrier and then continue.
- `Semaphore`: limit concurrent access to a resource.
- `Phaser`: dynamic phased coordination.

## 9. `ThreadLocal`
- Gives each thread its own isolated value.
- Good for per-thread context, formatters in older code, trace state, or request-scoped data.
- Dangerous if misused in thread pools, because pooled threads outlive tasks.
- Always clean up when necessary.

## 10. Synchronized wrappers vs concurrent collections
- `Collections.synchronizedMap(...)` wraps with one synchronized facade.
- Good for simple cases.
- Concurrent collections usually scale better for true concurrent workloads.

## 11. Common interview traps
- `ConcurrentHashMap` makes single operations thread-safe, not all business invariants.
- `CopyOnWriteArrayList` is terrible for heavy writes.
- `ThreadLocal` can leak stale data in pools if not cleared.
- Blocking queues are usually better than custom busy waiting.

## 12. Quick revision checklist
- Can you explain `ConcurrentHashMap` vs `HashMap`?
- Can you explain when `CopyOnWriteArrayList` is useful?
- Can you explain `AtomicInteger` vs `synchronized` counter?
- Can you explain `BlockingQueue` and producer-consumer?
- Can you explain why `ThreadLocal` needs care in pools?
