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
- Insert and search word.
- Starts with prefix.
- Word search with dictionary.

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
- Number of provinces.
- Redundant connection.
- Accounts merging style problems.

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
- Single number with XOR.
- Count set bits.
- Generate subsets using bitmask.
- Power of two checks.

## 5. Common mistakes
- Choosing trie when hash set is enough.
- Forgetting path compression in DSU.
- Using bit tricks without understanding signed behavior.

## 6. Interview answer in one line
- Trie is for prefix-based string lookup, Union Find is for dynamic connectivity, and bit manipulation is for compact state and XOR-style tricks.
