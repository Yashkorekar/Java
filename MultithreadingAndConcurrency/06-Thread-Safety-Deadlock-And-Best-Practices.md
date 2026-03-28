# Thread Safety, Deadlock, and Best Practices

## 1. What thread safety means
- A class is thread-safe if it behaves correctly under concurrent access.
- Correctness includes preserving invariants and visibility guarantees.

## 2. Common thread-safety strategies
- Immutability
- Thread confinement
- Synchronized access
- Explicit locks
- Atomic variables
- Stateless design
- Message passing via queues

## 3. Immutability
- Immutable objects are naturally easier to share safely.
- Prefer immutable DTOs, configs, and value objects when possible.
- Final fields help here.

## 4. Thread confinement
- If data is only used by one thread, synchronization is often unnecessary.
- Local variables are naturally thread-confined.
- Thread confinement is simple and powerful.

## 5. Deadlock
- Deadlock happens when threads wait forever on each other.
- Classic conditions:
  - mutual exclusion
  - hold and wait
  - no preemption
  - circular wait

## 6. How to prevent deadlock
- Use a consistent lock acquisition order.
- Avoid nested locking where possible.
- Use timeouts with `tryLock()` when appropriate.
- Prefer higher-level abstractions instead of manual multi-lock choreography.

## 7. Livelock and starvation
- Livelock: threads keep reacting to each other but make no real progress.
- Starvation: a thread rarely or never gets CPU or lock access.
- Fairness settings can help in some cases, but may reduce throughput.

## 8. Busy waiting
- Spinning wastes CPU when used carelessly.
- Blocking structures and condition mechanisms are usually better.
- Spin techniques are specialized and not a beginner default.

## 9. False sharing note
- High-performance concurrency can suffer when unrelated variables share cache lines.
- Usually not the first interview topic, but relevant in advanced performance tuning.

## 10. Thread pool sizing intuition
- CPU-bound: keep worker count relatively close to CPU capability.
- I/O-bound: more threads can help because tasks spend time blocked.
- Virtual threads are especially useful for blocking I/O-heavy workloads.

## 11. Debugging tools and practices
- Thread dumps and `jstack` style inspection.
- Look for blocked threads, waiting threads, and deadlock patterns.
- Name threads clearly.
- Log task boundaries carefully.
- Add timeouts around remote calls.

## 12. Testing concurrency
- Concurrency bugs are timing-dependent and can be hard to reproduce.
- Use repeated runs, stress tests, and deterministic coordination tools where possible.
- Avoid depending on `sleep()` for test correctness.

## 13. Best-practice summary
- Prefer immutability first.
- Minimize shared mutable state.
- Prefer executors over raw threads.
- Prefer concurrent collections and blocking queues over hand-rolled coordination.
- Honor interruption.
- Keep lock scope small.
- Measure before tuning.

## 14. Common interview traps
- Treating `volatile` as a full replacement for locking.
- Sharing mutable objects freely between threads.
- Holding locks during slow I/O or remote calls.
- Catching `InterruptedException` and doing nothing.
- Using one giant synchronized method for everything.

## 15. Quick revision checklist
- Can you explain thread safety strategies?
- Can you explain deadlock and prevention?
- Can you explain livelock vs starvation?
- Can you explain why immutability is powerful?
- Can you explain why blocking I/O inside locks is dangerous?
