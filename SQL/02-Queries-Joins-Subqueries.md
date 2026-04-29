# Queries, Joins, Subqueries, and CTEs — Interview Q&A

## Sample tables used in explanations

> See **01-SQL-Fundamentals.md** for full sample data with all rows.

**employees**

| emp_id | emp_name | dept_id | manager_id | salary    | join_date  |
|--------|----------|---------|------------|-----------|------------|
| 1      | Asha     | 10      | NULL       | 120000.00 | 2023-01-15 |
| 2      | Bob      | 20      | 1          | 85000.00  | 2023-03-22 |
| 3      | Chitra   | 10      | 1          | 95000.00  | 2023-06-10 |
| 4      | David    | 30      | 2          | 70000.00  | 2024-01-05 |
| 5      | Eva      | 20      | 2          | 110000.00 | 2024-04-18 |
| 6      | Faisal   | 10      | 1          | 120000.00 | 2024-07-01 |
| 7      | Gita     | 30      | 4          | 62000.00  | 2025-02-14 |
| 8      | Hari     | NULL    | NULL       | 55000.00  | 2025-09-30 |

**departments**

| dept_id | dept_name   |
|---------|-------------|
| 10      | Sales       |
| 20      | Engineering |
| 30      | HR          |
| 40      | Marketing   |

**customers**

| customer_id | customer_name | city    |
|-------------|---------------|---------|
| 101         | Ravi          | Delhi   |
| 102         | Sneha         | Pune    |
| 103         | Amit          | Mumbai  |
| 104         | Priya         | Delhi   |
| 105         | Kiran         | Chennai |

**orders**

| order_id | customer_id | order_date | amount   |
|----------|-------------|------------|----------|
| 1001     | 101         | 2026-01-05 | 2500.00  |
| 1002     | 102         | 2026-01-12 | 8500.00  |
| 1003     | 101         | 2026-02-03 | 1200.00  |
| 1004     | 103         | 2026-02-14 | 15000.00 |
| 1005     | 101         | 2026-03-01 | 3200.00  |
| 1006     | 104         | 2026-03-15 | 4500.00  |

---

## Basic filtering

```sql
SELECT emp_name, salary
FROM employees
WHERE salary >= 70000
  AND dept_id = 10;
```

**Result:**

| emp_name | salary    |
|----------|-----------|
| Asha     | 120000.00 |
| Chitra   | 95000.00  |
| Faisal   | 120000.00 |

## `IN`, `BETWEEN`, `LIKE`

```sql
SELECT *
FROM customers
WHERE city IN ('Delhi', 'Pune', 'Mumbai');
```

**Result:**

| customer_id | customer_name | city   |
|-------------|---------------|--------|
| 101         | Ravi          | Delhi  |
| 102         | Sneha         | Pune   |
| 103         | Amit          | Mumbai |
| 104         | Priya         | Delhi  |

```sql
SELECT *
FROM orders
WHERE amount BETWEEN 1000 AND 5000;
```

**Result:**

| order_id | customer_id | order_date | amount  |
|----------|-------------|------------|---------|
| 1001     | 101         | 2026-01-05 | 2500.00 |
| 1003     | 101         | 2026-02-03 | 1200.00 |
| 1005     | 101         | 2026-03-01 | 3200.00 |
| 1006     | 104         | 2026-03-15 | 4500.00 |

```sql
SELECT *
FROM employees
WHERE emp_name LIKE 'A%';
```

**Result:**

| emp_id | emp_name | dept_id | manager_id | salary    | join_date  |
|--------|----------|---------|------------|-----------|------------|
| 1      | Asha     | 10      | NULL       | 120000.00 | 2023-01-15 |

## Joins overview
- `INNER JOIN`: matching rows only
- `LEFT JOIN`: all rows from left, matching rows from right
- `RIGHT JOIN`: all rows from right, matching rows from left
- `FULL JOIN`: all rows from both sides with matches where possible
- `CROSS JOIN`: Cartesian product
- self join: table joined to itself

## `INNER JOIN`

```sql
SELECT e.emp_name, d.dept_name
FROM employees e
INNER JOIN departments d
  ON e.dept_id = d.dept_id;
```

