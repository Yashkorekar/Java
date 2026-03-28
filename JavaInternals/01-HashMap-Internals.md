# HashMap Internals

This file explains `HashMap` beyond basic usage. The focus is how it works internally, why operations are usually fast, when they become slow, and which details matter in interviews.

## 1. Why `HashMap` is so important

`HashMap` is one of the most frequently asked Java classes because it touches several core ideas at once:
- hashing
- arrays
- linked structures and trees
- `equals` and `hashCode`
- time complexity
- memory overhead
- resizing
- iteration semantics
- thread safety problems

If an interviewer asks about `HashMap`, they are often checking whether you understand data-structure trade-offs, not just one class.

## 2. Mental model first

A `HashMap` is mainly:
- an array of buckets
- each bucket holds zero or more entries
- each entry stores key, value, hash, and a link to the next entry
- collisions mean multiple keys land in the same bucket

In modern JDKs, a heavily-collided bucket can be converted from a linked list into a red-black tree to avoid worst-case linear scans.

Very simplified mental picture:

```text
table[0] -> null
table[1] -> [k1,v1] -> [k2,v2]
table[2] -> null
table[3] -> [k3,v3]
...
```

If many keys fall into one bucket, that chain can become expensive. Java 8+ added tree bins to reduce that risk.

## 3. Core internal fields you should know

The exact JDK code changes over time, but conceptually these fields matter:

| Field | Meaning |
| --- | --- |
| `table` | Array of buckets, usually `Node<K,V>[]` |
| `size` | Number of key-value entries currently stored |
| `loadFactor` | Fraction of capacity allowed before resize, default `0.75` |
| `threshold` | Resize trigger, usually `capacity * loadFactor` |
| `modCount` | Structural modification count, used by fail-fast iterators |

Simplified node idea:

```java
class Node<K, V> {
    final int hash;
    final K key;
    V value;
    Node<K, V> next;
}
```

When treeified, the bucket uses tree nodes instead of a plain linked list.

## 4. Why capacity is usually a power of 2

`HashMap` keeps capacity as a power of 2 because bucket index calculation becomes cheap:

```java
index = (table.length - 1) & hash;
```

This is faster than using modulo:

```java
index = hash % table.length;
```

It also makes resize behavior cleaner because doubling capacity introduces exactly one extra decision bit.

## 5. Hash calculation and hash spreading

The key’s `hashCode()` is not used completely raw. Java mixes the bits so high bits also influence low bits.

Simplified idea:

```java
hash = h ^ (h >>> 16)
```

Why this matters:
- bucket selection uses low bits when capacity is a power of 2
- if a key’s `hashCode()` has weak low-bit distribution, collisions increase
- bit spreading improves bucket distribution

Important interview point:
- `HashMap` performance depends not only on map size, but also on hash quality

## 6. `put()` internal flow step by step

When you do:

```java
map.put(key, value);
```

the internal logic is roughly:

1. If table is not initialized, allocate it.
2. Compute the key hash.
3. Find bucket index using `(n - 1) & hash`.
4. If bucket is empty, place new node directly.
5. If bucket is not empty, compare with existing entries:
   - same key and same hash: replace value
   - otherwise traverse linked list or tree
6. If collision chain becomes too large:
   - if capacity is small, prefer resize
   - if capacity is already big enough, treeify the bucket
7. Increment `size` if a new entry was inserted.
8. If `size > threshold`, resize the table.

The distinction between “replace existing value” and “insert new entry” matters. Replacing a value does not increase `size`.

## 7. `get()` internal flow

When you do:

```java
map.get(key);
```

the logic is roughly:

1. Compute hash.
2. Compute bucket index.
3. Read first node in that bucket.
4. If first node matches key, return value.
5. Otherwise traverse list or tree until match is found.
6. If nothing matches, return `null`.

That is why average-case lookup is close to O(1), but bad collisions push it toward O(n) for linked buckets or O(log n) for treeified buckets.

## 8. `remove()` internal flow

Removing is also bucket-based:

1. Compute hash and bucket index.
2. Find matching node in linked list or tree.
3. Relink surrounding nodes if it was in a list.
4. Decrease `size`.
5. Increase `modCount` because structure changed.

Removal does not usually shrink the table automatically.

## 9. Collision handling

