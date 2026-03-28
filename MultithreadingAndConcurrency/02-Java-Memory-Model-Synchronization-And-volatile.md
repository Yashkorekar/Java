# Java Memory Model, Synchronization, and volatile

## 1. Why concurrency bugs happen
- Multiple threads share mutable data.
- One thread updates state while another reads it.
- Without the right guarantees, you can get stale reads, lost updates, or out-of-order observations.

## 2. Atomicity, visibility, ordering
- Atomicity: an operation happens as one indivisible unit.
- Visibility: one thread sees another thread's latest write.
- Ordering: operations appear in a correct sequence across threads.

## 3. Java Memory Model in plain English
- The Java Memory Model defines what thread interactions are legal and visible.
- It explains when one thread is guaranteed to see another thread's changes.
- Without happens-before relationships, you should not assume timely visibility.

## 4. Race condition
- A race condition happens when correctness depends on timing between threads.
- Example: two threads incrementing `count++` on the same variable.
- `count++` is not atomic. It is read, modify, write.

## 5. `synchronized`
- `synchronized` provides mutual exclusion and visibility.
- Only one thread can hold the same monitor lock at a time.
- Entering and leaving a synchronized region establishes happens-before relationships.

## 6. Synchronized method vs block
- Synchronized instance method locks on `this`.
- Static synchronized method locks on the `Class` object.
- Synchronized block can lock on a chosen object.
- Prefer the smallest correct lock scope to reduce contention.

## 7. Reentrancy
- Java intrinsic locks are reentrant.
- A thread that already holds a lock can acquire it again.
- This avoids self-deadlock in common nested calls.

## 8. `volatile`
- `volatile` ensures visibility and ordering for reads and writes of that field.
- It does not make compound operations like `count++` atomic.
- Good use cases:
  - stop flags
  - simple state publication
  - one writer, many readers for simple status flags

## 9. `volatile` vs `synchronized`
| Need | Use |
| --- | --- |
| Only latest value visibility for one field | `volatile` |
| Mutual exclusion plus visibility | `synchronized` |
| Compound state updates | usually `synchronized`, `Lock`, or atomics |

## 10. Happens-before examples
- Unlock on a monitor happens-before a later lock of the same monitor.
- Write to a volatile field happens-before a later read of that same field.
- Starting a thread happens-before actions in that thread.
- Actions in a thread happen-before another thread successfully returns from `join()` on it.

## 11. Safe publication
- Publish immutable objects.
- Use final fields correctly.
- Publish through `volatile`, `synchronized`, thread-safe collections, or static initialization.
- Unsafe publication can expose partially constructed state.

## 12. `final` fields
- Properly constructed immutable objects with final fields have strong safety guarantees.
- Immutability is one of the simplest thread-safety tools.

## 13. Double-checked locking
- Double-checked locking for lazy initialization requires `volatile`.
- Without `volatile`, other threads may observe a partially initialized object.

## 14. Common interview traps
- `volatile` does not replace locking for read-modify-write logic.
- `HashMap` is not made thread-safe by making the reference volatile.
- `synchronized` is not only about exclusion; it also gives visibility.

## 15. Quick revision checklist
- Can you explain atomicity vs visibility?
- Can you explain why `count++` is not atomic?
- Can you explain `volatile` vs `synchronized`?
- Can you explain happens-before in simple words?
- Can you explain safe publication and immutability?