**Result:**

| emp_name | dept_name   |
|----------|-------------|
| Asha     | Sales       |
| Bob      | Engineering |
| Chitra   | Sales       |
| David    | HR          |
| Eva      | Engineering |
| Faisal   | Sales       |
| Gita     | HR          |

*Note: Hari (dept_id NULL) is excluded because INNER JOIN requires a match.*

## `LEFT JOIN`

```sql
SELECT e.emp_name, d.dept_name
FROM employees e
LEFT JOIN departments d
  ON e.dept_id = d.dept_id;
```

**Result:**

| emp_name | dept_name   |
|----------|-------------|
| Asha     | Sales       |
| Bob      | Engineering |
| Chitra   | Sales       |
| David    | HR          |
| Eva      | Engineering |
| Faisal   | Sales       |
| Gita     | HR          |
| Hari     | NULL        |

*Hari appears with NULL dept_name because LEFT JOIN keeps all left rows.*

Interview meaning:
- Every employee appears.
- If department is missing, department columns become `NULL`.

## Where people break outer joins

```sql
SELECT e.emp_name, d.dept_name
FROM employees e
LEFT JOIN departments d
  ON e.dept_id = d.dept_id
WHERE d.dept_name = 'Sales';
```

Why this is tricky:
- The `WHERE` condition filters out rows where right-side columns are `NULL`.
- That effectively turns the left join into something closer to an inner join.

Safer version when you want to preserve left rows:

```sql
SELECT e.emp_name, d.dept_name
FROM employees e
LEFT JOIN departments d
  ON e.dept_id = d.dept_id
 AND d.dept_name = 'Sales';
```

## Self join
Used when rows in the same table relate to each other.

```sql
SELECT e.emp_name AS employee,
       m.emp_name AS manager
FROM employees e
LEFT JOIN employees m
  ON e.manager_id = m.emp_id;
```

**Result:**

| employee | manager |
|----------|---------|
| Asha     | NULL    |
| Bob      | Asha    |
| Chitra   | Asha    |
| David    | Bob     |
| Eva      | Bob     |
| Faisal   | Asha    |
| Gita     | David   |
| Hari     | NULL    |

## `UNION` vs `UNION ALL`
- `UNION`
  - combines results and removes duplicates
  - usually more expensive because deduplication is required
- `UNION ALL`
  - combines results and keeps duplicates
  - usually faster

## `INTERSECT` and `EXCEPT`
- `INTERSECT`: common rows in both result sets
- `EXCEPT` / `MINUS`: rows in first result not in second
- Availability and exact names depend on vendor

## Subqueries
- A subquery is a query inside another query.
- It can appear in `SELECT`, `FROM`, `WHERE`, or `HAVING`.

### Scalar subquery
Returns exactly one value.

```sql
SELECT emp_name, salary
FROM employees
WHERE salary > (
  SELECT AVG(salary)
  FROM employees
);
```

**Result** (overall AVG salary = 89625.00):

| emp_name | salary    |
|----------|-----------|
| Asha     | 120000.00 |
| Chitra   | 95000.00  |
| Eva      | 110000.00 |
| Faisal   | 120000.00 |

### Multi-row subquery

```sql
SELECT *
FROM employees
WHERE dept_id IN (
  SELECT dept_id
  FROM departments
  WHERE dept_name IN ('Sales', 'HR')
);
```

**Result:**

| emp_id | emp_name | dept_id | manager_id | salary    | join_date  |
|--------|----------|---------|------------|-----------|------------|
| 1      | Asha     | 10      | NULL       | 120000.00 | 2023-01-15 |
| 3      | Chitra   | 10      | 1          | 95000.00  | 2023-06-10 |
| 4      | David    | 30      | 2          | 70000.00  | 2024-01-05 |
| 6      | Faisal   | 10      | 1          | 120000.00 | 2024-07-01 |
| 7      | Gita     | 30      | 4          | 62000.00  | 2025-02-14 |

## Correlated subquery
- Runs with reference to each row of the outer query.

```sql
SELECT e1.emp_name, e1.salary
FROM employees e1
WHERE e1.salary > (
  SELECT AVG(e2.salary)
  FROM employees e2
  WHERE e2.dept_id = e1.dept_id
);
```

