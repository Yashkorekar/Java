# Linked List Patterns

## 1. Why this pattern matters
- Linked list problems look simple, but many interviews use them to test pointer control.
- The main challenge is pointer movement, not syntax.

## 2. Recognition signals
- Singly or doubly linked list input.
- Need reverse, merge, detect cycle, delete node, or find middle.
- Need O(1) extra space pointer manipulation.

## 3. Core patterns
- Reverse linked list
- Slow and fast pointers
- Dummy node for insert or merge logic
- Merge two sorted lists
- Split and reconnect lists

## 4. Reverse list template
```java
ListNode reverse(ListNode head) {
    ListNode prev = null;
    ListNode curr = head;

    while (curr != null) {
        ListNode next = curr.next;
        curr.next = prev;
        prev = curr;
        curr = next;
    }
    return prev;
}
```

## 5. Slow and fast pointer template
```java
ListNode middle(ListNode head) {
    ListNode slow = head;
    ListNode fast = head;

    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
    }
    return slow;
}
```

## 6. Common variations
- Reverse list
- Reverse list in groups
- Merge two sorted lists
- Remove nth node from end
- Detect cycle and cycle start
- Palindrome linked list
- Reorder list
- Copy list with random pointer

## 7. Common mistakes
- Losing `next` pointer before re-linking.
- Forgetting dummy node in edge-heavy insert or delete problems.
- Off-by-one mistakes in fast and slow pointer loops.
- Not considering empty list or one-node list.

## 8. Interview answer in one line
- Linked list problems are usually solved by careful pointer movement, especially reverse, dummy node, and slow-fast pointer patterns.

## More interview practice
### Must do
- [LeetCode 206 - Reverse Linked List](https://leetcode.com/problems/reverse-linked-list/) (Easy)

### Very common
- [LeetCode 19 - Remove Nth Node From End of List](https://leetcode.com/problems/remove-nth-node-from-end-of-list/) (Medium)

### Good follow-up
- [LeetCode 138 - Copy List with Random Pointer](https://leetcode.com/problems/copy-list-with-random-pointer/) (Medium)