Collision means two different keys produce bucket indices that are the same.

Important point:
- collision does not mean the keys are equal
- it only means they landed in the same bucket

How collision is handled:
- first entry goes directly into the bucket
- later entries are linked into the same bucket structure
- Java 8+ may convert long chains into a red-black tree

### Treeification rules worth remembering
- `TREEIFY_THRESHOLD = 8`
- `UNTREEIFY_THRESHOLD = 6`
- `MIN_TREEIFY_CAPACITY = 64`

Meaning:
- bucket size reaching 8 may trigger treeification
- but only if table capacity is at least 64
- if table is still small, resize is preferred over treeification

Why Java prefers resize first for small tables:
- a small table often means collisions are happening because the table is too crowded
- resizing may distribute entries better and avoid the need for tree nodes

## 10. Resize internals are very important

This is one of the highest-value interview topics.

When `size > threshold`, the table grows, usually by doubling capacity.

Example:
- old capacity = 16
- new capacity = 32
- old threshold = 12 for default load factor
- new threshold = 24

### Naive understanding
You may think Java recomputes every bucket completely from scratch.

### Better understanding
Java uses the fact that capacity doubles.

Suppose old capacity is `16`, which is `10000` in binary.
For each old bucket chain, each entry either:
- stays at the same index
- or moves to `oldIndex + oldCapacity`

The deciding factor is one bit:

```text
(hash & oldCapacity) == 0  -> stays in low bucket
(hash & oldCapacity) != 0  -> moves to high bucket
```

This is a big optimization because Java does not need a full expensive recomputation of bucket placement logic for each entry.

### Why doubling is elegant
If old index was `i`, new index is either:
- `i`
- `i + oldCapacity`

This low/high split is one of the best details to mention in a senior interview.

## 11. Why `HashMap` allows one `null` key

`HashMap` permits:
- one `null` key
- multiple `null` values

The `null` key is typically stored in bucket 0 because its hash is treated specially.

Interview follow-up:
- `Hashtable` does not allow `null` key or `null` values
- `ConcurrentHashMap` does not allow `null` keys or values

## 12. `equals()` and `hashCode()` contract

This is where many candidates fail.

Rules:
- if two keys are equal according to `equals()`, they must return the same `hashCode()`
- if you override `equals()`, you should also override `hashCode()`

Why it matters:
- `hashCode()` decides bucket
- `equals()` decides exact key match inside bucket

Wrong implementation causes:
- duplicate logical keys
- lookup failure even though key looks equivalent
- removal failure
- very confusing bugs

### Correct mental sequence
`HashMap` does not scan all keys and call `equals()` globally.
It first narrows down by hash and bucket, then uses equality checks within that bucket.

## 13. Average and worst-case complexity

| Operation | Average | Worst case |
| --- | --- | --- |
| `get` | O(1) | O(n) with long list, O(log n) with tree bin |
| `put` | O(1) | O(n) or O(log n), plus resize cost sometimes |
| `remove` | O(1) | O(n) or O(log n) |

Important senior-level nuance:
- average O(1) does not mean constant in every situation
- bad hash functions, poor initial capacity, frequent resize, and collisions change behavior significantly

## 14. Memory overhead of `HashMap`

`HashMap` is fast partly because it spends extra memory.

Memory cost comes from:
- bucket array itself
- one node object per entry
- references for key, value, and next
- object headers and alignment
- tree node overhead if bucket becomes treeified

So `HashMap` is usually faster than a structure that stores data more compactly, but it is not memory-cheap.

This is useful in production discussions:
- millions of entries in a `HashMap` can consume much more memory than beginners expect

## 15. Iteration order is not guaranteed

`HashMap` does not preserve insertion order.

Many candidates get confused because a small test may appear stable for a particular run or JDK.
That apparent stability is accidental and should never be relied on.

If order matters:
- use `LinkedHashMap` for insertion/access order
- use `TreeMap` for sorted order

## 16. Fail-fast iterators and `modCount`

When you iterate over a `HashMap`, the iterator captures structural state.
If the map is structurally modified outside the iterator during iteration, Java may throw `ConcurrentModificationException`.

This behavior is called fail-fast.

Important nuance:
- fail-fast is a best-effort bug-detection mechanism
- it is not a concurrency guarantee

Typical example:

