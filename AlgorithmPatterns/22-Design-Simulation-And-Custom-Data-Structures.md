# Design, Simulation, and Custom Data Structures

## 1. Why this file exists
- Some LeetCode questions are mainly about implementing behavior correctly, not discovering one pure algorithm trick.

## 2. Recognition signals
- The prompt says “design a data structure” or “implement class with operations”.
- Many operations must be efficient together.
- Correct state transitions matter more than one formula.

## 3. High-yield design problems
- LRU cache. Example: [LeetCode 146 - LRU Cache](https://leetcode.com/problems/lru-cache/) (Medium)
- LFU cache. Example: [LeetCode 460 - LFU Cache](https://leetcode.com/problems/lfu-cache/) (Hard)
- Min stack. Example: [LeetCode 155 - Min Stack](https://leetcode.com/problems/min-stack/) (Easy)
- Time-based key-value store. Example: [LeetCode 981 - Time Based Key-Value Store](https://leetcode.com/problems/time-based-key-value-store/) (Medium)
- Randomized set. Example: [LeetCode 380 - Insert Delete GetRandom O(1)](https://leetcode.com/problems/insert-delete-getrandom-o1/) (Medium)
- Browser history / iterator / stack with max or min. Examples: [LeetCode 1472 - Design Browser History](https://leetcode.com/problems/design-browser-history/) (Medium), [LeetCode 173 - Binary Search Tree Iterator](https://leetcode.com/problems/binary-search-tree-iterator/) (Medium)
- Snapshot array. Example: [LeetCode 1146 - Snapshot Array](https://leetcode.com/problems/snapshot-array/) (Medium)

## 4. Simulation problems
- Process commands step by step. Example: [LeetCode 682 - Baseball Game](https://leetcode.com/problems/baseball-game/) (Easy)
- Use the right data structure and careful state updates. Example: [LeetCode 874 - Walking Robot Simulation](https://leetcode.com/problems/walking-robot-simulation/) (Medium)
- Examples: task simulation, robot movement, browser navigation, calendar booking. Representative problems: [LeetCode 621 - Task Scheduler](https://leetcode.com/problems/task-scheduler/) (Medium), [LeetCode 874 - Walking Robot Simulation](https://leetcode.com/problems/walking-robot-simulation/) (Medium), [LeetCode 1472 - Design Browser History](https://leetcode.com/problems/design-browser-history/) (Medium), [LeetCode 729 - My Calendar I](https://leetcode.com/problems/my-calendar-i/) (Medium)

## 5. Common data structure combinations
- HashMap + doubly linked list for LRU.
- HashMap + heap or frequency lists for LFU.
- Stack + auxiliary stack for min stack.
- Queue + maps for scheduling and cooldown simulation.

## 6. Common mistakes
- Focusing only on one operation and breaking another operation's complexity.
- Forgetting edge cases like duplicate keys, missing values, or empty structure operations.
- Not writing down the required time complexity before coding.

## 7. Interview answer in one line
- Design and simulation problems are solved by choosing the right underlying data structures so all required operations stay correct and efficient together.

## More interview practice
### Must do
- [LeetCode 355 - Design Twitter](https://leetcode.com/problems/design-twitter/) (Medium)

### Very common
- [LeetCode 729 - My Calendar I](https://leetcode.com/problems/my-calendar-i/) (Medium)

### Good follow-up
- [LeetCode 706 - Design HashMap](https://leetcode.com/problems/design-hashmap/) (Easy)
