# Prefix Sum and Hashing

## 1. Core idea
- Prefix sum helps compute range totals quickly.
- Hashing helps remember seen states or frequencies.
- Together, they solve many subarray and counting problems in linear time.

## 2. When to use it
- Subarray sum questions.
- Count subarrays with exact property.
- Longest subarray with target sum.
- Balance problems like equal zeros and ones.
- Need fast range sum queries.

## 3. Prefix sum formula
- `prefix[i]` stores sum from start up to index `i`.
- Sum from `l` to `r` equals:
  - `prefix[r] - prefix[l - 1]`
- With zero-based clean handling, often use prefix array of size `n + 1`.

## 4. Range-sum template
```java
int[] buildPrefix(int[] nums) {
    int[] prefix = new int[nums.length + 1];
    for (int i = 0; i < nums.length; i++) {
        prefix[i + 1] = prefix[i] + nums[i];
    }
    return prefix;
}

int rangeSum(int[] prefix, int left, int right) {
    return prefix[right + 1] - prefix[left];
}
```

## 5. Subarray sum equals target template
```java
int countSubarraysWithSum(int[] nums, int target) {
    Map<Integer, Integer> freq = new HashMap<>();
    freq.put(0, 1);

    int prefix = 0;
    int count = 0;

    for (int num : nums) {
        prefix += num;
        count += freq.getOrDefault(prefix - target, 0);
        freq.put(prefix, freq.getOrDefault(prefix, 0) + 1);
    }
    return count;
}
```

## 6. Why hashing matters here
- Hash map stores how often a prefix sum has appeared.
- If current prefix is `x` and target is `k`, then any earlier prefix `x - k` forms a valid subarray.

## 7. Common variations
- Count subarrays with sum `k`
- Longest subarray with sum `k`
- Equal number of 0 and 1
- Subarray divisibility problems
- Prefix XOR versions

## 8. Difference array note
- Difference array is useful when many range updates happen.
- It is less common than prefix sum in interviews, but still high value.
- Idea: mark start and end+1 changes, then rebuild actual values with prefix accumulation.

## 9. Common mistakes
- Forgetting to initialize map with `0 -> 1`.
- Using sliding window when negative numbers are allowed and exact sum is needed.
- Updating hash map in the wrong order.

## 10. Practice question types
- Count subarrays with exact sum. Example: [LeetCode 560 - Subarray Sum Equals K](https://leetcode.com/problems/subarray-sum-equals-k/)
- Longest subarray with target sum. Example: [LeetCode 325 - Maximum Size Subarray Sum Equals k](https://leetcode.com/problems/maximum-size-subarray-sum-equals-k/)
- Number of subarrays divisible by `k`. Example: [LeetCode 974 - Subarray Sums Divisible by K](https://leetcode.com/problems/subarray-sums-divisible-by-k/)
- Equal zeros and ones. Example: [LeetCode 525 - Contiguous Array](https://leetcode.com/problems/contiguous-array/)
- Immutable range sum query. Example: [LeetCode 303 - Range Sum Query - Immutable](https://leetcode.com/problems/range-sum-query-immutable/)

## 11. Interview answer in one line
- Prefix sum and hashing are used when a subarray condition can be rewritten using cumulative state and fast lookup of earlier states.