```java
for (String key : map.keySet()) {
    map.put("x", "y");
}
```

This may throw `ConcurrentModificationException`.

## 17. Why `HashMap` is not thread-safe

Concurrent writes are unsafe because:
- multiple threads can race while modifying buckets
- resize is a structural operation
- one thread may observe partially updated structure
- data can be lost or map state can become inconsistent

Historically, older JDK implementations could even end up in corrupted linked structures under concurrent resize.

For concurrent use cases, prefer:
- `ConcurrentHashMap` for high-concurrency maps
- external synchronization when appropriate

## 18. Initial capacity and load factor trade-off

### Default behavior
- default initial capacity is effectively 16 after first allocation
- default load factor is `0.75`

### Lower load factor
- fewer collisions
- more memory usage
- faster reads on average

### Higher load factor
- better memory utilization
- more collisions
- slower bucket scans

### Senior interview point
If you know approximate entry count, pre-sizing the map can reduce costly resizes.

## 19. Why bad keys are dangerous

Examples of bad keys:
- mutable keys whose fields change after insertion
- keys with poor `hashCode()` distribution
- keys with broken `equals()`/`hashCode()` contract

### Mutable key example problem
If a key’s state changes after insertion and that state participates in `hashCode()` or `equals()`, the entry may become practically unreachable by normal lookup.

This is a classic interview trap.

## 20. `HashMap` vs related maps

| Type | Key property |
| --- | --- |
| `HashMap` | Fast average lookup, no order guarantee, not thread-safe |
| `LinkedHashMap` | Maintains insertion or access order |
| `TreeMap` | Sorted order, usually O(log n) |
| `ConcurrentHashMap` | Thread-safe concurrent access |
| `Hashtable` | Legacy synchronized map, usually avoided in new code |

## 21. Practical insertion example

Suppose capacity is `16` and three different keys map to bucket `5`.

After inserts:

```text
table[5] -> [A=10] -> [B=20] -> [C=30]
```

Now `get(B)` does not search the whole table.
It:
- hashes `B`
- jumps directly to bucket `5`
- scans only that bucket structure

That is the full reason `HashMap` is usually fast.

## 22. Common senior interview questions and strong answers

### Q1. Why is `HashMap` usually O(1)?
Because it uses hashing to jump to a bucket directly, so it usually examines only a very small subset of entries.

### Q2. Why can it become slow?
Poor hash distribution, heavy collisions, linked bucket scans, tree operations, and resize cost can all hurt performance.

### Q3. Why is capacity a power of 2?
So index calculation is cheap with bit masking and resize splitting becomes efficient.

### Q4. What happens during resize?
Capacity doubles, threshold changes, and each old bucket entry either stays at the same index or moves to index plus old capacity depending on one hash bit.

### Q5. Why are `equals()` and `hashCode()` both needed?
`hashCode()` narrows the search to bucket level and `equals()` confirms the exact key.

### Q6. Why was treeification added in Java 8?
To improve worst-case behavior under heavy collisions from linear time toward logarithmic time.

### Q7. Is `HashMap` thread-safe?
No. Concurrent structural modifications can corrupt behavior and cause lost updates or inconsistent views.

### Q8. Can `HashMap` have duplicate keys?
Not logically. Inserting the same key replaces the old value.

## 23. Interview traps

- Saying `HashMap` is always O(1).
- Forgetting tree bins.
- Confusing collision with equality.
- Saying `hashCode()` alone decides equality.
- Saying `HashMap` preserves insertion order.
- Ignoring resize cost.
- Using mutable objects as keys without caution.

## 24. Final senior-level summary

If you want one strong summary answer:

`HashMap` is an array-backed hash table where each bucket stores entries that share the same bucket index. It uses a spread hash, power-of-two capacity, and bit masking for fast indexing. Average `get` and `put` are O(1), but collisions can degrade performance. Java 8+ can treeify long collision chains into red-black trees. When the number of entries crosses the threshold, the table resizes by doubling, and entries are redistributed using a low/high split based on one additional hash bit. Correct `equals()` and `hashCode()` implementations are essential, iteration order is not guaranteed, and the structure is not thread-safe.`

If you can explain that clearly and then discuss resize, collision handling, and bad-key behavior, your `HashMap` answer is already stronger than most interview answers.