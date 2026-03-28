# Binary Search

## 1. Core idea
- Binary search halves the search space each step.
- It works when the answer space is ordered and you can decide whether to go left or right.

## 2. When to use it
- Sorted array or sorted list.
- Need first occurrence, last occurrence, lower bound, upper bound.
- Rotated sorted array style questions.
- Monotonic answer questions like minimum speed, minimum capacity, maximum feasible value.

## 3. Recognition signals
- The input is sorted.
- The condition changes from false to true or true to false at some point.
- The question asks for first valid or last valid answer.
- A brute-force answer can be checked by a helper function and the answer space is monotonic.

## 4. Basic array template
```java
int binarySearch(int[] nums, int target) {
    int left = 0;
    int right = nums.length - 1;

    while (left <= right) {
        int mid = left + (right - left) / 2;

        if (nums[mid] == target) {
            return mid;
        }
        if (nums[mid] < target) {
            left = mid + 1;
        } else {
            right = mid - 1;
        }
    }
    return -1;
}
```

## 5. Lower bound template
- Lower bound means first index where value is greater than or equal to target.
```java
int lowerBound(int[] nums, int target) {
    int left = 0;
    int right = nums.length;

    while (left < right) {
        int mid = left + (right - left) / 2;
        if (nums[mid] < target) {
            left = mid + 1;
        } else {
            right = mid;
        }
    }
    return left;
}
```

## 6. Binary search on answer
- Sometimes you do not search inside the array itself.
- You search the answer range.
- Example pattern:
  - choose a candidate answer `mid`
  - check if that answer is possible
  - if possible, search smaller or larger depending on the goal
```java
int searchAnswer(int low, int high) {
    int answer = high;
    while (low <= high) {
        int mid = low + (high - low) / 2;
        if (isPossible(mid)) {
            answer = mid;
            high = mid - 1;
        } else {
            low = mid + 1;
        }
    }
    return answer;
}
```

## 7. Common variations
- Search insert position
- First occurrence / last occurrence
- Search in rotated sorted array
- Peak element
- Search in 2D sorted matrix
- Minimum feasible answer problems

## 8. Common mistakes
- Wrong loop condition: `left <= right` vs `left < right`.
- Mid overflow in some languages. In Java use `left + (right - left) / 2`.
- Confusing lower bound and upper bound.
- Updating the wrong side after checking `mid`.
- Forgetting binary search only works with ordered or monotonic structure.

## 9. Practice question types
- Find a target in sorted array.
- Find first and last position of target.
- Find insertion point.
- Search in rotated sorted array.
- Find minimum in rotated sorted array.
- Minimum speed or capacity to finish work.

## 10. Interview answer in one line
- Binary search is used when the search space is ordered or monotonic and each step can eliminate half the possibilities.
