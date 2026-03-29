# ArrayList Internals

This file explains `ArrayList` beyond normal usage. The focus is how it grows, why it is usually the right default `List`, where its costs come from, and which details matter in interviews.

## 1. Why `ArrayList` matters

`ArrayList` is one of the most common Java collections in real production code.
Interviewers ask it because it touches:
- arrays
- growth strategy
- amortized complexity
- cache locality
- shifting cost
- fail-fast iteration
- memory trade-offs

If a candidate chooses `LinkedList` casually over `ArrayList`, interviewers often probe deeply here.

## 2. Mental model first

An `ArrayList` is mainly:
- a resizable array
- a `size` telling how many logical elements are used
- extra capacity that may be larger than `size`

Simplified picture:

```text
elementData -> [A, B, C, null, null, null]
size = 3
capacity = 6
```

The gap between size and capacity is what allows cheap appends most of the time.

## 3. Core internal fields you should know

Conceptually these fields matter:

| Field | Meaning |
| --- | --- |
| `elementData` | Backing object array |
| `size` | Number of real elements currently stored |
| `modCount` | Structural modification count used by fail-fast iterators |

Simplified idea:

```java
class ArrayList<E> {
    Object[] elementData;
    int size;
}
```

The array stores references, not copied object bodies.

## 4. Default allocation behavior

Modern JDKs avoid eagerly allocating a full default array at construction time.

That means:
- `new ArrayList<>()` is very cheap initially
- real backing storage is created when elements are first added

This is a small but useful memory optimization.

## 5. Growth strategy is why append is amortized O(1)

When the array fills up, `ArrayList` allocates a larger array and copies existing references.

Common simplified interview answer:
- capacity usually grows by about 1.5x in modern JDKs

Important implication:
- one resize is expensive
- but resizes do not happen on every append
- so repeated `add(element)` at the end is amortized O(1)

Example growth intuition:

```text
10 -> 15 -> 22 -> 33 -> 49 -> ...
```

Exact formulas can vary by JDK, but the core idea is the same.

## 6. `add(E e)` internal flow

Appending at the end roughly does this:
1. check whether backing array has room
2. grow array if necessary
3. store element at `elementData[size]`
4. increment `size`
5. increment `modCount` because structure changed

That is why end-appends are usually fast.

## 7. `get(index)` is very fast

`ArrayList` shines at random access because:
- index lookup is direct array access
- no traversal is needed

That is why `get(index)` is usually O(1).

Interview-safe answer:
- it is fast because the structure is contiguous indexable storage, not because Java is doing anything magical

## 8. Why insert/remove in the middle are expensive

This is the main cost beginners underestimate.

If you insert at index `i`:
- all later elements must shift right by one position

If you remove at index `i`:
- all later elements must shift left by one position

So middle operations are usually O(n).

That is the real reason `ArrayList` is bad for frequent middle insertions/removals.

## 9. Cache locality is a real performance advantage

Senior interviewers often like this point.

Because `ArrayList` stores references in contiguous array storage:
- iteration is CPU-cache friendly
- random access is simple and predictable
- memory access patterns are better than pointer-chasing through nodes

This is one big reason `ArrayList` often outperforms `LinkedList` even in cases where big-O tables can mislead beginners.

## 10. Fail-fast iteration and `modCount`

`ArrayList` iterators usually fail fast.

That means:
- iterator captures structural state
- if the list is structurally modified outside the iterator during iteration
- Java may throw `ConcurrentModificationException`

This is bug detection, not a concurrency guarantee.

Typical trap:

```java
for (String s : list) {
    list.add("x");
}
```

This may throw `ConcurrentModificationException`.

## 11. Memory overhead and trade-offs

`ArrayList` has two main memory-related costs:
- the backing array may have unused spare capacity
- each entry is still a reference, not free

But compared with `LinkedList`, it usually has much lower per-element overhead.

That is one reason `ArrayList` is usually the practical default.

## 12. Pre-sizing can matter

If you know approximate element count in advance, this can help:

```java
new ArrayList<>(expectedSize)
```

Why it matters:
- fewer growth operations
- fewer array copies
- smoother throughput for bulk loading

Senior interview point:
- this is not always necessary, but it is useful when you know the scale up front

## 13. `subList()` is a common trap

`subList()` is a view, not an independent copy.

That means:
- changes through the view affect the original list
- structural changes to the parent list can invalidate assumptions around the view

Interview-safe answer:
- if you need an independent list, copy it explicitly

## 14. Complexity summary

| Operation | Cost |
| --- | --- |
| `get(index)` | O(1) |
| `set(index, value)` | O(1) |
| append at end | amortized O(1) |
| insert/remove in middle | O(n) |
| search by value | O(n) |

The key word is amortized. That is what separates strong answers from shallow ones.

## 15. `ArrayList` vs `LinkedList`

| Type | Main strength |
| --- | --- |
| `ArrayList` | Fast random access, good iteration locality, lower per-element overhead |
| `LinkedList` | Cheap link/unlink once you already have the node position |

Senior interview answer:
- `ArrayList` is the right default most of the time

## 16. Common senior interview questions and strong answers

### Q1. Why is `ArrayList` append amortized O(1) instead of always O(1)?
Because occasional growth requires allocating a bigger array and copying existing references.

### Q2. Why is `ArrayList` often faster than `LinkedList` in practice?
Because direct indexing and contiguous storage usually beat pointer-heavy node traversal and poor cache locality.

### Q3. Why is middle insertion expensive?
Because later elements must shift.

### Q4. What does initial capacity help with?
It reduces resize frequency and copying cost when approximate size is known in advance.

### Q5. Is `subList()` a copy?
No. It is a view over the original list.

## 17. Interview traps

- Saying `ArrayList` append is always O(1) without mentioning amortization.
- Forgetting resize copy cost.
- Ignoring cache locality advantages.
- Thinking `subList()` makes an independent copy.
- Choosing `LinkedList` as a default list without a real reason.

## 18. Short memory anchor

- `ArrayList`: resizable array + direct indexing + amortized append + shifting cost + strong locality.