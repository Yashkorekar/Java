# Java Internals (Index)

This folder is for deep Java internals that are asked in strong backend and senior Java interviews.

These are not just API notes. The goal here is to understand what the JDK and JVM are doing under the hood, what the trade-offs are, and how to explain them clearly in interviews.

## Files in this folder
1. `01-HashMap-Internals.md`
2. `02-JVM-Memory-And-Garbage-Collection.md`
3. `03-Senior-Java-Interview-Deep-Dive-Roadmap.md`

## Why this folder matters
- Mid-level interviews often stop at usage.
- Senior interviews usually ask internal working, performance, memory cost, failure modes, and trade-offs.
- If you can explain `HashMap`, heap generations, GC, and related internals properly, interviewers usually trust your Java fundamentals.

## Suggested order
1. Start with `01-HashMap-Internals.md`
2. Then study `02-JVM-Memory-And-Garbage-Collection.md`
3. Use `03-Senior-Java-Interview-Deep-Dive-Roadmap.md` to plan the next deep dives

## What a senior-level answer should sound like
- Not just “`HashMap` is key-value and gives O(1)” but how buckets, hashing, resize, tree bins, and `equals`/`hashCode` affect behavior.
- Not just “GC removes unused objects” but how objects move through Eden, survivor spaces, old generation, root scanning, promotion, stop-the-world phases, and collector trade-offs.
- Not just “Java is managed” but how the JVM manages memory, threads, metadata, and performance.

## Short memory anchors
- `HashMap`: bucket array + hash spread + collision handling + resize split logic.
- GC: allocation is cheap, reclamation is expensive, pause behavior matters.
- Senior interviews: internals + trade-offs + debugging + production impact.