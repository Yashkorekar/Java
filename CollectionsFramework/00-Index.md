# Java Collections Framework Track

This folder contains runnable Q&A-style demos for the most common collections and interview traps.

## Suggested order
1. `ListArrayListLinkedListQnA.java`
2. `SetHashSetTreeSetQnA.java`
3. `MapHashMapTreeMapQnA.java`
4. `LinkedHashMapQnA.java`
5. `QueueDequePriorityQueueLinkedListQnA.java`
6. `ComparableVsComparatorQnA.java`
7. `IteratorsEnhancedForForEachQnA.java`
8. `CollectionsUtilityQnA.java`
9. `ConcurrentCollectionsQnA.java`

## What this folder covers well
- List, Set, Map, Queue, Deque, PriorityQueue fundamentals
- ArrayList vs LinkedList tradeoffs
- HashSet/TreeSet and HashMap/TreeMap uniqueness/order rules
- Comparable vs Comparator, sorting, tie-breakers
- iterator safety, fail-fast behavior, removal patterns
- core `Collections` utility methods and immutable/view traps
- `LinkedHashMap` ordering and LRU-style cache pattern
- concurrent collections that show up in interviews

## Still useful but outside this folder
- Stream API and collectors
- deeper collection internals and JVM implementation details
- advanced concurrency patterns beyond collection choice

## How to use these notes
- Run the files and compare the printed output to your mental model.
- For every collection type, be able to answer: ordering, duplicates, null handling, complexity, and main trap.
- If a demo mentions fail-fast or weakly consistent iteration, explain the difference in your own words.