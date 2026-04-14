# Monotonic Stack and Queue

## 1. Core idea
- A monotonic stack keeps elements in increasing or decreasing order.
- A monotonic deque does the same for window-based problems.
- These structures help find next greater, next smaller, previous greater, or current window maximum efficiently.

## 2. When to use it
- Next greater element or next smaller element.
- Daily temperatures style questions.
- Largest rectangle in histogram.
- Sliding window maximum.
- Need nearest bigger or smaller element on one side.

## 3. Recognition signals
- The question asks nearest greater or smaller.
- Naive solution checks left or right repeatedly.
- Need O(n) instead of O(n^2).
- Sliding maximum or minimum over windows.

## 4. Monotonic stack template
```java
int[] nextGreater(int[] nums) {
    int n = nums.length;
    int[] answer = new int[n];
    Arrays.fill(answer, -1);
    Deque<Integer> stack = new ArrayDeque<>();

    for (int i = 0; i < n; i++) {
        while (!stack.isEmpty() && nums[stack.peek()] < nums[i]) {
            answer[stack.pop()] = nums[i];
        }
        stack.push(i);
    }
    return answer;
}
```

## 5. Sliding window maximum deque template
```java
int[] maxSlidingWindow(int[] nums, int k) {
    Deque<Integer> deque = new ArrayDeque<>();
    int[] result = new int[nums.length - k + 1];
    int write = 0;

    for (int i = 0; i < nums.length; i++) {
        while (!deque.isEmpty() && deque.peekFirst() <= i - k) {
            deque.pollFirst();
        }
        while (!deque.isEmpty() && nums[deque.peekLast()] <= nums[i]) {
            deque.pollLast();
        }
        deque.offerLast(i);

        if (i >= k - 1) {
            result[write++] = nums[deque.peekFirst()];
        }
    }
    return result;
}
```

## 6. Common variations
- Next greater element
- Previous smaller element
- Daily temperatures
- Stock span
- Largest rectangle in histogram
- Trapping rain water with stack version
- Sliding window maximum with deque

## 7. Common mistakes
- Storing values when indices are needed.
- Forgetting to remove out-of-window indices in deque problems.
- Using wrong increasing vs decreasing invariant.
- Not understanding whether equality should pop or stay.

## 8. Practice question types
- Next greater element. Example: [LeetCode 496 - Next Greater Element I](https://leetcode.com/problems/next-greater-element-i/)
- Daily temperatures. Example: [LeetCode 739 - Daily Temperatures](https://leetcode.com/problems/daily-temperatures/)
- Largest histogram rectangle. Example: [LeetCode 84 - Largest Rectangle in Histogram](https://leetcode.com/problems/largest-rectangle-in-histogram/)
- Sliding window maximum. Example: [LeetCode 239 - Sliding Window Maximum](https://leetcode.com/problems/sliding-window-maximum/)
- Sum of subarray minimums style problems. Example: [LeetCode 907 - Sum of Subarray Minimums](https://leetcode.com/problems/sum-of-subarray-minimums/)

## 9. Interview answer in one line
- Monotonic stack or queue is used when you need nearest greater or smaller relationships or window extrema in linear time.

## More interview practice
- [LeetCode 503 - Next Greater Element II](https://leetcode.com/problems/next-greater-element-ii/)
- [LeetCode 901 - Online Stock Span](https://leetcode.com/problems/online-stock-span/)
- [LeetCode 862 - Shortest Subarray with Sum at Least K](https://leetcode.com/problems/shortest-subarray-with-sum-at-least-k/)
