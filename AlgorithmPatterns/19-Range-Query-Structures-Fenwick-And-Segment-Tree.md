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
- Range sum query mutable. Example: [LeetCode 307 - Range Sum Query - Mutable](https://leetcode.com/problems/range-sum-query-mutable/)
- Count smaller numbers after self variants. Example: [LeetCode 315 - Count of Smaller Numbers After Self](https://leetcode.com/problems/count-of-smaller-numbers-after-self/)
- Dynamic frequency or inversion counting. Example: [LeetCode 1649 - Create Sorted Array through Instructions](https://leetcode.com/problems/create-sorted-array-through-instructions/)
- Interval aggregation with frequent updates. Example: [LeetCode 715 - Range Module](https://leetcode.com/problems/range-module/)

## 7. Common mistakes
- One-based indexing confusion in Fenwick tree.
- Wrong segment boundaries in segment tree recursion.
- Overusing segment tree when simpler methods are enough.

## 8. Interview answer in one line
- Use Fenwick tree or segment tree when the problem has many updates and many range queries, and simple prefix sums are no longer efficient enough.

## More interview practice
- [LeetCode 327 - Count of Range Sum](https://leetcode.com/problems/count-of-range-sum/)
- [LeetCode 493 - Reverse Pairs](https://leetcode.com/problems/reverse-pairs/)
- [LeetCode 699 - Falling Squares](https://leetcode.com/problems/falling-squares/)
