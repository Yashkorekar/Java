# Two Pointers

## 1. Core idea
- Two pointers means using two indices that move with a rule.
- Sometimes they move toward each other.
- Sometimes they move in the same direction at different speeds.

## 2. When to use it
- Sorted array problems with pair or triplet conditions.
- Removing duplicates in-place.
- Partitioning arrays.
- Palindrome checking.
- Linked list cycle or middle-node style questions.

## 3. Recognition signals
- Need pair sum or triplet sum in sorted data.
- Need to compare left and right ends.
- Need to move slow and fast pointers.
- Need in-place compaction or partitioning.

## 4. Opposite-direction template
```java
int[] pairSumSorted(int[] nums, int target) {
    int left = 0;
    int right = nums.length - 1;

    while (left < right) {
        int sum = nums[left] + nums[right];
        if (sum == target) {
            return new int[] {left, right};
        }
        if (sum < target) {
            left++;
        } else {
            right--;
        }
    }
    return new int[] {-1, -1};
}
```

## 5. Same-direction template
```java
int removeDuplicates(int[] nums) {
    if (nums.length == 0) {
        return 0;
    }

    int write = 1;
    for (int read = 1; read < nums.length; read++) {
        if (nums[read] != nums[read - 1]) {
            nums[write++] = nums[read];
        }
    }
    return write;
}
```

## 6. Fast and slow pointers
- Very common in linked list questions.
- Uses one slow pointer and one fast pointer.
- Typical uses:
  - detect cycle
  - find middle node
  - find start of cycle

## 7. Common variations
- Two sum in sorted array
- Three sum after sorting
- Container with most water
- Move zeros
- Remove duplicates from sorted array
- Valid palindrome with pointer skipping
- Linked list cycle detection

## 8. Common mistakes
- Forgetting the array must usually be sorted for pair-sum pointer logic.
- Wrong duplicate skipping in three-sum.
- Moving the wrong pointer after checking condition.
- Confusing two pointers with sliding window.

## 9. Practice question types
- Pair sum in sorted array.
- Three sum.
- Remove duplicates.
- Move zeros.
- Valid palindrome.
- Middle of linked list.
- Detect cycle in linked list.

## 10. Interview answer in one line
- Two pointers is useful when two moving indices can cut the search space faster than checking every pair or range.
