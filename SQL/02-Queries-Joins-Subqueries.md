# Queries, Joins, Subqueries, and CTEs — Interview Q&A

## Sample tables used in explanations
- `employees(emp_id, emp_name, dept_id, manager_id, salary)`
- `departments(dept_id, dept_name)`
- `orders(order_id, customer_id, order_date, amount)`
- `customers(customer_id, customer_name, city)`

## Basic filtering

```sql
SELECT emp_name, salary
FROM employees
WHERE salary >= 70000
  AND dept_id = 10;
```

## `IN`, `BETWEEN`, `LIKE`

```sql
SELECT *
FROM customers
WHERE city IN ('Delhi', 'Pune', 'Mumbai');
```

```sql
SELECT *
FROM orders
WHERE amount BETWEEN 1000 AND 5000;
```

```sql
SELECT *
FROM employees
WHERE emp_name LIKE 'A%';
```

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

## `LEFT JOIN`

```sql
SELECT e.emp_name, d.dept_name
FROM employees e
LEFT JOIN departments d
  ON e.dept_id = d.dept_id;
```

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