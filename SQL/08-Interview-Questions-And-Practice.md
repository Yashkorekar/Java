# SQL Interview Questions and Practice Set

> See **01-SQL-Fundamentals.md** for full sample tables with all rows.
> The CREATE TABLE statements and sample data are also in Part 6 below.

---

## Part 1: Fast theory questions
1. What is the difference between `WHERE` and `HAVING`?
2. What is the difference between `DELETE`, `TRUNCATE`, and `DROP`?
3. What is the difference between `PRIMARY KEY` and `UNIQUE`?
4. What is normalization and why is it needed?
5. What is denormalization and when would you use it?
6. What is the difference between `INNER JOIN` and `LEFT JOIN`?
7. What is a self join?
8. What is the difference between `UNION` and `UNION ALL`?
9. What is a correlated subquery?
10. What is the difference between `RANK()` and `DENSE_RANK()`?
11. What is the difference between `COUNT(*)` and `COUNT(column)`?
12. What are ACID properties?
13. What is a deadlock?
14. What is an index and why is it used?
15. What is the leftmost-prefix idea in composite indexes?

## Part 2: Common interview coding questions

### 1. Find duplicate emails

```sql
SELECT email
FROM users
GROUP BY email
HAVING COUNT(*) > 1;
```

**Result:**

| email            |
|------------------|
| asha@example.com |

### 2. Find second highest salary

```sql
SELECT MAX(salary) AS second_highest_salary
FROM employees
WHERE salary < (
  SELECT MAX(salary)
  FROM employees
);
```

**Result:**

| second_highest_salary |
|-----------------------|
| 110000.00             |

Tie-safe rank-based version:

```sql
WITH ranked_salary AS (
  SELECT salary,
         DENSE_RANK() OVER (ORDER BY salary DESC) AS dr
  FROM employees
)
SELECT salary
FROM ranked_salary
WHERE dr = 2;
```

**Result:**

| salary    |
|-----------|
| 110000.00 |

### 3. Find employees earning more than department average

```sql
SELECT e.emp_id, e.emp_name, e.dept_id, e.salary
FROM employees e
WHERE e.salary > (
  SELECT AVG(e2.salary)
  FROM employees e2
  WHERE e2.dept_id = e.dept_id
);
```

**Result** (dept 10 avg≈11166, dept 20 avg=97500, dept 30 avg=66000):

| emp_id | emp_name | dept_id | salary    |
|--------|----------|---------|----------|
| 1      | Asha     | 10      | 120000.00 |
| 6      | Faisal   | 10      | 120000.00 |
| 5      | Eva      | 20      | 110000.00 |
| 4      | David    | 30      | 70000.00  |

### 4. Find departments with at least 5 employees

```sql
SELECT dept_id, COUNT(*) AS emp_count
FROM employees
GROUP BY dept_id
HAVING COUNT(*) >= 5;
```

**Result:**

*(No rows — no department has 5+ employees in sample data. With `>= 3`, dept_id 10 would qualify.)*

### 5. Find customers who never placed an order

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

### 6. Top 3 salaries per department

```sql
WITH ranked_employees AS (
  SELECT emp_id,
         emp_name,
         dept_id,
         salary,
         DENSE_RANK() OVER (PARTITION BY dept_id ORDER BY salary DESC) AS dr
  FROM employees
)
SELECT *
FROM ranked_employees
WHERE dr <= 3;
```

**Result:**

| emp_id | emp_name | dept_id | salary    | dr |
|--------|----------|---------|-----------|----|
| 8      | Hari     | NULL    | 55000.00  | 1  |
| 1      | Asha     | 10      | 120000.00 | 1  |
| 6      | Faisal   | 10      | 120000.00 | 1  |
| 3      | Chitra   | 10      | 95000.00  | 2  |
| 5      | Eva      | 20      | 110000.00 | 1  |
| 2      | Bob      | 20      | 85000.00  | 2  |
| 4      | David    | 30      | 70000.00  | 1  |
| 7      | Gita     | 30      | 62000.00  | 2  |

*Note: Asha and Faisal both get dr=1 because DENSE_RANK gives same rank for ties.*

### 7. Delete duplicate rows but keep the smallest id

```sql
WITH duplicate_rows AS (
  SELECT id,
         email,
         ROW_NUMBER() OVER (PARTITION BY email ORDER BY id) AS rn
  FROM users
)
DELETE FROM users
WHERE id IN (
  SELECT id
  FROM duplicate_rows
  WHERE rn > 1
);
```

**Result:** 1 row deleted (user_id=3, the duplicate asha@example.com).

**users table after:**

| user_id | email              | gender |
|---------|--------------------|--------|
| 1       | asha@example.com   | F      |
| 2       | bob@example.com    | M      |
| 4       | chitra@example.com | F      |

