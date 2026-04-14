# String Algorithms: KMP, Rolling Hash, and Manacher

## 1. Why this file exists
- Basic string problems often use sliding window or hashing.
- Harder string problems may need actual string algorithms.

## 2. High-yield string families
- KMP for pattern matching.
- Rolling hash for substring comparison.
- Z-function for prefix-based matching.
- Manacher for longest palindromic substring in linear time.

## 3. Recognition signals
- Need repeated pattern search in large text.
- Need fast substring equality checks.
- Need prefix matching across all positions.
- Need optimized palindrome processing.

## 4. KMP
- Preprocess pattern using LPS array.
- Avoids re-checking characters unnecessarily.

## 5. Rolling hash
- Represents substring hashes numerically.
- Common in repeated substring, duplicate substring, and Rabin-Karp style matching.

## 6. Manacher
- Specialized linear-time palindrome algorithm.
- Rare in interviews, but high value for advanced string rounds.

## 7. Common question types
- Implement substring search. Example: [LeetCode 28 - Find the Index of the First Occurrence in a String](https://leetcode.com/problems/find-the-index-of-the-first-occurrence-in-a-string/)
- Repeated string or repeated pattern detection. Examples: [LeetCode 459 - Repeated Substring Pattern](https://leetcode.com/problems/repeated-substring-pattern/), [LeetCode 686 - Repeated String Match](https://leetcode.com/problems/repeated-string-match/)
- Longest happy prefix or prefix-suffix problems. Example: [LeetCode 1392 - Longest Happy Prefix](https://leetcode.com/problems/longest-happy-prefix/)
- Longest duplicated substring. Example: [LeetCode 1044 - Longest Duplicate Substring](https://leetcode.com/problems/longest-duplicate-substring/)
- Longest palindromic substring advanced variant. Example: [LeetCode 5 - Longest Palindromic Substring](https://leetcode.com/problems/longest-palindromic-substring/)

## 8. Common mistakes
- Using advanced string algorithms when simpler hash map or sliding window is enough.
- Mishandling indices in KMP prefix table.
- Ignoring hash collision risk in rolling hash.

## 9. Interview answer in one line
- Advanced string algorithms matter when simple scanning becomes too slow and the structure of prefixes, substrings, or palindromes must be exploited directly.

## More interview practice
- [LeetCode 214 - Shortest Palindrome](https://leetcode.com/problems/shortest-palindrome/)
- [LeetCode 647 - Palindromic Substrings](https://leetcode.com/problems/palindromic-substrings/)
- [LeetCode 1316 - Distinct Echo Substrings](https://leetcode.com/problems/distinct-echo-substrings/)
