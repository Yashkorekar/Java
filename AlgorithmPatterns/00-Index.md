# Algorithm Patterns for LeetCode-Style DSA

This folder is a pattern-based guide for solving LeetCode-style DSA problems.

## Important reality check
- No short checklist can literally solve every single LeetCode question.
- Some questions are hybrids, some are niche, and some combine multiple patterns.
- But this folder now aims to cover almost all major interview-relevant pattern families plus the most common niche extensions.

## Why this folder exists
- Most coding-round questions are not solved by memorizing one exact answer.
- They are solved by recognizing the pattern quickly.
- If you know when to use binary search, sliding window, two pointers, prefix sums, heaps, graphs, trees, backtracking, greedy, and DP, you solve far more questions with less stress.

## Suggested study order
1. `01-Binary-Search.md`
2. `02-Sliding-Window.md`
3. `03-Two-Pointers.md`
4. `04-Prefix-Sum-And-Hashing.md`
5. `05-Monotonic-Stack-And-Queue.md`
6. `06-Heap-PriorityQueue-And-TopK.md`
7. `13-Linked-List-Patterns.md`
8. `14-Matrix-And-Grid-Patterns.md`
9. `10-Binary-Tree-And-BST-Patterns.md`
10. `09-Graph-BFS-DFS-And-Topological-Sort.md`
11. `07-Backtracking.md`
12. `11-Greedy-And-Intervals.md`
13. `08-Dynamic-Programming-Basics.md`
14. `16-Advanced-DP-Knapsack-LIS-Interval-And-State-Compression.md`
15. `15-Recursion-And-Divide-And-Conquer.md`
16. `17-Shortest-Path-MST-And-Advanced-Graphs.md`
17. `18-Sorting-Quickselect-And-Line-Sweep.md`
18. `12-Trie-UnionFind-And-Bit-Manipulation.md`
19. `19-Range-Query-Structures-Fenwick-And-Segment-Tree.md`
20. `20-String-Algorithms-KMP-Rolling-Hash-And-Manacher.md`
21. `21-Math-Number-Theory-And-Combinatorics.md`
22. `22-Design-Simulation-And-Custom-Data-Structures.md`
23. `23-Geometry-Game-Theory-And-Randomized.md`
24. `24-Revision-Question-Map.md`

## How to use these notes
- First learn the recognition signals.
- Then learn the template.
- Then solve 5 to 10 problems of the same pattern back to back.
- After that, revisit the common mistakes section.

## High-yield recognition clues
- Sorted array or monotonic answer space: think binary search.
- Subarray or substring with contiguous range: think sliding window or prefix sum.
- Pair or triplet in sorted data: think two pointers.
- Next greater, previous smaller, daily temperatures style: think monotonic stack.
- Top K, repeated best candidate, stream of priorities: think heap.
- Reversal, cycle, middle node, merge sorted lists: think linked list patterns.
- Grid with directions, islands, flood fill, spiral traversal: think matrix and grid patterns.
- All subsets, all permutations, all combinations: think backtracking.
- Grid or graph reachability: think BFS or DFS.
- Tree path, height, diameter, validate BST: think tree DFS.
- Min or max answer with overlapping subproblems: think DP.
- Weighted graph shortest path: think Dijkstra or advanced graph patterns.
- Need range updates or range queries many times: think Fenwick tree or segment tree.
- Prefix search or advanced string matching: think trie or string algorithms.
- LRU cache, custom iterator, design a data structure: think design and simulation.

## What these notes include
- Algorithm intuition
- When to use it
- Java template
- Common variations
- Common mistakes
- Practice question types

## One-line memory anchors
- Binary search is not only for arrays; it is also for monotonic answers.
- Sliding window usually means contiguous range.
- Prefix sum is for fast range math and subarray counting.
- Heap is for repeated best candidate.
- DP is about state, transition, and reuse.
- Linked list questions are pointer questions first, not index questions.
- Grid questions often reduce to graph traversal or simulation.
- Advanced graph questions usually begin when weights or costs appear.
- Design questions are about API behavior plus data structure choice.

