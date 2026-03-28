# JVM Memory And Garbage Collection

This file explains JVM memory areas and garbage collection at interview depth, including young generation, survivor spaces, old generation, metaspace, object allocation, promotion, and collector trade-offs.

## 1. Why interviewers ask GC questions

GC questions test whether you understand:
- object lifetime
- heap pressure
- pause behavior
- memory leaks vs memory retention
- throughput vs latency trade-offs
- production debugging

At a senior level, interviewers usually do not want “GC removes unused objects.”
They want to hear how objects are allocated, how they survive, when they get promoted, what causes pauses, and how you would investigate a memory problem in production.

## 2. Big-picture JVM memory map

Java memory is not just “stack and heap.” A stronger picture is:

| Area | What it stores |
| --- | --- |
| Thread stack | One stack per thread, stores frames, local variables, method call state |
| Program counter | Current instruction pointer for each thread |
| Native stack | Used for native calls depending on JVM implementation |
| Heap | Objects and arrays |
| Young generation | New objects, usually Eden + survivor spaces |
| Old generation | Long-lived objects |
| Metaspace | Class metadata, replaces PermGen since Java 8 |
| Code cache | JIT-compiled machine code |
| Direct/native memory | Off-heap memory like direct byte buffers |

Important correction:
- class metadata is in metaspace
- object instances are in heap
- static field data is associated with class objects and practically reachable from heap structures, not stored as if it were normal stack data

## 3. Heap generations: new, survivor, old

When people say “new space” and “old space” in Java interviews, they usually mean generational heap organization.

### Young generation
Also called new generation.

Young generation is typically divided into:
- Eden
- Survivor From
- Survivor To

Often the two survivor spaces are just called `S0` and `S1`.

### Old generation
Also called tenured generation.

This stores objects that survived enough young-generation collections or were promoted for other reasons.

### Important mental model
- most objects die young
- because of that, Java optimizes collection of short-lived objects heavily
- this is the whole reason generational GC exists

## 4. Eden, survivor spaces, and object aging

This is the exact topic you asked for: old/new spaces and how objects move.

### Eden
- most newly created objects start here
- allocation in Eden is usually very cheap
- the JVM often uses bump-pointer allocation inside thread-local buffers

### Survivor spaces
- there are usually two survivor spaces
- only one acts as the source at a time, the other acts as destination
- after a young GC, surviving objects are copied from Eden and current survivor to the other survivor space

### Aging
- each time an object survives a young GC, its age increases
- once it reaches a promotion threshold, it may move to old generation

### Old generation
- stores longer-lived objects
- old generation collections are typically more expensive than young collections
- if old generation fills up, pause times and memory pressure usually become much more serious

## 5. Object allocation path

When Java executes:

```java
User user = new User();
```

the JVM generally does not immediately do anything magical or slow.

The path is roughly:

1. The JVM checks whether there is enough space for the object.
2. If thread-local allocation buffer (TLAB) space is available, allocation can be very fast.
3. The object is usually placed in Eden.
4. Constructor logic runs.
5. Object lives until nothing reachable points to it.

### Why allocation is often cheap
Java allocation is often just pointer movement in Eden or a TLAB.

The expensive part is not creating objects.
The expensive part is keeping many live objects and collecting memory later.

## 6. What is TLAB

`TLAB` means thread-local allocation buffer.

Idea:
- instead of all threads competing to allocate directly in shared Eden memory
- each thread gets a small local chunk for most allocations

Benefits:
- less synchronization overhead
- very fast allocation path for many objects

Senior-level nuance:
- people often think object allocation in Java is always expensive
- in many workloads, allocation is very cheap, but retention is what hurts

## 7. What makes an object eligible for GC

Java GC uses reachability, not just scope end.

An object becomes eligible when it is no longer reachable from GC roots.

### Common GC roots
- local variables in active stack frames
- static references
- thread references
- JNI references
- live monitor references

If a chain exists from a GC root to an object, that object is considered live.

## 8. Core GC algorithms you should know

Interviewers expect these names and why they exist.

### Mark
Find all reachable objects.

### Sweep
Reclaim memory of unreachable objects.

### Compact
Move live objects together to reduce fragmentation.

### Copying collection
Copy live objects from one space to another, leaving dead objects behind.

Generational collectors often use:
- copying in young generation
- mark/compact or region-based strategies in old generation

## 9. Minor, major, mixed, and full GC

These terms are often used loosely, so give careful answers.

### Minor GC
- collects young generation
- typically triggered when Eden fills
- often frequent but relatively short

### Major GC
- commonly used to mean old generation collection
- exact meaning can vary by collector and discussion context

### Mixed GC
- especially important in G1
- collects all young regions plus selected old regions with reclaim value

### Full GC
- collects the whole heap and often does a heavier stop-the-world compaction-style operation
- usually the most expensive and most feared event

Senior interview safe wording:
- define what you mean instead of assuming the term is universal

