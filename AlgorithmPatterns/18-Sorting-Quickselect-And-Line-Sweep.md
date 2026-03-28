# Sorting, Quickselect, and Line Sweep

## 1. Core idea
- Sorting is often not the final solution, but it is the step that makes the real solution simple.
- Quickselect finds kth order statistics faster than full sorting on average.
- Line sweep is for events over time or coordinates.

## 2. Recognition signals
- Sorting makes pairing or interval logic easier.
- Need kth largest or kth smallest without full order.
- Need to process events as positions or times change.

## 3. Quickselect
- Average O(n) kth element selection.
- Related to quicksort partitioning.
- Common for kth largest without maintaining a heap.

## 4. Line sweep
- Convert starts and ends into events.
- Sort events.
- Walk through them while maintaining active state.

## 5. Common variations
- Kth largest element
- Top intervals overlap
- Meeting rooms timeline
- Skyline-style event problems
- Merge intervals after sorting
- Count inversions using merge sort variant

## 6. Common mistakes
- Forgetting stable handling when start and end have same coordinate.
- Using full sort when quickselect or heap is enough.
- Bad comparator logic.

## 7. Interview answer in one line
- Sorting often unlocks the real pattern, quickselect handles kth order statistics, and line sweep handles event-driven interval or coordinate problems.
