# Heap, PriorityQueue, and Top K

## 1. Core idea
- Heap lets you repeatedly access the smallest or largest element efficiently.
- In Java, `PriorityQueue` is a min-heap by default.

## 2. When to use it
- Top K largest or smallest elements.
- Merge k sorted lists or arrays.
- Repeatedly pick the best candidate.
- Running median style problems.
- Task scheduling by priority.

## 3. Recognition signals
- Need smallest or largest repeatedly.
- Need top `k`, kth largest, kth smallest.
- Input arrives as a stream.
- Need best-first exploration.

## 4. Min-heap and max-heap in Java
```java
PriorityQueue<Integer> minHeap = new PriorityQueue<>();
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
```

## 5. Top K template
```java
int kthLargest(int[] nums, int k) {
    PriorityQueue<Integer> minHeap = new PriorityQueue<>();

    for (int num : nums) {
        minHeap.offer(num);
        if (minHeap.size() > k) {
            minHeap.poll();
        }
    }
    return minHeap.peek();
}
```

## 6. Common variations
- Kth largest element
- Top K frequent elements
- Merge k sorted lists
- Meeting rooms by end time
- Median from data stream
- Task scheduler with next available times

## 7. Common mistakes
- Forgetting Java `PriorityQueue` is min-heap by default.
- Using full sort when heap can do better.
- Not controlling heap size in top-k questions.
- Wrong comparator for custom objects.

## 8. Practice question types
- Kth largest or smallest. Example: [LeetCode 215 - Kth Largest Element in an Array](https://leetcode.com/problems/kth-largest-element-in-an-array/)
- Top K frequent words or numbers. Examples: [LeetCode 347 - Top K Frequent Elements](https://leetcode.com/problems/top-k-frequent-elements/), [LeetCode 692 - Top K Frequent Words](https://leetcode.com/problems/top-k-frequent-words/)
- Merge k sorted arrays or lists. Example: [LeetCode 23 - Merge k Sorted Lists](https://leetcode.com/problems/merge-k-sorted-lists/)
- Running median. Example: [LeetCode 295 - Find Median from Data Stream](https://leetcode.com/problems/find-median-from-data-stream/)
- Minimize cost with repeated smallest picks. Example: [LeetCode 1167 - Minimum Cost to Connect Sticks](https://leetcode.com/problems/minimum-cost-to-connect-sticks/)

## 9. Interview answer in one line
- Heap is used when you must repeatedly access or maintain the best candidate efficiently, especially for top-k and stream problems.

## More interview practice
- [LeetCode 703 - Kth Largest Element in a Stream](https://leetcode.com/problems/kth-largest-element-in-a-stream/)
- [LeetCode 373 - Find K Pairs with Smallest Sums](https://leetcode.com/problems/find-k-pairs-with-smallest-sums/)
- [LeetCode 973 - K Closest Points to Origin](https://leetcode.com/problems/k-closest-points-to-origin/)
