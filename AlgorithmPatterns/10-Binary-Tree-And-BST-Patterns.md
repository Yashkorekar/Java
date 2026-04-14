# Binary Tree and BST Patterns

## 1. Core idea
- Tree questions are usually recursive.
- The main question is what information a recursive call should return.
- For BSTs, the ordering rule gives extra power.

## 2. When to use tree DFS
- Height, depth, balance, diameter.
- Path sum.
- Lowest common ancestor.
- Validate BST.
- Serialize or deserialize style questions.

## 3. When to use tree BFS
- Level-order traversal.
- Right side view.
- Minimum depth.
- Level averages.

## 4. Recognition signals
- Node with left and right children.
- Need property from subtrees.
- Need root-left-right or left-right-root processing.
- BST ordering can prune search.

## 5. Basic DFS recursion template
```java
int height(TreeNode root) {
    if (root == null) {
        return 0;
    }
    return 1 + Math.max(height(root.left), height(root.right));
}
```

## 6. Level-order BFS template
```java
List<List<Integer>> levelOrder(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) {
        return result;
    }

    Queue<TreeNode> queue = new ArrayDeque<>();
    queue.offer(root);

    while (!queue.isEmpty()) {
        int size = queue.size();
        List<Integer> level = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            TreeNode node = queue.poll();
            level.add(node.val);
            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }
        result.add(level);
    }
    return result;
}
```

## 7. BST-specific pattern
- Left subtree values are smaller.
- Right subtree values are larger.
- Inorder traversal gives sorted values.
- Search, insert, and validate become easier because of ordering.

## 8. Common variations
- Maximum depth
- Same tree or symmetric tree
- Invert tree
- Validate BST
- Kth smallest in BST
- Lowest common ancestor
- Diameter of binary tree
- Path sum

## 9. Common mistakes
- Forgetting null base case.
- Using global variables carelessly.
- Mixing node count with edge count in height or diameter.
- For BST validation, checking only direct children instead of full valid range.

## 10. Practice question types
- Traversals. Example: [LeetCode 94 - Binary Tree Inorder Traversal](https://leetcode.com/problems/binary-tree-inorder-traversal/)
- Height or balance. Examples: [LeetCode 104 - Maximum Depth of Binary Tree](https://leetcode.com/problems/maximum-depth-of-binary-tree/), [LeetCode 110 - Balanced Binary Tree](https://leetcode.com/problems/balanced-binary-tree/)
- Validate BST. Example: [LeetCode 98 - Validate Binary Search Tree](https://leetcode.com/problems/validate-binary-search-tree/)
- Diameter. Example: [LeetCode 543 - Diameter of Binary Tree](https://leetcode.com/problems/diameter-of-binary-tree/)
- LCA. Example: [LeetCode 236 - Lowest Common Ancestor of a Binary Tree](https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-tree/)
- Level-order traversal. Example: [LeetCode 102 - Binary Tree Level Order Traversal](https://leetcode.com/problems/binary-tree-level-order-traversal/)
- Kth smallest in BST. Example: [LeetCode 230 - Kth Smallest Element in a BST](https://leetcode.com/problems/kth-smallest-element-in-a-bst/)

## 11. Interview answer in one line
- Tree problems are usually solved by deciding what each recursive call returns and how to combine left and right subtree results.
