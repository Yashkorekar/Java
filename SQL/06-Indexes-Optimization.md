# Indexes, Query Plans, and Optimization — Interview Q&A

> See **01-SQL-Fundamentals.md** for full sample tables and data.

---

## What is an index?
- An index is a data structure that helps the database find rows faster.
- Tradeoff: faster reads, but extra storage and slower writes.

## Common index types
- B-tree index
  - most common general-purpose index
- hash index
  - useful for exact lookups in some systems
- bitmap index
  - common in analytics scenarios
- full-text index
  - optimized for text search

## Clustered vs non-clustered index
- clustered index
  - affects physical order of table data in some databases
  - usually only one clustered order is possible
- non-clustered index
  - separate structure pointing to rows

Interview note:
- Exact implementation differs by database engine.

## Composite index
- Index on multiple columns.
- Column order matters.

Example:
- index on `(dept_id, salary)` helps queries filtering by `dept_id`, or by `dept_id` and `salary`.
- It usually does not help much for filtering only by `salary` due to leftmost-prefix behavior in many engines.

## Covering index
- An index that contains all columns needed by the query.
- Can reduce table lookups.

## When indexes help
- equality lookups
- range searches
- join predicates
- sorting and grouping in some cases

## When indexes may not help enough
- very small tables
- columns with very low selectivity
- queries returning large percentage of table rows
- functions applied to indexed columns without functional index support

## SARGable conditions
SARGable roughly means searchable in a way the optimizer can use indexes effectively.

Bad pattern:

```sql
SELECT *
FROM employees
WHERE YEAR(join_date) = 2025;
```

**Result** (works but not SARGable — index on join_date cannot be used efficiently):

| emp_id | emp_name | dept_id | manager_id | salary   | join_date  |
|--------|----------|---------|------------|----------|------------|
| 7      | Gita     | 30      | 4          | 62000.00 | 2025-02-14 |
| 8      | Hari     | NULL    | NULL       | 55000.00 | 2025-09-30 |

Better pattern:

```sql
SELECT *
FROM employees
WHERE join_date >= DATE '2025-01-01'
  AND join_date < DATE '2026-01-01';
```

**Result** (same result, but SARGable — index on join_date can be used):

| emp_id | emp_name | dept_id | manager_id | salary   | join_date  |
|--------|----------|---------|------------|----------|------------|
| 7      | Gita     | 30      | 4          | 62000.00 | 2025-02-14 |
| 8      | Hari     | NULL    | NULL       | 55000.00 | 2025-09-30 |

## `EXPLAIN` or execution plan
- Shows how database intends to execute a query.
- Important concepts:
  - table scan / full scan
  - index seek / index scan
  - nested loop join
  - hash join
  - sort
  - estimated rows vs actual rows

## Optimization basics
- select only needed columns
- avoid unnecessary `DISTINCT`
- filter early when possible
- index join and filter columns
- rewrite row-by-row logic into set-based SQL
- watch out for functions on indexed columns

## `EXISTS` can be better than `COUNT(*) > 0`

```sql
SELECT c.customer_id
FROM customers c
WHERE EXISTS (
  SELECT 1
  FROM orders o
  WHERE o.customer_id = c.customer_id
);
```

**Result:**

| customer_id |
|-------------|
| 101         |
| 102         |
| 103         |
| 104         |

Reason:
- `EXISTS` can stop at first match.

## Avoiding accidental row explosion
- Joining parent table to multiple child tables without proper aggregation can multiply rows.

## Partitioning
- Splits large tables into smaller logical pieces.
- Helps manage large data volumes and some query patterns.
- Not a replacement for good indexing.

## Common performance anti-patterns
- `SELECT *` in production queries
- leading wildcard searches like `LIKE '%text'`
- too many indexes on write-heavy tables
- missing indexes on foreign keys used in joins
- using cursors where set-based SQL is enough
- scalar subqueries executed repeatedly when a join or window function is better

## Tricky questions
**Q: Does adding an index always improve performance?**
- No. Writes become more expensive, storage increases, and bad indexes can still be ignored.

**Q: Why can query be slow even when an index exists?**
- Poor selectivity, stale statistics, wrong join order, non-SARGable predicate, or optimizer decides full scan is cheaper.

**Q: Is `SELECT COUNT(*) FROM big_table` always fast?**
- Not necessarily. It depends on engine internals, visibility rules, and whether metadata shortcuts exist.

## Common mistakes
- Creating indexes blindly without workload understanding
- Not checking execution plans
- Ignoring data distribution and selectivity
- Assuming syntax-level rewrite always beats optimizer decisions

## Quick revision
- Know what indexes are for.
- Know composite index order matters.
- Learn SARGable query patterns.
- Learn to read basic execution plans.