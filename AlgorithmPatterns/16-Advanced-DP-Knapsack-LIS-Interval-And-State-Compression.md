# Advanced DP: Knapsack, LIS, Interval, and State Compression

## 1. Why this file exists
- Basic DP is not enough for many medium and hard questions.
- Advanced DP mostly means recognizing the family correctly.

## 2. High-yield DP families
- Knapsack DP
- LIS-style DP
- Interval DP
- State compression / bitmask DP
- Digit DP

## 3. Recognition signals
- Choice to take or skip items under capacity: knapsack.
- Longest increasing or valid subsequence: LIS family.
- Answer depends on interval boundaries: interval DP.
- State is a subset of used items: bitmask DP.

## 4. Knapsack idea
- State often includes index and remaining capacity.
- Common problems: subset sum, partition equal subset sum, coin change variations.

## 5. LIS idea
- State usually tracks best subsequence ending at index.
- Variants include patience sorting optimization or segment tree versions.

## 6. Interval DP idea
- State often looks like `dp[left][right]`.
- Common in bursting balloons, matrix chain multiplication style, palindrome partitioning cost, merge cost problems.

## 7. Bitmask DP idea
- Use a bitmask to represent chosen or visited elements.
- Common in traveling-salesman-style subsets, assignment problems, and shortest superstring style questions.

## 8. Common mistakes
- Choosing the wrong state size and creating huge memory usage.
- Using DP before simpler greedy or graph solution is checked.
- Not understanding whether order matters.

## 9. Interview answer in one line
- Advanced DP starts when basic one-dimensional state is not enough and you need states over capacity, intervals, subsets, or subsequence structure.

## More interview practice
### Must do
- [LeetCode 416 - Partition Equal Subset Sum](https://leetcode.com/problems/partition-equal-subset-sum/) (Medium)

### Very common
- [LeetCode 312 - Burst Balloons](https://leetcode.com/problems/burst-balloons/) (Hard)

### Good follow-up
- [LeetCode 354 - Russian Doll Envelopes](https://leetcode.com/problems/russian-doll-envelopes/) (Hard)
