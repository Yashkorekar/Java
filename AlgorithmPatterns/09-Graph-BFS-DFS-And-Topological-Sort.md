# Graph BFS, DFS, and Topological Sort

## 1. Core idea
- Graph questions are about connectivity, traversal, dependencies, shortest path in unweighted graphs, or cycle detection.
- BFS and DFS are the two fundamental traversal tools.

## 2. When to use BFS
- Shortest path in unweighted graph.
- Level-order exploration.
- Grid expansion problems.
- Multi-source distance problems.

## 3. When to use DFS
- Reachability.
- Connected components.
- Cycle detection.
- Backtracking-like graph exploration.
- Tree-like recursion on graph structure.

## 4. Graph representation
```java
Map<Integer, List<Integer>> graph = new HashMap<>();
```
- For grid problems, the grid itself acts like a graph.

## 5. BFS template
```java
void bfs(Map<Integer, List<Integer>> graph, int start) {
    Queue<Integer> queue = new ArrayDeque<>();
    Set<Integer> visited = new HashSet<>();

    queue.offer(start);
    visited.add(start);

    while (!queue.isEmpty()) {
        int node = queue.poll();
        for (int next : graph.getOrDefault(node, List.of())) {
            if (visited.add(next)) {
                queue.offer(next);
            }
        }
    }
}
```

## 6. DFS template
```java
void dfs(Map<Integer, List<Integer>> graph, int node, Set<Integer> visited) {
    if (!visited.add(node)) {
        return;
    }

    for (int next : graph.getOrDefault(node, List.of())) {
        dfs(graph, next, visited);
    }
}
```

## 7. Topological sort
- Used in directed acyclic graphs with dependency order.
- Common for course schedule, build order, task ordering.
- Usually solved with indegree BFS or DFS finishing order.

## 8. Topological sort indegree template
```java
List<Integer> topoSort(int n, List<List<Integer>> graph, int[] indegree) {
    Queue<Integer> queue = new ArrayDeque<>();
    List<Integer> order = new ArrayList<>();

    for (int i = 0; i < n; i++) {
        if (indegree[i] == 0) {
            queue.offer(i);
        }
    }

    while (!queue.isEmpty()) {
        int node = queue.poll();
        order.add(node);
        for (int next : graph.get(node)) {
            if (--indegree[next] == 0) {
                queue.offer(next);
            }
        }
    }
    return order;
}
```

## 9. Common variations
- Number of islands
- Flood fill
- Rotten oranges
- Clone graph
- Course schedule
- Detect cycle in directed or undirected graph
- Shortest path in unweighted graph

## 10. Common mistakes
- Forgetting visited set.
- Marking visited too late in BFS.
- Mixing tree logic with graph logic and revisiting nodes forever.
- Forgetting indegree updates in topological sort.

## 11. Practice question types
- Count connected components. Example: [LeetCode 547 - Number of Provinces](https://leetcode.com/problems/number-of-provinces/)
- Grid BFS or DFS. Example: [LeetCode 200 - Number of Islands](https://leetcode.com/problems/number-of-islands/)
- Shortest path in binary matrix style. Example: [LeetCode 1091 - Shortest Path in Binary Matrix](https://leetcode.com/problems/shortest-path-in-binary-matrix/)
- Course dependency ordering. Example: [LeetCode 210 - Course Schedule II](https://leetcode.com/problems/course-schedule-ii/)
- Cycle detection. Example: [LeetCode 207 - Course Schedule](https://leetcode.com/problems/course-schedule/)

## 12. Interview answer in one line
- Use BFS for level-wise or shortest-path-in-unweighted problems, DFS for deep traversal and component or cycle exploration, and topological sort for dependency ordering.

## More interview practice
- [LeetCode 133 - Clone Graph](https://leetcode.com/problems/clone-graph/)
- [LeetCode 994 - Rotting Oranges](https://leetcode.com/problems/rotting-oranges/)
- [LeetCode 417 - Pacific Atlantic Water Flow](https://leetcode.com/problems/pacific-atlantic-water-flow/)
