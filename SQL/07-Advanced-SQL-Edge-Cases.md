# Advanced SQL and Interview Edge Cases — Interview Q&A

> See **01-SQL-Fundamentals.md** for full sample tables and data.

**Additional table for edge case examples — blacklist:**

| customer_id |
|-------------|
| 101         |
| NULL        |
| 103         |

---

## Common edge cases interviewers love
- duplicates after joins
- `NULL` behavior in comparisons
- `NOT IN` with nullable subqueries
- ties in ranking queries
- missing `ORDER BY` and non-deterministic results
- filtering after outer join
- aggregate vs window confusion
- date boundary bugs
- integer division and rounding issues

## Duplicate rows after joins
If one parent row matches multiple child rows, result count multiplies.

Example:
- one customer with three orders
- joining `customers` to `orders` gives three rows for that customer

Interview skill:
- always reason about cardinality: one-to-one, one-to-many, many-to-many.

## `NULL` in `NOT IN`

```sql
SELECT *
FROM customers
WHERE customer_id NOT IN (
  SELECT customer_id
  FROM blacklist
);
```

Problem:
- if `blacklist.customer_id` contains `NULL`, results may become unexpected.
- With our data above, this returns **no rows** because `NOT IN` with a NULL in the list makes every comparison `UNKNOWN`.

Safer pattern:

```sql
SELECT *
FROM customers c
WHERE NOT EXISTS (
  SELECT 1
  FROM blacklist b
  WHERE b.customer_id = c.customer_id
);
```

**Result:**

| customer_id | customer_name | city    |
|-------------|---------------|--------|
| 102         | Sneha         | Pune    |
| 104         | Priya         | Delhi   |
| 105         | Kiran         | Chennai |

*NOT EXISTS correctly returns non-blacklisted customers even though blacklist has a NULL row.*

## Ties in top salary problems
Question: find second highest salary.

Naive approach can fail when duplicate salaries exist.

Better ideas:
- `DENSE_RANK()` over salary descending and pick rank 2
- or `MAX(salary)` less than top salary

```sql
WITH salary_rank AS (
  SELECT salary,
         DENSE_RANK() OVER (ORDER BY salary DESC) AS dr
  FROM employees
)
SELECT salary
FROM salary_rank
WHERE dr = 2;
```

**Result:**

| salary    |
|-----------|
| 110000.00 |

*Asha and Faisal share rank 1 at 120000. DENSE_RANK gives Eva's 110000 rank 2 — no gap.*

## Highest salary vs employee with highest salary
- Highest salary is one scalar value.
- Employee with highest salary may be multiple rows if ties exist.

## Deleting duplicates safely
- Need business definition of duplicate.
- Need deterministic row to keep.
- Often keep smallest `id`, latest timestamp, or highest priority status.

## Window frame edge cases
- Default frame differs by database and function.
- `LAST_VALUE()` can surprise people because default frame may end at current row.
- Interview-safe answer: specify frame explicitly when behavior matters.

## Date and time traps
- inclusive vs exclusive range boundaries
- timezone conversion issues
- timestamp vs date comparison
- month-end arithmetic edge cases

Safer pattern for date range:

```sql
WHERE created_at >= TIMESTAMP '2026-03-01 00:00:00'
  AND created_at <  TIMESTAMP '2026-04-01 00:00:00'
```

## `CASE` evaluation
- Use `CASE` for derived categories and safe conditional logic.
- Some expressions can still error before `CASE` helps, depending on optimizer and vendor details.

## `COALESCE()` vs `ISNULL()` / `NVL()`
- `COALESCE()` is standard SQL and returns first non-null value.
- `ISNULL()` and `NVL()` are vendor-specific.

## `MERGE`
- Used for upsert-like logic in some databases.
- Powerful but can be tricky and vendor-specific.
- Many teams prefer explicit `INSERT` / `UPDATE` patterns depending on database guarantees.

## Temporary tables vs CTEs
- temp table
  - physical temporary storage for session or transaction
  - useful when reusing intermediate result many times
- CTE
  - logical query structuring tool
  - not always materialized

## View vs materialized view
- view: stored query definition
- materialized view: stored results, refreshed periodically or on demand

## Tricky questions
**Q: If no `ORDER BY` is used, can row order be trusted?**
- No.
- Never rely on incidental storage order.

**Q: Is `GROUP BY` guaranteed to sort rows?**
- No.
- Some engines may output grouped order incidentally, but that is not a contract.

**Q: Is `PRIMARY KEY` automatically indexed?**
- Usually yes in major systems, but explain at high level and avoid assuming all implementation details are identical.

**Q: Can `COUNT(DISTINCT col)` count `NULL`?**
- Usually it counts distinct non-null values only.

## Common mistakes
- Forgetting tie handling in top-N problems
- Using non-deterministic queries in interviews
- Assuming empty string and `NULL` are always the same
- Using `ORDER BY` outside vs inside window function interchangeably

## Quick revision
- Always test your query mentally against duplicates, nulls, and ties.
- Write deterministic ordering.
- Use `NOT EXISTS` for anti-joins when nulls can appear.