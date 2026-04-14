# Dynamic Programming Basics

## 1. Core idea
- DP is used when a problem has overlapping subproblems and optimal substructure.
- Instead of recomputing the same work, you store answers.

## 2. When to use it
- Count ways.
- Min or max cost.
- Longest subsequence or path with rules.
- Decision problems with repeated states.

## 3. Recognition signals
- Brute force recursion repeats the same state many times.
- Problem asks minimum, maximum, count, or feasibility.
- Current answer depends on smaller states.

## 4. How to think about DP
1. Define the state.
2. Define the transition.
3. Define base cases.
4. Choose memoization or tabulation.

## 5. Memoization template
```java
int solve(int index, int[] nums, Integer[] memo) {
    if (index >= nums.length) {
        return 0;
    }
    if (memo[index] != null) {
        return memo[index];
    }

    int take = nums[index] + solve(index + 2, nums, memo);
    int skip = solve(index + 1, nums, memo);
    return memo[index] = Math.max(take, skip);
}
```

## 6. Tabulation template
```java
int climbStairs(int n) {
    if (n <= 2) {
        return n;
    }
    int[] dp = new int[n + 1];
    dp[1] = 1;
    dp[2] = 2;
    for (int i = 3; i <= n; i++) {
        dp[i] = dp[i - 1] + dp[i - 2];
    }
    return dp[n];
}
```

## 7. High-yield DP families
- 1D DP: climbing stairs, house robber, coin change basic forms.
- 2D grid DP: unique paths, min path sum.
- Subsequence DP: longest increasing subsequence, longest common subsequence.
- Knapsack style DP.
- Partition DP.

## 8. Common mistakes
- Bad state definition.
- Mixing index meaning or dimensions.
- Forgetting base cases.
- Solving with DP when greedy or simple math is enough.
- Not reducing space when possible after understanding the full DP first.

## 9. Practice question types
- Climbing stairs. Example: [LeetCode 70 - Climbing Stairs](https://leetcode.com/problems/climbing-stairs/)
- House robber. Example: [LeetCode 198 - House Robber](https://leetcode.com/problems/house-robber/)
- Coin change. Example: [LeetCode 322 - Coin Change](https://leetcode.com/problems/coin-change/)
- Longest increasing subsequence. Example: [LeetCode 300 - Longest Increasing Subsequence](https://leetcode.com/problems/longest-increasing-subsequence/)
- Longest common subsequence. Example: [LeetCode 1143 - Longest Common Subsequence](https://leetcode.com/problems/longest-common-subsequence/)
- Edit distance. Example: [LeetCode 72 - Edit Distance](https://leetcode.com/problems/edit-distance/)
- Unique paths. Example: [LeetCode 62 - Unique Paths](https://leetcode.com/problems/unique-paths/)

## 10. Interview answer in one line
- Dynamic programming solves repeated-state optimization or counting problems by storing answers to smaller states and building from them.

## More interview practice
- [LeetCode 139 - Word Break](https://leetcode.com/problems/word-break/)
- [LeetCode 91 - Decode Ways](https://leetcode.com/problems/decode-ways/)
- [LeetCode 64 - Minimum Path Sum](https://leetcode.com/problems/minimum-path-sum/)
