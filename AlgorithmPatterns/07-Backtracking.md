# Backtracking

## 1. Core idea
- Backtracking explores all valid choices step by step.
- It chooses, recurses, and then undoes the choice.
- It is usually used when the question asks for all combinations, all permutations, or all valid configurations.

## 2. When to use it
- Generate all subsets.
- Generate all permutations.
- Combination sum style problems.
- N-Queens, sudoku, word search, partitioning.

## 3. Recognition signals
- The question asks for all possible answers.
- Need valid arrangement or combination.
- Choices branch recursively.
- There are constraints to prune bad paths.

## 4. Generic template
```java
void backtrack(List<Integer> path, int start, int[] nums, List<List<Integer>> result) {
    result.add(new ArrayList<>(path));

    for (int i = start; i < nums.length; i++) {
        path.add(nums[i]);
        backtrack(path, i + 1, nums, result);
        path.remove(path.size() - 1);
    }
}
```

## 5. Core pattern
- Choose
- Explore
- Undo

## 6. Pruning
- Stop recursion early if partial choice already violates a rule.
- Sort input when duplicates or bounded sum pruning helps.
- Good pruning is what makes backtracking practical.

## 7. Common variations
- Subsets
- Permutations
- Combination sum
- Combination choose k
- Letter combinations
- N-Queens
- Word search in grid

## 8. Common mistakes
- Forgetting to copy the current path before storing it.
- Forgetting to undo the last choice.
- Using wrong recursion start index.
- Handling duplicates incorrectly.

## 9. Practice question types
- All subsets. Example: [LeetCode 78 - Subsets](https://leetcode.com/problems/subsets/) (Medium)
- All permutations. Example: [LeetCode 46 - Permutations](https://leetcode.com/problems/permutations/) (Medium)
- Combination sum. Example: [LeetCode 39 - Combination Sum](https://leetcode.com/problems/combination-sum/) (Medium)
- Palindrome partitioning. Example: [LeetCode 131 - Palindrome Partitioning](https://leetcode.com/problems/palindrome-partitioning/) (Medium)
- N-Queens. Example: [LeetCode 51 - N-Queens](https://leetcode.com/problems/n-queens/) (Hard)
- Word search. Example: [LeetCode 79 - Word Search](https://leetcode.com/problems/word-search/) (Medium)

## 10. Interview answer in one line
- Backtracking is for exploring all valid choices with recursion and pruning invalid paths early.

## More interview practice
### Must do
- [LeetCode 22 - Generate Parentheses](https://leetcode.com/problems/generate-parentheses/) (Medium)

### Very common
- [LeetCode 17 - Letter Combinations of a Phone Number](https://leetcode.com/problems/letter-combinations-of-a-phone-number/) (Medium)

### Good follow-up
- [LeetCode 40 - Combination Sum II](https://leetcode.com/problems/combination-sum-ii/) (Medium)
