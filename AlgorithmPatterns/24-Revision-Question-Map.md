# Revision and Question Map

## 1. If you see this clue, think this pattern

| Clue | Pattern |
| --- | --- |
| sorted array, first true, last false, minimum feasible | Binary search |
| subarray, substring, contiguous, longest valid window | Sliding window |
| pair in sorted array, left and right comparison | Two pointers |
| exact subarray sum, longest subarray with target | Prefix sum + hashing |
| next greater, previous smaller, sliding maximum | Monotonic stack or deque |
| top k, kth largest, repeated minimum or maximum | Heap / PriorityQueue |
| reverse linked list, cycle, merge linked lists | Linked list patterns |
| islands, spiral matrix, flood fill, shortest grid path | Matrix and grid patterns |
| all subsets, all combinations, all arrangements | Backtracking |
| min or max with repeated states | Dynamic programming |
| capacity, subset, interval, bitmask state | Advanced DP |
| level traversal, shortest path in unweighted graph | BFS |
| component search, cycle exploration, deep recursion | DFS |
| weighted shortest path, spanning tree | Advanced graph algorithms |
| tree height, path, BST validation | Tree recursion |
| merge intervals, choose earliest end | Greedy |
| kth element without full sorting, event timeline | Quickselect / line sweep |
| prefix search of strings | Trie |
| connectivity after unions | Union Find |
| many updates + many range queries | Fenwick / Segment tree |
| advanced substring matching or palindrome engine | String algorithms |
| divisibility, modulo, gcd, combinatorics | Math / number theory |
| implement cache or custom DS API | Design / simulation |
| points, optimal game, random pick | Geometry / game theory / randomized |

## 2. Common question families

