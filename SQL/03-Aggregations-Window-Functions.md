# Aggregations and Window Functions — Interview Q&A

> See **01-SQL-Fundamentals.md** for full sample tables and data.

**Quick reference — employees:**

| emp_id | emp_name | dept_id | manager_id | salary    |
|--------|----------|---------|------------|-----------|
| 1      | Asha     | 10      | NULL       | 120000.00 |
| 2      | Bob      | 20      | 1          | 85000.00  |
| 3      | Chitra   | 10      | 1          | 95000.00  |
| 4      | David    | 30      | 2          | 70000.00  |
| 5      | Eva      | 20      | 2          | 110000.00 |
| 6      | Faisal   | 10      | 1          | 120000.00 |
| 7      | Gita     | 30      | 4          | 62000.00  |
| 8      | Hari     | NULL    | NULL       | 55000.00  |

**Quick reference — orders:**

| order_id | customer_id | order_date | amount   |
|----------|-------------|------------|----------|
| 1001     | 101         | 2026-01-05 | 2500.00  |
| 1002     | 102         | 2026-01-12 | 8500.00  |
| 1003     | 101         | 2026-02-03 | 1200.00  |
| 1004     | 103         | 2026-02-14 | 15000.00 |
| 1005     | 101         | 2026-03-01 | 3200.00  |
| 1006     | 104         | 2026-03-15 | 4500.00  |

**Quick reference — users:**

| user_id | email              | gender |
|---------|--------------------|--------|
| 1       | asha@example.com   | F      |
| 2       | bob@example.com    | M      |
| 3       | asha@example.com   | F      |
| 4       | chitra@example.com | F      |

---

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

**Result:**

| total_rows | rows_with_manager |
|------------|-------------------|
| 8          | 6                 |

*Asha and Hari have NULL manager_id, so COUNT(manager_id) = 6.*

## `GROUP BY`
Groups rows so aggregate functions can be computed per group.

```sql
SELECT dept_id, AVG(salary) AS avg_salary
FROM employees
GROUP BY dept_id;
```

**Result:**

| dept_id | avg_salary |
|---------|------------|
| 10      | 111666.67  |
| 20      | 97500.00   |
| 30      | 66000.00   |
| NULL    | 55000.00   |

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

**Result:**

| dept_id | emp_count |
|---------|-----------|
| 10      | 3         |

## `CASE WHEN`
Very important for conditional aggregation.

```sql
SELECT dept_id,
       SUM(CASE WHEN salary >= 100000 THEN 1 ELSE 0 END) AS high_paid_count
FROM employees
GROUP BY dept_id;
```

**Result:**

| dept_id | high_paid_count |
|---------|-----------------|
| 10      | 2               |
| 20      | 1               |
| 30      | 0               |
| NULL    | 0               |

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

**Result:**

| emp_name | dept_id | salary    | rn |
|----------|---------|-----------|----|
| Hari     | NULL    | 55000.00  | 1  |
| Asha     | 10      | 120000.00 | 1  |
| Faisal   | 10      | 120000.00 | 2  |
| Chitra   | 10      | 95000.00  | 3  |
| Eva      | 20      | 110000.00 | 1  |
| Bob      | 20      | 85000.00  | 2  |
| David    | 30      | 70000.00  | 1  |
| Gita     | 30      | 62000.00  | 2  |

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

**Result:**

| order_id | order_date | amount   | running_total |
|----------|------------|----------|---------------|
| 1001     | 2026-01-05 | 2500.00  | 2500.00       |
| 1002     | 2026-01-12 | 8500.00  | 11000.00      |
| 1003     | 2026-02-03 | 1200.00  | 12200.00      |
| 1004     | 2026-02-14 | 15000.00 | 27200.00      |
| 1005     | 2026-03-01 | 3200.00  | 30400.00      |
| 1006     | 2026-03-15 | 4500.00  | 34900.00      |

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

**Result:**

| order_id | order_date | amount   | moving_avg_3 |
|----------|------------|----------|--------------|
| 1001     | 2026-01-05 | 2500.00  | 2500.00      |
| 1002     | 2026-01-12 | 8500.00  | 5500.00      |
| 1003     | 2026-02-03 | 1200.00  | 4066.67      |
| 1004     | 2026-02-14 | 15000.00 | 8233.33      |
| 1005     | 2026-03-01 | 3200.00  | 6466.67      |
| 1006     | 2026-03-15 | 4500.00  | 7566.67      |

## `LAG()` and `LEAD()`

```sql
SELECT order_id,
       order_date,
       amount,
       LAG(amount) OVER (ORDER BY order_date) AS previous_amount,
       amount - LAG(amount) OVER (ORDER BY order_date) AS diff_from_previous
FROM orders;
```

**Result:**

| order_id | order_date | amount   | previous_amount | diff_from_previous |
|----------|------------|----------|-----------------|--------------------|
| 1001     | 2026-01-05 | 2500.00  | NULL            | NULL               |
| 1002     | 2026-01-12 | 8500.00  | 2500.00         | 6000.00            |
| 1003     | 2026-02-03 | 1200.00  | 8500.00         | -7300.00           |
| 1004     | 2026-02-14 | 15000.00 | 1200.00         | 13800.00           |
| 1005     | 2026-03-01 | 3200.00  | 15000.00        | -11800.00          |
| 1006     | 2026-03-15 | 4500.00  | 3200.00         | 1300.00            |

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

**Result:**

| emp_id | emp_name | dept_id | salary    | rn |
|--------|----------|---------|-----------|----|
| 8      | Hari     | NULL    | 55000.00  | 1  |
| 1      | Asha     | 10      | 120000.00 | 1  |
| 6      | Faisal   | 10      | 120000.00 | 2  |
| 3      | Chitra   | 10      | 95000.00  | 3  |
| 5      | Eva      | 20      | 110000.00 | 1  |
| 2      | Bob      | 20      | 85000.00  | 2  |
| 4      | David    | 30      | 70000.00  | 1  |
| 7      | Gita     | 30      | 62000.00  | 2  |

## Find duplicates

```sql
SELECT email, COUNT(*) AS duplicate_count
FROM users
GROUP BY email
HAVING COUNT(*) > 1;
```

**Result:**

| email            | duplicate_count |
|------------------|-----------------|
| asha@example.com | 2               |

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