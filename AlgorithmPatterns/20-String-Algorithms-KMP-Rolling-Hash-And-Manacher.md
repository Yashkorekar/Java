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
- Implement substring search.
- Repeated string or repeated pattern detection.
- Longest happy prefix or prefix-suffix problems.
- Longest duplicated substring.
- Longest palindromic substring advanced variant.

## 8. Common mistakes
- Using advanced string algorithms when simpler hash map or sliding window is enough.
- Mishandling indices in KMP prefix table.
- Ignoring hash collision risk in rolling hash.

## 9. Interview answer in one line
- Advanced string algorithms matter when simple scanning becomes too slow and the structure of prefixes, substrings, or palindromes must be exploited directly.
