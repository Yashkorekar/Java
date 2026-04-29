# Recursive CTEs, Advanced Patterns, and Case Studies — Interview Q&A

> See **01-SQL-Fundamentals.md** for full sample tables and data.

---

## Why this file matters
This is where SQL interviews move from syntax recall to pattern recognition.

## Recursive CTE mental model
A recursive CTE has two parts:
1. anchor query: starting rows
2. recursive query: repeatedly expands from previous rows

Common use cases:
- employee-manager hierarchies
- category trees
- graph traversal with limits

## Hierarchy example

```sql
WITH RECURSIVE org_tree AS (
  SELECT emp_id, emp_name, manager_id, 1 AS level_no
  FROM employees
  WHERE manager_id IS NULL

  UNION ALL

  SELECT e.emp_id, e.emp_name, e.manager_id, ot.level_no + 1
  FROM employees e
  JOIN org_tree ot
    ON e.manager_id = ot.emp_id
)
SELECT *
FROM org_tree;
```

**Result:**

| emp_id | emp_name | manager_id | level_no |
|--------|----------|------------|----------|
| 1      | Asha     | NULL       | 1        |
| 8      | Hari     | NULL       | 1        |
| 2      | Bob      | 1          | 2        |
| 3      | Chitra   | 1          | 2        |
| 6      | Faisal   | 1          | 2        |
| 4      | David    | 2          | 3        |
| 5      | Eva      | 2          | 3        |
| 7      | Gita     | 4          | 4        |

Interview warning:
- You may need cycle protection in real recursive data.

## Latest row per group
Very common interview problem.

```sql
WITH ranked_orders AS (
  SELECT o.*,
         ROW_NUMBER() OVER (
           PARTITION BY customer_id
           ORDER BY order_date DESC, order_id DESC
         ) AS rn
  FROM orders o
)
SELECT *
FROM ranked_orders
WHERE rn = 1;
```

**Result:**

| order_id | customer_id | order_date | amount   | rn |
|----------|-------------|------------|----------|----|
| 1005     | 101         | 2026-03-01 | 3200.00  | 1  |
| 1002     | 102         | 2026-01-12 | 8500.00  | 1  |
| 1004     | 103         | 2026-02-14 | 15000.00 | 1  |
| 1006     | 104         | 2026-03-15 | 4500.00  | 1  |

Key point:
- Use deterministic tie-breakers.

## Gaps and islands
This pattern is used for consecutive dates, login streaks, attendance runs, and booking intervals.

Basic idea:
- assign row numbers ordered by date
- subtract row number from date or sequence-based key
- equal results form one island

Example idea for consecutive login days:

```sql
WITH ordered_logins AS (
  SELECT user_id,
         login_date,
         ROW_NUMBER() OVER (PARTITION BY user_id ORDER BY login_date) AS rn
  FROM logins
), grouped AS (
  SELECT user_id,
         login_date,
         login_date - rn * INTERVAL '1 day' AS grp
  FROM ordered_logins
)
SELECT user_id,
       MIN(login_date) AS streak_start,
       MAX(login_date) AS streak_end,
       COUNT(*) AS streak_len
FROM grouped
GROUP BY user_id, grp;
```

Vendor note:
- Date arithmetic syntax varies.

## Anti-joins
Two common ways to find missing relationships:
- `NOT EXISTS`
- `LEFT JOIN ... WHERE right.id IS NULL`

Interview-safe preference:
- Use `NOT EXISTS` when null-related traps matter.

## Pivot and unpivot
- Pivot turns row values into columns.
- Unpivot turns columns back into rows.
- Syntax varies a lot by vendor.

Interview answer:
- Explain the transformation concept first, not only one vendor's syntax.

## Lateral joins / apply operators
Advanced systems may support:
- PostgreSQL: `LATERAL`
- SQL Server: `CROSS APPLY`, `OUTER APPLY`

Why they matter:
- They allow the right side to depend on each row from the left side.

## Case study: e-commerce reporting
Typical questions:
- latest order per customer
- total spend per customer
- customers who bought product A but not product B
- monthly revenue growth
- top-selling product per category

## Case study: booking system
Typical questions:
- overlapping bookings
- latest active booking per room
- no-show percentage by day
- utilization by time window

## Tricky questions
**Q: Is recursive CTE always the best way to model hierarchy?**
- Not always. It is a query technique, not a full modeling strategy.

**Q: Why can latest-row query still be wrong even with `MAX(order_date)`?**
- Because several rows can share the same max date.

**Q: What breaks gaps-and-islands solutions most often?**
- duplicates, missing tie-breakers, and wrong date arithmetic assumptions.

## Common mistakes
- writing recursive CTEs without stop/cycle awareness
- using non-deterministic ordering in latest-row queries
- forgetting null handling in anti-joins
- solving row-pattern problems with procedural thinking instead of window functions

## Quick revision
- practice recursive hierarchy queries
- practice latest-row-per-group
- practice gaps-and-islands
- learn pivot/unpivot conceptually even if syntax differs by vendor