## 10. Lifecycle of an object through Eden to old generation

This is the most important lifecycle story to know.

1. New object is allocated in Eden.
2. Eden fills up.
3. Minor GC happens.
4. Dead objects in Eden are ignored.
5. Live objects are copied to a survivor space.
6. On later minor GCs, survivors move between survivor spaces and age increases.
7. Once object age crosses the threshold, it gets promoted to old generation.
8. When old generation fills enough, old or mixed collection is needed.
9. If memory pressure is severe and collector cannot recover enough, a full GC or OOM can happen.

### Why two survivor spaces exist
Copying live objects from one survivor area to the other is simple and avoids fragmentation in the young generation.

## 11. Promotion to old generation

Promotion does not happen only because an object is “old” in human terms.
It happens because of collector rules.

Common reasons:
- object survived enough young GCs
- survivor spaces cannot hold all survivors
- large object policy for the collector

Important point:
- heavy survivor overflow can push objects into old generation earlier than expected
- this can create old-generation pressure and longer GC pauses

## 12. Stop-the-world and safepoints

Some GC phases require pausing application threads.

That pause is called stop-the-world.

### Why it exists
The collector needs a consistent view of references for certain phases.

### Safepoint
The JVM brings threads to safe states where runtime operations like GC can proceed safely.

Senior nuance:
- not all GC work is stop-the-world
- modern collectors do more work concurrently
- but some stop-the-world work nearly always remains

## 13. Fragmentation and compaction

If dead objects are removed from the middle of memory, free space can become scattered.

This is fragmentation.

Compaction moves live objects together so large allocations are easier.

Why it matters:
- fragmentation hurts allocation of large objects
- compaction helps memory utilization
- compaction can increase pause cost

This is one of the core throughput vs pause trade-offs.

## 14. What is Metaspace

Before Java 8, HotSpot used PermGen.

Since Java 8:
- PermGen is gone
- class metadata is stored in Metaspace

Key points:
- Metaspace uses native memory, not the normal Java heap
- excessive class loading or classloader leaks can cause `OutOfMemoryError: Metaspace`

Common senior interview follow-up:
- web apps and plugin systems sometimes leak classloaders
- that can keep classes alive and grow metaspace usage

## 15. Other memory areas that matter in production

### Thread stack
- every thread has its own stack
- too many threads can cause native memory pressure
- failure may show up as `OutOfMemoryError: unable to create native thread`

### Direct memory
- used by direct byte buffers and native integrations
- not part of normal heap accounting
- can still cause memory failures

### Code cache
- stores JIT-compiled code
- usually not the first thing interviewers ask, but useful in strong JVM discussions

## 16. Common collectors and when they matter

### Serial GC
- simple
- single-threaded collection
- good for tiny heaps or simple environments
- poor fit for large server workloads

### Parallel GC
- focuses on throughput
- uses multiple threads for collection
- often acceptable when total throughput matters more than pause latency

### CMS
- historical low-pause collector
- deprecated and removed from modern JDKs
- still worth knowing because many old articles mention it

### G1 GC
- region-based collector
- designed for large heaps and more predictable pauses
- common default in modern HotSpot usage

### ZGC
- very low pause collector
- designed for very large heaps with low latency goals

### Shenandoah
- low-pause concurrent collector
- good to know conceptually for senior interviews even if not used everywhere

## 17. G1 GC deep dive

If you study one collector in detail, study G1.

### Core idea
Instead of fixed contiguous young/old spaces in the old mental model, G1 divides the heap into many equal-sized regions.

Regions can play different roles:
- Eden regions
- Survivor regions
- Old regions
- Humongous regions

### Why regions help
- collector can focus on regions with the most garbage first
- supports more predictable pause planning

### Remembered sets
G1 tracks cross-region references.

Why needed:
- if object in old region points to object in young region, collector must know that without scanning the whole heap every time

This is where card tables and remembered sets matter.

### Concurrent marking
G1 performs marking mostly concurrently to identify live objects in old regions.

### Evacuation pause
Live objects are copied out of selected regions into new regions.

### Mixed collections
After marking, G1 can collect:
- all young regions
- plus some old regions that are good reclaim candidates

### Humongous objects
Very large objects may occupy special region handling and can become a tuning concern.

Senior interview point:
- G1 is not “no pause”
- it is “tries to make pauses more predictable”

## 18. Card tables and remembered sets

You do not need source-code-level mastery, but you should know the concept.

### Problem
Young GC cannot ignore old objects completely because old objects may reference young objects.

### Solution
The JVM tracks parts of old memory that may contain references into young regions.

Common structures used conceptually:
- card tables
- remembered sets

This avoids full old-generation scanning on every young collection.

## 19. Why most objects die young

Examples:
- request DTOs
- temporary strings
- stream pipeline intermediates
- short-lived collections
- JSON parsing objects

