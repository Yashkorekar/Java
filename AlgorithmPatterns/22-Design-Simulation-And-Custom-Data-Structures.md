# Design, Simulation, and Custom Data Structures

## 1. Why this file exists
- Some LeetCode questions are mainly about implementing behavior correctly, not discovering one pure algorithm trick.

## 2. Recognition signals
- The prompt says “design a data structure” or “implement class with operations”.
- Many operations must be efficient together.
- Correct state transitions matter more than one formula.

## 3. High-yield design problems
- LRU cache
- LFU cache
- Min stack
- Time-based key-value store
- Randomized set
- Browser history / iterator / stack with max or min
- Snapshot array

## 4. Simulation problems
- Process commands step by step.
- Use the right data structure and careful state updates.
- Examples: task simulation, robot movement, browser navigation, calendar booking.

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
