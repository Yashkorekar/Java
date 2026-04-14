# Recursion and Divide and Conquer

## 1. Core idea
- Recursion solves a problem by reducing it into smaller subproblems of the same kind.
- Divide and conquer splits the problem, solves subparts, and combines the results.

## 2. Recognition signals
- The problem naturally splits into left and right halves.
- Tree recursion feels natural.
- You need sort or combine information from subproblems.
- Merge sort or quicksort style thinking fits.

## 3. Generic recursion checklist
1. What is the base case?
2. What smaller subproblem do I call recursively?
3. What do I return upward?
4. How do I combine the results?

## 4. Divide-and-conquer examples
- Merge sort
- Quick sort
- Maximum subarray divide-and-conquer version
- Count inversions
- Binary tree recursion

## 5. Merge sort template
```java
void mergeSort(int[] nums, int left, int right) {
    if (left >= right) {
        return;
    }
    int mid = left + (right - left) / 2;
    mergeSort(nums, left, mid);
    mergeSort(nums, mid + 1, right);
    merge(nums, left, mid, right);
}
```

## 6. Common mistakes
- Missing or wrong base case.
- Infinite recursion.
- Returning the wrong thing from recursive calls.
- Forgetting divide-and-conquer combine step.

## 7. Interview answer in one line
- Use recursion or divide-and-conquer when the problem naturally breaks into smaller similar parts and the combined result is manageable.

## More interview practice
- [LeetCode 50 - Pow(x, n)](https://leetcode.com/problems/powx-n/)
- [LeetCode 241 - Different Ways to Add Parentheses](https://leetcode.com/problems/different-ways-to-add-parentheses/)
- [LeetCode 912 - Sort an Array](https://leetcode.com/problems/sort-an-array/)
