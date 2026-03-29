# Execution Plans, Statistics, and Tuning Workflow — Interview Q&A

## Why this file matters
Advanced SQL interviews often shift from "can you write the query" to "can you explain why it is slow".

## What is an execution plan?
- An execution plan is the optimizer's chosen strategy for running a query.
- It may include scans, seeks, joins, sorts, aggregates, spools, filters, and parallel steps.

## Estimated plan vs actual plan
- estimated plan: what optimizer predicts before running
- actual plan: what really happened at execution time

Important interview point:
- The biggest tuning clues often come from large differences between estimated rows and actual rows.

## Common plan operators
- table scan / full scan
- index seek
- index scan
- nested loop join
- hash join
- merge join
- sort
- aggregate

## Join algorithm intuition
- nested loop: good when outer side is small and indexed lookups are cheap
- hash join: often useful for large unsorted inputs
- merge join: great when both inputs are already sorted on join keys

Interview-safe answer:
- The optimizer chooses based on cost estimates, available indexes, row counts, and data distribution.

## Why statistics matter
- Statistics describe data distribution.
- Optimizers use them to estimate row counts and choose plans.

If statistics are stale or misleading:
- wrong join order may be chosen
- scan may be chosen over seek or vice versa
- memory grants may be poor

## Cardinality estimation
This means predicting how many rows each step will produce.

Why it matters:
- Nearly every major tuning decision depends on row-count estimates.

## Practical tuning workflow
1. Verify the query is logically correct.
2. Check the actual execution plan.
3. Look for expensive scans, large sorts, spills, or bad estimates.
4. Check indexes on filter, join, sort, and group-by columns.
5. Rewrite non-SARGable predicates.
6. Reduce row width and row count earlier if possible.
7. Re-test and compare plans again.

## SARGability revisited
Bad:

```sql
WHERE UPPER(email) = 'ASHA@EXAMPLE.COM'
```

Better:
- normalize data when storing it, or use a matching functional index if the database supports it.

## Parameter sensitivity / parameter sniffing
High-level idea:
- A plan compiled for one parameter value may be bad for another value.

Interview answer:
- Same query text can behave very differently depending on parameter selectivity and plan reuse.

## Covering indexes and lookup tradeoffs
- Covering index can avoid extra row lookups.
- But adding too many included columns increases index size and write cost.

## Partition pruning
- On partitioned tables, good predicates can let the database skip whole partitions.

Interview trap:
- Partitioning is not a magic speed feature if queries still touch most partitions.

## Common slow-query causes
- missing or weak indexes
- non-SARGable predicates
- stale statistics
- unnecessary `DISTINCT` or `ORDER BY`
- row explosion from joins
- retrieving too many columns or rows
- scalar subqueries executed repeatedly

## What to say when asked how to optimize a query
- first verify correctness
- inspect actual plan
- compare estimated vs actual rows
- fix predicates and indexes
- avoid premature hinting unless the optimizer truly needs help

## Tricky questions
**Q: Is index seek always faster than scan?**
- No. If a query needs a large portion of the table, a scan may be cheaper.

**Q: Can a query plan change without query text changing?**
- Yes. Data distribution, statistics, indexes, and parameter values can all change plan choice.

**Q: Should you always force an index or hint a join type?**
- Usually no. Hints can become technical debt if data changes.

## Common mistakes
- tuning before checking correctness
- reading only query text and not the actual plan
- assuming all scans are bad
- indexing every column instead of indexing workload patterns
- ignoring statistics and row-estimate errors

## Quick revision
- know estimated vs actual plan difference
- know major join algorithms
- know why statistics and cardinality matter
- know a repeatable tuning workflow instead of random rewrites