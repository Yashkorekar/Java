# Sliding Window

## 1. Core idea
- Sliding window is used for contiguous subarray or substring problems.
- Instead of recomputing from scratch for every range, you expand and shrink a moving window.

## 2. When to use it
- The problem talks about subarray, substring, or contiguous range.
- Need longest or shortest valid window.
- Need count or max or min over a moving range.
- Window condition can be updated incrementally.

## 3. Recognition signals
- Contiguous sequence is important.
- The question says at most `k`, exactly `k`, minimum window, maximum window, or fixed-size window.
- You can add one element to the right and remove one from the left to update the answer.

## 4. Fixed-size window template
```java
int maxSumWindow(int[] nums, int k) {
    int sum = 0;
    int best = Integer.MIN_VALUE;

    for (int right = 0; right < nums.length; right++) {
        sum += nums[right];

        if (right >= k) {
            sum -= nums[right - k];
        }

        if (right >= k - 1) {
            best = Math.max(best, sum);
        }
    }
    return best;
}
```

## 5. Variable-size window template
```java
int longestAtMostKDistinct(String s, int k) {
    Map<Character, Integer> freq = new HashMap<>();
    int left = 0;
    int best = 0;

    for (int right = 0; right < s.length(); right++) {
        char c = s.charAt(right);
        freq.put(c, freq.getOrDefault(c, 0) + 1);

        while (freq.size() > k) {
            char remove = s.charAt(left++);
            freq.put(remove, freq.get(remove) - 1);
            if (freq.get(remove) == 0) {
                freq.remove(remove);
            }
        }

        best = Math.max(best, right - left + 1);
    }
    return best;
}
```

## 6. Common variations
- Maximum sum of size `k`
- Longest substring without repeating characters
- Longest substring with at most `k` distinct characters
- Minimum size subarray with sum at least target
- Minimum window substring
- Number of subarrays with some bounded property

## 7. Common mistakes
- Using sliding window when the range is not contiguous.
- Forgetting to shrink the window in a `while` loop when the condition breaks.
- Confusing exact `k` with at most `k`.
- Bad frequency cleanup causing wrong map size.

## 8. Helpful trick
- Many exact `k` questions can be solved as:
  - answer for at most `k`
  - minus answer for at most `k - 1`

## 9. Practice question types
- Fixed-size max sum window.
- Longest unique substring.
- Longest repeating character replacement style.
- Minimum window covering required characters.
- Count subarrays with product or sum constraints.

## 10. Interview answer in one line
- Sliding window is for contiguous range problems where the answer can be updated by expanding right and shrinking left.
