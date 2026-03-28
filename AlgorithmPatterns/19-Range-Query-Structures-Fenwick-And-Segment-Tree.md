# Range Query Structures: Fenwick and Segment Tree

## 1. Why this file exists
- Some problems ask many updates and many range queries.
- Prefix sums alone are not enough once updates become frequent.

## 2. Recognition signals
- Many range sum or min or max queries.
- Many updates between queries.
- Need faster than O(n) per query.

## 3. Fenwick tree
- Also called Binary Indexed Tree.
- Great for prefix sums with point updates.
- Simpler than segment tree.

## 4. Segment tree
- Handles wider types of range queries.
- Can support min, max, sum, gcd, and more.
- Can be extended with lazy propagation for range updates.

## 5. When to use what
- Fenwick: point updates + prefix/range sums.
- Segment tree: more general range queries or updates.

## 6. Common question types
- Range sum query mutable.
- Count smaller numbers after self variants.
- Dynamic frequency or inversion counting.
- Interval aggregation with frequent updates.

## 7. Common mistakes
- One-based indexing confusion in Fenwick tree.
- Wrong segment boundaries in segment tree recursion.
- Overusing segment tree when simpler methods are enough.

## 8. Interview answer in one line
- Use Fenwick tree or segment tree when the problem has many updates and many range queries, and simple prefix sums are no longer efficient enough.