### 8. Running total of sales by date

```sql
SELECT sale_date,
       amount,
       SUM(amount) OVER (ORDER BY sale_date) AS running_total
FROM sales;
```

**Result** (using orders table as sales):

| order_date | amount   | running_total |
|------------|----------|---------------|
| 2026-01-05 | 2500.00  | 2500.00       |
| 2026-01-12 | 8500.00  | 11000.00      |
| 2026-02-03 | 1200.00  | 12200.00      |
| 2026-02-14 | 15000.00 | 27200.00      |
| 2026-03-01 | 3200.00  | 30400.00      |
| 2026-03-15 | 4500.00  | 34900.00      |

### 9. Swap gender values `M` and `F`

```sql
UPDATE users
SET gender = CASE
               WHEN gender = 'M' THEN 'F'
               WHEN gender = 'F' THEN 'M'
               ELSE gender
             END;
```

**users table after swap:**

| user_id | email              | gender |
|---------|--------------------|--------|
| 1       | asha@example.com   | M      |
| 2       | bob@example.com    | F      |
| 3       | asha@example.com   | M      |
| 4       | chitra@example.com | M      |

### 10. Find consecutive days of login
Typical idea:
- use `LAG()` to compare current login date with previous login date
- derive streak groups with gaps-and-islands logic

## Part 3: Schema design questions
1. Design an e-commerce schema for users, products, orders, order_items, payments.
2. Design a movie booking system schema.
3. Design a school system with students, courses, enrollments.
4. Model many-to-many relationship between employees and projects.
5. When would you use composite primary keys in a junction table?

## Part 4: Optimization questions
1. Why is this query slow even though an index exists?
2. What columns would you index for a table queried by `customer_id`, `status`, and `created_at`?
3. Why can `LIKE '%abc'` be slow?
4. Why might `SELECT *` hurt performance?
5. Why is function-wrapped indexed column often a performance problem?

## Part 5: Edge-case questions interviewers ask
1. What happens if `NOT IN` subquery returns `NULL`?
2. How do you handle ties when finding nth highest salary?
3. Why can left join behave like inner join accidentally?
4. Can you use aggregate and non-aggregate columns together without `GROUP BY`?
5. Why is result order undefined without `ORDER BY`?
6. What is the difference between `ROW_NUMBER` and `RANK` when ties exist?
7. Why can `COUNT(column)` return less than `COUNT(*)`?
8. How do you fetch latest row per user deterministically?

## Part 6: Mini practice dataset

```sql
CREATE TABLE employees (
  emp_id INT PRIMARY KEY,
  emp_name VARCHAR(100) NOT NULL,
  dept_id INT,
  manager_id INT,
  salary DECIMAL(10, 2),
  join_date DATE
);

CREATE TABLE departments (
  dept_id INT PRIMARY KEY,
  dept_name VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE customers (
  customer_id INT PRIMARY KEY,
  customer_name VARCHAR(100) NOT NULL,
  city VARCHAR(100)
);

CREATE TABLE orders (
  order_id INT PRIMARY KEY,
  customer_id INT,
  order_date DATE,
  amount DECIMAL(10, 2),
  FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);
```

## Practice prompts on the dataset
1. Find all employees with no manager.
2. Find department-wise employee count.
3. Find employees whose salary is above overall average.
4. Find employees whose salary is above their department average.
5. Find customers with more than 3 orders.
6. Find customer who spent the most in total.
7. Find employees who joined in the last 30 days.
8. Find top 2 salaries per department.
9. Find departments with no employees.
10. Find customers who ordered in January but not in February.

## Practice prompts without answers
1. Write a query to find the latest order for each customer.
2. Write a query to find duplicate customer names.
3. Write a query to find employees sharing same manager.
4. Write a query to pivot counts by department if your database supports pivoting.
5. Write a query to identify gaps in daily order dates.
6. Write a query to compute monthly revenue growth percentage.
7. Write a query to return the median salary if your database supports percentile functions.
8. Write a query to detect overlapping bookings.
9. Write a query to soft-delete duplicates by updating a status column.
10. Write a query to find the first order date for each customer.

## What interviewers evaluate
- correctness under `NULL`, duplicates, and ties
- clear explanation of join cardinality
- ability to choose between join, subquery, and window function
- awareness of performance and indexing
- deterministic SQL instead of lucky SQL

## Final revision strategy
1. Solve each query both with a subquery and with window functions where possible.
2. Explain every query in logical execution order.
3. For each solution, ask whether it breaks with `NULL`, duplicates, ties, or missing rows.
4. Review execution plans for at least a few non-trivial queries.