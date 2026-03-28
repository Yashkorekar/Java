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
- Kth largest or smallest.
- Top K frequent words or numbers.
- Merge k sorted arrays or lists.
- Running median.
- Minimize cost with repeated smallest picks.

## 9. Interview answer in one line
- Heap is used when you must repeatedly access or maintain the best candidate efficiently, especially for top-k and stream problems.