That observation shaped generational GC design.

Implication:
- collecting young generation frequently is okay
- most dead objects can be discarded cheaply

## 20. Large objects and humongous allocation

Large arrays or other large objects can behave differently depending on collector.

Why large objects matter:
- copying them repeatedly is expensive
- fragmentation pressure changes
- some collectors treat them specially

In G1, humongous objects occupy special region arrangements and can affect reclaim behavior.

## 21. Common `OutOfMemoryError` variants

### `Java heap space`
Heap is exhausted.

### `GC overhead limit exceeded`
The JVM is spending too much time in GC and recovering too little memory.

### `Metaspace`
Class metadata area exhausted, often classloader leak or too much dynamic class generation.

### `unable to create native thread`
Often too many threads or native memory exhaustion.

### `Direct buffer memory`
Off-heap direct memory problem.

Senior interview point:
- not every memory problem is a plain heap problem

## 22. Memory leak in Java does not mean “no GC”

This is a critical point.

In Java, a memory leak usually means:
- objects are still reachable
- but they are no longer useful

Examples:
- cache with no eviction
- listener collections never cleaned up
- static maps growing forever
- thread locals not cleared
- classloader leaks in app servers

The GC cannot collect reachable garbage.

## 23. GC logging and investigation tools

High-value tools and techniques:
- `-Xlog:gc*` for GC logging on modern JDKs
- `jcmd` for runtime diagnostics
- `jmap` or heap dump workflows where appropriate
- Java Flight Recorder for production-friendly profiling
- Eclipse MAT for heap dump analysis

Useful `jcmd` style areas to inspect:
- heap info
- class histogram
- native memory summary
- thread information

## 24. Important tuning knobs to know

Do not memorize hundreds of flags. Know the big ones.

| Setting | Why it matters |
| --- | --- |
| `-Xms` | Initial heap size |
| `-Xmx` | Maximum heap size |
| `-XX:+UseG1GC` | Choose G1 collector |
| `-XX:MaxGCPauseMillis` | G1 pause target hint |
| `-XX:InitiatingHeapOccupancyPercent` | When concurrent marking starts in some collectors |
| `-XX:+UseStringDeduplication` | Can reduce duplicate string memory in suitable workloads |

Important senior answer:
- tuning must be evidence-based
- GC flags without measurement are guessing

## 25. Throughput vs latency trade-off

This is one of the most important conceptual interview points.

### Throughput focus
- maximize total work done
- pauses may be longer if overall processing is strong

### Latency focus
- minimize worst pause times
- may accept some extra overhead to keep pauses shorter and more predictable

Example:
- batch job may prefer throughput
- trading system or user-facing low-latency API may prefer lower pause collectors

## 26. Strong production-debugging answer flow

If interviewer says “our app has high GC pauses,” a strong answer is:

1. Check GC logs and pause frequency.
2. Identify allocation rate and live-set size.
3. Check if problem is young churn, promotion pressure, or old-generation retention.
4. Inspect heap dump or class histogram if memory growth exists.
5. Look for leaks, over-caching, oversized object graphs, too many threads, or wrong collector choice.
6. Only then tune heap or GC settings.

This answer sounds more senior than immediately suggesting random flags.

## 27. Common interview traps

- Saying GC frees objects when scope ends.
- Forgetting GC roots and reachability.
- Confusing Metaspace with heap.
- Thinking all GC is stop-the-world.
- Thinking low pause means zero pause.
- Not knowing Eden, survivor spaces, and promotion.
- Not distinguishing heap OOM from metaspace or native-thread problems.

## 28. One compact lifecycle example

Imagine a web request creates 10,000 small temporary objects.

- Most land in Eden.
- Request ends.
- Most objects are no longer reachable.
- Next minor GC clears them cheaply.
- A few objects still referenced by session/cache/request pipeline survive.
- They move to survivor space.
- If they keep surviving, they age and may be promoted to old generation.
- If some global cache keeps growing forever, old generation pressure grows.
- Pause times increase and eventually you may see heavy old-gen or full-GC behavior.

That single story explains a big part of real-world Java memory behavior.

## 29. Final senior-level summary

If you need one strong summary answer:

`Java uses managed memory, but understanding it means understanding reachability, generations, and collector trade-offs. Most objects are allocated cheaply in Eden, usually via TLABs. Minor GCs reclaim dead young objects and copy survivors through survivor spaces, increasing age until promotion to old generation. Long-lived objects and retained graphs pressure old generation, where collection is more expensive. Modern collectors try to balance throughput, pause time, and fragmentation using concurrent work, region tracking, remembered sets, and compaction strategies. Metaspace, native memory, thread stacks, and direct buffers also matter, so not every memory issue is just heap usage.`

If you can explain Eden, survivor spaces, old generation, GC roots, stop-the-world pauses, G1 basics, and memory-leak diagnosis clearly, your GC answer is already at a strong senior-interview level.