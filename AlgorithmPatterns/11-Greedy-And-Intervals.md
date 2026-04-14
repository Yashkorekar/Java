# Greedy and Intervals

## 1. Core idea
- Greedy means choosing the locally best step with the hope and proof that it leads to a global optimum.
- Interval problems often rely on sorting by start or end time and then making greedy decisions.

## 2. When to use greedy
- The problem asks for minimum or maximum result and local best choices stay valid.
- You can sort items and make one-pass decisions.
- Need merge or select intervals efficiently.

## 3. Recognition signals
- Interval merging or overlap.
- Earliest finish time matters.
- Need minimum arrows, meeting rooms, non-overlapping intervals, task scheduling style decisions.
- DP feels possible but greedy may be simpler.

## 4. Merge intervals template
```java
int[][] merge(int[][] intervals) {
    Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));
    List<int[]> merged = new ArrayList<>();

    for (int[] interval : intervals) {
        if (merged.isEmpty() || merged.get(merged.size() - 1)[1] < interval[0]) {
            merged.add(interval.clone());
        } else {
            merged.get(merged.size() - 1)[1] = Math.max(merged.get(merged.size() - 1)[1], interval[1]);
        }
    }
    return merged.toArray(new int[0][]);
}
```

## 5. Non-overlapping selection idea
- Sort by end time.
- Keep the interval that ends earliest when overlap matters.
- This is a very common greedy proof pattern.

## 6. Common variations
- Merge intervals
- Insert interval
- Minimum number of intervals to remove
- Meeting rooms
- Partition labels style greedy scanning
- Jump game reachability
- Gas station style greedy proof problem

## 7. Common mistakes
- Sorting by wrong field.
- Using greedy without being able to justify why it works.
- Forgetting edge cases when intervals just touch.
- Confusing interval overlap condition.

## 8. Practice question types
- Merge intervals. Example: [LeetCode 56 - Merge Intervals](https://leetcode.com/problems/merge-intervals/)
- Insert interval. Example: [LeetCode 57 - Insert Interval](https://leetcode.com/problems/insert-interval/)
- Non-overlapping intervals. Example: [LeetCode 435 - Non-overlapping Intervals](https://leetcode.com/problems/non-overlapping-intervals/)
- Meeting rooms. Example: [LeetCode 253 - Meeting Rooms II](https://leetcode.com/problems/meeting-rooms-ii/)
- Minimum arrows to burst balloons style. Example: [LeetCode 452 - Minimum Number of Arrows to Burst Balloons](https://leetcode.com/problems/minimum-number-of-arrows-to-burst-balloons/)
- Jump game. Example: [LeetCode 55 - Jump Game](https://leetcode.com/problems/jump-game/)

## 9. Interview answer in one line
- Greedy works when a locally best decision can be proven to preserve a globally optimal answer, and interval problems often become greedy after sorting.