**Result** (dept 10 avg=111666, dept 20 avg=97500, dept 30 avg=66000):

| emp_name | salary    |
|----------|-----------|
| Asha     | 120000.00 |
| Eva      | 110000.00 |
| Faisal   | 120000.00 |
| David    | 70000.00  |

Interview angle:
- Correlated subqueries can be expressive, but sometimes a join or window function is clearer or faster.

## `EXISTS` vs `IN`
- `EXISTS` checks whether matching rows exist.
- `IN` compares against a list or subquery result.
- Performance depends on optimizer and indexes.

```sql
SELECT c.customer_id, c.customer_name
FROM customers c
WHERE EXISTS (
  SELECT 1
  FROM orders o
  WHERE o.customer_id = c.customer_id
);
```

**Result:**

| customer_id | customer_name |
|-------------|---------------|
| 101         | Ravi          |
| 102         | Sneha         |
| 103         | Amit          |
| 104         | Priya         |

*Kiran (105) excluded because she has no orders.*

## `NOT EXISTS` vs `NOT IN`
This is a classic interview trap.

```sql
SELECT c.customer_id, c.customer_name
FROM customers c
WHERE NOT EXISTS (
  SELECT 1
  FROM orders o
  WHERE o.customer_id = c.customer_id
);
```

**Result:**

| customer_id | customer_name |
|-------------|---------------|
| 105         | Kiran         |

Why `NOT IN` is dangerous:
- If the subquery contains `NULL`, comparison can become `UNKNOWN` and return no rows unexpectedly.

## Common Table Expressions (CTEs)
CTEs improve readability and allow stepwise thinking.

```sql
WITH high_value_orders AS (
  SELECT customer_id, amount
  FROM orders
  WHERE amount >= 10000
)
SELECT customer_id, COUNT(*) AS order_count
FROM high_value_orders
GROUP BY customer_id;
```

**Result:**

| customer_id | order_count |
|-------------|-------------|
| 103         | 1           |

## Recursive CTE
Used for hierarchies like employee-manager trees or category trees.

```sql
WITH RECURSIVE employee_tree AS (
  SELECT emp_id, emp_name, manager_id, 1 AS level_no
  FROM employees
  WHERE manager_id IS NULL

  UNION ALL

  SELECT e.emp_id, e.emp_name, e.manager_id, et.level_no + 1
  FROM employees e
  JOIN employee_tree et
    ON e.manager_id = et.emp_id
)
SELECT *
FROM employee_tree;
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

## Pagination
- MySQL/PostgreSQL: `LIMIT` and `OFFSET`
- SQL Server: `OFFSET ... FETCH`
- Interview warning: offset-based pagination gets slower on large offsets; keyset pagination is often better.

## Top-N queries

```sql
SELECT *
FROM employees
ORDER BY salary DESC
LIMIT 3;
```

**Result:**

| emp_id | emp_name | dept_id | manager_id | salary    | join_date  |
|--------|----------|---------|------------|-----------|------------|
| 1      | Asha     | 10      | NULL       | 120000.00 | 2023-01-15 |
| 6      | Faisal   | 10      | 1          | 120000.00 | 2024-07-01 |
| 5      | Eva      | 20      | 2          | 110000.00 | 2024-04-18 |

## Tricky questions
**Q: Does join order matter logically?**
- For inner joins, the optimizer can often reorder.
- For outer joins, order and placement of conditions can change results.

**Q: Can a subquery return multiple columns in `IN`?**
- Usually `IN` compares one expression to one-column results, though some systems support row-value comparisons.

**Q: Is a CTE always materialized?**
- No. It depends on the database optimizer.

## Common mistakes
- Writing left joins and then filtering right-table columns in `WHERE`
- Using `NOT IN` on nullable subqueries
- Forgetting duplicates when joining one-to-many tables
- Using `SELECT *` in interview answers instead of precise columns

## Quick revision
- Understand inner vs outer joins deeply.
- Prefer `NOT EXISTS` over nullable `NOT IN` cases.
- Practice self joins, correlated subqueries, and CTEs.