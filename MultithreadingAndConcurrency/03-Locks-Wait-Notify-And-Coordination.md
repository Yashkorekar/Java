# Locks, wait/notify, and Coordination

## 1. Intrinsic lock vs explicit lock
- Intrinsic lock means `synchronized` and object monitors.
- Explicit lock means `Lock` implementations such as `ReentrantLock`.
- Use `synchronized` when simple monitor-based locking is enough.
- Use explicit locks when you need timed lock attempts, fairness options, multiple conditions, or more control.

## 2. `ReentrantLock`
- Offers `lock()`, `unlock()`, `tryLock()`, and timed locking.
- Must be released in `finally`.
- Easier to misuse than `synchronized`, but more flexible.

## 3. `ReadWriteLock`
- Multiple readers can proceed together.
- Writers need exclusive access.
- Useful when reads dominate and writes are relatively infrequent.
- Not automatically better; more complexity and overhead can hurt small workloads.

## 4. `StampedLock`
- Offers optimistic reads and more advanced control.
- Can be good for high-read scenarios.
- More advanced and easier to misuse.
- Not reentrant.

## 5. `wait`, `notify`, `notifyAll`
- These belong to `Object` because they operate on the monitor of that object.
- The calling thread must hold that object's monitor.
- `wait()` releases the monitor and suspends.
- `notify()` wakes one waiting thread.
- `notifyAll()` wakes all waiting threads.

## 6. Golden rules for `wait`
- Always call `wait()` inside a loop, not a simple `if`.
- Re-check the condition after waking.
- Spurious wakeups can happen.
- Condition should guard the action.

## 7. Producer-consumer pattern
- Producer adds work to a shared buffer.
- Consumer removes work.
- The buffer should block or coordinate when full or empty.
- In modern code, `BlockingQueue` is usually better than hand-written `wait/notify`.

## 8. `Condition`
- With `ReentrantLock`, `Condition` is the explicit-lock alternative to `wait/notify`.
- It allows multiple condition queues per lock.
- Useful for more structured coordination.

## 9. Coordination utilities overview
- `CountDownLatch`: one-shot gate, threads wait until a count reaches zero.
- `CyclicBarrier`: multiple threads wait for each other and then continue together.
- `Semaphore`: controls limited permits to a shared resource.
- `Phaser`: flexible multi-phase coordination.
- `Exchanger`: two threads swap data.

## 10. When to use what
- Use `join()` for one thread waiting for another to finish.
- Use `CountDownLatch` when tasks must all complete before moving on.
- Use `Semaphore` when a limited resource must be protected.
- Use `BlockingQueue` for producer-consumer.
- Use `wait/notify` mainly to understand fundamentals or when building low-level coordination.

## 11. Interrupts and blocking operations
- Blocking methods often throw `InterruptedException`.
- Swallowing interrupts silently is a common mistake.
- Either stop work or restore interrupt status.

## 12. Common interview traps
- Calling `wait()` or `notify()` without synchronizing on the same object.
- Using `notify()` when `notifyAll()` is safer for the condition design.
- Forgetting the condition loop around `wait()`.
- Forgetting `unlock()` in explicit lock code.

## 13. Quick revision checklist
- Can you explain `synchronized` vs `ReentrantLock`?
- Can you explain when `ReadWriteLock` helps?
- Can you explain why `wait()` is used inside `while`?
- Can you explain `CountDownLatch`, `CyclicBarrier`, and `Semaphore`?
- Can you explain why `BlockingQueue` is often better than manual `wait/notify`?