### Array and string families
- Binary search on sorted data. Example: [LeetCode 704 - Binary Search](https://leetcode.com/problems/binary-search/)
- Sliding window on substrings. Example: [LeetCode 3 - Longest Substring Without Repeating Characters](https://leetcode.com/problems/longest-substring-without-repeating-characters/)
- Prefix sum on subarrays. Example: [LeetCode 560 - Subarray Sum Equals K](https://leetcode.com/problems/subarray-sum-equals-k/)
- Two pointers on sorted arrays. Example: [LeetCode 167 - Two Sum II - Input Array Is Sorted](https://leetcode.com/problems/two-sum-ii-input-array-is-sorted/)
- Monotonic stack and queue for next greater or window extrema. Examples: [LeetCode 496 - Next Greater Element I](https://leetcode.com/problems/next-greater-element-i/), [LeetCode 239 - Sliding Window Maximum](https://leetcode.com/problems/sliding-window-maximum/)
- String algorithms for advanced prefix or palindrome problems. Examples: [LeetCode 1392 - Longest Happy Prefix](https://leetcode.com/problems/longest-happy-prefix/), [LeetCode 5 - Longest Palindromic Substring](https://leetcode.com/problems/longest-palindromic-substring/)
- Math and bit tricks for arithmetic-heavy questions. Examples: [LeetCode 231 - Power of Two](https://leetcode.com/problems/power-of-two/), [LeetCode 136 - Single Number](https://leetcode.com/problems/single-number/)

### Tree and graph families
- Tree DFS and BFS. Examples: [LeetCode 104 - Maximum Depth of Binary Tree](https://leetcode.com/problems/maximum-depth-of-binary-tree/), [LeetCode 102 - Binary Tree Level Order Traversal](https://leetcode.com/problems/binary-tree-level-order-traversal/)
- Graph BFS and DFS. Example: [LeetCode 200 - Number of Islands](https://leetcode.com/problems/number-of-islands/)
- Topological sorting for dependencies. Example: [LeetCode 210 - Course Schedule II](https://leetcode.com/problems/course-schedule-ii/)
- Union Find for grouping and connectivity. Example: [LeetCode 547 - Number of Provinces](https://leetcode.com/problems/number-of-provinces/)
- Weighted shortest path and MST patterns. Examples: [LeetCode 743 - Network Delay Time](https://leetcode.com/problems/network-delay-time/), [LeetCode 1584 - Min Cost to Connect All Points](https://leetcode.com/problems/min-cost-to-connect-all-points/)
- Matrix and grid traversal. Examples: [LeetCode 54 - Spiral Matrix](https://leetcode.com/problems/spiral-matrix/), [LeetCode 200 - Number of Islands](https://leetcode.com/problems/number-of-islands/)
- Linked list pointer patterns. Example: [LeetCode 206 - Reverse Linked List](https://leetcode.com/problems/reverse-linked-list/)

### Optimization families
- Heap for top-k and streaming. Examples: [LeetCode 347 - Top K Frequent Elements](https://leetcode.com/problems/top-k-frequent-elements/), [LeetCode 295 - Find Median from Data Stream](https://leetcode.com/problems/find-median-from-data-stream/)
- Greedy for intervals and local-best proofs. Examples: [LeetCode 56 - Merge Intervals](https://leetcode.com/problems/merge-intervals/), [LeetCode 55 - Jump Game](https://leetcode.com/problems/jump-game/)
- DP for overlapping subproblems. Example: [LeetCode 70 - Climbing Stairs](https://leetcode.com/problems/climbing-stairs/)
- Advanced DP for knapsack, LIS, intervals, and subset state. Examples: [LeetCode 416 - Partition Equal Subset Sum](https://leetcode.com/problems/partition-equal-subset-sum/), [LeetCode 300 - Longest Increasing Subsequence](https://leetcode.com/problems/longest-increasing-subsequence/)
- Range query structures when queries and updates repeat. Example: [LeetCode 307 - Range Sum Query - Mutable](https://leetcode.com/problems/range-sum-query-mutable/)
- Design problems where API and complexity matter together. Example: [LeetCode 146 - LRU Cache](https://leetcode.com/problems/lru-cache/)

## 3. 45-day study order
1. Binary search
2. Sliding window
3. Two pointers
4. Prefix sum and hashing
5. Monotonic stack
6. Heap and top-k
7. Linked list patterns
8. Matrix and grid patterns
9. Binary tree basics and patterns
10. Graph BFS and DFS
11. Backtracking
12. Greedy and intervals
13. DP basics
14. Advanced DP
15. Recursion and divide and conquer
16. Advanced graph algorithms
17. Sorting, quickselect, and line sweep
18. Trie, DSU, and bit manipulation
19. Range query structures
20. String algorithms
21. Math and number theory
22. Design and simulation
23. Geometry, game theory, and randomized

## 4. What to do after each pattern
- Solve 3 easy questions.
- Solve 3 medium questions.
- Write one clean template from memory.
- Write down your common mistakes.

## 5. If you are stuck in interview
1. Ask whether the data is sorted or can be sorted.
2. Ask whether the answer range is monotonic.
3. Ask whether the problem is contiguous.
4. Ask whether repeated states exist.
5. Ask whether you need top-k or nearest greater or dependency order.
6. Ask whether this is really a linked list, grid, or design problem.
7. Ask whether weights, costs, or many updates change the default pattern.

## 6. Final revision checklist
- Can you explain when to use binary search beyond arrays?
- Can you explain sliding window vs prefix sum?
- Can you explain two pointers vs sliding window?
- Can you explain when heap beats sorting?
- Can you explain when greedy works and when it does not?
- Can you define DP state and transition?
- Can you choose BFS vs DFS quickly?
- Can you choose basic graph vs weighted graph patterns?
- Can you recognize linked list, grid, and design questions quickly?
- Can you identify when the problem is actually math or string-algorithm heavy?

## 7. Best mindset for LeetCode-style rounds
- Recognize the pattern first.
- Write the template second.
- Check edge cases third.
- Optimize only after correctness is clear.
