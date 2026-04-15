# Shortest Path, MST, and Advanced Graphs

## 1. Why this file exists
- Basic BFS and DFS are not enough once edges have weights, costs, or advanced constraints.

## 2. Recognition signals
- Weighted edges: think Dijkstra or Bellman-Ford.
- Need minimum total spanning cost: think MST.
- Need all-pairs shortest paths: think Floyd-Warshall for small graphs.
- Edge weights are only 0 or 1: think 0-1 BFS.

## 3. Dijkstra
- Use for non-negative weighted shortest path.
- Usually uses a min-heap.

## 4. Dijkstra template idea
```java
PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
```
- Common state is `{node, distance}`.

## 5. Bellman-Ford
- Handles negative weights.
- Can detect negative cycles.
- Slower than Dijkstra.

## 6. MST
- Kruskal uses sorting edges + DSU.
- Prim uses a growing tree + priority queue.

## 7. Common advanced graph questions
- Network delay time. Example: [LeetCode 743 - Network Delay Time](https://leetcode.com/problems/network-delay-time/) (Medium)
- Cheapest flights within K stops. Example: [LeetCode 787 - Cheapest Flights Within K Stops](https://leetcode.com/problems/cheapest-flights-within-k-stops/) (Medium)
- Minimum spanning cost to connect points. Example: [LeetCode 1584 - Min Cost to Connect All Points](https://leetcode.com/problems/min-cost-to-connect-all-points/) (Medium)
- Course schedule variants with weights or costs. Example: [LeetCode 2050 - Parallel Courses III](https://leetcode.com/problems/parallel-courses-iii/) (Hard)
- Word ladder shortest transformation path. Examples: [LeetCode 127 - Word Ladder](https://leetcode.com/problems/word-ladder/) (Hard), [LeetCode 126 - Word Ladder II](https://leetcode.com/problems/word-ladder-ii/) (Hard)

## 8. Common mistakes
- Using BFS on weighted shortest-path problems.
- Using Dijkstra when negative weights exist.
- Forgetting stale heap entries in Dijkstra.
- Not using DSU correctly in Kruskal.

## 9. Interview answer in one line
- Advanced graph patterns begin when graphs become weighted, cost-sensitive, or require global connectivity optimization like shortest path or MST.

## More interview practice
### Must do
- [LeetCode 1631 - Path With Minimum Effort](https://leetcode.com/problems/path-with-minimum-effort/) (Medium)

### Very common
- [LeetCode 778 - Swim in Rising Water](https://leetcode.com/problems/swim-in-rising-water/) (Hard)

### Good follow-up
- [LeetCode 1514 - Path with Maximum Probability](https://leetcode.com/problems/path-with-maximum-probability/) (Medium)
