# Aggregations and Window Functions — Interview Q&A

## Aggregate functions
- `COUNT()`
- `SUM()`
- `AVG()`
- `MIN()`
- `MAX()`

## `COUNT(*)` vs `COUNT(column)`
- `COUNT(*)` counts rows.
- `COUNT(column)` counts non-`NULL` values in that column.

```sql
SELECT COUNT(*) AS total_rows,
       COUNT(manager_id) AS rows_with_manager
FROM employees;
```

## `GROUP BY`
Groups rows so aggregate functions can be computed per group.

```sql
SELECT dept_id, AVG(salary) AS avg_salary
FROM employees
GROUP BY dept_id;
```

## Why non-grouped columns are not allowed
- If a selected column is neither grouped nor aggregated, the result is ambiguous.
- Some databases allow non-standard behavior depending on SQL mode, but interview-safe SQL should be explicit.

## `HAVING`
- Used to filter groups after aggregation.

```sql
SELECT dept_id, COUNT(*) AS emp_count
FROM employees
GROUP BY dept_id
HAVING COUNT(*) >= 3;
```

## `CASE WHEN`
Very important for conditional aggregation.

```sql
SELECT dept_id,
       SUM(CASE WHEN salary >= 100000 THEN 1 ELSE 0 END) AS high_paid_count
FROM employees
GROUP BY dept_id;
```

## Window functions
- A window function performs calculations across a set of rows related to the current row.
- Unlike `GROUP BY`, it does not collapse rows into one row per group.

## Common window functions
- ranking: `ROW_NUMBER()`, `RANK()`, `DENSE_RANK()`, `NTILE()`
- analytic: `LAG()`, `LEAD()`, `FIRST_VALUE()`, `LAST_VALUE()`
- aggregate over window: `SUM() OVER`, `AVG() OVER`, `COUNT() OVER`

## `ROW_NUMBER()`

```sql
SELECT emp_name,
       dept_id,
       salary,
       ROW_NUMBER() OVER (PARTITION BY dept_id ORDER BY salary DESC) AS rn
FROM employees;
```

Use case:
- Find top earner per department by filtering `rn = 1`.

## `RANK()` vs `DENSE_RANK()`
- `RANK()` leaves gaps after ties.
- `DENSE_RANK()` does not leave gaps.

Example salaries: `100, 100, 90`
- `RANK()`: `1, 1, 3`
- `DENSE_RANK()`: `1, 1, 2`

## Running total

```sql
SELECT order_id,
       order_date,
       amount,
       SUM(amount) OVER (ORDER BY order_date, order_id) AS running_total
FROM orders;
```

## Moving average

```sql
SELECT order_id,
       order_date,
       amount,
       AVG(amount) OVER (
         ORDER BY order_date
         ROWS BETWEEN 2 PRECEDING AND CURRENT ROW
       ) AS moving_avg_3
FROM orders;
```

## `LAG()` and `LEAD()`

```sql
SELECT order_id,
       order_date,
       amount,
       LAG(amount) OVER (ORDER BY order_date) AS previous_amount,
       amount - LAG(amount) OVER (ORDER BY order_date) AS diff_from_previous
FROM orders;
```

Use cases:
- compare row with previous row
- detect trend changes
- calculate daily differences

## Top-N per group
Classic interview problem.

```sql
WITH ranked_employees AS (
  SELECT emp_id,
         emp_name,
         dept_id,
         salary,
         ROW_NUMBER() OVER (PARTITION BY dept_id ORDER BY salary DESC, emp_id) AS rn
  FROM employees
)
SELECT *
FROM ranked_employees
WHERE rn <= 3;
```

## Find duplicates

```sql
SELECT email, COUNT(*) AS duplicate_count
FROM users
GROUP BY email
HAVING COUNT(*) > 1;
```

## Remove duplicates while keeping one row

```sql
WITH duplicate_rows AS (
  SELECT user_id,
         email,
         ROW_NUMBER() OVER (PARTITION BY email ORDER BY user_id) AS rn
  FROM users
)
DELETE FROM users
WHERE user_id IN (
  SELECT user_id
  FROM duplicate_rows
  WHERE rn > 1
);
```

## Tricky questions
**Q: Can `WHERE` use window functions directly?**
- Usually no, because window functions are computed after `WHERE`.
- Use a subquery or CTE first.

**Q: What is the difference between `PARTITION BY` and `GROUP BY`?**
- `GROUP BY` collapses rows.
- `PARTITION BY` keeps rows and defines the calculation window.

**Q: Why can running total change when order is not deterministic?**
- If `ORDER BY` inside the window does not uniquely order rows, ties may produce non-deterministic sequence-dependent results.

## Common mistakes
- Using `COUNT(column)` when null-sensitive row count is required
- Forgetting tie-breakers in ranking queries
- Confusing aggregate functions with window versions of the same functions
- Using `WHERE` instead of outer filtering on computed rank

## Quick revision
- Learn `COUNT(*)` vs `COUNT(column)`.
- Practice top-N per group.
- Know `ROW_NUMBER`, `RANK`, `DENSE_RANK`, `LAG`, `LEAD`.