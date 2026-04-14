# Trie, Union Find, and Bit Manipulation

## 1. Why this file exists
- These patterns are not in every beginner question, but they appear often enough in good DSA rounds that you should know the basics.

## 2. Trie

### When to use it
- Prefix search.
- Word dictionary.
- Autocomplete.
- Replace words by prefix.

### Recognition signals
- The problem keeps mentioning prefixes.
- Need to store many strings for fast prefix lookup.

### Simple trie template idea
```java
class TrieNode {
    TrieNode[] children = new TrieNode[26];
    boolean isWord;
}
```

### Common questions
- Insert and search word. Example: [LeetCode 208 - Implement Trie (Prefix Tree)](https://leetcode.com/problems/implement-trie-prefix-tree/)
- Starts with prefix. Example: [LeetCode 208 - Implement Trie (Prefix Tree)](https://leetcode.com/problems/implement-trie-prefix-tree/)
- Word search with dictionary. Example: [LeetCode 212 - Word Search II](https://leetcode.com/problems/word-search-ii/)

## 3. Union Find / Disjoint Set Union

### When to use it
- Connectivity under edge additions.
- Number of connected components.
- Cycle detection in undirected graph.
- Group merging problems.

### Recognition signals
- Need to repeatedly connect items and ask whether they belong to same group.
- Graph connectivity but DFS or BFS each time would be too expensive.

### Template
```java
class DSU {
    int[] parent;
    int[] rank;

    DSU(int n) {
        parent = new int[n];
        rank = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }
    }

    int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }

    boolean union(int a, int b) {
        int pa = find(a);
        int pb = find(b);
        if (pa == pb) {
            return false;
        }
        if (rank[pa] < rank[pb]) {
            parent[pa] = pb;
        } else if (rank[pb] < rank[pa]) {
            parent[pb] = pa;
        } else {
            parent[pb] = pa;
            rank[pa]++;
        }
        return true;
    }
}
```

### Common questions
- Number of provinces. Example: [LeetCode 547 - Number of Provinces](https://leetcode.com/problems/number-of-provinces/)
- Redundant connection. Example: [LeetCode 684 - Redundant Connection](https://leetcode.com/problems/redundant-connection/)
- Accounts merging style problems. Example: [LeetCode 721 - Accounts Merge](https://leetcode.com/problems/accounts-merge/)

## 4. Bit manipulation

### When to use it
- Need set membership in compact form.
- Need parity, powers of two, subsets by bitmask.
- XOR tricks.

### High-yield facts
- `x & 1` checks odd or even.
- `x << 1` doubles in simple arithmetic interpretation.
- `x >> 1` halves in simple arithmetic interpretation.
- `x & (x - 1)` removes the lowest set bit.
- A power of two has exactly one set bit.

### Common questions
- Single number with XOR. Example: [LeetCode 136 - Single Number](https://leetcode.com/problems/single-number/)
- Count set bits. Example: [LeetCode 338 - Counting Bits](https://leetcode.com/problems/counting-bits/)
- Generate subsets using bitmask. Example: [LeetCode 78 - Subsets](https://leetcode.com/problems/subsets/)
- Power of two checks. Example: [LeetCode 231 - Power of Two](https://leetcode.com/problems/power-of-two/)

## 5. Common mistakes
- Choosing trie when hash set is enough.
- Forgetting path compression in DSU.
- Using bit tricks without understanding signed behavior.

## 6. Interview answer in one line
- Trie is for prefix-based string lookup, Union Find is for dynamic connectivity, and bit manipulation is for compact state and XOR-style tricks.

## More interview practice
- Trie: [LeetCode 1268 - Search Suggestions System](https://leetcode.com/problems/search-suggestions-system/)
- Trie: [LeetCode 648 - Replace Words](https://leetcode.com/problems/replace-words/)
- Union Find: [LeetCode 1319 - Number of Operations to Make Network Connected](https://leetcode.com/problems/number-of-operations-to-make-network-connected/)
- Union Find: [LeetCode 1202 - Smallest String With Swaps](https://leetcode.com/problems/smallest-string-with-swaps/)
- Bit manipulation: [LeetCode 260 - Single Number III](https://leetcode.com/problems/single-number-iii/)
- Bit manipulation: [LeetCode 190 - Reverse Bits](https://leetcode.com/problems/reverse-bits/)
