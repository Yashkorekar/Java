# Matrix and Grid Patterns

## 1. Why this pattern matters
- Many array questions are actually matrix traversal or grid graph problems.
- Once you see rows, columns, and directional movement, the right pattern becomes clearer.

## 2. Recognition signals
- 2D grid, matrix, board, maze, island, image, or board game.
- Need to move in 4 or 8 directions.
- Need spiral order, rotate matrix, set zeroes, island count, shortest path in grid.

## 3. Common sub-patterns
- Direction-array traversal
- Matrix simulation
- Grid BFS
- Grid DFS
- In-place marking

## 4. Direction array template
```java
int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

for (int[] direction : directions) {
    int nextRow = row + direction[0];
    int nextCol = col + direction[1];
}
```

## 5. Common variations
- Number of islands
- Flood fill
- Rotting oranges
- Spiral matrix
- Rotate image
- Set matrix zeroes
- Word search
- Shortest path in binary matrix

## 6. Common mistakes
- Row and column bound errors.
- Revisiting cells because visited handling is missing.
- Using DFS when shortest path BFS is needed.
- Not deciding whether to mutate grid or keep separate visited state.

## 7. Interview answer in one line
- Matrix and grid problems usually reduce to traversal with direction arrays, simulation, or grid-as-graph BFS or DFS.

## More interview practice
### Must do
- [LeetCode 48 - Rotate Image](https://leetcode.com/problems/rotate-image/) (Medium)

### Very common
- [LeetCode 73 - Set Matrix Zeroes](https://leetcode.com/problems/set-matrix-zeroes/) (Medium)

### Good follow-up
- [LeetCode 54 - Spiral Matrix](https://leetcode.com/problems/spiral-matrix/) (Medium)
